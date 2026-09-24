## Context

Depende do `GET /api/busca?q=` da fase-1 (estratégia vetorial). Ver proposal.md - Por que.
Ainda não existe busca fonética, a menos que seja a abordagem original/legada do projeto
mencionada no plano ("sua busca fonética atual") — checar o código por uma implementação
existente antes de construir uma nova; se não existir nenhuma, essa fase precisa construir
uma mínima só pra ter uma baseline.

## Goals / Non-Goals

**Goals:**
- Um conjunto de queries e calculadores de métrica reutilizáveis, sem mudança, entre a
  fase-2 e a fase-3.
- Números reproduzíveis rodando um único comando.

**Non-Goals:**
- Sem avaliação da estratégia híbrida ainda (a fase-3 roda esse mesmo harness de novo).
- Sem teste de significância estatística — isso é um projeto de portfólio, uma tabela
  comparativa simples é o alvo, não um paper acadêmico.

## Decisions

- **Armazenamento do conjunto de queries**: um arquivo JSON/YAML versionado no repo (texto
  da query + ids de produto esperados), não uma tabela de banco — é dado de fixture de
  avaliação, versionado junto com o código que o produziu.
- **Runner de métricas**: um runner pequeno tipo CLI/teste (ex.: um `@Test` ou um `main()`
  num pacote `tools`/`eval`) que recebe uma função `SearchStrategy` e o conjunto de queries,
  e imprime/escreve a tabela de Recall@10 e MRR@10 — reutilizado sem mudança pela fase-3.
- **Baseline fonética**: se o projeto não tiver nenhuma busca fonética pré-existente,
  implementar a mais simples viável (ex.: `soundex`/`metaphone` do `fuzzystrmatch` do
  Postgres, ou Beider-Morse se metaphone for fraco demais pra português) — só o suficiente
  pra ser uma baseline real, não uma segunda feature de busca polida.

## Risks / Trade-offs

- [Risk] Rotular 50-100 queries à mão é subjetivo / ruidoso → Mitigação: rotular contra o
  dataset semente que o próprio autor controla (fase-0), então "produto relevante esperado" é
  inequívoco por construção.
- [Risk] Nenhuma baseline fonética pré-existente no código, aumentando o escopo → Mitigação:
  manter no mínimo descrito em Decisions acima; só precisa ser boa o suficiente pra perder
  pra vetorial/híbrida de forma legível.
