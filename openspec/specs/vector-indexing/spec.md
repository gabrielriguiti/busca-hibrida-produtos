# vector-indexing Specification

## Purpose
Coloca o embedding de cada produto no Postgres, em massa e pesquisável por vizinho mais
próximo aproximado, sem embedar produto por produto.

## Requirements

### Requirement: Embedding em batch durante a indexação
O sistema SHALL embedar textos de produto em lotes de 32 a 64 textos por chamada de
inferência durante o job de indexação, em vez de uma chamada de inferência por produto.

#### Scenario: Indexação do catálogo completo usa batching
- **WHEN** o job de indexação roda sobre o catálogo semente completo
- **THEN** o número de chamadas de inferência de embedding é aproximadamente
  tamanho_catalogo / tamanho_lote (tamanho_lote entre 32 e 64), não igual ao tamanho do
  catálogo

### Requirement: Índice HNSW pra busca de similaridade
O sistema SHALL manter um índice HNSW na coluna vector dos produtos pra que consultas de
vizinho mais próximo não exijam varredura completa da tabela.

#### Scenario: Índice existe depois que o job de indexação termina
- **WHEN** o job de indexação termina
- **THEN** existe um índice HNSW na coluna vector de produtos no Postgres

### Requirement: Reindexação idempotente
O sistema SHALL permitir que o job de indexação seja executado novamente com segurança,
atualizando os vetores de produtos existentes em vez de duplicar linhas.

#### Scenario: Nova execução após uma mudança no dataset
- **WHEN** o job de indexação é executado duas vezes seguidas sem nenhuma mudança no
  catálogo
- **THEN** a contagem de linhas da tabela de produtos permanece a mesma após a segunda
  execução
