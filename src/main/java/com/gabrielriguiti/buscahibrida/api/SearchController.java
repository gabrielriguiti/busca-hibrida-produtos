package com.gabrielriguiti.buscahibrida.api;

import com.gabrielriguiti.buscahibrida.search.SearchService;
import com.gabrielriguiti.buscahibrida.search.SearchService.ProductResult;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/api/busca")
    public List<ProductResult> busca(@RequestParam(required = false) String q) {
        if (q == null || q.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "parametro q e obrigatorio");
        }
        return searchService.search(q);
    }
}
