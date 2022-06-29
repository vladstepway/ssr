package ru.croc.ugd.ssr.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum SsrCcoOrganizationType {

    CONTRACTOR(1),
    DEVELOPER(2);

    private final Integer typeCode;

    public static SsrCcoOrganizationType ofTypeCode(final int typeCode) {
        return Arrays.stream(values())
            .filter(source -> source.getTypeCode() == typeCode)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(String.valueOf(typeCode)));
    }
}
