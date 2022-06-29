package ru.croc.ugd.ssr.dto;

import lombok.Builder;
import lombok.Value;
import ru.croc.ugd.ssr.FlatType;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.RealEstateDataType;

import java.time.LocalDate;

@Value
@Builder
public class PersonDataAndFlatInfoDto {

    private final PersonType personData;
    private final RealEstateDataType realEstateData;
    private final FlatType flat;
    private final LocalDate moveDate;
}
