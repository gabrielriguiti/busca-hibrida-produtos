# phonetic-search Specification

## Purpose

Busca por similaridade fonética (Soundex) entre os tokens da query e os tokens do produto,
como uma baseline deliberadamente simples que tolera variação de escrita que soa igual, mas
não deve confundir um código de produto alfanumérico curto (ex.: "M8") com uma palavra real
que acidentalmente reduz ao mesmo código fonético.

## Requirements

### Requirement: Ranking por overlap de código fonético
O sistema SHALL ranquear produtos pela quantidade de tokens da query cujo código Soundex
bate com algum token do produto, descartando produtos sem nenhum overlap em vez de
completar a lista de resultados com produtos sem relevância fonética.

#### Scenario: Erro de digitação que preserva o som ainda ranqueia o produto certo primeiro
- **WHEN** uma query contém um termo com erro de digitação que preserva os sons principais
  do termo correto (ex.: "furadeira eletrika" no lugar de "furadeira eletrica")
- **THEN** o produto com a grafia correta aparece no topo do ranking

#### Scenario: Query sem nenhum overlap fonético não aparece nos resultados
- **WHEN** um produto não compartilha nenhum código Soundex com nenhum token da query
- **THEN** esse produto não aparece na lista de resultados, mesmo que a lista fique com
  menos de 10 itens

### Requirement: Tokens sem conteúdo fonético suficiente não participam do ranking
O sistema SHALL excluir do cálculo de overlap fonético qualquer token cuja parte alfabética,
depois de descartar dígitos, tenha menos de 3 letras — tokens assim (ex.: "M8", "M6") não
carregam informação fonética real e colidem por acidente com palavras curtas não
relacionadas.

#### Scenario: Código de produto alfanumérico curto não colide com palavra curta não relacionada
- **WHEN** a query contém uma palavra curta cujo código Soundex coincide com o de um código
  de produto alfanumérico presente em produtos de categoria diferente (ex.: a query "meio
  litro" e o token de produto "M8", ambos reduzindo ao mesmo código Soundex)
- **THEN** o produto da categoria diferente não aparece no ranking só por causa dessa
  colisão
