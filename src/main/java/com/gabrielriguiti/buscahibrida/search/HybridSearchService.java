package com.gabrielriguiti.buscahibrida.search;

import com.gabrielriguiti.buscahibrida.search.SearchService.ProductResult;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Modo hibrido: funde o ranking lexical (pg_trgm) e o vetorial (e5-small) via RRF,
 * expondo o rank de origem de cada resultado em cada ranking pro toggle da fase-4. */
@Service
public class HybridSearchService {

    private static final int RESULT_LIMIT = 10;

    private final LexicalSearchService lexicalSearchService;
    private final SearchService vectorSearchService;

    public HybridSearchService(LexicalSearchService lexicalSearchService, SearchService vectorSearchService) {
        this.lexicalSearchService = lexicalSearchService;
        this.vectorSearchService = vectorSearchService;
    }

    public List<HybridResult> search(String q) {
        List<ProductResult> lexical = lexicalSearchService.search(q);
        List<ProductResult> vector = vectorSearchService.search(q, LexicalSearchService.CANDIDATE_POOL_SIZE);

        Map<Long, ProductResult> byId = new LinkedHashMap<>();
        lexical.forEach(p -> byId.put(p.id(), p));
        vector.forEach(p -> byId.putIfAbsent(p.id(), p));

        List<Long> lexicalIds = lexical.stream().map(ProductResult::id).toList();
        List<Long> vectorIds = vector.stream().map(ProductResult::id).toList();

        return RrfFusion.fuse(lexicalIds, vectorIds, RrfFusion.K).stream()
                .limit(RESULT_LIMIT)
                .map(fused -> {
                    ProductResult product = byId.get(fused.id());
                    return new HybridResult(product.id(), product.name(), product.description(),
                            fused.score(), fused.lexicalRank(), fused.vectorRank());
                })
                .toList();
    }

    public record HybridResult(Long id, String name, String description, double score,
            Integer lexicalRank, Integer vectorRank) {
    }
}
