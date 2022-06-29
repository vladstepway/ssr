package ru.croc.ugd.ssr.enums;

import lombok.AllArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
public enum NotaryApplicationSource {

    SSR("ССР"),
    MPGU("МПГУ");

    private final String value;

    public String value() {
        return value;
    }

    /**
     * Returns source by value.
     * @param value value
     * @return source
     */
    public static NotaryApplicationSource fromValue(final String value) {
        return Arrays.stream(NotaryApplicationSource.values())
            .filter(source -> source.value.equalsIgnoreCase(value))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
