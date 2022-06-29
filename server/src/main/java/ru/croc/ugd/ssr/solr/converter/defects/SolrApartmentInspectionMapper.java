package ru.croc.ugd.ssr.solr.converter.defects;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static org.mapstruct.ReportingPolicy.IGNORE;

import org.apache.commons.lang.StringUtils;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import ru.croc.ugd.ssr.ApartmentEliminationDefectType;
import ru.croc.ugd.ssr.ApartmentInspectionType;
import ru.croc.ugd.ssr.DelayReasonData;
import ru.croc.ugd.ssr.FlatType;
import ru.croc.ugd.ssr.OrganizationInformation;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.RealEstateDataType;
import ru.croc.ugd.ssr.enums.DistrictCode;
import ru.croc.ugd.ssr.enums.SsrCcoOrganizationType;
import ru.croc.ugd.ssr.solr.UgdSsrApartmentInspection;
import ru.croc.ugd.ssr.ssrcco.SsrCcoData;
import ru.croc.ugd.ssr.ssrcco.SsrCcoEmployee;
import ru.croc.ugd.ssr.ssrcco.SsrCcoOrganization;
import ru.croc.ugd.ssr.utils.ApartmentInspectionUtils;
import ru.croc.ugd.ssr.utils.PersonUtils;
import ru.croc.ugd.ssr.utils.RealEstateUtils;
import ru.reinform.cdp.ldap.model.UserBean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mapper(componentModel = "spring", unmappedTargetPolicy = IGNORE)
public interface SolrApartmentInspectionMapper {

    @Mapping(
        target = "ugdSsrApartmentInspectionAddressFrom",
        expression = "java(toFullAddressFrom(realEstate, flat))"
    )
    @Mapping(
        target = "ugdSsrApartmentInspectionFlat",
        source = "apartmentInspection.flat"
    )
    @Mapping(
        target = "ugdSsrApartmentInspectionAddress",
        source = "apartmentInspection",
        qualifiedByName = "toFullAddress"
    )
    @Mapping(
        target = "ugdSsrApartmentInspectionPersonFio",
        source = "person.FIO"
    )
    @Mapping(
        target = "ugdSsrApartmentInspectionPlannedFixedDate",
        source = "apartmentInspection.delayReason",
        qualifiedByName = "toPlannedFixedDate"
    )
    @Mapping(
        target = "ugdSsrApartmentInspectionIsReady",
        source = "apartmentInspection.defectsEliminatedNotificationDate",
        qualifiedByName = "toIsReadyResult"
    )
    @Mapping(
        target = "ugdSsrApartmentInspectionActCreationDate",
        source = "apartmentInspection.filingDate",
        qualifiedByName = "toActCreationDate"
    )
    @Mapping(
        target = "ugdSsrApartmentInspectionHasConsent",
        source = "apartmentInspection.hasConsent"
    )
    @Mapping(
        target = "ugdSsrApartmentInspectionIsDeleted",
        source = "apartmentInspection.isRemoved"
    )
    @Mapping(
        target = "ugdSsrApartmentInspectionGeneralContractor",
        source = "apartmentInspection.generalContractors"
    )
    @Mapping(
        target = "ugdSsrApartmentInspectionDeveloper",
        source = "apartmentInspection.developers"
    )
    @Mapping(
        target = "ugdSsrApartmentInspectionPending",
        source = "apartmentInspection.pending",
        qualifiedByName = "toPending"
    )
    @Mapping(
        target = "ugdSsrApartmentInspectionArea",
        source = "realEstate.DISTRICT.name"
    )
    @Mapping(
        target = "ugdSsrApartmentInspectionDistrict",
        source = "realEstate.munOkrugP5.name"
    )
    @Mapping(
        target = "ugdSsrApartmentInspectionDistrictCode",
        source = "realEstate.munOkrugP5.name",
        qualifiedByName = "toDistrictCode"
    )
    @Mapping(
        target = "ugdSsrApartmentInspectionHasDefects",
        source = "apartmentInspection.hasDefects"
    )
    @Mapping(
        target = "ugdSsrApartmentInspectionIsDefectsAccepted",
        source = "apartmentInspection.acceptedDefectsDate",
        qualifiedByName = "toIsDefectsAccepted"
    )
    @Mapping(
        target = "ugdSsrApartmentInspectionResponsibleEmployee",
        source = "ssrCco.employees",
        qualifiedByName = "toResponsibleEmployee"
    )
    @Mapping(
        target = "ugdSsrApartmentInspectionOrganizationName",
        source = "ssrCco.organizations",
        qualifiedByName = "toOrganizationName"
    )
    @Mapping(
        target = "ugdSsrApartmentInspectionActNumber",
        source = "apartmentInspection.actNum"
    )
    @Mapping(
        target = "ugdSsrApartmentInspectionSignedActFileId",
        source = "apartmentInspection.signedActFileId"
    )
    @Mapping(
        target = "ugdSsrApartmentInspectionDefects",
        source = "apartmentInspection.apartmentDefects",
        qualifiedByName = "toDefects"
    )
    @Mapping(
        target = "ugdSsrApartmentInspectionExcludedDefects",
        source = "apartmentInspection.excludedApartmentDefects",
        qualifiedByName = "toExcludedDefects"
    )
    @Mapping(
        target = "ugdSsrApartmentInspectionDefectExclusionReason",
        source = "apartmentInspection.defectExclusionReason"
    )
    @Mapping(
        target = "ugdSsrApartmentInspectionIsKpUgs",
        source = "apartmentInspection.developers",
        qualifiedByName = "toIsKpUgs"
    )
    @Mapping(
        target = "ugdSsrApartmentInspectionWarrantyPeriod",
        expression = "java( toWarrantyPeriod(apartmentInspection, person) )"
    )
    @Mapping(
        target = "ugdSsrApartmentInspectionAssignedGeneralContractor",
        source = "apartmentInspection.generalContractors",
        qualifiedByName = "toAssignedGeneralContractor"
    )
    @Mapping(
        target = "ugdSsrApartmentInspectionUnom",
        source = "apartmentInspection.unom"
    )
    @Mapping(
        target = "ugdSsrApartmentInspectionClosingInitiatorFio",
        source = "closingInitiator.displayName"
    )
    @Mapping(
        target = "ugdSsrApartmentInspectionClosingInitiatorOrganizationName",
        source = "closingInitiator.departmentFullName"
    )
    UgdSsrApartmentInspection toUgdSsrApartmentInspection(
        @MappingTarget final UgdSsrApartmentInspection solrApartmentInspection,
        final ApartmentInspectionType apartmentInspection,
        final RealEstateDataType realEstate,
        final FlatType flat,
        final PersonType person,
        final SsrCcoData ssrCco,
        final UserBean closingInitiator,
        @Context final String kpUgsInn
    );

