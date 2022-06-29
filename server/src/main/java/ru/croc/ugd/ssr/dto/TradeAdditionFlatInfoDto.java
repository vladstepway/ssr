package ru.croc.ugd.ssr.dto;

import lombok.Builder;
import lombok.Value;
import ru.croc.ugd.ssr.FlatType;
import ru.croc.ugd.ssr.RealEstateDataType;
import ru.croc.ugd.ssr.trade.EstateInfoType;

import java.time.LocalDate;

@Value
@Builder
public class TradeAdditionFlatInfoDto {

    private final EstateInfoType estateInfoType;
    private final RealEstateDataType realEstateData;
    private final FlatType flat;
    private final LocalDate moveDate;
    private final String cadNumber;
}
