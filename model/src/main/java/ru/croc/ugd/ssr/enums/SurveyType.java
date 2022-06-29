package ru.croc.ugd.ssr.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum SurveyType {
    MPGU(1, "Работа с электронными сервисами"),
    SSR(2, "Работа с ЦИП");

    private final int code;
    private final String value;
}
