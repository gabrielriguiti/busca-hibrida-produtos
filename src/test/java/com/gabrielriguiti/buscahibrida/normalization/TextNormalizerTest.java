package com.gabrielriguiti.buscahibrida.normalization;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TextNormalizerTest {

    private final TextNormalizer normalizer = new TextNormalizer();

    @Test
    void lowercasesAndStripsAccents() {
        String result = normalizer.normalize("PARAFUSO SEXTAVADO Nº8 AÇO");

        assertThat(result).isEqualTo(result.toLowerCase());
        assertThat(result).doesNotContainPattern("[À-ÿ]");
        assertThat(result).isEqualTo("parafuso sextavado nº8 aco");
    }

    @Test
    void expandsEachAbbreviationSeededInFase0Dataset() {
        assertThat(normalizer.normalize("PARAF SEXTAVADO M8 C/ PORCA"))
                .isEqualTo("parafuso sextavado m8 com porca");
        assertThat(normalizer.normalize("Parafuso sext. M8 com porca"))
                .isEqualTo("parafuso sextavado m8 com porca");
        assertThat(normalizer.normalize("Paraf. sextavado M6"))
                .isEqualTo("parafuso sextavado m6");
        assertThat(normalizer.normalize("Furad. eletrica 500W"))
                .isEqualTo("furadeira eletrica 500w");
    }
}
