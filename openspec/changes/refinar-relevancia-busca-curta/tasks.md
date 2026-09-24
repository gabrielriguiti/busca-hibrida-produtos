## 1. Fonético

- [x] 1.1 Descartar tokens cuja parte alfabética (após remover dígitos) tenha menos de 3 letras do cálculo de overlap fonético, verificar com um teste unitário que "m8"/"m6" não colidem mais com "meio" e que os testes fonéticos existentes continuam passando
- [x] 1.2 Verificar manualmente contra o catálogo real que `modo=fonetico` pra "meio litro" não traz mais nenhum parafuso

## 2. Léxico / Híbrido

- [ ] 2.1 Trocar a query de recuperação de candidatos lexicais de `similarity()`/`%` pra `word_similarity()`/`<%` com limiar 0.5, verificar com uma query manual que "meio litro" bate nos 3 óleos e não bate nos parafusos
- [ ] 2.2 Verificar manualmente contra o catálogo real que `modo=hibrido` pra "meio litro" devolve os 3 óleos com `lexicalRank` preenchido (não só `vectorRank`)

## 3. Reavaliação e documentação

- [ ] 3.1 Rodar o harness de avaliação completo (`EvalReportTest`) e confirmar que Recall@10/MRR@10 das três estratégias não regrediu em relação ao último resultado documentado
- [ ] 3.2 Atualizar `eval-results.md` e a tabela de métricas do README com os números da nova rodada, se tiverem mudado
