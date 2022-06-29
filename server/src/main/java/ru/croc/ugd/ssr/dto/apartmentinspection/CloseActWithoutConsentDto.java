package ru.croc.ugd.ssr.dto.apartmentinspection;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CloseActWithoutConsentDto {
    /**
     * Код причины закрытия акта
     */
    private final String actClosureReasonCode;
    /**
     * Комментарий к причине закрытия акта
     */
    private final String actClosureReasonComment;
}
