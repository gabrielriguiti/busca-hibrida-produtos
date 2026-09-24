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

    /** similarity()/% compara a query contra a string INTEIRA de texto_busca (nome+
     * descricao concatenados) - dilui demais queries curtas contra um texto_busca longo,
     * mesmo com match literal (ver design.md do refinar-relevancia-busca-curta).
     * word_similarity()/<% compara contra a melhor sub-sequencia de palavras. Limiar 0.5 em
     * vez do default 0.6 da extensao - calibrado contra os 70 pares do harness (0.5 cobre
     * 69/70, 0.6 so 64/70), aplicado como comparacao explicita em vez de
     * pg_trgm.word_similarity_threshold pra nao depender de estado de sessao numa conexao
     * de pool. */
    private static final double WORD_SIMILARITY_THRESHOLD = 0.5;

    private final JdbcTemplate jdbcTemplate;
    private final TextNormalizer normalizer;

    public LexicalSearchService(JdbcTemplate jdbcTemplate, TextNormalizer normalizer) {
        this.jdbcTemplate = jdbcTemplate;
        this.normalizer = normalizer;
    }

    public List<ProductResult> search(String q) {
        String normalized = normalizer.normalize(q);
        return jdbcTemplate.query(
                "SELECT id, name, description FROM products "
                        + "WHERE word_similarity(?, texto_busca) >= ? "
                        + "ORDER BY word_similarity(?, texto_busca) DESC LIMIT ?",
                (rs, rowNum) -> new ProductResult(rs.getLong("id"), rs.getString("name"),
                        rs.getString("description")),
                normalized, WORD_SIMILARITY_THRESHOLD, normalized, CANDIDATE_POOL_SIZE);
    }
}
