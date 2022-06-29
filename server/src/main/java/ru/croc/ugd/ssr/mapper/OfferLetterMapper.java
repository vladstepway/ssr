package ru.croc.ugd.ssr.mapper;

import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.Mapper;
import ru.croc.ugd.ssr.OfferLetterParsedFlatData;
import ru.croc.ugd.ssr.dto.OfferLetterDocData;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface OfferLetterMapper {
    OfferLetterParsedFlatData toOfferLetterParsedFlatData(
        OfferLetterDocData offerLetterDocData
    );
}
