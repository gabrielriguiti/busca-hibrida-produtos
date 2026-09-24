/** Cada chave é o valor de `modo` aceito por GET /api/busca; o valor é o rótulo do toggle.
 * Adicionar um quarto modo de busca é uma mudança de uma linha aqui. */
export const SEARCH_MODES = {
  fonetico: 'Fonético',
  vetorial: 'Vetorial',
  hibrido: 'Híbrido',
} as const

export type SearchMode = keyof typeof SEARCH_MODES

export const MODE_DESCRIPTIONS: Record<SearchMode, string> = {
  fonetico: 'Compara como as palavras soam (Soundex). Pega erro de digitação que não muda o som, mas não entende sinônimo.',
  vetorial: 'Compara o significado via embeddings (e5-small). Pega sinônimo e reformulação, mas erro de digitação pode confundir o modelo.',
  hibrido: 'Funde o ranking lexical (pg_trgm) com o vetorial via RRF. Cobre erro de digitação e sinônimo na mesma busca.',
}

/** Queries de exemplo que demonstram bem a diferença entre os modos (erro de digitação,
 * abreviação, sinônimo/reformulação) — todas batem com produtos do catálogo semente. */
export const EXAMPLE_QUERIES = [
  'parafuso sextavdo m8',
  'oleo multiuso 500 mililitros',
  'furadeira eletrika 500w',
  'chave d fenda 6mm',
]
