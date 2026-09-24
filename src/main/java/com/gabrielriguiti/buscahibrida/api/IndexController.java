package com.gabrielriguiti.buscahibrida.api;

import com.gabrielriguiti.buscahibrida.indexing.CatalogIndexer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Indexacao", description = "Reindexacao do catalogo (embeddings + texto_busca)")
public class IndexController {

    private final CatalogIndexer catalogIndexer;

    public IndexController(CatalogIndexer catalogIndexer) {
        this.catalogIndexer = catalogIndexer;
    }

    @PostMapping("/api/index")
    @Operation(summary = "Reindexa o catalogo",
            description = "Recalcula embedding e texto_busca pra todos os produtos existentes (update, nao insere linhas).")
    public IndexResponse index() {
        return new IndexResponse(catalogIndexer.reindex());
    }

    public record IndexResponse(int indexed) {
    }
}
