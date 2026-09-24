## Por que

Um avaliador gasta minutos, não horas, decidindo se um projeto de portfólio merece uma olhada
mais de perto. Essa fase faz essa primeira impressão valer: um comando pra rodar tudo, um
README que declara o problema e prova o resultado com números reais, e docs de API
descobríveis. Entregável: `docker compose up` sobe a stack inteira, com Swagger e um README
que um estranho consegue seguir.

## O que Muda

- Escrever o README: declaração do problema, visão geral da arquitetura, a tabela
  comparativa de métricas da fase-2/fase-3, e um GIF da demo do frontend.
- Adicionar documentação Swagger/OpenAPI pros endpoints do backend.
- Consolidar o setup do Docker Compose pra que um único arquivo suba Postgres, backend e
  frontend juntos (antes o frontend rodava separado via `npm run dev`).

## Capabilities

### New Capabilities
- `api-documentation`: docs interativas OpenAPI/Swagger pra todos os endpoints do backend
  (`/embed`, `/api/busca` em seus modos), acessíveis sem ler código-fonte.
- `release-packaging`: um único arquivo Docker Compose que sobe Postgres, backend e frontend
  juntos, e um README que documenta o problema, a arquitetura, a tabela de métricas e a
  demo.

### Modified Capabilities
(nenhuma)

## Impact

- Estende o `docker-compose.yml` da fase-0 pra adicionar o serviço de frontend (construído
  na fase-4).
- Adiciona uma dependência Swagger/OpenAPI ao app Spring Boot (ex.: springdoc-openapi).
- Novo `README.md` na raiz e um asset de GIF de demo.
