package com.gabrielriguiti.buscahibrida.eval;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MetricsTest {

    // Query 1: relevante (5) aparece na posicao 3 -> contribui 1 pro recall, 1/3 pro MRR.
    // Query 2: relevante (9) nao aparece -> contribui 0 pros dois.
    // Recall@10 = (1 + 0) / 2 = 0.5. MRR@10 = (1/3 + 0) / 2 = 1/6.
    private final List<List<Long>> ranked = List.of(
            List.of(1L, 2L, 5L, 3L),
            List.of(1L, 2L, 3L));
    private final List<List<Long>> expected = List.of(
            List.of(5L),
            List.of(9L));

    @Test
    void recallAt10IsFractionOfQueriesWithAtLeastOneHit() {
        assertThat(Metrics.recallAt10(ranked, expected)).isEqualTo(0.5);
    }

    @Test
    void mrrAt10IsAverageOfReciprocalRankOfFirstHit() {
        assertThat(Metrics.mrrAt10(ranked, expected)).isEqualTo(1.0 / 6.0);
    }

    @Test
    void ignoresHitsPastTheTenthResult() {
        List<List<Long>> rankedPast10 = List.of(
                List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 99L));
        List<List<Long>> expectedAt11 = List.of(List.of(99L));

        assertThat(Metrics.recallAt10(rankedPast10, expectedAt11)).isEqualTo(0.0);
        assertThat(Metrics.mrrAt10(rankedPast10, expectedAt11)).isEqualTo(0.0);
    }
}
