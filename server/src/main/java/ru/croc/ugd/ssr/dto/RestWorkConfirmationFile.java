package ru.croc.ugd.ssr.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RestWorkConfirmationFile {
    /**
     * ИД файла в FileStore.
     */
    private final String fileStoreId;
}
