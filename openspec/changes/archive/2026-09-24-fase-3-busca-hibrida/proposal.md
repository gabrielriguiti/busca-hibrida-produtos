## Por que

Busca vetorial sozinha erra em erros de digitação (embeddings borram significado, não
grafia) e busca lexical/trigrama sozinha erra em sinônimos e reformulações. Combinar as duas,
e provar isso com o harness da fase-2, é o que mostra entendimento real do trade-off em vez
de simplesmente escolher uma abordagem. Entregável: três linhas na tabela de métricas
(fonético / vetorial / híbrido) mostrando o modo de falha distinto de cada abordagem.

## O que Muda

- Adicionar geração de candidatos lexicais via similaridade de trigrama do Postgres
  (`pg_trgm`).
- Fundir o ranking lexical e o ranking vetorial usando Reciprocal Rank Fusion (RRF).
- Rodar o harness de avaliação da fase-2 sem mudança, adicionando a estratégia híbrida como
  uma terceira linha junto com fonético e vetorial.

## Capabilities

### New Capabilities
- `hybrid-search`: combina candidatos lexicais do `pg_trgm` com resultados de busca vetorial
  via Reciprocal Rank Fusion, e é avaliada pelo harness da fase-2 como uma terceira
  estratégia.

### Modified Capabilities
(nenhuma — o harness de avaliação em si é reutilizado sem mudança; só uma nova estratégia é
plugada nele, o que é aditivo do ponto de vista do harness)

## Impact

- Depende de `vector-search-api` (fase-1) e `search-evaluation` (fase-2).
- Nova extensão Postgres: `pg_trgm`, e um índice de trigrama (GIN/GiST) na coluna de texto
  pesquisável.
- `GET /api/busca?q=` ganha uma opção `modo=hibrido` (ou similar), ainda devolvendo uma lista
  ranqueada, agora com os scores lexical e vetorial visíveis por resultado pro toggle da
  fase-4.
