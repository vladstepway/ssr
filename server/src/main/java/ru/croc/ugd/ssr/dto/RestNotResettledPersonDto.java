package ru.croc.ugd.ssr.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;


/**
 * Данные жителя по которому не завершился процесс переселения.
 *
 */
@Value
@Builder
public class RestNotResettledPersonDto {
    private final String personDocumentId;
    private final String fullName;
    private final LocalDate birthDate;
    private final String relocationStatusCode;
    private final String relocationStatusName;
    private final String flatNumber;
}
