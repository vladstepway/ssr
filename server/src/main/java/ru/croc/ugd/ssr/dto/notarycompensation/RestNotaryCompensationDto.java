package ru.croc.ugd.ssr.dto.notarycompensation;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class RestNotaryCompensationDto {
    /**
     * Номер заявления.
     */
    private final String eno;
    /**
     * Дата и время подачи заявления.
     */
    private final LocalDateTime applicationDateTime;
    /**
     * ИД статуса заявления.
     */
    private final String statusId;
    /**
     * Статус заявления.
     */
    private final String status;
    /**
     * ИД семьи.
     */
    private final String affairId;
    /**
     * Сведения о заявителе.
     */
    private final RestNotaryCompensationApplicantDto applicant;
    /**
     * Сведения о банковских реквизитах.
     */
    private final RestNotaryCompensationBankDetailsDto bankDetails;
}