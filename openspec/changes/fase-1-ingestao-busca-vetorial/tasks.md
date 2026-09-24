## 1. Normalização

- [x] 1.1 Implementar o normalizador `texto_busca` (lowercase, remoção de acento via `java.text.Normalizer`) e verificar com um teste unitário em entrada acentuada/caixa mista
- [x] 1.2 Adicionar dicionário de abreviações + passo de expansão, verificar com um teste unitário cobrindo cada padrão semeado no dataset da fase-0

## 2. Indexação em batch

- [ ] 2.1 Implementar o job de indexação em batch (normalizar -> embedar em lotes de 32-64 -> upsert na coluna vector), verificar que o número de chamadas de embedding é ~tamanho_catalogo/tamanho_lote via log ou teste
- [ ] 2.2 Adicionar migration do índice HNSW na coluna vector de produtos, verificar com `\d products` ou um `EXPLAIN` mostrando uso do índice
- [ ] 2.3 Tornar a reindexação idempotente (upsert por id de produto), verificar que a contagem de linhas fica estável rodando o job duas vezes

## 3. API de busca vetorial

- [ ] 3.1 Implementar `GET /api/busca?q=` (normalizar -> prefixo `query: ` -> embedar -> consulta de vizinho mais próximo), verificar com um curl manual contra o dataset semeado
- [ ] 3.2 Adicionar resposta 400 pra `q` ausente/vazio, verificar com um teste
