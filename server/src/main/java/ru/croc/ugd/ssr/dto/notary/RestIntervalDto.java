package ru.croc.ugd.ssr.dto.notary;

import lombok.Builder;
import lombok.Value;

import java.time.LocalTime;

/**
 * Режим работы.
 */
@Builder
@Value
public class RestIntervalDto {

    /**
     * Время начала.
     */
    private final LocalTime start;

    /**
     * Время окончания.
     */
    private final LocalTime end;
}
