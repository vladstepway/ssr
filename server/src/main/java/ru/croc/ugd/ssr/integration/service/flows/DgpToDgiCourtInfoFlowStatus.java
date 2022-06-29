package ru.croc.ugd.ssr.integration.service.flows;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DgpToDgiCourtInfoFlowStatus {
    OK(1075, "Сообщение успешно обработано"),
    FAILED(1080, "Ошибка обработки");

    private final Integer code;
    private final String title;
}
