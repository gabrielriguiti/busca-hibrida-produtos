## 1. Documentação de API

- [x] 1.1 Adicionar a dependência `springdoc-openapi` e anotar os controllers, verificar que a Swagger UI lista `/embed` e `/api/busca`
- [x] 1.2 Verificar que o endpoint JSON do OpenAPI devolve um documento válido (ex.: via um validador de schema ou inspeção manual)

## 2. Stack única no Docker Compose

- [x] 2.1 Escrever um Dockerfile multi-stage pro frontend, verificar que ele builda e serve o frontend construído localmente
- [x] 2.2 Adicionar o serviço de frontend ao `docker-compose.yml` junto com backend e Postgres, verificar que `docker compose up` a partir de um clone limpo sobe os três e o frontend consegue alcançar o backend

## 3. README e demo

- [x] 3.1 Escrever as seções de declaração do problema e arquitetura, verificar contra a arquitetura de fato implementada (não o plano original, caso tenham divergido)
- [x] 3.2 Inserir a tabela comparativa de métricas da fase-2/fase-3, verificar que os números batem com a saída da última rodada de avaliação
- [x] 3.3 Gravar e incorporar um GIF de demo do toggle de modo do frontend, verificar que ele renderiza no README no GitHub
