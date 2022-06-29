package ru.croc.ugd.ssr.dto;

import lombok.Builder;
import lombok.Value;
import ru.croc.ugd.ssr.FlatType;
import ru.croc.ugd.ssr.RealEstateDataType;

@Value
@Builder
public class RealEstateDataAndFlatInfoDto {

    private final RealEstateDataType realEstateData;
    private final FlatType flat;
}