    default String map(List<OrganizationInformation> value) {
        return ApartmentInspectionUtils.joinOrgInfoFullName(value);
    }

    @Named("toPlannedFixedDate")
    default LocalDate toPlannedFixedDate(final List<DelayReasonData> delayReason) {
        if (delayReason.isEmpty()) {
            return null;
        }

        return ofNullable(delayReason.get(delayReason.size() - 1))
            .map(DelayReasonData::getDelayDate)
            .map(LocalDateTime::toLocalDate)
            .orElse(null);
    }

    @Named("toIsReadyResult")
    default Boolean toIsReadyResult(final LocalDateTime defectsEliminatedNotificationDate) {
        return defectsEliminatedNotificationDate != null;
    }

    @Named("toActCreationDate")
    default LocalDate toActCreationDate(final LocalDateTime filingDate) {
        return ofNullable(filingDate)
            .map(LocalDateTime::toLocalDate)
            .orElse(null);
    }

    @Named("toFullAddress")
    default String toFullAddress(final ApartmentInspectionType apartmentInspection) {
        return ofNullable(apartmentInspection)
            .map(ApartmentInspectionType::getAddress)
            .map(address -> address.concat(
                ofNullable(apartmentInspection.getFlat())
                    .map(flatNumber -> ", квартира " + flatNumber)
                    .orElse("")
            ))
            .orElse(null);
    }

    @Named("toFullFromAddress")
    default String toFullAddressFrom(final RealEstateDataType realEstate, final FlatType flat) {
        return RealEstateUtils.getFlatAddress(realEstate, flat);
    }

    @Named("toDistrictCode")
    default String toDistrictCode(final String munOkrugP5Name) {
        return DistrictCode.ofName(munOkrugP5Name)
            .map(DistrictCode::getCode)
            .orElse(null);
    }

    @Named("toPending")
    default boolean toPending(final Boolean pending) {
        return ofNullable(pending).orElse(false);
    }

    @Named("toIsDefectsAccepted")
    default boolean toIsDefectsAccepted(final LocalDateTime acceptedDefectsDate) {
        return acceptedDefectsDate != null;
    }

