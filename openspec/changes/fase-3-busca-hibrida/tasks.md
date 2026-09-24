## 1. Candidatos lexicais

- [x] 1.1 Habilitar a extensão `pg_trgm` e adicionar um índice GIN de trigrama em `texto_busca`, verificar com `EXPLAIN` mostrando uso do índice numa query de similaridade
- [x] 1.2 Implementar a recuperação de candidatos lexicais (top-N por similaridade de trigrama), verificar com uma query manual incluindo um erro de digitação

## 2. Fusão RRF

- [x] 2.1 Implementar a fusão RRF combinando os rankings top-N lexical e vetorial, verificar com um teste unitário usando um exemplo pequeno montado à mão com ordem fundida esperada conhecida
- [ ] 2.2 Adicionar o modo híbrido ao endpoint de busca devolvendo resultados fundidos com scores lexical/vetorial por resultado, verificar com uma chamada curl manual

## 3. Reavaliação

- [ ] 3.1 Plugar a busca híbrida no harness de avaliação da fase-2 como uma terceira estratégia, verificar que o harness roda sem modificação no harness em si
- [ ] 3.2 Rodar a comparação entre as três (fonético/vetorial/híbrido) e registrar os resultados pra tabela do README (fase-5)
