# api-documentation Specification

## Purpose

Permite que qualquer pessoa descubra e teste os endpoints da API sem ler o código-fonte
Java, o que importa pra um projeto de portfólio que um estranho vai folhear.

## Requirements

### Requirement: Docs de API interativas
O sistema SHALL expor uma UI interativa Swagger/OpenAPI listando todos os endpoints do
backend (`/embed`, `/api/busca` e seus modos) com seus formatos de request/response.

#### Scenario: Swagger UI acessível
- **WHEN** o backend está rodando e um browser abre o path da Swagger UI
- **THEN** ela lista `/embed` e `/api/busca` com schemas de exemplo de request/response

### Requirement: Exportação da spec OpenAPI
O sistema SHALL expor um documento OpenAPI JSON/YAML legível por máquina, derivado dos
mesmos endpoints anotados que alimentam a UI interativa.

#### Scenario: Documento OpenAPI é buscável
- **WHEN** um cliente pede o endpoint JSON do OpenAPI
- **THEN** ele devolve um documento OpenAPI válido descrevendo todos os endpoints do backend
