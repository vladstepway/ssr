package ru.croc.ugd.ssr.dto.notary;

import lombok.Builder;
import lombok.Value;

/**
 * Адрес нотариуса.
 */
@Builder
@Value
public class RestNotaryAddressDto {

    /**
     * Адрес.
     */
    private final String address;
    /**
     * УНОМ.
     */
    private final String unom;
    /**
     * Дополнительные сведения об адресе.
     */
    private final String additionalInformation;
}
