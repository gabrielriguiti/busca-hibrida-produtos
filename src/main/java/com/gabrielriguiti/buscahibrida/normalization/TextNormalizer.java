package com.gabrielriguiti.buscahibrida.normalization;

import org.springframework.core.io.ClassPathResource;
import tools.jackson.databind.json.JsonMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Forma canonica texto_busca: lowercase, remocao de acento, expansao de abreviacoes
 * conhecidas. Usada tanto pra indexar produtos quanto pra normalizar queries, pra que as
 * duas pontas fiquem comparaveis.
 */
@Component
public class TextNormalizer {

    private static final Pattern DIACRITICS = Pattern.compile("\\p{M}");

    private final Map<String, String> abbreviations;

    public TextNormalizer() {
        this.abbreviations = loadAbbreviations();
    }

    public String normalize(String text) {
        String lower = text.toLowerCase(Locale.ROOT);
        String withoutAccents = DIACRITICS.matcher(Normalizer.normalize(lower, Normalizer.Form.NFD))
                .replaceAll("");
        return java.util.Arrays.stream(withoutAccents.split("\\s+"))
                .map(token -> abbreviations.getOrDefault(token, token))
                .collect(Collectors.joining(" "))
                .trim();
    }

    private static Map<String, String> loadAbbreviations() {
        try (InputStream in = new ClassPathResource("abbreviations.json").getInputStream()) {
            return new JsonMapper().readValue(in, Map.class);
        } catch (IOException e) {
            throw new IllegalStateException("Falha ao carregar dicionario de abreviacoes", e);
        }
    }
}
