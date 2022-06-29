package ru.croc.ugd.ssr.mapper;

import static java.util.Optional.ofNullable;
import static org.mapstruct.ReportingPolicy.IGNORE;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import ru.croc.ugd.ssr.application.Applicant;
import ru.croc.ugd.ssr.commission.CommissionInspectionData;
import ru.croc.ugd.ssr.commission.CommissionInspectionHistoryEvent;
import ru.croc.ugd.ssr.commission.DefectType;
import ru.croc.ugd.ssr.commission.InspectionType;
import ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionCheckResponse;
import ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus;
import ru.croc.ugd.ssr.dto.commissioninspection.DefectDto;
import ru.croc.ugd.ssr.dto.commissioninspection.RestApplicantDto;
import ru.croc.ugd.ssr.dto.commissioninspection.RestCommissionInspectionDto;
import ru.croc.ugd.ssr.dto.commissioninspection.RestHistoryEventDto;
import ru.croc.ugd.ssr.generated.dto.RestCommissionInspectionCheckResponseDto;
import ru.croc.ugd.ssr.generated.dto.RestDefectDto;
import ru.croc.ugd.ssr.model.commissioninspection.CommissionInspectionDocument;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = IGNORE)
public interface RestCommissionInspectionMapper {

    @Mapping(target = "ISPOSSBLE", source = "isPossible")
    @Mapping(target = "REASON", source = "reason")
    @Mapping(target = "to", source = "to")
    RestCommissionInspectionCheckResponseDto toRestCommissionInspectionCheckResponseDto(
        final CommissionInspectionCheckResponse checkResponse
    );

    List<RestDefectDto> toRestDefectDtoList(final List<DefectDto> defects);

    default List<RestCommissionInspectionDto> toRestCommissionInspectionDtoList(
        final List<CommissionInspectionDocument> commissionInspectionDocuments, final String personFullName
    ) {
        return commissionInspectionDocuments.stream()
            .map(commissionInspectionDocument -> toRestCommissionInspectionDto(
                commissionInspectionDocument.getId(),
                commissionInspectionDocument.getDocument().getCommissionInspectionData(),
                personFullName
            ))
            .collect(Collectors.toList());
    }

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "commissionInspectionId")
    @Mapping(target = "eno", source = "commissionInspection.eno")
    @Mapping(target = "applicationStatusId", source = "commissionInspection.applicationStatusId")
    @Mapping(target = "applicationStatus", source = "commissionInspection.applicationStatusId",
        qualifiedByName = "toApplicationStatus")
    @Mapping(target = "flatNumber", source = "commissionInspection.flatNum")
    @Mapping(target = "ccoUnom", source = "commissionInspection.ccoUnom")
    @Mapping(target = "address", source = "commissionInspection.address")
    @Mapping(target = "completionDateTime", source = "commissionInspection.completionDateTime")
    @Mapping(target = "applicationDateTime", source = "commissionInspection.applicationDateTime")
    @Mapping(target = "confirmedDateTime", source = "commissionInspection.confirmedInspectionDateTime")
    @Mapping(target = "processInstanceId", source = "commissionInspection.processInstanceId")
    @Mapping(target = "currentApartmentInspectionId", source = "commissionInspection.currentApartmentInspectionId")
    @Mapping(target = "completionReasonCode", source = "commissionInspection.completionReasonCode")
    @Mapping(target = "completionReason", source = "commissionInspection.completionReason")
    @Mapping(target = "applicant", source = "commissionInspection.applicant")
    @Mapping(target = "letter.id", source = "commissionInspection.letter.id")
    @Mapping(target = "letter.fileLink", source = "commissionInspection.letter.fileLink")
    @Mapping(target = "defects", source = "commissionInspection.defects.defect")
    @Mapping(target = "history", source = "commissionInspection.history.events")
    RestCommissionInspectionDto toRestCommissionInspectionDto(
        final String commissionInspectionId,
        final CommissionInspectionData commissionInspection,
        @Context final String fullName
    );

    @Mapping(target = "id", source = "personId")
    RestApplicantDto toRestApplicant(final Applicant applicant, @Context final String fullName);

    @AfterMapping
    default RestApplicantDto populatePersonFullName(
        @MappingTarget RestApplicantDto.RestApplicantDtoBuilder applicantDtoBuilder,
        @Context final String fullName
    ) {
        applicantDtoBuilder.fullName(fullName);
        return applicantDtoBuilder.build();
    }

    RestDefectDto toRestDefectDto(final DefectType defectType);

    @Mapping(target = "inspectionType", source = "inspectionType")
    RestHistoryEventDto toRestHistoryEventDto(final CommissionInspectionHistoryEvent commissionInspectionHistoryEvent);

    @Named("toApplicationStatus")
    default String toApplicationStatus(final String statusId) {
        return ofNullable(statusId)
            .map(CommissionInspectionFlowStatus::of)
            .map(CommissionInspectionFlowStatus::getSsrStatus)
            .orElse(null);
    }

    default String toInspectionTypeValue(final InspectionType inspectionType) {
        return ofNullable(inspectionType)
            .map(InspectionType::value)
            .orElse(null);
    }

}
