package ru.croc.ugd.ssr.integration.util;

import static java.util.Optional.ofNullable;

import org.springframework.stereotype.Component;

/**
 * NullableUtils.
 */
@Component
public class NullableUtils {

    /**
     * Return either string value or default value.
     *
     * @param nullableString nullableString
     * @return string value
     */
    public String retrieveNullable(final String nullableString) {
        return retrieveNullable(nullableString, "-");
    }

    /**
     * Return either string value or default value.
     *
     * @param nullableString nullableString
     * @param defaultValue defaultValue
     * @return string value
     */
    public String retrieveNullable(final String nullableString, final String defaultValue) {
        return ofNullable(nullableString).orElse(defaultValue);
    }
}
