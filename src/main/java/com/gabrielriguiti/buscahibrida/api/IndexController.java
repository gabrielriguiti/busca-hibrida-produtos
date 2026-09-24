package com.gabrielriguiti.buscahibrida.api;

import com.gabrielriguiti.buscahibrida.indexing.CatalogIndexer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    private final CatalogIndexer catalogIndexer;

    public IndexController(CatalogIndexer catalogIndexer) {
        this.catalogIndexer = catalogIndexer;
    }

    @PostMapping("/api/index")
    public IndexResponse index() {
        return new IndexResponse(catalogIndexer.reindex());
    }

    public record IndexResponse(int indexed) {
    }
}
