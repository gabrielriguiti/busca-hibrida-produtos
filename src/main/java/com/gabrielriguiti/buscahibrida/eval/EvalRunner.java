package com.gabrielriguiti.buscahibrida.eval;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Roda uma estrategia de busca pluggavel contra o conjunto de queries e produz Recall@10 /
 * MRR@10, reutilizado sem mudanca pela fase-3 (ver design.md). */
public final class EvalRunner {

    private EvalRunner() {
    }

    public record StrategyResult(String strategyName, double recallAt10, double mrrAt10) {
    }

    public static StrategyResult evaluate(String strategyName, SearchStrategy strategy,
            List<EvalQuery> queries) {
        List<List<Long>> ranked = new ArrayList<>(queries.size());
        List<List<Long>> expected = new ArrayList<>(queries.size());
        for (EvalQuery evalQuery : queries) {
            ranked.add(strategy.search(evalQuery.query()));
            expected.add(evalQuery.expectedProductIds());
        }
        return new StrategyResult(strategyName, Metrics.recallAt10(ranked, expected),
                Metrics.mrrAt10(ranked, expected));
    }

    public static String formatTable(List<StrategyResult> results) {
        StringBuilder table = new StringBuilder();
        table.append(String.format(Locale.ROOT, "%-24s %12s %12s%n", "Estrategia", "Recall@10", "MRR@10"));
        for (StrategyResult result : results) {
            table.append(String.format(Locale.ROOT, "%-24s %12.4f %12.4f%n", result.strategyName(),
                    result.recallAt10(), result.mrrAt10()));
        }
        return table.toString();
    }
}
