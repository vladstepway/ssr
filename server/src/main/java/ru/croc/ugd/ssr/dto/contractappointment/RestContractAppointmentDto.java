package ru.croc.ugd.ssr.dto.contractappointment;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Данные заявления на заключение договора.
 */
@Value
@Builder
public class RestContractAppointmentDto {

    /**
     * ИД карточки.
     */
    private final String id;
    /**
     * Номер заявления.
     */
    private final String eno;
    /**
     * ID статуса заявления.
     */
    private final String statusId;
    /**
     * Статус заявления.
     */
    private final String status;
    /**
     * Заявитель.
     */
    private final RestContractAppointmentApplicantDto applicant;
    /**
     * Адрес центра переселения.
     */
    private final String cipAddress;
    /**
     * Дата и время приема.
     */
    private final LocalDateTime appointmentDateTime;
    /**
     * Идентификатор договора.
     */
    private final String contractOrderId;
    /**
     * Адрес заселения.
     */
    private final String addressTo;
    /**
     * Заключен ли договор.
     */
    private final boolean isContractEntered;
    /**
     * ЦИП ID.
     */
    private final String cipId;
    /**
     * Дата отмены.
     */
    private final LocalDate cancelDate;
    /**
     * Причина отмены.
     */
    private final String cancelReason;
    /**
     * Причина отказа в заключении договора.
     */
    private final String refuseReason;
    /**
     * Дата подписания договора.
     */
    private final LocalDate contractSignDate;
    /**
     * Дата подписания акта.
     */
    private final LocalDate actSignDate;
    /**
     * ID запущенного процесса.
     */
    private final String processInstanceId;
    /**
     * Способ подписания
     */
    private final int signType;
}
