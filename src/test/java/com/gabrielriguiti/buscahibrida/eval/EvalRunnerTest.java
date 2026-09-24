package com.gabrielriguiti.buscahibrida.eval;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EvalRunnerTest {

    private final List<EvalQuery> queries = List.of(
            new EvalQuery("a", List.of(1L)),
            new EvalQuery("b", List.of(2L)));

    @Test
    void evaluatesAPluggableStrategyEndToEnd() {
        SearchStrategy alwaysCorrect = q -> "a".equals(q) ? List.of(1L, 9L) : List.of(2L, 9L);

        EvalRunner.StrategyResult result = EvalRunner.evaluate("perfeita", alwaysCorrect, queries);

        assertThat(result.recallAt10()).isEqualTo(1.0);
        assertThat(result.mrrAt10()).isEqualTo(1.0);
    }

    @Test
    void formatsAComparisonTableAcrossStrategies() {
        SearchStrategy neverCorrect = q -> List.of(9L, 8L);
        SearchStrategy alwaysCorrect = q -> "a".equals(q) ? List.of(1L) : List.of(2L);

        String table = EvalRunner.formatTable(List.of(
                EvalRunner.evaluate("fraca", neverCorrect, queries),
                EvalRunner.evaluate("forte", alwaysCorrect, queries)));

        assertThat(table).contains("fraca").contains("forte");
    }
}
