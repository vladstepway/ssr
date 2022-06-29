package ru.croc.ugd.ssr.dto.personaldocument;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Заявление на предоставление документов.
 */
@Value
@Builder
public class RestPersonalDocumentApplicationDto {

    /**
     * Идентификатор документа заявления.
     */
    private final String id;
    /**
     * Номер заявления.
     */
    private final String eno;
    /**
     * Дата и время заявления.
     */
    private final LocalDateTime applicationDateTime;
    /**
     * Идентификатор документа жителя.
     */
    private final String personDocumentId;
    /**
     * ФИО жителя.
     */
    private final String fullName;
    /**
     * Адрес отселяемой квартиры (откуда).
     */
    private final String addressFrom;
    /**
     * ИД единого файла документов в FileStore.
     */
    private final String unionFileStoreId;
    /**
     * Правоустанавливающие документы на объект (квартиру).
     */
    private final List<RestDocumentDto> documents;
    /**
     * Жильцы.
     */
    private final List<RestTenantDto> tenants;
}
