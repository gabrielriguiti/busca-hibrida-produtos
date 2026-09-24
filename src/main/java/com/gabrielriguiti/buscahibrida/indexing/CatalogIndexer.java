package com.gabrielriguiti.buscahibrida.indexing;

import com.gabrielriguiti.buscahibrida.embedding.EmbeddingService;
import com.gabrielriguiti.buscahibrida.embedding.PgVectorFormat;
import com.gabrielriguiti.buscahibrida.normalization.TextNormalizer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Reindexa o catalogo: normaliza cada produto, embeda em lotes com o prefixo "passage: "
 * do e5, e grava o vetor na coluna embedding dos produtos ja existentes (update por id,
 * portanto idempotente - a contagem de linhas nunca muda).
 */
@Component
public class CatalogIndexer {

    private static final int BATCH_SIZE = 64;

    private final JdbcTemplate jdbcTemplate;
    private final TextNormalizer normalizer;
    private final EmbeddingService embeddingService;

    public CatalogIndexer(JdbcTemplate jdbcTemplate, TextNormalizer normalizer,
            EmbeddingService embeddingService) {
        this.jdbcTemplate = jdbcTemplate;
        this.normalizer = normalizer;
        this.embeddingService = embeddingService;
    }

    public int reindex() {
        List<Product> products = jdbcTemplate.query(
                "SELECT id, name, description FROM products",
                (rs, rowNum) -> new Product(rs.getLong("id"), rs.getString("name"),
                        rs.getString("description")));

        for (int start = 0; start < products.size(); start += BATCH_SIZE) {
            List<Product> batch = products.subList(start, Math.min(start + BATCH_SIZE, products.size()));
            indexBatch(batch);
        }
        return products.size();
    }

    private void indexBatch(List<Product> batch) {
        List<String> normalizedTexts = new ArrayList<>(batch.size());
        List<String> prefixedTexts = new ArrayList<>(batch.size());
        for (Product product : batch) {
            String texto = product.description() == null || product.description().isBlank()
                    ? product.name()
                    : product.name() + ". " + product.description();
            String normalized = normalizer.normalize(texto);
            normalizedTexts.add(normalized);
            prefixedTexts.add("passage: " + normalized);
        }

        List<float[]> vectors = embeddingService.embedBatch(prefixedTexts);

        List<Object[]> updateArgs = new ArrayList<>(batch.size());
        for (int i = 0; i < batch.size(); i++) {
            updateArgs.add(new Object[] {PgVectorFormat.toLiteral(vectors.get(i)), normalizedTexts.get(i),
                    batch.get(i).id()});
        }
        jdbcTemplate.batchUpdate(
                "UPDATE products SET embedding = CAST(? AS vector), texto_busca = ? WHERE id = ?",
                updateArgs);
    }

    private record Product(Long id, String name, String description) {
    }
}
