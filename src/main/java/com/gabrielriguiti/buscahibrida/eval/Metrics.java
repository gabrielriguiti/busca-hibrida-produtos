package com.gabrielriguiti.buscahibrida.eval;

import java.util.List;

/** Recall@10 e MRR@10 de uma estrategia de busca sobre um conjunto de queries, ver
 * search-evaluation spec. */
public final class Metrics {

    private static final int K = 10;

    private Metrics() {
    }

    public static double recallAt10(List<List<Long>> rankedResults, List<List<Long>> expectedIds) {
        validateSameSize(rankedResults, expectedIds);
        long hits = 0;
        for (int i = 0; i < rankedResults.size(); i++) {
            if (rankOfFirstHit(rankedResults.get(i), expectedIds.get(i)) > 0) {
                hits++;
            }
        }
        return (double) hits / rankedResults.size();
    }

    public static double mrrAt10(List<List<Long>> rankedResults, List<List<Long>> expectedIds) {
        validateSameSize(rankedResults, expectedIds);
        double sum = 0;
        for (int i = 0; i < rankedResults.size(); i++) {
            int rank = rankOfFirstHit(rankedResults.get(i), expectedIds.get(i));
            sum += rank > 0 ? 1.0 / rank : 0.0;
        }
        return sum / rankedResults.size();
    }

    private static int rankOfFirstHit(List<Long> ranked, List<Long> expected) {
        int limit = Math.min(K, ranked.size());
        for (int i = 0; i < limit; i++) {
            if (expected.contains(ranked.get(i))) {
                return i + 1;
            }
        }
        return 0;
    }

    private static void validateSameSize(List<List<Long>> rankedResults, List<List<Long>> expectedIds) {
        if (rankedResults.size() != expectedIds.size()) {
            throw new IllegalArgumentException("rankedResults e expectedIds devem ter o mesmo tamanho");
        }
    }
}
