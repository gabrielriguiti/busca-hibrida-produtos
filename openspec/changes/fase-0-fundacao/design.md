## Context

Repo greenfield. Ver proposal.md - Por que. O único risco técnico real nessa fase é saber se
um modelo de embedding no estilo HuggingFace (e5-small) roda de forma aceitável a partir do
Java sem chamar Python externamente.

## Goals / Non-Goals

**Goals:**
- Provar que o e5-small roda em processo a partir do Java e produz vetores de 384 dimensões.
- Ter uma stack Docker Compose (app + Postgres/pgvector) que sobe com um único comando.
- Ter um dataset semente sujo o suficiente pra tornar os resultados das próximas fases
  significativos.

**Non-Goals:**
- Nenhuma lógica de busca ainda (isso é fase-1). `/embed` só embeda; não consulta nada.
- Sem batching, cache ou tuning de performance da inferência ainda.
- Sem frontend.

## Decisions

- **DJL vs. ONNX Runtime for Java**: tentar DJL primeiro (`ai.djl.huggingface:tokenizers` +
  `ai.djl.onnxruntime:onnxruntime-engine`), já que ele traz compatibilidade com o tokenizer
  estilo HuggingFace (WordPiece/SentencePiece) que o ONNX Runtime puro não traz — o tokenizer
  do e5-small é a parte delicada, não o grafo ONNX em si. Cair pro ONNX Runtime for Java puro
  só se o suporte a tokenizer do DJL não bater com o vocabulário exato do e5-small. Registrar
  aqui a escolha final depois de implementado.
- **Fonte do modelo**: baixar os pesos do e5-small exportados em ONNX do HuggingFace Hub
  (export ONNX do `intfloat/e5-small`) no build/startup, em vez de commitar pesos binários no
  repo.
- **Formato do dataset**: um script SQL de seed versionado (ou CSV carregado por uma
  migration Flyway/Liquibase), em vez de um serviço de seeding separado, pra que
  `docker compose up` seja suficiente.

## Risks / Trade-offs

- [Risk] Export ONNX do e5-small indisponível ou binding do DJL problemático na plataforma
  alvo (ex.: Docker arm64) → Mitigação: fixar uma combinação de versão DJL/ONNX Runtime já
  testada e documentar a plataforma usada no README.
- [Risk] Download do modelo no startup do container deixa o primeiro boot lento / exige
  acesso à rede → Mitigação: cachear o modelo num volume Docker após o primeiro pull;
  documentar fallback offline (montar pesos já baixados).
- [Risk] Dataset sintético "limpo demais" pra ser um teste justo → Mitigação: montar
  deliberadamente um checklist de padrões de sujeira (abreviações, erros de digitação,
  variações de unidade) e verificar que cada um está presente antes de considerar o dataset
  pronto.

## Open Questions

- Fonte exata do ONNX do e5-small (export oficial do HuggingFace vs. um re-export da
  comunidade) — resolver durante a implementação checando qual carrega sem problema na
  biblioteca escolhida; não muda a spec nem a divisão de tasks de qualquer forma.
