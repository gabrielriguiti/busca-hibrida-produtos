package com.gabrielriguiti.buscahibrida.api;

import com.gabrielriguiti.buscahibrida.search.HybridSearchService;
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

    public SearchController(SearchService searchService, HybridSearchService hybridSearchService) {
        this.searchService = searchService;
        this.hybridSearchService = hybridSearchService;
    }

    @GetMapping("/api/busca")
    public Object busca(@RequestParam(required = false) String q,
            @RequestParam(required = false, defaultValue = "vetorial") String modo) {
        if (q == null || q.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "parametro q e obrigatorio");
        }
        return "hibrido".equals(modo) ? hybridSearchService.search(q) : searchService.search(q);
    }
}
