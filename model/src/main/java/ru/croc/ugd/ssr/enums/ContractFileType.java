package ru.croc.ugd.ssr.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ContractFileType {

    SIGNED_CONTRACT(4),
    SIGNED_ACT(6);

    private final Integer value;

    public Integer value() {
        return value;
    }

    public String getStringValue() {
        return value.toString();
    }
}
