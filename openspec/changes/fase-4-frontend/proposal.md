## Por que

Um backend com três estratégias de busca só convence quem estiver disposto a ler código e
comandos curl. Uma UI onde o avaliador digita uma query cheia de erro de digitação e vê o
híbrido acertar onde o vetorial falha é o que transforma "funciona" numa demo memorável.
Entregável: uma UI de busca funcionando com um toggle fonético/vetorial/híbrido mostrando os
scores de cada resultado.

## O que Muda

- Estruturar um app Vite + React + TypeScript (sem Next.js — sem necessidade de SSR ou
  rotas complexas).
- Adicionar um campo de busca com debounce chamando o endpoint de busca do backend.
- Renderizar uma lista de resultados.
- Adicionar um toggle de modo (fonético / vetorial / híbrido) que troca qual estratégia do
  backend é consultada e exibe o(s) score(s) de cada resultado, pra que a diferença entre
  modos fique visível, não só implícita.

## Capabilities

### New Capabilities
- `search-frontend`: uma SPA Vite/React/TS com campo de busca com debounce, lista de
  resultados, e um toggle de modo de busca que expõe os scores por resultado.

### Modified Capabilities
(nenhuma)

## Impact

- Depende do(s) endpoint(s) de busca da fase-1 (vetorial) e fase-3 (híbrido), além de
  qualquer endpoint fonético exposto/reutilizado pela fase-2.
- Novo diretório `frontend/` no repo (separado do app Spring Boot), depois conectado à stack
  única do Docker Compose na fase-5.
