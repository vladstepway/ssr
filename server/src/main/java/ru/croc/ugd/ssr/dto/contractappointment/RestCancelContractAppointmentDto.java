package ru.croc.ugd.ssr.dto.contractappointment;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

/**
 * Запрос на отмену записи на подписание контракта.
 */
@Builder
@Value
public class RestCancelContractAppointmentDto {

    /**
     * Дата отмены записи.
     */
    private final LocalDate cancelDate;

    /**
     * Причина отмены записи.
     */
    private final String cancelReason;

}
