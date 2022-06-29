package ru.croc.ugd.ssr.utils;

import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

import org.springframework.util.CollectionUtils;
import ru.croc.ugd.ssr.ApartmentInspectionType;
import ru.croc.ugd.ssr.DelayReasonData;
import ru.croc.ugd.ssr.OrganizationInformation;
import ru.croc.ugd.ssr.model.ApartmentInspectionDocument;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Утилита для ApartmentInspection.
 */
public class ApartmentInspectionUtils {

    /**
     * Joins with \n fullName of each org.
     *
     * @param organizationInformationList list of orgInformation.
     * @return joined string.
     */
    public static String joinOrgInfoFullName(final List<OrganizationInformation> organizationInformationList) {
        if (CollectionUtils.isEmpty(organizationInformationList)) {
            return null;
        }
        return ofNullable(organizationInformationList)
            .map(List::stream)
            .orElse(Stream.empty())
            .filter(StreamUtils.distinctByKey(OrganizationInformation::getOrgFullName))
            .map(OrganizationInformation::getOrgFullName)
            .collect(Collectors.joining("\n"));
    }

    /**
     * Returns list of distinct  organisation full names.
     * @param organizationInformationList organizationInformationList
     * @return list of distinct  organisation full names
     */
    public static List<String> getDistinctOrgInfoFullName(
        final List<OrganizationInformation> organizationInformationList
    ) {
        return ofNullable(organizationInformationList)
            .map(List::stream)
            .orElse(Stream.empty())
            .filter(StreamUtils.distinctByKey(OrganizationInformation::getOrgFullName))
            .map(OrganizationInformation::getOrgFullName)
            .collect(Collectors.toList());
    }

    public static Optional<LocalDate> getLastDelayDate(final ApartmentInspectionDocument apartmentInspectionDocument) {
        return of(apartmentInspectionDocument.getDocument().getApartmentInspectionData())
            .flatMap(ApartmentInspectionUtils::getLastDelayDate);
    }

    public static Optional<LocalDate> getLastDelayDate(final ApartmentInspectionType apartmentInspection) {
        return ofNullable(apartmentInspection.getDelayReason())
            .map(Collection::stream)
            .orElse(Stream.empty())
            .map(DelayReasonData::getDelayDate)
            .reduce((first, second) -> second)
            .map(LocalDateTime::toLocalDate);
    }
}
