package ru.croc.ugd.ssr.dictionaryupload;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class DictionaryUploadDataDto {

    private final String fileLink;

    private final LocalDateTime lastModifiedDate;

    private final DictionaryUploadDataType type;

}
