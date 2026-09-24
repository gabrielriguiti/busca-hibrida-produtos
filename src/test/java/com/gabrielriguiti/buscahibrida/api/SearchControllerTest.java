package com.gabrielriguiti.buscahibrida.api;

import com.gabrielriguiti.buscahibrida.search.SearchService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class SearchControllerTest {

    private final SearchService searchService = mock(SearchService.class);
    private final SearchController controller = new SearchController(searchService);

    @Test
    void missingQReturns400() {
        assertThatThrownBy(() -> controller.busca(null))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);
        verifyNoInteractions(searchService);
    }

    @Test
    void blankQReturns400() {
        assertThatThrownBy(() -> controller.busca("   "))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);
        verifyNoInteractions(searchService);
    }
}
