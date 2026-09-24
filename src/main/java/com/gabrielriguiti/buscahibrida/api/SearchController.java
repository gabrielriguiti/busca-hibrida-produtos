package com.gabrielriguiti.buscahibrida.api;

import com.gabrielriguiti.buscahibrida.search.HybridSearchService;
import com.gabrielriguiti.buscahibrida.search.PhoneticSearchService;
import com.gabrielriguiti.buscahibrida.search.SearchService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class SearchController {

    private final SearchService searchService;
    private final HybridSearchService hybridSearchService;
    private final PhoneticSearchService phoneticSearchService;

    public SearchController(SearchService searchService, HybridSearchService hybridSearchService,
            PhoneticSearchService phoneticSearchService) {
        this.searchService = searchService;
        this.hybridSearchService = hybridSearchService;
        this.phoneticSearchService = phoneticSearchService;
    }

    @GetMapping("/api/busca")
    public Object busca(@RequestParam(required = false) String q,
            @RequestParam(required = false, defaultValue = "vetorial") String modo) {
        if (q == null || q.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "parametro q e obrigatorio");
        }
        return switch (modo) {
            case "hibrido" -> hybridSearchService.search(q);
            case "fonetico" -> phoneticSearchService.search(q);
            default -> searchService.search(q);
        };
    }
}
