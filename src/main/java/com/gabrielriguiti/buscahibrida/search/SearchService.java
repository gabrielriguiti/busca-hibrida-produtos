package com.gabrielriguiti.buscahibrida.search;

import com.gabrielriguiti.buscahibrida.embedding.EmbeddingService;
import com.gabrielriguiti.buscahibrida.embedding.PgVectorFormat;
import com.gabrielriguiti.buscahibrida.normalization.TextNormalizer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/** Busca semantica pura: normaliza a query, embeda com o prefixo "query: " do e5, e ordena
 * produtos por distancia vetorial (cosine, mesmo espaco da indexacao). */
@Service
public class SearchService {

    private static final int RESULT_LIMIT = 10;

    private final JdbcTemplate jdbcTemplate;
    private final TextNormalizer normalizer;
    private final EmbeddingService embeddingService;

    public SearchService(JdbcTemplate jdbcTemplate, TextNormalizer normalizer,
            EmbeddingService embeddingService) {
        this.jdbcTemplate = jdbcTemplate;
        this.normalizer = normalizer;
        this.embeddingService = embeddingService;
    }

    public List<ProductResult> search(String q) {
        return search(q, RESULT_LIMIT);
    }

    public List<ProductResult> search(String q, int limit) {
        String prefixed = "query: " + normalizer.normalize(q);
        float[] queryVector = embeddingService.embed(prefixed);
        String vectorLiteral = PgVectorFormat.toLiteral(queryVector);

        return jdbcTemplate.query(
                "SELECT id, name, description FROM products WHERE embedding IS NOT NULL "
                        + "ORDER BY embedding <=> CAST(? AS vector) LIMIT ?",
                (rs, rowNum) -> new ProductResult(rs.getLong("id"), rs.getString("name"),
                        rs.getString("description")),
                vectorLiteral, limit);
    }

    public record ProductResult(Long id, String name, String description) {
    }
}
