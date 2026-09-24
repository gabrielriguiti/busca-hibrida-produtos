package com.gabrielriguiti.buscahibrida.eval;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EvalQuerySetTest {

    @Test
    void loadsBetween50And100QueriesEachWithAtLeastOneExpectedId() {
        List<EvalQuery> queries = EvalQuerySet.load();

        assertThat(queries.size()).isBetween(50, 100);
        assertThat(queries).allSatisfy(
                q -> assertThat(q.expectedProductIds()).isNotEmpty());
    }
}
