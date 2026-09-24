## Context

Segue direto do fix de limiar de relevância do vetorial já implementado (ver commits
`aac7537`/`b8c832f`). Depois desse fix, testando "meio litro" no frontend de verdade, dois
modos ainda vazavam parafuso: fonético (colisão de Soundex) e léxico/híbrido (diluição de
`pg_trgm` em query curta contra `texto_busca` longo). Ver proposal.md - Why.

## Goals / Non-Goals

**Goals:**
- Fonético e léxico param de trazer resultado de categoria errada pra queries curtas e
  específicas, sem regredir nenhum dos 70 casos rotulados do harness de avaliação.
- Toda calibração de limiar é validada contra dado real (consulta direta no Postgres já
  reindexado), não chutada - mesma metodologia usada no fix anterior do limiar vetorial.

**Non-Goals:**
- Não trocar o algoritmo fonético (Soundex continua sendo a baseline fraca de propósito,
  ver `Soundex.java`) - só evitar que ele produza colisão espúria em tokens sem conteúdo
  fonético real.
- Não resolver a variação de unidade "500 ml"/"500 mililitros" (grafias com espaço) que
  ainda não normalizam pra "meio litro" - o dicionário de abreviações só cobre os tokens
  colados ("500ml", "0,5l"), mapear tokens separados por espaço é mais arriscado (ex.: "500"
  isolado colidiria com outros usos futuros) e fica fora do escopo desta change.

## Decisions

- **Soundex: exigir 3+ letras (depois de descartar dígitos) pra um token participar do
  ranking fonético.** Alternativa considerada: manter o filtro `score > 0` já existente sem
  mudança — insuficiente, porque a colisão "m8"→"m" vs "meio"→"M000" já produz `score > 0`
  de forma genuína (mesmo código Soundex), não é um score zero escondido. Validado
  manualmente: todo token de conteúdo real no catálogo (parafuso, sextavado, furadeira,
  martelo, borracha, oleo, etc.) tem 3+ letras depois de descartar dígitos; os únicos tokens
  abaixo do corte são códigos de produto ("m8"→"m", "500w"→"w", "6mm"→"mm") e stopwords
  curtas ("de"→"de", "wd-40"→"wd") - nenhum deles carrega sinal fonético que valha a pena
  preservar.
- **Léxico: trocar `similarity()`/`%` (string inteira) por `word_similarity()`/`<%`
  (melhor sub-sequência de palavras), com limiar 0.5 em vez do default 0.6 da extensão.**
  Validado contra os 70 pares (query, produto esperado) do harness: no default 0.6, só
  64/70 queries têm o produto correto acima do limiar; em 0.5, sobem pra 69/70, sem
  reintroduzir os falsos positivos anteriores (produtos de outra categoria ficam ~0.18-0.36
  pra queries como "meio litro", bem abaixo de 0.5). A única query que fica sem candidato
  léxico em qualquer limiar razoável é "martelo pra tirar prego" (0.364) - aceitável, porque
  o vetorial (já calibrado na change anterior a 0.18 de distância, cobrindo 220/220 pares)
  continua contribuindo pro híbrido nesse caso via RRF.

## Risks / Trade-offs

- [Risk] Regra de "3+ letras" no fonético descarta silenciosamente conteúdo fonético de
  tokens como "wd" (de "WD-40") → Mitigação: aceitável - Soundex já é uma baseline fraca de
  propósito, e "wd" não é uma palavra com fonética real pra começo de conversa.
- [Risk] Limiar de `word_similarity` calibrado (0.5) é específico deste catálogo pequeno →
  Mitigação: mesmo trade-off já aceito pro limiar vetorial na change anterior; documentar o
  valor e a metodologia de calibração no código, não só decidir e esquecer.
- [Risk] Trocar o operador de recuperação lexical muda quais candidatos entram no pool de
  50 usado pela fusão RRF do híbrido → Mitigação: re-rodar o harness de avaliação completo
  (não só os testes unitários) depois da troca, e comparar Recall@10/MRR@10 antes/depois
  antes de considerar a task concluída.
