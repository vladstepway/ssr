package ru.croc.ugd.ssr.solr.converter.commissioninspection;

import static java.util.Optional.ofNullable;
import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import ru.croc.ugd.ssr.commission.CommissionInspectionData;
import ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus;
import ru.croc.ugd.ssr.solr.UgdSsrCommissionInspection;
import ru.croc.ugd.ssr.utils.DateTimeUtils;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface SolrCommissionInspectionMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(
        target = "ugdSsrCommissionInspectionApplicationNumber",
        source = "commissionInspectionData.eno"
    )
    @Mapping(
        target = "ugdSsrCommissionInspectionCreationDateTime",
        source = "commissionInspectionData.applicationDateTime"
    )
    @Mapping(
        target = "ugdSsrCommissionInspectionFio",
        source = "personFio"
    )
    @Mapping(
        target = "ugdSsrCommissionInspectionAddressTo",
        source = "commissionInspectionData.address"
    )
    @Mapping(
        target = "ugdSsrCommissionInspectionFlatTo",
        source = "commissionInspectionData.flatNum"
    )
    @Mapping(
        target = "ugdSsrCommissionInspectionDate",
        source = "commissionInspectionData.confirmedInspectionDateTime",
        qualifiedByName = "toCommissionInspectionDate"
    )
    @Mapping(
        target = "ugdSsrCommissionInspectionStatus",
        source = "commissionInspectionData.applicationStatusId",
        qualifiedByName = "toApplicationStatus"
    )
    @Mapping(
        target = "ugdSsrCommissionInspectionOfferLetterFileLink",
        source = "commissionInspectionData.letter.fileLink"
    )
    @Mapping(
        target = "ugdSsrCommissionInspectionArea",
        source = "area"
    )
    @Mapping(
        target = "ugdSsrCommissionInspectionDistrict",
        source = "district"
    )
    @Mapping(
        target = "ugdSsrCommissionInspectionDistrictCode",
        source = "districtCode"
    )
    @Mapping(
        target = "ugdSsrCommissionInspectionUnom",
        source = "commissionInspectionData.ccoUnom"
    )
    @Mapping(
        target = "ugdSsrCommissionInspectionOrganizationType",
        source = "organisationType"
    )
    @Mapping(
        target = "ugdSsrCommissionInspectionTaskId",
        source = "taskId"
    )
    UgdSsrCommissionInspection toUgdSsrCommissionInspection(
        @MappingTarget final UgdSsrCommissionInspection ugdSsrCommissionInspection,
        final CommissionInspectionData commissionInspectionData,
        final String personFio,
        final String area,
        final String district,
        final String districtCode,
        final Integer organisationType,
        final String taskId
    );

    @Named("toCommissionInspectionDate")
    default String toCommissionInspectionDate(final LocalDateTime inspectionDateTime) {
        return ofNullable(inspectionDateTime)
            .map(DateTimeUtils::getFullDateWithTime)
            .orElse("нет");
    }

    @Named("toApplicationStatus")
    default String toApplicationStatus(final String statusId) {
        return ofNullable(statusId)
            .map(CommissionInspectionFlowStatus::of)
            .map(CommissionInspectionFlowStatus::getSsrStatus)
            .orElse(null);
    }
}
