package com.gabrielriguiti.buscahibrida.search;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RrfFusionTest {

    @Test
    void overlappingResultOutranksResultsFromASingleRanking() {
        List<Long> lexical = List.of(1L, 2L, 3L);
        List<Long> vector = List.of(2L, 4L, 1L);

        List<RrfFusion.FusedResult> fused = RrfFusion.fuse(lexical, vector, 60);

        // id 2 (rank 2 lexical, rank 1 vetorial): 1/62 + 1/61 = 0.032522...
        // id 1 (rank 1 lexical, rank 3 vetorial): 1/61 + 1/63 = 0.032266...
        // id 4 (so vetorial, rank 2):             1/62        = 0.016129...
        // id 3 (so lexical, rank 3):               1/63        = 0.015873...
        assertThat(fused).extracting(RrfFusion.FusedResult::id).containsExactly(2L, 1L, 4L, 3L);
        assertThat(fused.get(0).score()).isCloseTo(1.0 / 62 + 1.0 / 61, org.assertj.core.data.Offset.offset(1e-9));
        assertThat(fused.get(1).score()).isCloseTo(1.0 / 61 + 1.0 / 63, org.assertj.core.data.Offset.offset(1e-9));
        assertThat(fused.get(2).score()).isCloseTo(1.0 / 62, org.assertj.core.data.Offset.offset(1e-9));
        assertThat(fused.get(3).score()).isCloseTo(1.0 / 63, org.assertj.core.data.Offset.offset(1e-9));
    }

    @Test
    void ranksReflectWhichSourceRankingEachResultCameFrom() {
        List<RrfFusion.FusedResult> fused = RrfFusion.fuse(List.of(5L), List.of(6L), 60);

        RrfFusion.FusedResult lexicalOnly = find(fused, 5L);
        assertThat(lexicalOnly.lexicalRank()).isEqualTo(1);
        assertThat(lexicalOnly.vectorRank()).isNull();

        RrfFusion.FusedResult vectorOnly = find(fused, 6L);
        assertThat(vectorOnly.lexicalRank()).isNull();
        assertThat(vectorOnly.vectorRank()).isEqualTo(1);
    }

    private static RrfFusion.FusedResult find(List<RrfFusion.FusedResult> fused, Long id) {
        return fused.stream().filter(f -> f.id().equals(id)).findFirst().orElseThrow();
    }
}
