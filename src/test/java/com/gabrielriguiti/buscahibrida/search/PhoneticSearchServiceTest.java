package com.gabrielriguiti.buscahibrida.search;

import com.gabrielriguiti.buscahibrida.normalization.TextNormalizer;
import com.gabrielriguiti.buscahibrida.search.SearchService.ProductResult;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PhoneticSearchServiceTest {

    private final JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
    private final PhoneticSearchService service =
            new PhoneticSearchService(jdbcTemplate, new TextNormalizer());

    @Test
    void ranksThePhoneticallyClosestProductFirst() throws SQLException {
        stubProducts(
                new Object[] {1L, "Furadeira eletrica 500W", "Furadeira de impacto"},
                new Object[] {2L, "Martelo de borracha", "Martelo com cabeca de borracha"});

        List<ProductResult> results = service.search("furadeira eletrika");

        assertThat(results.get(0).id()).isEqualTo(1L);
    }

    @Test
    void dropsProductsWithNoPhoneticOverlapInsteadOfPaddingResults() throws SQLException {
        stubProducts(
                new Object[] {1L, "Furadeira eletrica 500W", "Furadeira de impacto"},
                new Object[] {2L, "Martelo de borracha", "Martelo com cabeca de borracha"});

        List<ProductResult> results = service.search("furadeira eletrika");

        assertThat(results).hasSize(1);
    }

    private void stubProducts(Object[]... rows) throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        Long[] ids = new Long[rows.length];
        String[] names = new String[rows.length];
        String[] descriptions = new String[rows.length];
        for (int i = 0; i < rows.length; i++) {
            ids[i] = (Long) rows[i][0];
            names[i] = (String) rows[i][1];
            descriptions[i] = (String) rows[i][2];
        }
        when(rs.getLong("id")).thenReturn(ids[0], java.util.Arrays.copyOfRange(ids, 1, ids.length));
        when(rs.getString("name")).thenReturn(names[0], java.util.Arrays.copyOfRange(names, 1, names.length));
        when(rs.getString("description")).thenReturn(descriptions[0],
                java.util.Arrays.copyOfRange(descriptions, 1, descriptions.length));

        when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenAnswer(invocation -> {
            RowMapper<?> mapper = invocation.getArgument(1);
            List<Object> mapped = new ArrayList<>(rows.length);
            for (int i = 0; i < rows.length; i++) {
                mapped.add(mapper.mapRow(rs, i));
            }
            return mapped;
        });
    }
}
