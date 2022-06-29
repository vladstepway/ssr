package ru.croc.ugd.ssr.dto.contractdigitalsign;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RestSaveSignatureDto {
    /**
     * ИД подписанного файла в FileStore.
     */
    private final String fileStoreId;
}
