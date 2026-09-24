package com.gabrielriguiti.buscahibrida.eval;

import java.util.List;

public record EvalQuery(String query, List<Long> expectedProductIds) {
}
