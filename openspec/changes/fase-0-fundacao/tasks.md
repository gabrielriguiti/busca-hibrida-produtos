## 1. Estrutura do repo

- [x] 1.1 Criar app Spring Boot (Maven/Gradle, Java 21) e verificar que `./mvnw spring-boot:run` (ou equivalente gradle) sobe sem nenhum endpoint ainda
- [x] 1.2 Escrever `docker-compose.yml` com `app` + `postgres` (imagem pgvector, ex. `pgvector/pgvector:pg16`) e verificar que `docker compose up` sobe os dois containers saudáveis
- [x] 1.3 Adicionar uma migration Postgres (Flyway/Liquibase) habilitando a extensão `pgvector` e criando a tabela `products` com uma coluna `vector(384)`, verificar que ela aplica num container novo

## 2. Dataset sintético sujo

- [x] 2.1 Redigir um checklist dos padrões de "sujeira" a incluir (abreviações, erros de digitação, 5+ variações de unidade) e verificar que o checklist está capturado no rascunho do README
- [x] 2.2 Escrever os dados de seed (SQL ou CSV + loader) cobrindo cada padrão do checklist, verificar contagem de linhas e cobertura dos padrões com um script/query rápido

## 3. Serviço de embedding

- [ ] 3.1 Adicionar dependência DJL ou ONNX Runtime for Java e carregar o e5-small, verificar que o modelo carrega no startup do app sem erro
- [ ] 3.2 Implementar `POST /embed` devolvendo `{"vector": [...]}`, verificar com uma chamada curl manual
- [ ] 3.3 Escrever um teste unitário garantindo que o vetor devolvido tem exatamente 384 dimensões, verificar que passa em CI/execução local
