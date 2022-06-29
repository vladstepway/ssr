package ru.croc.ugd.ssr.dto.personaldocument;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Запрос документов.
 */
@Value
@Builder
public class RestPersonalDocumentRequestDto {

    /**
     * Идентификатор документа запроса.
     */
    private final String id;
    /**
     * Идентификатор документа жителя.
     */
    private final String personDocumentId;
    /**
     * Дата и время формирования запроса.
     */
    private final LocalDateTime requestDateTime;
    /**
     * Адрес отселяемой квартиры (откуда).
     */
    private final String addressFrom;
    /**
     * Правоустанавливающие документы на объект (квартиру).
     */
    private final List<RestRequestedDocumentDto> documents;
    /**
     * Владельцы запрашиваемых личных документов.
     */
    private final List<RestRequestedDocumentOwnerDto> owners;
    /**
     * Заявление на предоставление документов.
     */
    private final RestPersonalDocumentApplicationDto application;
}
