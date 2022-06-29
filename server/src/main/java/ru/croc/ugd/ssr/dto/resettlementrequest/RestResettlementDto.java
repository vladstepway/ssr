package ru.croc.ugd.ssr.dto.resettlementrequest;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class RestResettlementDto {
    private final List<RestPartResettlementDto> partResettlements;
    private final List<RestFullResettlementDto> fullResettlements;
    private final String realEstateUnom;
}
