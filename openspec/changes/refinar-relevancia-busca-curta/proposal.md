## Why

Testando a busca com a query curta "meio litro" nesta sessão, dois modos ainda misturavam
parafuso com óleo mesmo depois do fix de limiar de relevância do vetorial: o fonético
porque o Soundex colide o código de produto "m8"/"m6" (que sobra só a letra "m" depois de
descartar o dígito) com palavras curtas como "meio"; e o híbrido/léxico porque `pg_trgm`
compara a query inteira contra a string INTEIRA de `texto_busca` (nome+descrição
concatenados) — uma query de 2 palavras dilui demais contra um texto longo, mesmo quando a
frase aparece literalmente como substring (similarity 0.2157 pra "meio litro" contra um
produto que literalmente contém "meio litro", abaixo do limiar padrão 0.3). O entregável
único desta change: nenhum dos dois modos deve trazer resultado de categoria errada pra uma
query curta e específica, sem regredir os 70 casos do harness de avaliação.

## What Changes

- Fonético: parar de tratar tokens curtos que sobram só como uma letra depois de descartar
  dígitos (ex.: "m8" → "m") como se tivessem conteúdo fonético real — hoje eles colidem com
  qualquer palavra curta cujas consoantes também se resumem a essa letra.
- Léxico/híbrido: trocar a recuperação de candidatos lexicais de `similarity()`/`%` (compara
  a query contra a string inteira de `texto_busca`) para `word_similarity()`/`<%` (compara a
  query contra a melhor sub-sequência de palavras dentro do `texto_busca`), que não dilui
  pra queries curtas contra campos longos.
- Recalibrar o limiar de similaridade lexical (se necessário) pro operador `word_similarity`,
  já que sua escala/distribuição de valores é diferente da similaridade de string inteira.

## Capabilities

### New Capabilities
- `phonetic-search`: busca fonética via Soundex, incluindo a regra de quais tokens têm
  conteúdo fonético suficiente pra participar do ranking. Introduzida na fase-2 só como
  baseline do harness de avaliação e depois exposta via API/frontend na fase-4, mas nunca
  formalizada como capability própria — esta change corrige seu comportamento, então é a
  oportunidade de documentá-la.

### Modified Capabilities
- `hybrid-search`: o requisito "Candidatos lexicais via similaridade de trigrama" muda de
  `similarity()`/`%` (string inteira) pra `word_similarity()`/`<%` (melhor sub-sequência de
  palavras), pra não diluir queries curtas contra `texto_busca` longo.

## Impact

- `PhoneticSearchService`/`Soundex`: lógica de quais tokens participam do ranking fonético.
- `LexicalSearchService`: query SQL de recuperação de candidatos (usada tanto standalone
  quanto como metade do pool do híbrido).
- Testes existentes (`PhoneticSearchServiceTest`, harness de avaliação via `EvalReportTest`)
  precisam continuar passando sem regressão de Recall@10/MRR@10 nos 70 casos rotulados.
- `README.md`: nota de arquitetura sobre a estratégia de recuperação lexical, se o texto
  atual ainda mencionar `similarity()`/`%`.
