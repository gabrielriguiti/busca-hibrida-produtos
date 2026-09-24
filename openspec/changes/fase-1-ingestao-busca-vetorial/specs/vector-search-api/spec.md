## Purpose

Expõe busca semântica de produtos via HTTP, pra que um cliente envie texto livre e receba
produtos ranqueados por significado em vez de correspondência exata de palavra-chave.

## ADDED Requirements

### Requirement: Endpoint de busca vetorial
O sistema SHALL expor `GET /api/busca?q=<texto>` que normaliza `q`, embeda com o prefixo `query: ` do e5, e devolve produtos ranqueados por similaridade vetorial.

#### Scenario: Busca devolve resultados ranqueados
- **WHEN** um cliente chama `GET /api/busca?q=parafuso 8mm`
- **THEN** a resposta é `200 OK` com uma lista de produtos ordenada por similaridade
  decrescente em relação ao embedding da query

#### Scenario: Prefixo assimétrico do lado da query
- **WHEN** o endpoint de busca embeda a query recebida
- **THEN** ele embeda o texto com o prefixo `query: ` (não o texto normalizado puro),
  batendo com o que o e5 espera pra embedar queries

### Requirement: Tratamento de query vazia
O sistema SHALL devolver `400 Bad Request` quando `q` estiver ausente ou vazio, em vez de
rodar uma busca com um embedding vazio.

#### Scenario: Parâmetro de query ausente
- **WHEN** um cliente chama `GET /api/busca` sem nenhum parâmetro `q`
- **THEN** a resposta é `400 Bad Request`
