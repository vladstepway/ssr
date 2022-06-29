package ru.croc.ugd.ssr.dto.personaldocument;

import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * Данные о документе для объединения файлов в единый файл.
 */
@Value
@Builder
public class RestMergeDocumentFileDto {

    /**
     * ИД файлов в FileStore.
     */
    private final List<String> fileStoreIds;
    /**
     * Код типа документа.
     */
    private final String documentTypeCode;
}