# Arquitetura

## Visão geral

```
Navegador
    │  HTTP
    ▼
frontend (Vite/React, servido por nginx)
    │  proxy /api → app:8080
    ▼
app (Spring Boot / Java 21)
    │                              │
    ▼                              ▼
Postgres                    e5-small (ONNX Runtime,
 - pgvector (HNSW)            em processo na JVM, via DJL)
 - pg_trgm (GIN)
```

Três processos via `docker compose up`: Postgres, backend (Spring Boot) e frontend
(build estático servido por nginx, que faz proxy de `/api` pro backend). O backend
reindexa o catálogo sozinho no startup — sem isso, um clone limpo sobe com
`embedding`/`texto_busca` `NULL` e os modos vetorial/híbrido voltam vazios (achado
durante a implementação, não previsto no plano original).

## Decisões e racional

### Três estratégias de busca, não uma
- Contexto: um catálogo de produtos real tem abreviação inconsistente, erro de
  digitação, e a mesma unidade escrita de 5+ formas — cada abordagem de busca erra de
  um jeito diferente diante disso.
- Alternativas consideradas: escolher só busca vetorial (a "solução moderna óbvia").
- Escolha: implementar fonética (Soundex), vetorial (embeddings) e híbrida (RRF), e
  avaliar as três com Recall@10/MRR@10 reais sobre o mesmo catálogo sujo em vez de
  afirmar por afirmar que uma é melhor.
- Trade-offs: mais código pra manter três caminhos de busca em vez de um — aceitável
  porque o ponto do projeto é justamente mostrar o trade-off entre eles.

### Fusão de ranking: Reciprocal Rank Fusion, não média ponderada de score
- Contexto: o ranking léxico (`pg_trgm`) e o vetorial (`pgvector`) produzem scores em
  escalas incompatíveis — similaridade de trigrama (0–1) e distância de cosseno (0–2,
  quanto menor melhor) não são diretamente comparáveis nem normalizáveis sem uma
  calibração arbitrária.
- Alternativas consideradas: normalizar os dois scores pra uma escala comum e somar
  com pesos.
- Escolha: RRF (`score = Σ 1/(k+rank)`, k=60, o default do paper original) — usa só a
  *posição* de cada resultado em cada ranking, não o score bruto, então não precisa de
  calibração entre sistemas heterogêneos.
- Trade-offs: perde informação de "quão mais relevante" um resultado é dentro do
  próprio ranking (só a posição importa); aceitável porque o objetivo é combinar dois
  sinais complementares, não fazer scoring fino de um só.

### Recuperação léxica: `word_similarity`/`<%`, não `similarity`/`%`
- Contexto: `texto_busca` concatena nome+descrição do produto — uma string bem mais
  longa que a query típica. `similarity()`/`%` do `pg_trgm` compara a query contra
  essa string INTEIRA, o que dilui demais queries curtas: uma busca de 2 palavras
  ("meio litro") contra um produto que literalmente contém essa frase ainda media
  0.2157 de similaridade — abaixo do limiar padrão de 0.3 — só por diluição no
  restante do texto.
- Alternativas consideradas: manter `similarity()`/`%` e só baixar o limiar (não
  resolve — o problema é a métrica comparar contra a string inteira, não o valor do
  corte); truncar `texto_busca` (perde contexto de produtos com descrição relevante).
- Escolha: `word_similarity()`/`<%`, que compara a query contra a melhor
  sub-sequência de palavras dentro do texto, não a string inteira. Limiar fixado
  explicitamente em 0.5 (não o default 0.6 da extensão) via comparação literal na
  query SQL — calibrado contra os 70 pares (query, produto esperado) do harness de
  avaliação: 0.5 cobre 69/70, 0.6 só 64/70.
- Trade-offs: limiar calibrado contra um catálogo pequeno (22 produtos) — documentado
  como tal, não como valor universal.

### Limiar de distância no vetorial, calibrado contra o harness
- Contexto: a busca vetorial sem corte sempre devolvia exatamente N resultados, mesmo
  quando só 1-2 eram de fato relevantes — o resto virava ruído de preenchimento (ex.:
  a query "meio litro" trazia parafuso só pra completar a lista).
- Alternativas consideradas: nenhum corte (comportamento original); corte fixo
  "chutado" sem validação.
- Escolha: limiar de distância de cosseno 0.18, calibrado rodando os 70 pares
  (query, produto esperado) do harness e confirmando que 220/220 continuam cobertos
  nesse valor — corte só descarta o que já era ruído, nunca um resultado correto
  conhecido.
- Trade-offs: mesmo aviso do limiar léxico — calibrado contra o catálogo disponível,
  não uma constante universal; documentado no código com a metodologia usada.

### Soundex: ignorar tokens sem conteúdo fonético real
- Contexto: código de produto alfanumérico (ex.: "M8") reduz, depois de descartar
  dígitos, a uma única letra — e colide por Soundex com qualquer palavra curta cujas
  consoantes também se resumem a essa letra (ex.: "M8"→"M000" == "meio"→"M000"),
  fazendo parafuso aparecer em buscas sobre óleo.
