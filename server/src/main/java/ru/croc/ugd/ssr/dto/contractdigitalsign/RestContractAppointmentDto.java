package ru.croc.ugd.ssr.dto.contractdigitalsign;

import lombok.Builder;
import lombok.Value;

/**
 * Данные заявления.
 */
@Value
@Builder
public class RestContractAppointmentDto {
    /**
     * ИД заявления.
     */
    private final String id;
    /**
     * Номер заявления.
     */
    private final String eno;
}
