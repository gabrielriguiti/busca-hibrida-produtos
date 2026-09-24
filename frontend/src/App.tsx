import { useEffect, useState } from 'react'
import './App.css'
import { search, type ProductResult } from './api'
import { EXAMPLE_QUERIES, MODE_DESCRIPTIONS, SEARCH_MODES, type SearchMode } from './searchModes'
import { useDebounce } from './useDebounce'

function App() {
  const [query, setQuery] = useState('')
  const [mode, setMode] = useState<SearchMode>('vetorial')
  const [results, setResults] = useState<ProductResult[]>([])
  const [isLoading, setIsLoading] = useState(false)
  const debouncedQuery = useDebounce(query, 300)

  useEffect(() => {
    if (!debouncedQuery.trim()) {
      setResults([])
      setIsLoading(false)
      return
    }
    let cancelled = false
    setIsLoading(true)
    search(debouncedQuery, mode).then((data) => {
      if (cancelled) return
      setResults(data)
      setIsLoading(false)
    })
    return () => {
      cancelled = true
    }
  }, [debouncedQuery, mode])

  const hasQuery = debouncedQuery.trim().length > 0

  return (
    <div className="layout">
      <main className="page">
        <header className="header">
          <h1>Busca Híbrida</h1>
          <p>Compare busca fonética, vetorial e híbrida (RRF) no mesmo catálogo.</p>
        </header>

        <div className="search-box">
          <span className="search-icon" aria-hidden="true">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <circle cx="11" cy="11" r="7" />
              <line x1="21" y1="21" x2="16.65" y2="16.65" />
            </svg>
          </span>
          <input
            type="search"
            className="search-input"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="Buscar produtos..."
            aria-label="Buscar produtos"
          />
        </div>

        <div className="mode-toggle" role="radiogroup" aria-label="Modo de busca">
          {(Object.keys(SEARCH_MODES) as SearchMode[]).map((m) => (
            <label className="mode-option" key={m}>
              <input type="radio" name="modo" value={m} checked={mode === m} onChange={() => setMode(m)} />
              <span>{SEARCH_MODES[m]}</span>
            </label>
          ))}
        </div>

        <div className="status-line">{isLoading ? 'Buscando...' : null}</div>

        {hasQuery && !isLoading && results.length === 0 ? (
          <p className="empty-state">Nenhum resultado pra "{debouncedQuery}".</p>
        ) : (
          <ul className="results">
            {results.map((r) => (
              <li className="result-card" key={r.id}>
                <div className="result-name">{r.name}</div>
                {r.description && <div className="result-description">{r.description}</div>}
                <ScoreChips result={r} />
              </li>
            ))}
          </ul>
        )}
      </main>

      <aside className="sidebar">
        <section className="sidebar-section">
          <h2>Exemplos de busca</h2>
          <ul className="example-list">
            {EXAMPLE_QUERIES.map((example) => (
              <li key={example}>
                <button type="button" className="example-chip" onClick={() => setQuery(example)}>
                  {example}
                </button>
              </li>
            ))}
          </ul>
        </section>

        <section className="sidebar-section">
          <h2>Como funciona cada modo</h2>
          <dl className="mode-explainer">
            {(Object.keys(SEARCH_MODES) as SearchMode[]).map((m) => (
              <div className={`mode-explainer-item${m === mode ? ' mode-explainer-item--active' : ''}`} key={m}>
                <dt>{SEARCH_MODES[m]}</dt>
                <dd>{MODE_DESCRIPTIONS[m]}</dd>
              </div>
            ))}
          </dl>
        </section>
      </aside>
    </div>
  )
}

function ScoreChips({ result }: { result: ProductResult }) {
  const hasAnyScore = result.score !== undefined || result.lexicalRank != null || result.vectorRank != null
  if (!hasAnyScore) return null

  return (
    <div className="result-scores">
      {result.score !== undefined && <span className="chip chip--rrf">RRF {result.score.toFixed(4)}</span>}
      {result.lexicalRank != null && <span className="chip chip--lexical">lexical #{result.lexicalRank}</span>}
      {result.vectorRank != null && <span className="chip chip--vector">vetorial #{result.vectorRank}</span>}
    </div>
  )
}

export default App
