## Por que

A fase 0 provou que a geração de embedding funciona isolada. Essa fase transforma isso numa
funcionalidade de busca de ponta a ponta: normalizar o texto sujo do catálogo, embedar e
indexar, e expor um endpoint de consulta. Entregável: busca semântica funcionando ponta a
ponta via HTTP.

## O que Muda

- Construir um pipeline de normalização (`texto_busca`): lowercase, remoção de acento,
  expansão de abreviações conhecidas a partir do dataset semente da fase 0.
- Indexar o catálogo em batch: embedar produtos em lotes de 32-64 textos por chamada de
  inferência e guardar os vetores resultantes.
- Adicionar um índice HNSW na coluna `pgvector` pra busca aproximada de vizinhos mais
  próximos.
- Expor `GET /api/busca?q=` que normaliza a query, prefixa com `query: ` (convenção de
  retrieval assimétrico do e5), embeda, e devolve os produtos mais próximos por distância
  vetorial.

## Capabilities

### New Capabilities
- `text-normalization`: transformação determinística `texto_busca` (lowercase, remoção de
  acento, expansão de abreviação) aplicada tanto no texto indexado do produto quanto nas
  queries recebidas.
- `vector-indexing`: job em batch que normaliza, embeda (32-64 textos/chamada de inferência)
  e grava os vetores dos produtos no Postgres com um índice HNSW.
- `vector-search-api`: `GET /api/busca?q=` executando busca de similaridade vetorial pura.

### Modified Capabilities
(nenhuma)

## Impact

- Depende de `embedding-service` e `catalog-seed-dataset` da fase-0-fundacao.
- Novo índice Postgres (HNSW) na coluna vector de produtos.
- Novo job em batch / task de CLI ou startup pra (re)indexar o catálogo.
