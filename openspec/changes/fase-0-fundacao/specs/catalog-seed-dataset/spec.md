## Purpose

Fornece um dataset de produtos fixo e propositalmente sujo, junto com o schema Postgres para
guardá-lo, para que todas as fases seguintes (normalização, indexação, avaliação) trabalhem
sobre a mesma entrada realista, em vez de exemplos limpos escolhidos a dedo.

## ADDED Requirements

### Requirement: Dataset sintético sujo
O sistema SHALL disponibilizar um dataset sintético de produtos que inclua deliberadamente
abreviações inconsistentes, erros de digitação, e a mesma unidade de medida escrita em pelo
menos 5 formas diferentes (ex.: `ml`, `mL`, `mililitros`, `500 ml`, `0.5L`).

#### Scenario: Dataset cobre inconsistência de unidades
- **WHEN** o dataset semente é carregado
- **THEN** ele contém pelo menos 5 representações textuais distintas da mesma unidade em
  produtos diferentes

### Requirement: Schema de produto com pgvector
O sistema SHALL definir uma tabela Postgres para produtos com uma coluna `pgvector`
dimensionada para embeddings de 384 posições, carregável via Docker Compose com a extensão
`pgvector` habilitada.

#### Scenario: Schema carrega em um container novo
- **WHEN** `docker compose up` sobe o serviço Postgres pela primeira vez
- **THEN** a tabela de produtos existe com uma coluna vector(384) e a extensão `pgvector`
  está habilitada, sem nenhum passo manual de setup
