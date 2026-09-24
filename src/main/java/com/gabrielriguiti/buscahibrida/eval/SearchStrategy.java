package com.gabrielriguiti.buscahibrida.eval;

import java.util.List;

/** Uma estrategia de busca pluggavel pro runner de avaliacao: recebe texto livre, devolve
 * ids de produto ranqueados por relevancia decrescente. */
@FunctionalInterface
public interface SearchStrategy {
    List<Long> search(String query);
}
