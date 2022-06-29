package ru.croc.ugd.ssr.dto.notary;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

/**
 * Изменение заявления на посещение нотариуса.
 */
@Value
@Builder
public class RestNotaryApplicationHistoryEventDto {

    /**
     * Дата изменения.
     */
    private final LocalDateTime eventDate;
    /**
     * Комментарий.
     */
    private final String comment;
    /**
     * Идентификатор изменения.
     */
    private final String eventId;

}
