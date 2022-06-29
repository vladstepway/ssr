package ru.croc.ugd.ssr.mapper;

import static java.util.Optional.ofNullable;
import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.croc.ugd.ssr.FlatType;
import ru.croc.ugd.ssr.OfferLetterParsedFlatData;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.RealEstateDataType;
import ru.croc.ugd.ssr.dto.offerletterparsing.RestAutomaticallyParsedFlatDataDto;
import ru.croc.ugd.ssr.dto.offerletterparsing.RestOfferLetterParsingDto;
import ru.croc.ugd.ssr.model.offerletterparsing.OfferLetterParsingDocument;
import ru.croc.ugd.ssr.utils.PersonUtils;
import ru.croc.ugd.ssr.utils.RealEstateUtils;

import java.util.Objects;
import java.util.function.Function;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface OfferLetterParsingMapper {

    @Mapping(target = "document.offerLetterParsingData.affairId", source = "personData.affairId")
    @Mapping(target = "document.offerLetterParsingData.letterId", source = "letterId")
    @Mapping(
        target = "document.offerLetterParsingData.addressFrom", source = "personData", qualifiedByName = "toAddressFrom"
    )
    OfferLetterParsingDocument toOfferLetterParsingDocument(
        final String letterId,
        final PersonType personData,
        @Context final Function<PersonType, RealEstateDataType> retrieveRealEstate
    );

    @Named("toAddressFrom")
    default String toAddressFrom(
        final PersonType personData, @Context final Function<PersonType, RealEstateDataType> retrieveRealEstate
    ) {
        final RealEstateDataType realEstateData = retrieveRealEstate.apply(personData);

        final FlatType flat = ofNullable(personData.getFlatID())
            .map(flatId -> RealEstateUtils.getFlatByFlatId(realEstateData, flatId))
            .orElse(null);

        return RealEstateUtils.getFlatAddress(realEstateData, flat);
    }

    @Mapping(target = "id", source = "offerLetterParsingDocument.id")
    @Mapping(target = "addressFrom", source = "offerLetterParsingDocument.document.offerLetterParsingData.addressFrom")
    @Mapping(target = "fileStoreId", source = "offerLetter", qualifiedByName = "toLetterFileStoreId")
    @Mapping(target = "affairId", source = "offerLetterParsingDocument.document.offerLetterParsingData.affairId")
    @Mapping(target = "letterId", source = "offerLetterParsingDocument.document.offerLetterParsingData.letterId")
    @Mapping(target = "automaticallyParsedFlatData", source = "offerLetter.flatData")
    RestOfferLetterParsingDto toRestOfferLetterParsingDto(
        final OfferLetterParsingDocument offerLetterParsingDocument,
        final PersonType.OfferLetters.OfferLetter offerLetter
    );

    @Mapping(target = "addressTo", source = "flatData", qualifiedByName = "toAddressTo")
    @Mapping(target = "cadNumber", source = "flatData.cadNumber")
    RestAutomaticallyParsedFlatDataDto toRestAutomaticallyParsedFlatDataDto(
        final OfferLetterParsedFlatData flatData
    );

    @Named("toLetterFileStoreId")
    default String toLetterFileStoreId(final PersonType.OfferLetters.OfferLetter offerLetter) {
        return PersonUtils.extractOfferLetterFileByType(offerLetter, PersonUtils.OFFER_FILE_TYPE)
            .map(PersonType.OfferLetters.OfferLetter.Files.File::getFileLink)
            .orElse(null);
    }

    @Named("toAddressTo")
    default String toAddressTo(final OfferLetterParsedFlatData flatData) {
        final String address = ofNullable(flatData)
            .map(OfferLetterParsedFlatData::getAddress)
            .orElse(null);
        final String flatNum = ofNullable(flatData)
            .map(OfferLetterParsedFlatData::getFlatNumber)
            .orElse(null);

        if (Objects.isNull(address) || Objects.isNull(flatNum)) {
            return address;
        } else {
            return address + " кв. " + flatNum;
        }
    }
}
