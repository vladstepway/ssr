package ru.croc.ugd.ssr.dto.commissioninspection;

import lombok.Builder;
import lombok.Value;
import ru.croc.ugd.ssr.db.projection.CommissionInspectionProjection;

import java.time.LocalDateTime;

/**
 * RestCommissionInspectionConfirmedAppointmentDto.
 */
@Value
@Builder
public class RestCommissionInspectionVisitsDto {

    private final String commissionInspectionId;

    private final String eno;

    private final String flatNum;

    private final LocalDateTime dateTime;

    public static RestCommissionInspectionVisitsDto of(final CommissionInspectionProjection projection) {
        return RestCommissionInspectionVisitsDto
            .builder()
            .dateTime(LocalDateTime.parse(projection.getInspectionDateTime()))
            .eno(projection.getEno())
            .commissionInspectionId(projection.getId())
            .flatNum(projection.getFlatNum())
            .build();
    }
}
