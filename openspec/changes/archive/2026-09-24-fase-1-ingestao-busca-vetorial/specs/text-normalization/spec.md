## Purpose

Produz uma única forma canônica `texto_busca` pra qualquer texto de produto ou query, pra que
a mesma entrada suja (caixa mista, acentos, abreviações inconsistentes) fique comparável
independente de como foi digitada originalmente.

## ADDED Requirements

### Requirement: Normalização determinística
O sistema SHALL transformar qualquer texto de entrada numa forma canônica `texto_busca`
convertendo para minúsculas, removendo diacríticos/acentos, e expandindo abreviações
conhecidas a partir de um dicionário de abreviações mantido pelo projeto.

#### Scenario: Normalização de caixa e acento
- **WHEN** a entrada é "PARAFUSO SEXTAVADO Nº8 AÇO"
- **THEN** o `texto_busca` normalizado não tem letras maiúsculas nem caracteres acentuados

#### Scenario: Expansão de abreviação conhecida
- **WHEN** a entrada contém uma abreviação presente no dicionário de abreviações (ex.:
  "parag." para "parafuso")
- **THEN** o `texto_busca` normalizado contém a forma expandida

### Requirement: Normalização simétrica entre produtos e queries
O sistema SHALL aplicar exatamente a mesma função de normalização no texto do produto no
momento da indexação e no texto da query no momento da busca.

#### Scenario: Mesma função, dois pontos de chamada
- **WHEN** o `texto_busca` de um produto é calculado durante a indexação e o `texto_busca`
  de uma query é calculado durante a busca
- **THEN** ambos passam pela mesma implementação de normalização, sem lógica divergente