    @Named("toResponsibleEmployee")
    default String toResponsibleEmployee(final List<SsrCcoEmployee> employees) {
        return ofNullable(employees)
            .map(Collection::stream)
            .orElse(Stream.empty())
            .filter(Objects::nonNull)
            .filter(e -> SsrCcoOrganizationType.ofTypeCode(e.getType()) == SsrCcoOrganizationType.DEVELOPER)
            .filter(this::isEmployeeActive)
            .map(SsrCcoEmployee::getFullName)
            .filter(Objects::nonNull)
            .collect(Collectors.joining(", "));
    }

    @Named("toOrganizationName")
    default String toOrganizationName(final List<SsrCcoOrganization> organizations) {
        return ofNullable(organizations)
            .map(Collection::stream)
            .orElse(Stream.empty())
            .filter(Objects::nonNull)
            .filter(o -> SsrCcoOrganizationType.ofTypeCode(o.getType()) == SsrCcoOrganizationType.DEVELOPER)
            .map(SsrCcoOrganization::getFullName)
            .filter(Objects::nonNull)
            .collect(Collectors.joining(", "));
    }

    default boolean isEmployeeActive(final SsrCcoEmployee ssrCcoEmployee) {
        return (isNull(ssrCcoEmployee.getPeriodFrom())
            || !ssrCcoEmployee.getPeriodFrom().isAfter(LocalDate.now()))
            && (isNull(ssrCcoEmployee.getPeriodTo())
            || !ssrCcoEmployee.getPeriodTo().isBefore(LocalDate.now()));
    }

    @Named("toDefects")
    default List<String> toDefects(final List<ApartmentInspectionType.ApartmentDefects> apartmentDefects) {
        final List<ApartmentEliminationDefectType> defects = apartmentDefects.stream()
            .map(ApartmentInspectionType.ApartmentDefects::getApartmentDefectData)
            .collect(Collectors.toList());
        return retrieveDefectStrings(defects);
    }

    @Named("toExcludedDefects")
    default List<String> toExcludedDefects(
        final List<ApartmentInspectionType.ExcludedApartmentDefects> excludedApartmentDefects
    ) {
        final List<ApartmentEliminationDefectType> defects = excludedApartmentDefects.stream()
            .map(ApartmentInspectionType.ExcludedApartmentDefects::getApartmentDefectData)
            .collect(Collectors.toList());
        return retrieveDefectStrings(defects);
    }

    default List<String> retrieveDefectStrings(final List<ApartmentEliminationDefectType> apartmentDefects) {
        return apartmentDefects.stream()
            .collect(Collectors.groupingBy(
                ApartmentEliminationDefectType::getFlatElement,
                Collectors.mapping(ApartmentEliminationDefectType::getDescription, Collectors.toList())
            ))
            .entrySet()
            .stream()
            .map(entry -> retrieveDefectString(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
    }

    default String retrieveDefectString(final String flatElement, final List<String> descriptions) {
        final String description = String.join("; ", descriptions);
        return String.join(": ", flatElement, description);
    }

    @Named("toIsKpUgs")
    default boolean toIsKpUgs(final List<OrganizationInformation> developers, @Context final String kpUgsInn) {
        return developers
            .stream()
            .map(OrganizationInformation::getExternalId)
            .anyMatch(orgInn -> StringUtils.equals(kpUgsInn, orgInn));
    }

    default boolean toWarrantyPeriod(final ApartmentInspectionType apartmentInspection, final PersonType person) {
        final String unom = ofNullable(apartmentInspection)
            .map(ApartmentInspectionType::getUnom)
            .orElse(null);
        final String flat = ofNullable(apartmentInspection)
            .map(ApartmentInspectionType::getFlat)
            .orElse(null);
        if (isNull(unom) || isNull(flat)) {
            return false;
        }
        return PersonUtils.getNewFlatByUnomAndFlatNumber(person, unom, flat)
            .flatMap(newFlat -> PersonUtils.getContractByNewFlat(person, newFlat))
            .map(PersonType.Contracts.Contract::getContractSignDate)
            .isPresent();
    }

    @Named("toAssignedGeneralContractor")
    default String toAssignedGeneralContractor(final List<OrganizationInformation> generalContractors) {
        return generalContractors.stream()
            .filter(OrganizationInformation::isIsAssigned)
            .findFirst()
            .map(OrganizationInformation::getOrgFullName)
            .orElse(null);
    }
}
