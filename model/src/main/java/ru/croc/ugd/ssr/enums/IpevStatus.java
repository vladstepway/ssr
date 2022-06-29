package ru.croc.ugd.ssr.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum IpevStatus {

    NEW("Новое"),
    PROCESSED("Обработано");

    private final String status;
}
