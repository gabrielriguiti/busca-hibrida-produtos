/** Cada chave é o valor de `modo` aceito por GET /api/busca; o valor é o rótulo do toggle.
 * Adicionar um quarto modo de busca é uma mudança de uma linha aqui. */
export const SEARCH_MODES = {
  fonetico: 'Fonético',
  vetorial: 'Vetorial',
  hibrido: 'Híbrido',
} as const

export type SearchMode = keyof typeof SEARCH_MODES
