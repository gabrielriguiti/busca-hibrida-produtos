package com.buscahibrida.embedding;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class EmbeddingServiceTest {

    @Autowired
    private EmbeddingService embeddingService;

    @Test
    void embedReturnsExactly384Dimensions() {
        assertThat(embeddingService.embed("parafuso sextavado M8")).hasSize(384);
        assertThat(embeddingService.embed("furadeira")).hasSize(384);
    }
}
