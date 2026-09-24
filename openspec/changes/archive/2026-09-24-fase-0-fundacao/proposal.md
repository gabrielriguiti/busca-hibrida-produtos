## Por que

Ainda não existe nenhuma peça do projeto funcionando. Antes de construir normalização,
indexação ou avaliação, é preciso provar que a aposta tecnicamente mais arriscada — rodar o
modelo de embedding e5-small a partir do Java, sem um sidecar em Python — realmente funciona,
e ter um dataset realista (sujo) pra testar contra ele. Entregável: `POST /embed` devolve um
vetor de 384 dimensões para um texto qualquer.

## O que Muda

- Estruturar um repo Spring Boot com Docker Compose (app + Postgres com a extensão
  `pgvector` habilitada).
- Produzir um dataset sintético "sujo" de produtos (abreviações inconsistentes, erros de
  digitação, unidades escritas de 5 jeitos diferentes), já que não há uma base real
  disponível — isso vira parte da narrativa do README, não só dado de preenchimento.
- Integrar o modelo e5-small na JVM via DJL ou ONNX Runtime for Java.
- Expor `POST /embed` devolvendo o vetor de embedding para um texto dado.
- Teste unitário garantindo que o embedding tem exatamente 384 dimensões.

## Capabilities

### New Capabilities
- `embedding-service`: carrega o e5-small em processo (DJL/ONNX) e expõe `POST /embed`,
  devolvendo um vetor de ponto flutuante com 384 dimensões para o texto de entrada.
- `catalog-seed-dataset`: um dataset sintético de produtos, deliberadamente inconsistente (e
  o schema Postgres/pgvector em que ele é carregado), usado como entrada fixa em todas as
  fases seguintes.

### Modified Capabilities
(nenhuma — essa é a primeira change)

## Impact

- Nova estrutura de repo: `docker-compose.yml`, módulo Spring Boot, serviço Postgres com a
  extensão `pgvector`.
- Nova dependência de runtime: pesos do e5-small (empacotados ou baixados no build/startup),
  mais DJL ou ONNX Runtime for Java no classpath.
- Nenhum código existente afetado (projeto greenfield).
