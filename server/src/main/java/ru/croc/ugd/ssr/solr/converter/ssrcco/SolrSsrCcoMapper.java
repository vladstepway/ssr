package ru.croc.ugd.ssr.solr.converter.ssrcco;

import static java.util.Optional.ofNullable;
import static org.mapstruct.ReportingPolicy.ERROR;
import static ru.croc.ugd.ssr.enums.SsrCcoOrganizationType.CONTRACTOR;
import static ru.croc.ugd.ssr.enums.SsrCcoOrganizationType.DEVELOPER;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import ru.croc.ugd.ssr.enums.DistrictCode;
import ru.croc.ugd.ssr.enums.SsrCcoOrganizationType;
import ru.croc.ugd.ssr.solr.UgdSsrSsrCco;
import ru.croc.ugd.ssr.ssrcco.SsrCcoData;
import ru.croc.ugd.ssr.ssrcco.SsrCcoEmployee;
import ru.croc.ugd.ssr.ssrcco.SsrCcoOrganization;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface SolrSsrCcoMapper {

    @Mapping(target = "sysDocumentId", ignore = true)
    @Mapping(target = "sysType", ignore = true)
    @Mapping(target = "sysAccessTypes", ignore = true)
    @Mapping(target = "ugdSsrSsrCcoAddress", source = "ssrCcoData.address")
    @Mapping(target = "ugdSsrSsrCcoArea", source = "ssrCcoData.area")
    @Mapping(target = "ugdSsrSsrCcoDistrict", source = "ssrCcoData.district")
    @Mapping(target = "ugdSsrSsrCcoUnom", source = "ssrCcoData.unom")
    @Mapping(target = "ugdSsrSsrCcoDistrictCode", source = "ssrCcoData.district", qualifiedByName = "toDistrictCode")
    @Mapping(target = "ugdSsrSsrCcoPsDocumentId", source = "ssrCcoData.psDocumentId")
    @Mapping(
        target = "ugdSsrSsrCcoGeneralContractors",
        source = "ssrCcoData.organizations",
        qualifiedByName = "toUgdSsrSsrCcoGeneralContractors"
    )
    @Mapping(
        target = "ugdSsrSsrCcoGeneralDevelopers",
        source = "ssrCcoData.organizations",
        qualifiedByName = "toUgdSsrSsrCcoGeneralDevelopers"
    )
    @Mapping(
        target = "ugdSsrSsrCcoGeneralContractorExternalIds",
        source = "ssrCcoData.organizations",
        qualifiedByName = "toUgdSsrSsrCcoGeneralContractorExternalIds"
    )
    @Mapping(
        target = "ugdSsrSsrCcoGeneralDeveloperExternalIds",
        source = "ssrCcoData.organizations",
        qualifiedByName = "toUgdSsrSsrCcoGeneralDeveloperExternalIds"
    )
    @Mapping(
        target = "ugdSsrSsrCcoGeneralDeveloperEmployees",
        source = "ssrCcoData.employees",
        qualifiedByName = "toUgdSsrSsrCcoGeneralDeveloperEmployees"
    )
    @Mapping(
        target = "ugdSsrSsrCcoGeneralDeveloperEmployeeLogins",
        source = "ssrCcoData.employees",
        qualifiedByName = "toUgdSsrSsrCcoGeneralDeveloperEmployeeLogins"
    )
    @Mapping(
        target = "ugdSsrSsrCcoGeneralContractorEmployees",
        source = "ssrCcoData.employees",
        qualifiedByName = "toUgdSsrSsrCcoGeneralContractorEmployees"
    )
    @Mapping(
        target = "ugdSsrSsrCcoGeneralContractorEmployeeLogins",
        source = "ssrCcoData.employees",
        qualifiedByName = "toUgdSsrSsrCcoGeneralContractorEmployeeLogins"
    )
    @Mapping(target = "ugdSsrSsrCcoFlatCount", source = "ssrCcoData.flatCount")
    @Mapping(target = "ugdSsrSsrCcoFlatWithoutOpenActCount", source = "ssrCcoData.flatWithoutOpenActCount")
    @Mapping(target = "ugdSsrSsrCcoActCount", source = "ssrCcoData.actCount")
    @Mapping(target = "ugdSsrSsrCcoExpiredActCount", source = "ssrCcoData.expiredActCount")
    @Mapping(target = "ugdSsrSsrCcoActInWorkCount", source = "ssrCcoData.actInWorkCount")
    @Mapping(target = "ugdSsrSsrCcoDefectCount", source = "ssrCcoData.defectCount")
    @Mapping(target = "ugdSsrSsrCcoFixDefectCount", source = "ssrCcoData.fixDefectCount")
    @Mapping(target = "ugdSsrSsrCcoExpiredDefectCount", source = "ssrCcoData.expiredDefectCount")
    @Mapping(target = "ugdSsrSsrCcoFlatWithAgreementCount", source = "ssrCcoData.flatWithAgreementCount")
    @Mapping(target = "ugdSsrSsrCcoFlatWithContractCount", source = "ssrCcoData.flatWithContractCount")
    @Mapping(target = "ugdSsrSsrCcoFlatWithKeyIssueCount", source = "ssrCcoData.flatWithKeyIssueCount")
    @Mapping(target = "ugdSsrSsrCcoFlatToResettleCount", source = "ssrCcoData.flatToResettleCount")
    @Mapping(target = "ugdSsrSsrCcoFlatDgiCount", source = "ssrCcoData.flatDgiCount")
    @Mapping(target = "ugdSsrSsrCcoFlatMfrCount", source = "ssrCcoData.flatMfrCount")
    UgdSsrSsrCco toUgdSsrSsrCco(
        @MappingTarget final UgdSsrSsrCco ugdSsrSsrCco,
        final SsrCcoData ssrCcoData
    );

    @Named("toUgdSsrSsrCcoGeneralDevelopers")
    default List<String> toUgdSsrSsrCcoGeneralDevelopers(final List<SsrCcoOrganization> organizations) {
        return toOrganizationNames(organizations, DEVELOPER);
    }

    @Named("toUgdSsrSsrCcoGeneralContractors")
    default List<String> toUgdSsrSsrCcoGeneralContractors(final List<SsrCcoOrganization> organizations) {
        return toOrganizationNames(organizations, CONTRACTOR);
    }

    @Named("toUgdSsrSsrCcoGeneralDeveloperExternalIds")
    default List<String> toUgdSsrSsrCcoGeneralDeveloperExternalIds(final List<SsrCcoOrganization> organizations) {
        return toExternalIds(organizations, DEVELOPER);
    }

    @Named("toUgdSsrSsrCcoGeneralContractorExternalIds")
    default List<String> toUgdSsrSsrCcoGeneralContractorExternalIds(final List<SsrCcoOrganization> organizations) {
        return toExternalIds(organizations, CONTRACTOR);
    }

    default List<String> toOrganizationNames(
        final List<SsrCcoOrganization> organizations,
        final SsrCcoOrganizationType organizationType
    ) {
        return ofNullable(organizations)
            .map(List::stream)
            .orElseGet(Stream::empty)
            .filter(organization -> organization.getType() == organizationType.getTypeCode())
            .map(SsrCcoOrganization::getFullName)
            .collect(Collectors.toList());
    }

    default List<String> toExternalIds(
        final List<SsrCcoOrganization> organizations,
        final SsrCcoOrganizationType organizationType
    ) {
        return ofNullable(organizations)
            .map(List::stream)
            .orElseGet(Stream::empty)
            .filter(organization -> organization.getType() == organizationType.getTypeCode())
            .map(SsrCcoOrganization::getExternalId)
            .collect(Collectors.toList());
    }

    @Named("toUgdSsrSsrCcoGeneralDeveloperEmployees")
    default List<String> toUgdSsrSsrCcoGeneralDeveloperEmployees(final List<SsrCcoEmployee> employees) {
        return retrieveFullNamesByType(employees, DEVELOPER.getTypeCode());
    }

    @Named("toUgdSsrSsrCcoGeneralContractorEmployees")
    default List<String> toUgdSsrSsrCcoGeneralContractorEmployees(final List<SsrCcoEmployee> employees) {
        return retrieveFullNamesByType(employees, CONTRACTOR.getTypeCode());
    }

    default List<String> retrieveFullNamesByType(final List<SsrCcoEmployee> employees, final int typeCode) {
        return ofNullable(employees)
            .map(List::stream)
            .orElseGet(Stream::empty)
            .filter(ssrCcoEmployee -> ssrCcoEmployee.getType() == typeCode)
            .map(SsrCcoEmployee::getFullName)
            .collect(Collectors.toList());
    }

    @Named("toUgdSsrSsrCcoGeneralDeveloperEmployeeLogins")
    default List<String> toUgdSsrSsrCcoGeneralDeveloperEmployeeLogins(final List<SsrCcoEmployee> employees) {
        return retrieveLoginsByType(employees, DEVELOPER.getTypeCode());
    }

    @Named("toUgdSsrSsrCcoGeneralContractorEmployeeLogins")
    default List<String> toUgdSsrSsrCcoGeneralContractorEmployeeLogins(final List<SsrCcoEmployee> employees) {
        return retrieveLoginsByType(employees, CONTRACTOR.getTypeCode());
    }

    default List<String> retrieveLoginsByType(final List<SsrCcoEmployee> employees, final Integer typeCode) {
        return ofNullable(employees)
            .map(List::stream)
            .orElseGet(Stream::empty)
            .filter(ssrCcoEmployee -> ssrCcoEmployee.getType() == typeCode)
            .map(SsrCcoEmployee::getLogin)
            .collect(Collectors.toList());
    }

    @Named("toDistrictCode")
    default String toDistrictCode(final String district) {
        return DistrictCode.ofCcoName(district)
            .map(DistrictCode::getCode)
            .orElse(null);
    }
}
