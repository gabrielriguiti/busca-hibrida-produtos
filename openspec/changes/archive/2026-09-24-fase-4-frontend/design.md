## Context

Consome o(s) endpoint(s) de busca construído(s) na fase-1/fase-2/fase-3. Ver proposal.md -
Por que. Ainda não existe nenhum frontend.

## Goals / Non-Goals

**Goals:**
- Tornar o trade-off fonético-vs-vetorial-vs-híbrido visível e interativo em menos de 30
  segundos de uso.
- Manter a stack mínima — isso é uma UI de demo, não um produto.

**Non-Goals:**
- Sem auth, sem rotas além de uma única página de busca, sem biblioteca de state management
  — o state local de uma única página já basta.
- Nenhuma decisão de framework de estilo além de "legível e limpo"; não é um exercício de
  design system.

## Decisions

- **Stack**: Vite + React + TypeScript, conforme o plano — menor e mais rápido que o
  Next.js já que não há necessidade de SSR ou múltiplas rotas.
- **State**: `useState`/`useEffect` puro com um hook de debounce (um `useDebounce` feito à
  mão, ou uma dependência já idiomática e pequena se existir) — sem Redux/Zustand pra um
  único campo de busca.
- **Chamadas de API**: `fetch` nativo, sem client library — três endpoints, sem auth, sem
  requisito de cache além do que o browser já faz.
- **Mapeamento modo -> endpoint**: um único objeto de config pequeno mapeando cada valor do
  toggle pro seu param/path de backend, pra que adicionar um quarto modo depois seja uma
  mudança de uma linha, não um refactor.

## Risks / Trade-offs

- [Risk] O formato do score do backend difere entre modos (distância vetorial vs.
  similaridade de trigrama vs. score RRF) → Mitigação: renderizar quaisquer campos de score
  que a resposta de cada modo realmente incluir, em vez de forçar um conceito único de
  "score" normalizado entre modos.
