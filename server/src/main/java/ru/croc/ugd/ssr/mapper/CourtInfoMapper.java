package ru.croc.ugd.ssr.mapper;

import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.croc.ugd.ssr.dto.courtinfo.RestCourtInfoDto;
import ru.croc.ugd.ssr.model.courtinfo.CourtInfoDocument;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPCourtInfoType;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface CourtInfoMapper {

    @Mapping(target = "affairId", source = "document.courtInfoData.affairId")
    @Mapping(target = "letterId", source = "document.courtInfoData.letterId")
    @Mapping(target = "caseId", source = "document.courtInfoData.caseId")
    @Mapping(target = "courtDate", source = "document.courtInfoData.courtDate")
    @Mapping(target = "courtResultDate", source = "document.courtInfoData.courtResultDate")
    @Mapping(target = "courtLawDate", source = "document.courtInfoData.courtLawDate")
    @Mapping(target = "courtResult", source = "document.courtInfoData.courtResult")
    RestCourtInfoDto toRestCourtInfoDto(final CourtInfoDocument document);

    @Mapping(target = "document.courtInfoData.affairId", source = "affairId")
    @Mapping(target = "document.courtInfoData.letterId", source = "letterId")
    @Mapping(target = "document.courtInfoData.caseId", source = "caseId")
    @Mapping(target = "document.courtInfoData.courtDate", source = "dateLastCourt")
    @Mapping(target = "document.courtInfoData.courtResultDate", source = "dateLastAct")
    @Mapping(target = "document.courtInfoData.courtLawDate", source = "dateLaw")
    @Mapping(target = "document.courtInfoData.courtResult", source = "resultDelo")
    CourtInfoDocument toCourtInfoDocument(
        @MappingTarget final CourtInfoDocument existingDocument, final SuperServiceDGPCourtInfoType courtInfo
    );
}
