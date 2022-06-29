package ru.croc.ugd.ssr.dto.personaldocument;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RestCreatePersonalDocumentDto {
    /**
     * Идентификатор документа жителя.
     */
    private final String personDocumentId;
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
}
