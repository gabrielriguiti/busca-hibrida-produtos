## Context

Constrói em cima da busca vetorial da fase-1 e reutiliza o harness de avaliação da fase-2 sem
mudança como mecanismo de prova. Ver proposal.md - Por que.

## Goals / Non-Goals

**Goals:**
- Mostrar um caso concreto e medido onde o híbrido bate as duas baselines de estratégia
  única.
- Manter a fusão RRF simples e inspecionável (sem re-ranker aprendido).

**Non-Goals:**
- Nenhum modelo de ranking com machine learning — RRF é uma fórmula fixa e com poucos
  parâmetros por design.
- Nenhuma mudança de frontend aqui (a fase-4 adiciona a UI do toggle de modo).

## Decisions

- **Constante k do RRF**: usar o default comum `k=60` do paper original de RRF, a menos que a
  avaliação mostre um motivo claro pra ajustar; registrar o valor final usado.
- **Tamanho do pool de candidatos**: puxar o top-N (ex.: 50) de ambos os rankings lexical e
  vetorial antes de fundir, não só o top-10, pra que o RRF tenha sinal de sobreposição
  suficiente pra trabalhar.
- **Índice de trigrama**: índice GIN usando `gin_trgm_ops` do `pg_trgm` na coluna
  `texto_busca` normalizada, junto com o índice HNSW vetorial já existente — dois índices em
  colunas relacionadas mas com propósitos distintos, não substituindo um pelo outro.

## Risks / Trade-offs

- [Risk] RRF com k default não bate claramente as duas baselines nesse dataset → Mitigação:
  ainda é um resultado válido e honesto pra tabela do README — o objetivo é mostrar o
  trade-off, não garantir que o híbrido ganhe em toda query.
- [Risk] Rodar duas queries (lexical + vetorial) por requisição de busca dobra a latência →
  Mitigação: aceitável na escala desse projeto; anotar como um teto conhecido em vez de
  construir paralelização de query pra um catálogo do tamanho de um portfólio.
