package ru.croc.ugd.ssr.dto.contractdigitalsign;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

/**
 * Данные правообладателя.
 */
@Value
@Builder
public class RestOwnerDto {

    /**
     * ИД документа жителя.
     */
    private final String personDocumentId;
    /**
     * ФИО.
     */
    private final String fullName;
    /**
     * Дата рождения.
     */
    private final LocalDate birthDate;
}
