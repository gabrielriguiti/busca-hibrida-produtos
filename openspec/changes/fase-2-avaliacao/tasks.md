## 1. Conjunto de queries

- [ ] 1.1 Escrever 50-100 queries com id(s) de produto relevante esperado(s) contra o dataset semente da fase-0, verificar contagem e que toda query tem pelo menos um id esperado
- [ ] 1.2 Guardar o conjunto de queries como um fixture JSON/YAML versionado no repo, verificar que carrega sem erro

## 2. Métricas

- [ ] 2.1 Implementar o calculador de Recall@10, verificar com um exemplo pequeno calculado à mão (teste unitário)
- [ ] 2.2 Implementar o calculador de MRR@10, verificar com um exemplo pequeno calculado à mão (teste unitário)

## 3. Baseline e comparação

- [ ] 3.1 Checar o código por uma busca fonética existente; se não houver, implementar uma mínima (ex.: fuzzystrmatch do Postgres), verificar que devolve resultados pra uma query de exemplo
- [ ] 3.2 Construir o runner de avaliação que recebe uma estratégia de busca plugável e o conjunto de queries e imprime uma tabela de métricas, verificar que roda ponta a ponta
- [ ] 3.3 Rodar o runner contra a baseline fonética e a busca vetorial da fase-1, registrar as duas linhas num arquivo de resultados consumido pelo README (fase-5)
