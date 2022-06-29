package ru.croc.ugd.ssr.dto.apartmentinspection;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class RestSendTasksToGeneralContractorDto {
    /**
     * Список идентификаторов актов
     */
    private final List<String> apartmentInspectionIds;
    /**
     * Генподрядчик
     */
    private final String generalContractorId;
}
