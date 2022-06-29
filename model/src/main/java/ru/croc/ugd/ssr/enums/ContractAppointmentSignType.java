package ru.croc.ugd.ssr.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum ContractAppointmentSignType {

    PERSONAL(1, "Очное"),
    ELECTRONIC(2, "Электронное");

    private final int typeCode;
    private final String typeName;

    public static ContractAppointmentSignType ofTypeCode(final int typeCode) {
        return Arrays.stream(values())
            .filter(source -> source.getTypeCode() == typeCode)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(String.valueOf(typeCode)));
    }
}
