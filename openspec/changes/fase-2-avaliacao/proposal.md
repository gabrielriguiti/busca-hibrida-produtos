## Por que

Uma busca que "parece funcionar" não é evidência. Essa fase produz os números que fazem um
avaliador técnico confiar no projeto em vez de acreditar numa demo. Entregável: uma tabela
comparativa no README com números reais de Recall@10/MRR@10 pra busca fonética vs. vetorial.

## O que Muda

- Montar um conjunto de avaliação com ~50-100 queries e seu(s) produto(s) relevante(s)
  esperado(s), escrito à mão contra o dataset semente da fase-0.
- Implementar os calculadores de métrica Recall@10 e MRR@10.
- Implementar (ou reaproveitar, se já existir) uma busca fonética baseline pra comparação.
- Rodar tanto a baseline fonética quanto a busca vetorial da fase-1 pelo conjunto de
  avaliação e registrar os resultados.

## Capabilities

### New Capabilities
- `search-evaluation`: um conjunto de queries rotulado e fixo, mais um runner que calcula
  Recall@10 e MRR@10 pra qualquer estratégia de busca plugável, e um relatório comparando
  estratégias.

### Modified Capabilities
(nenhuma)

## Impact

- Depende de `vector-search-api` (fase-1) e, pra baseline, de uma estratégia de busca
  fonética (implementada aqui caso ainda não exista no código).
- Produz um relatório/tabela de métricas consumido pelo README (fase-5) e rodado de novo na
  fase-3 assim que a busca híbrida existir.
