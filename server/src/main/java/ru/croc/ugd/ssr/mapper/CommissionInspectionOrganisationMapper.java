package ru.croc.ugd.ssr.mapper;

import static java.util.Optional.ofNullable;
import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.croc.ugd.ssr.commissionorganisation.CommissionInspectionOrganisationData;
import ru.croc.ugd.ssr.dto.commissionorganisation.RestCommissionInspectionOrganisationDto;
import ru.croc.ugd.ssr.enums.CommissionInspectionOrganisationType;
import ru.croc.ugd.ssr.model.commissionorganisation.CommissionInspectionOrganisationDocument;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface CommissionInspectionOrganisationMapper {

    List<RestCommissionInspectionOrganisationDto> toRestCommissionInspectionOrganisationDtoList(
        final List<CommissionInspectionOrganisationDocument> documents
    );

    @Mapping(target = "id", source = "document.documentID")
    @Mapping(
        target = "name",
        source = "document.commissionInspectionOrganisationData",
        qualifiedByName = "toOrganisationName"
    )
    @Mapping(target = "address", source = "document.commissionInspectionOrganisationData.address")
    @Mapping(target = "phone", source = "document.commissionInspectionOrganisationData.phone")
    RestCommissionInspectionOrganisationDto toRestCommissionInspectionOrganisationDto(
        final CommissionInspectionOrganisationDocument document
    );

    default String toOrganisationName(final CommissionInspectionOrganisationData commissionInspectionOrganisation) {
        final String organisationName = CommissionInspectionOrganisationType
            .ofTypeValue(commissionInspectionOrganisation.getType())
            .getSsrValue();
        final String area = ofNullable(commissionInspectionOrganisation.getArea())
            .map(areaValue -> " " + areaValue)
            .orElse("");

        return organisationName + area;
    }
}
