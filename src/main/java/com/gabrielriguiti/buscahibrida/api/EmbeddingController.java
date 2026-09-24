package com.gabrielriguiti.buscahibrida.api;

import com.gabrielriguiti.buscahibrida.embedding.EmbeddingService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmbeddingController {

    private final EmbeddingService embeddingService;

    public EmbeddingController(EmbeddingService embeddingService) {
        this.embeddingService = embeddingService;
    }

    @PostMapping("/embed")
    public EmbedResponse embed(@RequestBody EmbedRequest request) {
        return new EmbedResponse(embeddingService.embed(request.text()));
    }

    public record EmbedRequest(String text) {
    }

    public record EmbedResponse(float[] vector) {
    }
}
