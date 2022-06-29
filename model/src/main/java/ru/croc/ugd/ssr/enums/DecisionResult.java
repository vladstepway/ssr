package ru.croc.ugd.ssr.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum DecisionResult {
    CONSENT(1, "Согласие на предложенную квартиру"),
    REJECTION(2, "Отказ от предложенной квартиры");

    @Getter
    private final Integer code;
    private final String value;

    public String value() {
        return value;
    }
}
