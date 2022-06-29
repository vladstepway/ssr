package ru.croc.ugd.ssr.dto.apartmentdefectconfirmation;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RestCcoDto {
    /**
     * Идентификатор документа ОКСа
     */
    private final String ccoDocumentId;
    /**
     * UNOM заселяемого дома
     */
    private final String unom;
    /**
     * Адрес заселяемого дома
     */
    private final String address;
}
