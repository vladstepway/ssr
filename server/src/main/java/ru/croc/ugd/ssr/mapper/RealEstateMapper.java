package ru.croc.ugd.ssr.mapper;

import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.croc.ugd.ssr.dto.realestate.RestDemolitionDto;
import ru.croc.ugd.ssr.dto.realestate.RestHousePreservationDto;
import ru.croc.ugd.ssr.dto.realestate.RestResettlementCompletionDto;
import ru.croc.ugd.ssr.model.RealEstateDocument;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface RealEstateMapper {

    @Mapping(
        target = "document.realEstateData.resettlementCompletionDate",
        source = "restResettlementCompletionDto.date"
    )
    @Mapping(
        target = "document.realEstateData.resettlementCompletionData.documentNumber",
        source = "restResettlementCompletionDto.documentNumber"
    )
    @Mapping(
        target = "document.realEstateData.resettlementCompletionData.documentDate",
        source = "restResettlementCompletionDto.documentDate"
    )
    @Mapping(
        target = "document.realEstateData.resettlementCompletionData.fileStoreId",
        source = "restResettlementCompletionDto.fileStoreId"
    )
    RealEstateDocument toRealEstateDocument(
        @MappingTarget final RealEstateDocument realEstateDocument,
        final RestResettlementCompletionDto restResettlementCompletionDto
    );

    @Mapping(
        target = "document.realEstateData.housePreservationData.isPreserved",
        source = "restHousePreservationDto.preserved"
    )
    RealEstateDocument toRealEstateDocument(
        @MappingTarget final RealEstateDocument realEstateDocument,
        final RestHousePreservationDto restHousePreservationDto
    );

    @Mapping(target = "document.realEstateData.demolitionDate", source = "restDemolitionDto.date")
    @Mapping(
        target = "document.realEstateData.demolitionData.isDemolished", source = "restDemolitionDto.demolished"
    )
    @Mapping(
        target = "document.realEstateData.demolitionData.documentNumber",
        source = "restDemolitionDto.documentNumber"
    )
    @Mapping(
        target = "document.realEstateData.demolitionData.documentDate", source = "restDemolitionDto.documentDate"
    )
    @Mapping(
        target = "document.realEstateData.demolitionData.fileStoreId", source = "restDemolitionDto.fileStoreId"
    )
    @Mapping(
        target = "document.realEstateData.demolitionData.documentType", source = "restDemolitionDto.documentType"
    )
    @Mapping(
        target = "document.realEstateData.demolitionData.documentId", source = "restDemolitionDto.documentId"
    )
    @Mapping(
        target = "document.realEstateData.demolitionData.documentContent", source = "restDemolitionDto.documentContent"
    )
    RealEstateDocument toRealEstateDocument(
        @MappingTarget final RealEstateDocument realEstateDocument,
        final RestDemolitionDto restDemolitionDto
    );
}
