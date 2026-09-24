package com.gabrielriguiti.buscahibrida.eval;

import com.gabrielriguiti.buscahibrida.search.PhoneticSearchService;
import com.gabrielriguiti.buscahibrida.search.SearchService;
import com.gabrielriguiti.buscahibrida.search.SearchService.ProductResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

/** Roda a baseline fonetica e a busca vetorial da fase-1 contra o conjunto de avaliacao e
 * grava a comparacao em eval-results.md, consumido pelo README (fase-5). Requer o catalogo
 * ja indexado (POST /api/index) contra o Postgres local (ver docker-compose.yml). */
@SpringBootTest
class EvalReportTest {

    @Autowired
    private SearchService vectorSearch;

    @Autowired
    private PhoneticSearchService phoneticSearch;

    @Test
    void writesComparisonReport() throws IOException {
        List<EvalQuery> queries = EvalQuerySet.load();

        EvalRunner.StrategyResult phonetic = EvalRunner.evaluate("fonetica (soundex)",
                q -> ids(phoneticSearch.search(q)), queries);
        EvalRunner.StrategyResult vector = EvalRunner.evaluate("vetorial (e5-small)",
                q -> ids(vectorSearch.search(q)), queries);

        System.out.println(EvalRunner.formatTable(List.of(phonetic, vector)));

        assertThat(phonetic.recallAt10()).isBetween(0.0, 1.0);
        assertThat(vector.recallAt10()).isBetween(0.0, 1.0);

        String report = String.format(Locale.ROOT, """
                # Resultados de avaliacao (fase-2)

                Recall@10 e MRR@10 sobre %d queries rotuladas (ver \
                `src/main/resources/eval/queries.json`) contra o catalogo semente da fase-0.

                | Estrategia | Recall@10 | MRR@10 |
                |---|---|---|
                | %s | %.4f | %.4f |
                | %s | %.4f | %.4f |
                """, queries.size(),
                phonetic.strategyName(), phonetic.recallAt10(), phonetic.mrrAt10(),
                vector.strategyName(), vector.recallAt10(), vector.mrrAt10());
        Files.writeString(Path.of("eval-results.md"), report);
    }

    private static List<Long> ids(List<ProductResult> results) {
        return results.stream().map(ProductResult::id).toList();
    }
}
