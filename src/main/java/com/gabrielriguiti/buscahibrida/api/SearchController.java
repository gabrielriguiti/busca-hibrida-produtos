package com.gabrielriguiti.buscahibrida.api;

import com.gabrielriguiti.buscahibrida.search.HybridSearchService;
import com.gabrielriguiti.buscahibrida.search.PhoneticSearchService;
import com.gabrielriguiti.buscahibrida.search.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@Tag(name = "Busca", description = "Busca de produtos nos modos fonetico, vetorial e hibrido")
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
    @Operation(summary = "Busca produtos por texto livre",
            description = "Devolve produtos ranqueados. Nos modos fonetico/vetorial cada "
                    + "resultado tem id/name/description; no modo hibrido cada resultado "
                    + "tambem inclui score (RRF), lexicalRank e vectorRank.")
    public Object busca(
            @Parameter(description = "Texto de busca livre", example = "parafuso sextavdo m8")
            @RequestParam(required = false) String q,
            @Parameter(description = "Estrategia de busca",
                    schema = @Schema(allowableValues = {"fonetico", "vetorial", "hibrido"}))
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
