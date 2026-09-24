## Context

A última fase — todas as capabilities da fase-0 até a fase-4 já existem. Ver proposal.md -
Por que. Essa fase é empacotamento e documentação, não lógica nova de busca.

## Goals / Non-Goals

**Goals:**
- Startup com zero passo manual pra um avaliador.
- Um README que já mostra a evidência logo no início (a tabela de métricas) em vez de
  enterrá-la.

**Non-Goals:**
- Sem pipeline de CI/CD, sem deploy pra um provedor de nuvem — Docker Compose local é todo o
  alvo de entrega pra uma peça de portfólio.
- Sem autenticação ou hardening de produção.

## Decisions

- **Biblioteca OpenAPI**: `springdoc-openapi` (orientada a anotação, gera tanto a UI quanto
  a spec JSON a partir dos controllers já existentes) — evita escrever um YAML OpenAPI à
  mão.
- **Frontend no Compose**: um Dockerfile multi-stage pro frontend (build com `npm run
  build`, servir a saída estática, ex. via `nginx` leve ou stage `vite preview`) adicionado
  como um novo serviço no `docker-compose.yml` já existente da fase-0.
- **GIF de demo**: gravado manualmente contra o frontend finalizado (fase-4) só depois que
  tudo mais estiver pronto — não é automatizável, não vale a pena scriptar pra um asset de
  uso único.

## Risks / Trade-offs

- [Risk] O passo de build do frontend deixa o `docker compose up` mais lento na primeira
  execução → Mitigação: custo único aceitável em dev local; anotar no README em vez de
  adicionar infraestrutura de cache de build pra um projeto de portfólio de um único
  desenvolvedor.
