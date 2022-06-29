package ru.croc.ugd.ssr.dto.notary;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

/**
 * Заявление на посещение нотариуса.
 */
@Builder
@Value
public class RestNotaryApplicationFileDto {

    /**
     * Название файла.
     */
    private final String name;
    /**
     * ИД файла в filestore.
     */
    private final String fileStoreId;
    /**
     * Дата создания файла в системе.
     */
    private final LocalDateTime creationDate;
    /**
     * Получение документа подтверждено.
     */
    @JsonProperty(value = "isConfirmed")
    private final boolean confirmed;
}
