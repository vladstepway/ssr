package ru.croc.ugd.ssr.dto.flatappointment;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

/**
 * Запрос на отмену записи на осмотр квартиры.
 */
@Value
@Builder
public class RestCancelFlatAppointmentDto {

    /**
     * Дата отмены записи.
     */
    private final LocalDate cancelDate;

    /**
     * Причина отмены записи.
     */
    private final String cancelReason;

}
