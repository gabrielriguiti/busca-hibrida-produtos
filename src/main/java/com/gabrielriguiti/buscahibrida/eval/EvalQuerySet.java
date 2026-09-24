package com.gabrielriguiti.buscahibrida.eval;

import org.springframework.core.io.ClassPathResource;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/** Carrega o conjunto fixo de queries rotuladas da fase-2, reutilizado sem mudanca pela
 * fase-3 (ver design.md). */
public final class EvalQuerySet {

    private EvalQuerySet() {
    }

    public static List<EvalQuery> load() {
        try (InputStream in = new ClassPathResource("eval/queries.json").getInputStream()) {
            return List.of(new JsonMapper().readValue(in, EvalQuery[].class));
        } catch (IOException e) {
            throw new IllegalStateException("Falha ao carregar conjunto de queries de avaliacao", e);
        }
    }
}
