package ru.croc.ugd.ssr.dto.personaldocument;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RestAddApplicationDocumentDto {

    /**
     * ИД единого файла документов в FileStore.
     */
    private final String unionFileStoreId;
    /**
     * ИД файла заявления о согласии/отказе в FileStore.
     */
    private final String applicationFileStoreId;
}
