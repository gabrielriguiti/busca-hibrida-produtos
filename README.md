# Busca Híbrida

Motor de busca híbrida de produtos: busca fonética -> busca vetorial -> busca híbrida
(RRF), cada uma avaliada com números reais de Recall@10/MRR@10 sobre um catálogo
sintético propositalmente "sujo" (o tipo de dado que um catálogo real de e-commerce tem).

> Este README é um rascunho, expandido a cada fase. A versão final (arquitetura,
> tabela de métricas, GIF de demo) fica pronta na fase 5.

## Padrões de sujeira do dataset semente

O catálogo semente (`src/main/resources/db/migration/V2__seed_catalog.sql`) inclui
deliberadamente:

- [x] **Abreviações inconsistentes** — o mesmo termo escrito por extenso e abreviado
  (`sextavado` / `sext.` / `SEXTAVADO`, `elétrica` / `elétr.`).
- [x] **Erros de digitação** — letra faltando, duplicada ou trocada (`sextavdo`,
  `borrachaa`, `eletrika`).
- [x] **5+ variações da mesma unidade** — o mesmo volume (500 ml) escrito de pelo menos
  5 formas diferentes: `500ml`, `500 ml`, `0,5L`, `500 mililitros`, `meio litro`.

## Rodando localmente

```bash
docker compose up
```

Sobe a API (porta 8080) e o Postgres com `pgvector` habilitado; a migration Flyway cria
o schema e carrega o catálogo semente automaticamente.

```bash
curl -X POST localhost:8080/embed -H 'Content-Type: application/json' \
  -d '{"text": "parafuso sextavado M8"}'
```
