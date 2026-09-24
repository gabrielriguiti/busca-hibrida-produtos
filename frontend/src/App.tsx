import { useEffect, useState } from 'react'
import { search, type ProductResult } from './api'
import { SEARCH_MODES, type SearchMode } from './searchModes'
import { useDebounce } from './useDebounce'

function App() {
  const [query, setQuery] = useState('')
  const [mode, setMode] = useState<SearchMode>('vetorial')
  const [results, setResults] = useState<ProductResult[]>([])
  const debouncedQuery = useDebounce(query, 300)

  useEffect(() => {
    if (!debouncedQuery.trim()) {
      setResults([])
      return
    }
    let cancelled = false
    search(debouncedQuery, mode).then((data) => {
      if (!cancelled) setResults(data)
    })
    return () => {
      cancelled = true
    }
  }, [debouncedQuery, mode])

  return (
    <main style={{ maxWidth: 640, margin: '2rem auto', padding: '0 1rem' }}>
      <h1>Busca Híbrida</h1>
      <input
        type="search"
        value={query}
        onChange={(e) => setQuery(e.target.value)}
        placeholder="Buscar produtos..."
        style={{ width: '100%', padding: '0.5rem', fontSize: '1rem' }}
      />
      <div role="radiogroup" aria-label="Modo de busca" style={{ margin: '0.75rem 0', display: 'flex', gap: '1rem' }}>
        {(Object.keys(SEARCH_MODES) as SearchMode[]).map((m) => (
          <label key={m}>
            <input type="radio" name="modo" value={m} checked={mode === m} onChange={() => setMode(m)} />
            {' '}
            {SEARCH_MODES[m]}
          </label>
        ))}
      </div>
      <ul style={{ listStyle: 'none', padding: 0 }}>
        {results.map((r) => (
          <li key={r.id} style={{ padding: '0.5rem 0', borderBottom: '1px solid #4448' }}>
            <strong>{r.name}</strong>
            {r.description && <div>{r.description}</div>}
            <ScoreLine result={r} />
          </li>
        ))}
      </ul>
    </main>
  )
}

function ScoreLine({ result }: { result: ProductResult }) {
  const parts: string[] = []
  if (result.score !== undefined) parts.push(`RRF: ${result.score.toFixed(4)}`)
  if (result.lexicalRank != null) parts.push(`lexical #${result.lexicalRank}`)
  if (result.vectorRank != null) parts.push(`vetorial #${result.vectorRank}`)
  if (parts.length === 0) return null
  return <small>{parts.join(' · ')}</small>
}

export default App
