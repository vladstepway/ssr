package ru.croc.ugd.ssr.dto.personaldocument;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Данные о документе.
 */
@Value
@Builder
public class RestDocumentDto {

    /**
     * Код типа документа.
     */
    private final String typeCode;
    /**
     * Комментарий.
     */
    private final String comment;
    /**
     * Файлы.
     */
    private final List<RestFileDto> files;
    /**
     * Идентификатор документа заявления.
     */
    private final String applicationDocumentId;
    /**
     * Номер заявления.
     */
    private final String eno;
    /**
     * Дата и время загрузки.
     */
    private final LocalDateTime uploadDateTime;
}
