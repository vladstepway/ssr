package ru.croc.ugd.ssr.enums;

import lombok.AllArgsConstructor;

import java.util.Arrays;

/**
 * Статус проживания.
 */
@AllArgsConstructor
public enum StatusLiving {

    OWNER(1, "Собственник (частная собственность)"),
    USER(2, "Пользователь (частная собственность)"),
    TENANT(3, "Наниматель (найм/пользование)"),
    RESIDENT(4, "Проживающий (найм/пользователь)"),
    FREE(5, "Свободная"),
    MISSING(0, "Отсутствует");

    private final Integer value;
    private final String label;

    public Integer value() {
        return value;
    }

}
