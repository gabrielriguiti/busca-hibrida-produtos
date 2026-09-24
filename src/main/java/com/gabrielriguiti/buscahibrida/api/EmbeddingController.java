package com.gabrielriguiti.buscahibrida.api;

import com.gabrielriguiti.buscahibrida.embedding.EmbeddingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Embedding", description = "Geracao de embeddings e5-small (384 dimensoes)")
public class EmbeddingController {

    private final EmbeddingService embeddingService;

    public EmbeddingController(EmbeddingService embeddingService) {
        this.embeddingService = embeddingService;
    }

    @PostMapping("/embed")
    @Operation(summary = "Embeda um texto",
            description = "Devolve o vetor de 384 dimensoes do e5-small pro texto recebido, "
                    + "exatamente como recebido (o chamador decide o prefixo \"query: \"/\"passage: \").")
    public EmbedResponse embed(@RequestBody EmbedRequest request) {
        return new EmbedResponse(embeddingService.embed(request.text()));
    }

    public record EmbedRequest(String text) {
    }

    public record EmbedResponse(float[] vector) {
    }
}
