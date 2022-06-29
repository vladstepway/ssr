package ru.croc.ugd.ssr.dto.personaldocument;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.List;

/**
 * Данные о владельце и запрашиваемых документах.
 */
@Value
@Builder
public class RestRequestedDocumentOwnerDto {

    /**
     * Идентификатор документа жителя.
     */
    private final String personDocumentId;
    /**
     * ФИО жителя.
     */
    private final String fullName;
    /**
     * Дата рождения.
     */
    private final LocalDate birthDate;
    /**
     * Документы личного характера.
     */
    private final List<RestRequestedDocumentDto> documents;
}
