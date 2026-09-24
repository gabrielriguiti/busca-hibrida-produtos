## Purpose

Transforma texto qualquer em um vetor numérico de tamanho fixo, em processo (sem depender de
um serviço Python externo), para que todas as fases seguintes possam gerar embeddings de
queries e de texto de produto da mesma forma.

## ADDED Requirements

### Requirement: Endpoint de embedding
O sistema SHALL expor `POST /embed` aceitando um corpo JSON com um campo `text` e devolvendo
um corpo JSON com um campo `vector` contendo o embedding como um array de floats.

#### Scenario: Embedar texto qualquer
- **WHEN** um cliente faz POST de `{"text": "parafuso sextavado M8"}` para `/embed`
- **THEN** a resposta é `200 OK` com um array `vector`

### Requirement: Dimensão fixa do embedding
O sistema SHALL sempre devolver um vetor com exatamente 384 dimensões, correspondendo à saída
do e5-small, independente do tamanho do texto de entrada.

#### Scenario: Checagem de dimensão em entrada curta
- **WHEN** o texto de entrada é uma única palavra
- **THEN** o array `vector` devolvido tem exatamente 384 elementos

#### Scenario: Checagem de dimensão em entrada longa
- **WHEN** o texto de entrada é uma descrição de produto completa (várias frases)
- **THEN** o array `vector` devolvido tem exatamente 384 elementos

### Requirement: Inferência em processo
O sistema SHALL rodar o modelo e5-small dentro do processo da JVM (via DJL ou ONNX Runtime
for Java) sem chamar um serviço Python externo para fazer a inferência.

#### Scenario: Sem dependência de inferência externa no momento da requisição
- **WHEN** `/embed` é chamado com o container do Postgres como o único outro serviço rodando
- **THEN** a chamada funciona, provando que nenhum processo separado de model-serving é
  necessário
