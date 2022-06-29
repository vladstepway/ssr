package ru.croc.ugd.ssr.dto.apartmentinspection;

import lombok.Builder;
import lombok.Value;
import ru.croc.ugd.ssr.dto.commissioninspection.DefectDto;

import java.time.LocalDate;
import java.util.List;

@Value
@Builder
public class DelayReasonDto {
    /**
     * Планируемая дата устранения дефектов
     */
    private final LocalDate delayDate;

    /**
     * Причина продления срока устранения дефектов
     */
    private final String delayReason;

    /**
     * Дефекты устранены
     */
    private final boolean areDefectsEliminated;

    /**
     * Список дефектов
     */
    private final List<DefectDto> defects;

    /**
     * Утверждение информации об устранении дефектов инициировано генподрядчиком
     */
    private final boolean createdByGeneralContractor;
}
