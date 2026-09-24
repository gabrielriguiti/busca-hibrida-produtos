package com.gabrielriguiti.buscahibrida.search;

import java.util.Locale;

/** Codigo fonetico American Soundex (1 letra + 3 digitos) - aproximacao simples usada so
 * como baseline fraca de comparacao (ver search-evaluation spec), nao desenhada pro
 * portugues. */
final class Soundex {

    private Soundex() {
    }

    static String encode(String word) {
        String letters = word.toLowerCase(Locale.ROOT).replaceAll("[^a-z]", "");
        if (letters.isEmpty()) {
            return "0000";
        }

        StringBuilder code = new StringBuilder();
        code.append(Character.toUpperCase(letters.charAt(0)));
        char lastDigit = digitFor(letters.charAt(0));
        for (int i = 1; i < letters.length() && code.length() < 4; i++) {
            char digit = digitFor(letters.charAt(i));
            if (digit != '0' && digit != lastDigit) {
                code.append(digit);
            }
            lastDigit = digit;
        }
        while (code.length() < 4) {
            code.append('0');
        }
        return code.toString();
    }

    private static char digitFor(char c) {
        return switch (c) {
            case 'b', 'f', 'p', 'v' -> '1';
            case 'c', 'g', 'j', 'k', 'q', 's', 'x', 'z' -> '2';
            case 'd', 't' -> '3';
            case 'l' -> '4';
            case 'm', 'n' -> '5';
            case 'r' -> '6';
            default -> '0';
        };
    }
}
