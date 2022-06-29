package ru.croc.ugd.ssr.dto.commissioninspection;

import lombok.Builder;
import lombok.Data;
import ru.croc.ugd.ssr.generated.dto.RestCommissionInspectionApartmentToDto;
import ru.croc.ugd.ssr.generated.dto.RestCommissionInspectionPersonInfoDto;

import java.util.List;

/**
 * CommissionInspectionCheckResponse.
 */
@Data
@Builder
public class CommissionInspectionCheckResponse {

    /**
     * Возможно ли предоставление услуги.
     */
    private Boolean isPossible;
    /**
     * Причина, по которой невозможно предоставить услугу.
     */
    private String reason;

    /**
     * Заселяемые адреса.
     */
    private List<RestCommissionInspectionApartmentToDto> to;

}
