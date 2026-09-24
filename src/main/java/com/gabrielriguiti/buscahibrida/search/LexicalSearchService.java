package com.gabrielriguiti.buscahibrida.search;

import com.gabrielriguiti.buscahibrida.normalization.TextNormalizer;
import com.gabrielriguiti.buscahibrida.search.SearchService.ProductResult;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/** Busca lexical via similaridade de trigrama do pg_trgm (`texto_busca`), tolerante a erros
 * de digitacao. Um dos dois rankings de entrada da fusao RRF da fase-3 (ver design.md). */
@Service
public class LexicalSearchService {

    static final int CANDIDATE_POOL_SIZE = 50; // ver design.md - Tamanho do pool de candidatos

    private final JdbcTemplate jdbcTemplate;
    private final TextNormalizer normalizer;

    public LexicalSearchService(JdbcTemplate jdbcTemplate, TextNormalizer normalizer) {
        this.jdbcTemplate = jdbcTemplate;
        this.normalizer = normalizer;
    }

    public List<ProductResult> search(String q) {
        String normalized = normalizer.normalize(q);
        return jdbcTemplate.query(
                "SELECT id, name, description FROM products WHERE texto_busca % ? "
                        + "ORDER BY similarity(texto_busca, ?) DESC LIMIT ?",
                (rs, rowNum) -> new ProductResult(rs.getLong("id"), rs.getString("name"),
                        rs.getString("description")),
                normalized, normalized, CANDIDATE_POOL_SIZE);
    }
}
