package ru.croc.ugd.ssr.dto.personaldocument;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class RestCreatePersonalDocumentRequestDto {
    /**
     * Идентификатор документа жителя.
     */
    private final String personDocumentId;
    /**
     * Адрес отселяемой квартиры (откуда).
     */
    private final String addressFrom;
    /**
     * Правоустанавливающие документы на объект (квартиру).
     */
    private final List<RestRequestedDocumentDto> titleDocuments;
    /**
     * Документы личного характера.
     */
    private final List<RestRequestedDocumentDto> tenantDocuments;
}
