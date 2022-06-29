package ru.croc.ugd.ssr.solr.converter.notarycompensation;

import static java.util.Optional.ofNullable;
import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import ru.croc.ugd.ssr.dto.notarycompensation.NotaryCompensationFlowStatus;
import ru.croc.ugd.ssr.notarycompensation.NotaryCompensationData;
import ru.croc.ugd.ssr.solr.UgdSsrNotaryCompensation;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface SolrNotaryCompensationMapper {

    @Mapping(target = "sysDocumentId", ignore = true)
    @Mapping(target = "sysType", ignore = true)
    @Mapping(target = "sysAccessTypes", ignore = true)
    @Mapping(
        target = "ugdSsrNotaryCompensationEno",
        source = "notaryCompensationData.eno"
    )
    @Mapping(
        target = "ugdSsrNotaryCompensationDateTime",
        source = "notaryCompensationData.applicationDateTime"
    )
    @Mapping(
        target = "ugdSsrNotaryCompensationApplicantFullName",
        source = "applicantFullName"
    )
    @Mapping(
        target = "ugdSsrNotaryCompensationOwnersFullName",
        source = "ownersFullNames"
    )
    @Mapping(
        target = "ugdSsrNotaryCompensationStatus",
        source = "notaryCompensationData.statusId",
        qualifiedByName = "toApplicationStatus"
    )
    UgdSsrNotaryCompensation toUgdSsrNotaryCompensation(
        @MappingTarget final UgdSsrNotaryCompensation ugdSsrNotaryCompensation,
        final NotaryCompensationData notaryCompensationData,
        final String applicantFullName,
        final List<String> ownersFullNames
    );

    @Named("toApplicationStatus")
    default String toApplicationStatus(final String statusId) {
        return ofNullable(statusId)
            .map(NotaryCompensationFlowStatus::of)
            .map(NotaryCompensationFlowStatus::getSsrStatus)
            .orElse(null);
    }
}
