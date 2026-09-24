package com.gabrielriguiti.buscahibrida.indexing;

import com.gabrielriguiti.buscahibrida.embedding.EmbeddingService;
import com.gabrielriguiti.buscahibrida.normalization.TextNormalizer;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CatalogIndexerTest {

    private final JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
    private final EmbeddingService embeddingService = mock(EmbeddingService.class);
    private final CatalogIndexer indexer =
            new CatalogIndexer(jdbcTemplate, new TextNormalizer(), embeddingService);

    @Test
    void indexesCatalogInBatchesOf64InferenceCalls() throws SQLException {
        stubCatalogOf(130);

        int indexed = indexer.reindex();

        assertThat(indexed).isEqualTo(130);
        // 130 produtos / lote de 64 => 3 chamadas (64 + 64 + 2), nao 130 chamadas de embed().
        verify(embeddingService, times(3)).embedBatch(anyList());
    }

    @Test
    void reindexingOnlyUpdatesExistingRowsNeverInserts() throws SQLException {
        stubCatalogOf(5);

        indexer.reindex();
        indexer.reindex();

        verify(jdbcTemplate, times(2)).batchUpdate(anyString(), anyList());
    }

    private void stubCatalogOf(int catalogSize) throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getLong("id")).thenReturn(1L);
        when(rs.getString("name")).thenReturn("Produto");
        when(rs.getString("description")).thenReturn("Descricao");

        when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenAnswer(invocation -> {
            RowMapper<?> mapper = invocation.getArgument(1);
            List<Object> rows = new ArrayList<>(catalogSize);
            for (int i = 0; i < catalogSize; i++) {
                rows.add(mapper.mapRow(rs, i));
            }
            return rows;
        });
        when(embeddingService.embedBatch(anyList())).thenAnswer(invocation -> {
            List<String> texts = invocation.getArgument(0);
            List<float[]> vectors = new ArrayList<>(texts.size());
            texts.forEach(t -> vectors.add(new float[384]));
            return vectors;
        });
    }
}
