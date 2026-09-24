package com.gabrielriguiti.buscahibrida.search;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SoundexTest {

    @Test
    void classicExampleRobertAndRupertCollapseToSameCode() {
        assertThat(Soundex.encode("Robert")).isEqualTo("R163");
        assertThat(Soundex.encode("Rupert")).isEqualTo("R163");
    }

    @Test
    void blankWordEncodesToZeroCode() {
        assertThat(Soundex.encode("   ")).isEqualTo("0000");
    }
}