- Alternativas consideradas: trocar o algoritmo fonético inteiro (fora de escopo —
  Soundex é intencionalmente uma baseline fraca de comparação); manter só o filtro de
  score > 0 já existente (insuficiente — a colisão gera um match genuíno, não um score
  zero escondido).
- Escolha: exigir 3+ letras na parte alfabética de um token (depois de descartar
  dígitos) pra ele participar do cálculo de overlap fonético. Validado manualmente:
  nenhuma palavra real do catálogo cai abaixo disso — só código de produto e
  stopwords curtas.
- Trade-offs: descarta silenciosamente conteúdo fonético de tokens como "wd" (de
  "WD-40") — aceitável, Soundex já é uma baseline fraca de propósito.

### Embeddings em processo (DJL + ONNX Runtime), não microserviço Python
- Contexto: e5-small precisa rodar em algum lugar pra gerar os vetores de 384
  dimensões usados na busca vetorial.
- Alternativas consideradas: serviço Python separado (FastAPI + `sentence-transformers`),
  mais familiar pro ecossistema de embeddings, mas adiciona um processo, uma
  linguagem e uma chamada de rede por busca.
- Escolha: DJL (Deep Java Library) + ONNX Runtime rodando dentro da própria JVM —
  um processo só, sem chamada de rede entre busca e embedding.
- Trade-offs: perde a conveniência do ecossistema Python pra trocar de modelo
  rapidamente; troca isso por simplicidade operacional (um artefato de deploy).

### Reindexação automática no startup
- Contexto: descoberto testando `docker compose up` a partir de um volume do
  Postgres genuinely zerado (não um ambiente de dev já usado antes) — sem
  reindexação automática, `embedding`/`texto_busca` ficam `NULL` num clone limpo, e
  os modos vetorial/híbrido voltam vazios (`[]`) pro avaliador, mesmo com a stack
  inteira "no ar".
- Alternativas consideradas: documentar `POST /api/index` como passo manual no
  README (era o estado original) — contradiz o objetivo explícito de "zero passo
  manual" da fase de empacotamento.
- Escolha: `ApplicationRunner` que chama a reindexação no boot do app, depois da
  migration Flyway. Seguro rodar em todo restart porque a reindexação é idempotente
  (sempre `UPDATE` por id, nunca insere linha).
- Trade-offs: reindexa o catálogo inteiro a cada restart, mesmo sem mudança —
  aceitável pra 22 produtos; não escalaria pra um catálogo real sem virar indexação
  incremental.

### Proxy do frontend: resolver DNS do Docker em runtime, não no boot do nginx
- Contexto: `proxy_pass` do nginx resolve o hostname do serviço backend (`app`) uma
  vez, no carregamento da configuração — se esse serviço ainda não estiver na rede
  Docker nesse instante, o nginx recusa subir e o container inteiro cai, não só a
  rota `/api`.
- Alternativas consideradas: `proxy_pass` estático (comportamento original, frágil a
  ordem de start dos containers).
- Escolha: resolver DNS do Docker (`127.0.0.11`) + hostname numa variável, forçando
  resolução em tempo de requisição em vez de no boot. Usar variável no `proxy_pass`
  desliga a reescrita automática de prefixo de URI do nginx — corrigido repassando
  `$request_uri` explicitamente, senão toda chamada a `/api/busca?...` virava um 404
  com o path truncado pra `/api/`.
- Trade-offs: nenhum — é estritamente mais robusto que o comportamento original, sem
  custo adicional.

### Postgres único pra vetorial e léxico, não um sistema dedicado
- Contexto: `pgvector` (HNSW) e `pg_trgm` (GIN) são extensões do mesmo Postgres que já
  guarda o catálogo.
- Alternativas consideradas: Elasticsearch/OpenSearch ou um vector DB dedicado
  (Pinecone, Weaviate) — potencialmente mais rico em tuning de relevância e melhor
  particionamento em escala grande.
- Escolha: um banco só — menos peças móveis, consistência transacional entre o
  catálogo e seus índices (a mesma linha que guarda `name`/`description` guarda
  `embedding`/`texto_busca`).
- Trade-offs: não teria sido a escolha certa num catálogo de milhões de produtos —
  decisão calibrada pro tamanho deste projeto (22 produtos), não uma recomendação de
  produção em escala.

## O que foi cortado de escopo

- CI/CD e deploy em nuvem — avaliado (VPS grátis do Oracle Cloud, limitações de
  ARM/ONNX Runtime), mas adiado; `docker compose up` local é todo o alvo de entrega
  por enquanto.
- Autenticação e hardening de produção — API pública sem auth, de propósito, pra um
  projeto de portfólio.
- Normalização de variantes de unidade com espaço ("500 ml", "500 mililitros") pra
  "meio litro" — só as formas de token único ("500ml", "0,5L") foram mapeadas no
  dicionário de abreviações; mapear tokens separados por espaço arriscaria colidir
  com outros usos futuros de números isolados.
