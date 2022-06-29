package ru.croc.ugd.ssr.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum SurveyCategory {
    RELOCATION_FINISHED(1, "Опрос по завершению этапа переселения"),
    RELOCATION_COMMON(2, "Общий опрос о Программе реновации");

    private final int code;
    private final String value;
}
