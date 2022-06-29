package ru.croc.ugd.ssr.dto.apartmentinspection;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.List;

@Value
@Builder
public class RestDefectsEliminationDto {
    /**
     * Список идентификаторов актов
     */
    private final List<String> apartmentInspectionIds;
    /**
     * Планируемая дата устранения дефектов
     */
    private final LocalDate delayDate;
}
