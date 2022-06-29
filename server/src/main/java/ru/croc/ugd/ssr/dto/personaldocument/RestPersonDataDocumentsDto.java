package ru.croc.ugd.ssr.dto.personaldocument;

import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * Документы.
 */
@Value
@Builder
public class RestPersonDataDocumentsDto {

    /**
     * Правоустанавливающие документы на объект (квартиру).
     */
    private final List<RestDocumentDto> documents;
    /**
     * Жильцы.
     */
    private final List<RestTenantDto> tenants;
}
