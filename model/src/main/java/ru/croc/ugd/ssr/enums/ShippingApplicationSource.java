package ru.croc.ugd.ssr.enums;

import lombok.AllArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
public enum ShippingApplicationSource {

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
    public static ShippingApplicationSource fromValue(final String value) {
        return Arrays.stream(ShippingApplicationSource.values())
            .filter(source -> source.value.equalsIgnoreCase(value))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(value));
    }
}
