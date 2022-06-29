package ru.croc.ugd.ssr.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Value;

import java.time.LocalTime;

/**
 * Информация о слоте.
 */
@Builder
@Value
public class RestDayScheduleSlotDto {

    /**
     * ID слота.
     */
    private final String slotId;

    /**
     * Время начала приема.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private final LocalTime timeFrom;

    /**
     * Время окончания приема.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private final LocalTime timeTo;

    /**
     * Количество окон приема.
     */
    private final int totalWindows;

    /**
     * Количество забронированных окон.
     */
    private final int totalBooked;
}
