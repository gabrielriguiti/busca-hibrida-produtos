# search-frontend Specification

## Purpose

Dá a um humano um jeito de sentir a diferença entre busca fonética, vetorial e híbrida
diretamente, em vez de precisar ler saída de curl ou código pra entender o ponto do projeto.

## Requirements

### Requirement: Campo de busca com debounce
O sistema SHALL aplicar debounce no campo de busca, de forma que uma requisição ao backend
só seja enviada depois que o usuário parar de digitar por um pequeno intervalo, não a cada
tecla pressionada.

#### Scenario: Digitação rápida envia uma única requisição
- **WHEN** um usuário digita uma query com vários caracteres rapidamente
- **THEN** apenas uma requisição de busca é enviada, depois que a digitação para

### Requirement: Lista de resultados
O sistema SHALL exibir os resultados de busca devolvidos pelo backend como uma lista, cada
um mostrando no mínimo o nome do produto.

#### Scenario: Resultados renderizam depois de uma busca
- **WHEN** uma requisição de busca devolve resultados
- **THEN** a UI exibe cada resultado numa lista visível

### Requirement: Toggle de modo de busca
O sistema SHALL fornecer um toggle pra selecionar o modo de busca (fonético / vetorial /
híbrido), e reexecutar a query atual contra o endpoint do backend correspondente ao modo
selecionado quando ele mudar.

#### Scenario: Trocar de modo reconsulta
- **WHEN** um usuário tem uma query ativa e troca o toggle de modo
- **THEN** a lista de resultados atualiza pra refletir os resultados do modo recém-
  selecionado

### Requirement: Exibição de score por resultado
O sistema SHALL exibir o(s) score(s) relevante(s) de cada resultado pro modo ativo (ex.:
distância vetorial, similaridade lexical, ou score RRF fundido) junto com o resultado.

#### Scenario: Modo híbrido mostra os dois scores componentes
- **WHEN** o toggle de modo está em híbrido
- **THEN** cada resultado mostra tanto sua contribuição lexical quanto a vetorial, não só o
  score fundido
