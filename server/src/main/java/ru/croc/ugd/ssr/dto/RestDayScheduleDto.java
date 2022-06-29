package ru.croc.ugd.ssr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO рабочего дня.
 */
@Builder(toBuilder = true)
@Value
public class RestDayScheduleDto {

    /**
     * Уникальный идентификатор.
     */
    private final String id;

    /**
     * ID ЦИП.
     */
    private final String cipId;

    /**
     * Нотариус.
     */
    private final String notaryId;

    /**
     * Дата работы.
     */
    private final LocalDate date;

    /**
     * Количество окон приема.
     */
    private final int totalWindows;

    /**
     * Количество забронированных окон.
     */
    private final int totalBooked;

    /**
     * Количество слотов, доступных для одновременного бронирования временных интервалов подряд.
     */
    private final Integer slotNumber;

    /**
     * Слоты.
     */
    private final List<RestDayScheduleSlotDto> slots;

    /**
     * Слот удален.
     */
    private final boolean isRemoved;
    /**
     * Слот скопирован.
     */
    private final boolean isCopied;

    @JsonProperty("isRemoved")
    public boolean isRemoved() {
        return isRemoved;
    }

    @JsonProperty("isCopied")
    public boolean isCopied() {
        return isCopied;
    }
}
