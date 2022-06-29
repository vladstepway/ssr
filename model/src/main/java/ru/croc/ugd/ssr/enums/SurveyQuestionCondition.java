package ru.croc.ugd.ssr.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum SurveyQuestionCondition {
    LESS_THAN("lt", "Меньше"),
    GREATER_THAN("gt", "Больше"),
    EQUALS("eq", "Равно");

    private final String code;
    private final String value;
}
