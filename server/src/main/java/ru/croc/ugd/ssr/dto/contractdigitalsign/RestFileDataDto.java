package ru.croc.ugd.ssr.dto.contractdigitalsign;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

/**
 * Данные файла.
 */
@Value
@Builder
public class RestFileDataDto {

    private final String fileStoreId;
    private final String fileName;
    private final String fileType;
    private final String mimeType;
    private final long fileSize;
    private final LocalDateTime creationDateTime;
    private final LocalDateTime lastModifiedDateTime;
    private final Boolean isVerified;
    private final LocalDateTime signDateTime;
}
