package ru.croc.ugd.ssr.dto;

import lombok.Value;

import java.time.LocalDate;
import java.util.List;

/**
 * RestDaySchedulesRequestDto
 */
@Value
public class RestDaySchedulePeriodDto {

    /**
     * Дата или начало периода копирования/удаления.
     */
    private final LocalDate from;

    /**
     * Конец периода копирования/удаления.
     */
    private final LocalDate to;

    /**
     * ID ЦИП.
     */
    private final String cipId;

    /**
     * ID нотариуса.
     */
    private final String notaryId;

    /**
     * Список дней недели ("MONDAY","TUESDAY" ...).
     */
    private final List<String> weekdays;

    /**
     * Копировать на каждую weekRepeatNumber неделю.
     */
    private final Integer weekRepeatNumber;

    /**
     * Копировать на каждый dayRepeatNumber день.
     */
    private final Integer dayRepeatNumber;

}
