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

    /** Tokens cuja parte alfabetica (depois de descartar digitos) fica abaixo disso nao
     * carregam conteudo fonetico real - sao codigos de produto (ex.: "m8"->"m", "500w"->"w")
     * que colidem por acidente com palavras curtas sem relacao (ex.: "meio"->M000 == "m8"->
     * M000). Nenhuma palavra real do catalogo cai abaixo de 3 letras depois de descartar
     * digitos. */
    private static final int MIN_PHONETIC_LETTERS = 3;

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
                .filter(s -> s.score > 0)
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
            if (hasEnoughPhoneticContent(token)) {
                codes.add(Soundex.encode(token));
            }
        }
        return codes;
    }

    private static boolean hasEnoughPhoneticContent(String token) {
        return token.replaceAll("[^a-z]", "").length() >= MIN_PHONETIC_LETTERS;
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
