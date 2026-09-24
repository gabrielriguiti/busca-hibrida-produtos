## Purpose

Torna o projeto inteiro executável e compreensível nos primeiros cinco minutos que um
avaliador gasta nele, sem nenhum passo de setup manual além de um comando.

## ADDED Requirements

### Requirement: Stack em um único comando
O sistema SHALL subir Postgres, backend e frontend juntos via um único comando
`docker compose up`, sem nenhum outro passo de setup manual necessário.

#### Scenario: De clone limpo a stack rodando
- **WHEN** um avaliador clona o repo e roda `docker compose up` sem nenhum setup prévio
- **THEN** Postgres, backend e frontend estão todos rodando e o frontend consegue chamar com
  sucesso o endpoint de busca do backend

### Requirement: README cobre problema, arquitetura, métricas e demo
O sistema SHALL incluir um `README.md` na raiz descrevendo o problema sendo resolvido, a
arquitetura do sistema, a tabela comparativa de Recall@10/MRR@10 (fonético / vetorial /
híbrido), e um GIF demonstrando o frontend.

#### Scenario: README tem as quatro seções
- **WHEN** um avaliador abre o `README.md`
- **THEN** ele contém uma declaração do problema, uma seção de arquitetura, a tabela
  comparativa de métricas, e um GIF de demo incorporado
