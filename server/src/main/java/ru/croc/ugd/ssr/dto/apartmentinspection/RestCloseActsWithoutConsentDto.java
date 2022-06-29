package ru.croc.ugd.ssr.dto.apartmentinspection;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class RestCloseActsWithoutConsentDto {
    /**
     * Список идентификаторов актов
     */
    private final List<String> apartmentInspectionIds;
    /**
     * Причина закрытия актов
     */
    private final CloseActWithoutConsentDto reason;
}
