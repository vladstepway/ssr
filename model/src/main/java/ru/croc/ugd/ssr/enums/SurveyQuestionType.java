package ru.croc.ugd.ssr.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum SurveyQuestionType {
    MARK(1, "Оценка"),
    SINGLE_ANSWER(2, "Выбор из справочника"),
    MULTIPLE_ANSWER(3, "Множественный выбор из справочника"),
    FREE_FORM(4, "Ответ на вопрос в свободной форме");

    private final int code;
    private final String value;
}
