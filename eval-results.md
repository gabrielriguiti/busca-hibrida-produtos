# Resultados de avaliacao (fase-2/fase-3)

Recall@10 e MRR@10 sobre 70 queries rotuladas (ver `src/main/resources/eval/queries.json`) contra o catalogo semente da fase-0.

| Estrategia | Recall@10 | MRR@10 |
|---|---|---|
| fonetica (soundex) | 1.0000 | 0.8579 |
| vetorial (e5-small) | 1.0000 | 1.0000 |
| hibrida (pg_trgm + RRF) | 1.0000 | 0.9643 |
