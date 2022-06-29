package ru.croc.ugd.ssr.dto.notary;

import lombok.Builder;
import lombok.Value;

/**
 * Запрос на изменение статуса заявления на приём к нотарийсу.
 */
@Builder
@Value
public class RestNotaryApplicationChangeStatusDto {

    /**
     * Комментарий.
     */
    private final String comment;
}
