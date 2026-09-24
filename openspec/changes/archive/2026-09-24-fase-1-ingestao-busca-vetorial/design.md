## Context

Constrói diretamente em cima do `embedding-service` (POST /embed) e do
`catalog-seed-dataset` da fase-0. Ver proposal.md - Por que. Ainda não existe nenhuma lógica
de normalização ou busca; `/embed` só transforma texto em vetor, nada indexa ou consulta o
catálogo.

## Goals / Non-Goals

**Goals:**
- Uma função de normalização determinística compartilhada entre indexação e busca.
- Indexação rápida o suficiente pra rodar de novo durante o desenvolvimento (em batch, não
  linha a linha).
- Um `GET /api/busca?q=` funcionando que devolva algo que um humano consiga avaliar.

**Non-Goals:**
- Sem correspondência lexical/trigrama ainda (fase-3).
- Sem métricas de avaliação ainda (fase-2) — essa fase só prova que o pipe funciona, não que
  é bom.
- Sem paginação, filtros ou scoring além da distância vetorial bruta.

## Decisions

- **Implementação da normalização**: processamento de string puro em Java (`Normalizer` do
  Java pra remover acento + lowercase + um expansor de abreviação por tabela de lookup) —
  nenhuma biblioteca de NLP necessária nesse escopo.
- **Dicionário de abreviações**: um arquivo YAML/JSON pequeno versionado no repo, derivado
  das inconsistências propositalmente semeadas no dataset da fase-0, extensível depois. Ver
  proposal.md - o dicionário deve cobrir exatamente o que o dataset semente exercita. Evitar:
  generalizar demais um schema de config pra abreviações que provavelmente nunca precisaria
  edição em runtime — um único arquivo estático já basta.
- **Trigger de indexação**: um `CommandLineRunner` do Spring Boot disparável manualmente/via
  CLI, ou um endpoint admin `POST /api/index` (o que der menos código), não um job agendado
  — isso é um projeto de portfólio com catálogo pequeno e fixo, não um pipeline em produção.
- **Convenção de prefixo do e5**: texto no momento da indexação é embedado com o prefixo
  `passage: ` do e5; texto no momento da busca é embedado com `query: `, conforme o uso de
  retrieval assimétrico documentado pelo e5.

## Risks / Trade-offs

- [Risk] Tempo de build do índice HNSW cresce com o tamanho do catálogo → Mitigação: o
  catálogo é sintético e pequeno por design (ver fase-0), então isso não é um problema nessa
  escala; anotar o teto no README em vez de engenheirar em torno disso.
- [Risk] Dicionário de abreviações é curado manualmente e incompleto → Mitigação: a avaliação
  da fase-2 vai expor as lacunas como falhas de recall; expandir o dicionário é um follow-up
  rápido e de baixo risco.
