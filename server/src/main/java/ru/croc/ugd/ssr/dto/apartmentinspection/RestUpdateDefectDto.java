package ru.croc.ugd.ssr.dto.apartmentinspection;

import lombok.Builder;
import lombok.Value;
import ru.croc.ugd.ssr.dto.commissioninspection.DefectDto;

import java.util.List;

@Value
@Builder
public class RestUpdateDefectDto {
    /**
     * Дефекты квартиры.
     */
    private final List<DefectDto> defects;
    /**
     * Дефекты квартиры, исключенные из списка дефектов.
     */
    private final List<DefectDto> excludedDefects;
    /**
     * Причина исключения дефектов.
     */
    private final String defectExclusionReason;
}
