import type { SearchMode } from './searchModes'

/** Formato varia por modo (fonetico/vetorial nao tem score, hibrido tem score/ranks) -
 * campos ficam opcionais e a UI so renderiza o que vier preenchido (ver design.md - Risks). */
export type ProductResult = {
  id: number
  name: string
  description: string | null
  score?: number
  lexicalRank?: number | null
  vectorRank?: number | null
}

export async function search(query: string, mode: SearchMode): Promise<ProductResult[]> {
  const url = `/api/busca?q=${encodeURIComponent(query)}&modo=${mode}`
  const response = await fetch(url)
  if (!response.ok) {
    throw new Error(`Busca falhou: ${response.status}`)
  }
  return response.json()
}
