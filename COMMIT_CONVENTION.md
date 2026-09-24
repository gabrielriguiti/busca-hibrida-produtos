# Padrão de commit

[Conventional Commits](https://www.conventionalcommits.org/), com escopo por fase, para que o
próprio log conte a história do projeto (útil num portfólio: quem revisa dá um
`git log --oneline` e já vê as fases sendo construídas).

As mensagens ficam em português (decisão do autor do projeto) — só a estrutura
`<type>(<scope>): <description>` em si segue o vocabulário padrão do Conventional Commits,
exigido por qualquer ferramenta de changelog/semantic-release.

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
feat(embed): adiciona POST /embed devolvendo vetores e5-small de 384 dimensoes
feat(dataset): semeia catalogo sintetico com unidades inconsistentes e erros de digitacao
feat(normalize): expande abreviacoes conhecidas no pipeline de texto_busca
feat(index): embeda o catalogo em lotes de 64 e indexa com HNSW
feat(search): adiciona endpoint GET /api/busca de busca vetorial
test(eval): adiciona calculo de Recall@10 e MRR@10
feat(eval): roda comparacao fonetica vs vetorial, registra numeros da baseline
feat(hybrid): funde rankings pg_trgm e vetorial com RRF
feat(frontend): adiciona toggle de modo de busca com scores por resultado
docs: adiciona diagrama de arquitetura e tabela de metricas ao README
chore(compose): adiciona servico de frontend ao docker-compose.yml
fix(search): retorna 400 em query vazia em vez de embedar string vazia
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
- Commits anteriores à fase-3 ficaram em inglês (convenção original) e não foram reescritos —
  o histórico não é rebaseado retroativamente só pra mudar idioma.
