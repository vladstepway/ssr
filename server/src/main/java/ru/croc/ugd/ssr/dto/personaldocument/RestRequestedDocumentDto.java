package ru.croc.ugd.ssr.dto.personaldocument;

import lombok.Builder;
import lombok.Value;

/**
 * Информация о запрашиваемом документе.
 */
@Value
@Builder
public class RestRequestedDocumentDto {

    /**
     * Код типа документа.
     */
    private final String typeCode;
    /**
     * Комментарий.
     */
    private final String comment;
    /**
     * Идентификатор документа владельца (для документа личного характера).
     */
    private final String personDocumentId;
}
