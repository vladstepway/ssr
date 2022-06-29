package ru.croc.ugd.ssr.solr.converter.notaryapplication;

import static java.util.Optional.ofNullable;
import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import ru.croc.ugd.ssr.dto.notary.NotaryApplicationFlowStatus;
import ru.croc.ugd.ssr.notary.Apartment;
import ru.croc.ugd.ssr.notary.Apartments;
import ru.croc.ugd.ssr.notary.NotaryApplicationType;
import ru.croc.ugd.ssr.solr.UgdSsrNotaryApplication;
import ru.croc.ugd.ssr.utils.DateTimeUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface SolrNotaryApplicationMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(
        target = "ugdSsrNotaryApplicationNumber",
        source = "notaryApplicationType.eno"
    )
    @Mapping(
        target = "ugdSsrNotaryApplicationCreationDateTime",
        source = "notaryApplicationType.applicationDateTime"
    )
    @Mapping(
        target = "ugdSsrNotaryApplicationSource",
        source = "notaryApplicationType.source"
    )
    @Mapping(
        target = "ugdSsrNotaryApplicationPersonFullName",
        source = "notaryApplicationType.applicant.fullName"
    )
    @Mapping(
        target = "ugdSsrNotaryApplicationNotaryFullName",
        source = "notaryFullName"
    )
    @Mapping(
        target = "ugdSsrNotaryApplicationAppointmentDateTime",
        source = "notaryApplicationType.appointmentDateTime",
        qualifiedByName = "toFullDatetime"
    )
    @Mapping(
        target = "ugdSsrNotaryApplicationAddressFrom",
        source = "notaryApplicationType.apartmentFrom",
        qualifiedByName = "toAddressFrom"
    )
    @Mapping(
        target = "ugdSsrNotaryApplicationAddressTo",
        source = "notaryApplicationType.apartmentTo",
        qualifiedByName = "toAddressTo"
    )
    @Mapping(
        target = "ugdSsrNotaryApplicationStatus",
        source = "notaryApplicationType.statusId",
        qualifiedByName = "toNotaryApplicationStatusValue"
    )
    @Mapping(
        target = "ugdSsrNotaryApplicationEmployeeLogins",
        source = "notaryEmployeeLogins"
    )
    UgdSsrNotaryApplication toUgdSsrNotaryApplication(
        @MappingTarget final UgdSsrNotaryApplication ugdSsrNotaryApplication,
        final NotaryApplicationType notaryApplicationType,
        final String notaryFullName,
        final List<String> notaryEmployeeLogins
    );

    @Named("toFullDatetime")
    default String toFullDatetime(final LocalDateTime dateTime) {
        return DateTimeUtils.getFullDateWithTime(dateTime);
    }

    @Named("toNotaryApplicationStatusValue")
    default String toNotaryApplicationStatusValue(final String notaryStatusId) {
        return ofNullable(notaryStatusId)
            .map(NotaryApplicationFlowStatus::of)
            .map(NotaryApplicationFlowStatus::getSsrStatus)
            .orElse(null);
    }

    @Named("toAddressFrom")
    default String toAddressFrom(final Apartment addressFrom) {
        return ofNullable(addressFrom)
            .map(Apartment::getAddress)
            .map(address -> {
                if (Objects.nonNull(addressFrom.getFlatNumber())) {
                    return String.format("%s, кв. %s", address, addressFrom.getFlatNumber());
                }
                return address;
            })
            .orElse(null);
    }

    @Named("toAddressTo")
    default String toAddressTo(final Apartments addressTo) {
       return ofNullable(addressTo)
           .map(Apartments::getApartment)
           .map(List::stream)
           .orElse(Stream.empty())
           .map(this::toAddressFrom)
           .filter(Objects::nonNull)
           .collect(Collectors.joining(",\n"));
    }

}
