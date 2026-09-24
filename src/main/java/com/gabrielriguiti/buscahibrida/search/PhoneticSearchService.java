package com.gabrielriguiti.buscahibrida.search;

import com.gabrielriguiti.buscahibrida.normalization.TextNormalizer;
import com.gabrielriguiti.buscahibrida.search.SearchService.ProductResult;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** Baseline fonetica (Soundex) pra comparar contra a busca vetorial na avaliacao da fase-2
 * - nao havia nenhuma busca fonetica no projeto antes desta fase. Ranqueia produtos pela
 * quantidade de tokens da query cujo codigo Soundex bate com algum token do produto. */
@Service
public class PhoneticSearchService {

    private static final int RESULT_LIMIT = 10;

    private final JdbcTemplate jdbcTemplate;
    private final TextNormalizer normalizer;

    public PhoneticSearchService(JdbcTemplate jdbcTemplate, TextNormalizer normalizer) {
        this.jdbcTemplate = jdbcTemplate;
        this.normalizer = normalizer;
    }

    public List<ProductResult> search(String q) {
        Set<String> queryCodes = soundexCodes(q);

        List<ScoredProduct> scored = jdbcTemplate.query(
                "SELECT id, name, description FROM products",
                (rs, rowNum) -> {
                    ProductResult product = new ProductResult(rs.getLong("id"), rs.getString("name"),
                            rs.getString("description"));
                    return new ScoredProduct(product, overlap(queryCodes, soundexCodes(searchableText(product))));
                });

        return scored.stream()
                .sorted(Comparator.<ScoredProduct>comparingInt(s -> -s.score)
                        .thenComparing(s -> s.product.id()))
                .limit(RESULT_LIMIT)
                .map(s -> s.product)
                .toList();
    }

    private static String searchableText(ProductResult product) {
        return product.description() == null || product.description().isBlank()
                ? product.name()
                : product.name() + " " + product.description();
    }

    private Set<String> soundexCodes(String text) {
        Set<String> codes = new LinkedHashSet<>();
        for (String token : normalizer.normalize(text).split("\\s+")) {
            if (!token.isBlank()) {
                codes.add(Soundex.encode(token));
            }
        }
        return codes;
    }

    private static int overlap(Set<String> a, Set<String> b) {
        int count = 0;
        for (String code : a) {
            if (b.contains(code)) {
                count++;
            }
        }
        return count;
    }

    private record ScoredProduct(ProductResult product, int score) {
    }
}
