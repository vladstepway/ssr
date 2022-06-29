package ru.croc.ugd.ssr.dto.contractappointment;

import lombok.Builder;
import lombok.Value;

/**
 * Данные об электронных подписях правообладателя.
 */
@Value
@Builder
public class RestOwnerSignatureDto {

    /**
     * Идентификатор документа жителя.
     */
    private final String personDocumentId;
    /**
     * Данные электронной подписи акта.
     */
    private final RestFileSignatureDto actSignature;
    /**
     * Данные электронной подписи договора.
     */
    private final RestFileSignatureDto contractSignature;
}
