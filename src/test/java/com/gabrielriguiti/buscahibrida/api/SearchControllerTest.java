package com.gabrielriguiti.buscahibrida.api;

import com.gabrielriguiti.buscahibrida.search.HybridSearchService;
import com.gabrielriguiti.buscahibrida.search.PhoneticSearchService;
import com.gabrielriguiti.buscahibrida.search.SearchService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class SearchControllerTest {

    private final SearchService searchService = mock(SearchService.class);
    private final HybridSearchService hybridSearchService = mock(HybridSearchService.class);
    private final PhoneticSearchService phoneticSearchService = mock(PhoneticSearchService.class);
    private final SearchController controller =
            new SearchController(searchService, hybridSearchService, phoneticSearchService);

    @Test
    void missingQReturns400() {
        assertThatThrownBy(() -> controller.busca(null, "vetorial"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);
        verifyNoInteractions(searchService, hybridSearchService, phoneticSearchService);
    }

    @Test
    void blankQReturns400() {
        assertThatThrownBy(() -> controller.busca("   ", "vetorial"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);
        verifyNoInteractions(searchService, hybridSearchService, phoneticSearchService);
    }

    @Test
    void defaultModeDelegatesToVectorSearch() {
        when(searchService.search("parafuso")).thenReturn(List.of());

        controller.busca("parafuso", "vetorial");

        verifyNoInteractions(hybridSearchService, phoneticSearchService);
    }

    @Test
    void hybridModeDelegatesToHybridSearch() {
        when(hybridSearchService.search("parafuso")).thenReturn(List.of());

        Object result = controller.busca("parafuso", "hibrido");

        assertThat(result).isEqualTo(List.of());
        verifyNoInteractions(searchService, phoneticSearchService);
    }

    @Test
    void phoneticModeDelegatesToPhoneticSearch() {
        when(phoneticSearchService.search("parafuso")).thenReturn(List.of());

        Object result = controller.busca("parafuso", "fonetico");

        assertThat(result).isEqualTo(List.of());
        verifyNoInteractions(searchService, hybridSearchService);
    }
}
