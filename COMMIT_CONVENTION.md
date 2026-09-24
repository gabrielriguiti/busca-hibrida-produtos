# Padrão de commit

[Conventional Commits](https://www.conventionalcommits.org/), com escopo por fase, para que o
próprio log conte a história do projeto (útil num portfólio: quem revisa dá um
`git log --oneline` e já vê as fases sendo construídas).

As mensagens em si ficam em inglês — é o vocabulário padrão do Conventional Commits e o que
qualquer ferramenta de changelog/semantic-release espera —, só a documentação abaixo está em
português.

```
<type>(<scope>): <description>

[corpo opcional]
```

## Types

| Type       | Quando usar                                                        |
|------------|---------------------------------------------------------------------|
| `feat`     | nova capability (um endpoint, uma feature de UI, um índice, uma métrica) |
| `fix`      | correção de bug                                                     |
| `test`     | só testes, sem mudança de comportamento                             |
| `docs`     | README, comentários, descrições do OpenAPI                          |
| `chore`    | tooling, dependências, Docker/Compose, CI                           |
| `refactor` | mudança de código sem mudança de comportamento                      |
| `perf`     | melhoria de performance, sem mudança de comportamento                |

## Scopes

Um por capability/fase, batendo com os nomes das changes do OpenSpec em `openspec/changes/`:

- `embed` — serviço de embedding e5-small (fase-0)
- `dataset` — dados semente / schema (fase-0)
- `normalize` — pipeline de normalização de texto (fase-1)
- `index` — indexação vetorial em batch (fase-1)
- `search` — endpoint(s) de busca, qualquer modo (fase-1/3)
- `eval` — harness de avaliação e métricas (fase-2)
- `hybrid` — fusão pg_trgm + RRF (fase-3)
- `frontend` — app React (fase-4)
- `docs`/`compose` — docs da API, Docker Compose, README (fase-5)

## Exemplos

```
feat(embed): add POST /embed returning 384-dim e5-small vectors
feat(dataset): seed synthetic catalog with inconsistent units and typos
feat(normalize): expand known abbreviations in texto_busca pipeline
feat(index): batch-embed catalog in groups of 64 and index with HNSW
feat(search): add GET /api/busca vector search endpoint
test(eval): add Recall@10 and MRR@10 calculators
feat(eval): run phonetic vs vector comparison, record baseline numbers
feat(hybrid): fuse pg_trgm and vector rankings with RRF
feat(frontend): add search mode toggle with per-result scores
docs: add architecture diagram and metrics table to README
chore(compose): add frontend service to docker-compose.yml
fix(search): return 400 on empty query instead of embedding empty string
```

## Regras

- Modo imperativo, minúsculo depois dos dois-pontos, sem ponto final.
- Uma mudança lógica por commit — geralmente uma task do `tasks.md` do OpenSpec vira um
  commit, às vezes duas.
- Mudança realmente breaking se marca com `!` depois do scope (`feat(search)!: ...`) mais um
  rodapé `BREAKING CHANGE:`. Difícil de acontecer num portfólio solo, mas a regra existe pra
  quando uma fase mudar o contrato de um endpoint que já foi ao ar.
- Quando um commit fecha uma task do OpenSpec, pode referenciar no corpo, ex.:
  `Closes fase-1 task 3.1`.
