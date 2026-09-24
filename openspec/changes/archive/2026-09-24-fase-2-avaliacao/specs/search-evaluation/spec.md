## Purpose

Transforma "a busca funciona" num número medido e repetível, pra que qualquer mudança na
estratégia de busca seja julgada contra uma baseline fixa, e não no olho.

## ADDED Requirements

### Requirement: Conjunto de queries rotulado
O sistema SHALL manter um conjunto fixo de 50 a 100 queries contra o catálogo semente, cada
uma com uma lista rotulada manualmente de id(s) de produto relevante(s) esperado(s).

#### Scenario: Tamanho e formato do conjunto de queries
- **WHEN** o conjunto de queries de avaliação é carregado
- **THEN** ele contém entre 50 e 100 queries, cada uma com pelo menos um id de produto
  relevante esperado

### Requirement: Métrica Recall@10
O sistema SHALL calcular o Recall@10 de uma estratégia de busca como a fração de queries pra
qual pelo menos um produto relevante esperado aparece nos 10 primeiros resultados.

#### Scenario: Recall@10 calculado sobre o conjunto completo de queries
- **WHEN** uma estratégia de busca é rodada contra cada query do conjunto de avaliação
- **THEN** o sistema reporta um único valor de Recall@10 entre 0 e 1

### Requirement: Métrica MRR@10
O sistema SHALL calcular o MRR@10 de uma estratégia de busca como a média, sobre todas as
queries, do inverso da posição do primeiro produto relevante esperado encontrado nos 10
primeiros resultados (0 se nenhum for encontrado).

#### Scenario: MRR@10 calculado sobre o conjunto completo de queries
- **WHEN** uma estratégia de busca é rodada contra cada query do conjunto de avaliação
- **THEN** o sistema reporta um único valor de MRR@10 entre 0 e 1

### Requirement: Comparação de estratégias plugável
O sistema SHALL ser capaz de rodar o mesmo conjunto de queries e as mesmas métricas contra
mais de uma estratégia de busca (no mínimo: baseline fonética e busca vetorial) e produzir um
relatório comparativo.

#### Scenario: Relatório de comparação entre duas estratégias
- **WHEN** a avaliação é rodada tanto pra baseline fonética quanto pra estratégia de busca
  vetorial
- **THEN** o relatório mostra Recall@10 e MRR@10 de cada estratégia lado a lado
