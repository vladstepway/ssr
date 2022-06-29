package ru.croc.ugd.ssr.dto.apartmentinspection;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RestConfirmClosingWithoutConsentDto {
    /**
     * Закрытие акта без согласия подтверждено
     */
    private final boolean confirmed;
}
