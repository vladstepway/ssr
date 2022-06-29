package ru.croc.ugd.ssr.dto.agreement;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

/**
 * Данные жителя на момент просмотра.
 */
@Value
@Builder
public class RestAgreementParticipantDto {
    /**
     * ИД жителя.
     */
    private final String personDocumentId;
    /**
     * Полное имя.
     */
    private final String fullName;
    /**
     * Дата рождения.
     */
    private final LocalDate birthDate;
}
