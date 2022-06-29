package ru.croc.ugd.ssr.dto.personaldocument;

import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * Сведения о документах.
 */
@Value
@Builder
public class RestPersonalDocumentDto {

    /**
     * Идентификатор документа сведений о документах.
     */
    private final String id;
    /**
     * Идентификатор семьи.
     */
    private final String affairId;
    /**
     * ИД единого файла документов в FileStore.
     */
    private final String unionFileStoreId;
    /**
     * Адрес отселяемой квартиры (откуда).
     */
    private final String addressFrom;
    /**
     * ИД письма с предложением.
     */
    private final String letterId;
    /**
     * Правоустанавливающие документы на объект (квартиру).
     */
    private final List<RestDocumentDto> documents;
    /**
     * Жильцы.
     */
    private final List<RestTenantDto> tenants;
    /**
     * ИД запущенного процесса
     */
    private final String processInstanceId;
}
