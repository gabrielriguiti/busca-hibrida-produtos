package com.gabrielriguiti.buscahibrida.search;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Reciprocal Rank Fusion: combina o ranking lexical e o vetorial numa unica lista ordenada
 * pelo score fundido = soma de 1/(k+rank) nos rankings em que cada id aparece (ver
 * design.md - Decisions). */
public final class RrfFusion {

    public static final int K = 60; // default do paper original de RRF, ver design.md

    private RrfFusion() {
    }

    public record FusedResult(Long id, double score, Integer lexicalRank, Integer vectorRank) {
    }

    public static List<FusedResult> fuse(List<Long> lexicalRanking, List<Long> vectorRanking, int k) {
        Map<Long, Integer> lexicalRanks = ranksOf(lexicalRanking);
        Map<Long, Integer> vectorRanks = ranksOf(vectorRanking);

        Set<Long> allIds = new LinkedHashSet<>(lexicalRanking);
        allIds.addAll(vectorRanking);

        List<FusedResult> fused = new ArrayList<>(allIds.size());
        for (Long id : allIds) {
            Integer lexicalRank = lexicalRanks.get(id);
            Integer vectorRank = vectorRanks.get(id);
            double score = (lexicalRank != null ? 1.0 / (k + lexicalRank) : 0.0)
                    + (vectorRank != null ? 1.0 / (k + vectorRank) : 0.0);
            fused.add(new FusedResult(id, score, lexicalRank, vectorRank));
        }

        fused.sort(Comparator.<FusedResult>comparingDouble(FusedResult::score).reversed()
                .thenComparing(FusedResult::id));
        return fused;
    }

    private static Map<Long, Integer> ranksOf(List<Long> ranking) {
        Map<Long, Integer> ranks = new LinkedHashMap<>();
        for (int i = 0; i < ranking.size(); i++) {
            ranks.putIfAbsent(ranking.get(i), i + 1);
        }
        return ranks;
    }
}
