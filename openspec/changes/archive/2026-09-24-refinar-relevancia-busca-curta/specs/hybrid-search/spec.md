## MODIFIED Requirements

### Requirement: Candidatos lexicais via similaridade de trigrama
O sistema SHALL recuperar candidatos lexicais usando similaridade de trigrama por
sub-sequência de palavras (`word_similarity`) entre a query normalizada e o `texto_busca`
do produto, em vez de comparar a query contra a string inteira do `texto_busca`.

#### Scenario: Query com erro de digitação ainda encontra correspondência de trigrama
- **WHEN** uma query contém um erro de digitação de um ou dois caracteres num termo de
  produto (ex.: "parafuzo" no lugar de "parafuso")
- **THEN** o conjunto de candidatos lexicais inclui o produto escrito corretamente

#### Scenario: Query curta encontra correspondência literal dentro de um texto_busca longo
- **WHEN** uma query curta (ex.: duas palavras) aparece literalmente como substring dentro
  do `texto_busca` de um produto, mas o `texto_busca` é bem mais longo que a query (nome
  concatenado com descrição)
- **THEN** o conjunto de candidatos lexicais inclui esse produto, mesmo que a similaridade
  da string inteira ficasse abaixo do limiar antigo por diluição
