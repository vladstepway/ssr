package ru.croc.ugd.ssr.dto.personaldocument;

import lombok.Builder;
import lombok.Value;

/**
 * Данные о файле.
 */
@Value
@Builder
public class RestFileDto {

    /**
     * ИД файла в FileStore.
     */
    private final String fileStoreId;
    /**
     * Тип файла.
     */
    private final String fileType;
}
