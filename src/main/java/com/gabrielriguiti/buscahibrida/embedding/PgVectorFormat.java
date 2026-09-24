package com.gabrielriguiti.buscahibrida.embedding;

import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/** Formata um vetor float[] como literal textual `[v1,v2,...]` pro tipo `vector` do pgvector. */
public final class PgVectorFormat {

    private PgVectorFormat() {
    }

    public static String toLiteral(float[] vector) {
        return IntStream.range(0, vector.length)
                .mapToObj(i -> String.format(Locale.ROOT, "%.8f", vector[i]))
                .collect(Collectors.joining(",", "[", "]"));
    }
}
