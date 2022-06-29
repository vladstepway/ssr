package ru.croc.ugd.ssr.utils;

import org.apache.commons.lang.StringUtils;

import java.util.Comparator;

public class Comparators {
    private static final String DIGIT_AND_DECIMAL_REGEX = "[^\\d.]";

    public static Comparator<String> createNaturalOrderRegexComparator() {
        return Comparator.comparingDouble(Comparators::parseStringToNumber);
    }

    private static double parseStringToNumber(final String input) {
        final String digitsOnly = input.replaceAll(DIGIT_AND_DECIMAL_REGEX, "");
        if (StringUtils.isEmpty(digitsOnly)) {
            return 0;
        }
        try {
            return Double.parseDouble(digitsOnly);
        } catch (NumberFormatException nfe) {
            return 0;
        }
    }
}
