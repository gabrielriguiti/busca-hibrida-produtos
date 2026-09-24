## Purpose

Cobre o que a busca vetorial sozinha perde (erros de digitação, correspondências quase-exatas
de substring) fundindo um ranking lexical de trigrama com o ranking vetorial, mantendo a
busca robusta tanto a erros de digitação quanto a reformulações.

## ADDED Requirements

### Requirement: Candidatos lexicais via similaridade de trigrama
O sistema SHALL recuperar candidatos lexicais usando similaridade `pg_trgm` entre a query
normalizada e o `texto_busca` do produto.

#### Scenario: Query com erro de digitação ainda encontra correspondência de trigrama
- **WHEN** uma query contém um erro de digitação de um ou dois caracteres num termo de
  produto (ex.: "parafuzo" no lugar de "parafuso")
- **THEN** o conjunto de candidatos lexicais inclui o produto escrito corretamente

### Requirement: Reciprocal Rank Fusion
O sistema SHALL fundir o ranking lexical e o ranking vetorial numa única lista ranqueada
usando Reciprocal Rank Fusion, onde o score fundido de cada resultado é a soma de
`1 / (k + rank)` entre os rankings em que ele aparece.

#### Scenario: Resultado presente nos dois rankings ranqueia mais alto que resultados de um só ranking
- **WHEN** um produto aparece nos 10 primeiros tanto do ranking lexical quanto do vetorial
  pra uma query
- **THEN** seu score RRF fundido é maior que o de um produto que aparece em apenas um dos
  dois rankings

### Requirement: Modo de busca híbrido
O sistema SHALL expor um modo híbrido no endpoint de busca que devolve resultados fundidos
por RRF, junto com o modo puramente vetorial já existente desde a fase-1.

#### Scenario: Modo híbrido selecionável via parâmetro de query
- **WHEN** um cliente chama o endpoint de busca pedindo o modo híbrido
- **THEN** a resposta contém resultados fundidos por RRF, e cada resultado inclui seus
  scores/ranks lexical e vetorial contribuintes pra exibição

### Requirement: Estratégia híbrida avaliada pelo harness existente
O sistema SHALL ser avaliável pelo harness de avaliação da fase-2 como uma terceira
estratégia plugável, produzindo Recall@10 e MRR@10 junto com fonético e vetorial.

#### Scenario: Comparação entre as três estratégias
- **WHEN** o harness de avaliação é rodado pras estratégias fonética, vetorial e híbrida
- **THEN** o relatório mostra o Recall@10 e o MRR@10 das três estratégias numa única tabela
