package ru.croc.ugd.ssr.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class RegularExpressionUtils {

    public static String escapePostgreRegexCharacters(final String regExp) {
        return regExp
            .replaceAll("\\\\", "\\\\\\\\")
            .replaceAll("/", "\\\\/")
            .replaceAll("\\[", "\\\\[")
            .replaceAll("]", "\\\\]")
            .replaceAll(":", "\\\\:")
            .replaceAll("\\(", "\\\\(")
            .replaceAll("\\)", "\\\\)")
            .replaceAll("\\^", "\\\\^");
    }

    public static String escapeSolrRegexCharacters(final String regExp) {
        return regExp.replaceAll(":", "\\\\:");
    }
}
