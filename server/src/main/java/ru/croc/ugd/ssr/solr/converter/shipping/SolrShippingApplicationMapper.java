package ru.croc.ugd.ssr.solr.converter.shipping;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import ru.croc.ugd.ssr.MessageType;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.shipping.ShippingApplicationType;
import ru.croc.ugd.ssr.solr.UgdSsrShippingApplication;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface SolrShippingApplicationMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(
        target = "ugdSsrShippingApplicationAddressFrom",
        source = "applicationData.apartmentFrom.stringAddress"
    )
    @Mapping(
        target = "ugdSsrShippingApplicationAddressTo",
        source = "applicationData.apartmentTo.stringAddress"
    )
    @Mapping(
        target = "ugdSsrShippingApplicationEno",
        source = "applicationData.eno"
    )
    @Mapping(
        target = "ugdSsrShippingApplicationFullName",
        source = "applicationData.applicant.fullName"
    )
    @Mapping(
        target = "ugdSsrShippingApplicationMoveDateTime",
        source = "applicationData.shippingDateTimeInfo"
    )
    @Mapping(
        target = "ugdSsrShippingApplicationStatus",
        source = "applicationData.status"
    )
    @Mapping(
        target = "ugdSsrShippingApplicationSource",
        source = "applicationData.source"
    )
    @Mapping(
        target = "ugdSsrShippingApplicationDistrict",
        source = "applicationData.district"
    )
    @Mapping(
        target = "ugdSsrShippingApplicationBrigade",
        source = "applicationData.brigade"
    )
    @Mapping(
        target = "ugdSsrShippingApplicationShippingDate",
        source = "applicationData.shippingDateStart",
        qualifiedByName = "toLocalDate"
    )
    @Mapping(
        target = "ugdSsrShippingApplicationCreatedAt",
        source = "applicationData.creationDate",
        qualifiedByName = "toLocalDate"
    )
    @Mapping(
        target = "ugdSsrShippingApplicationMessageEnoList",
        source = "person",
        qualifiedByName = "toMessageEnoList"
    )
    UgdSsrShippingApplication toUgdSsrShippingApplication(
        @MappingTarget final UgdSsrShippingApplication solrShippingApplication,
        final ShippingApplicationType applicationData,
        final PersonType person
    );

    @Named("toLocalDate")
    default LocalDate toLocalDate(final LocalDateTime localDateTime) {
        return ofNullable(localDateTime)
            .map(LocalDateTime::toLocalDate)
            .orElse(null);
    }

    @Named("toMessageEnoList")
    default String toMessageEnoList(final PersonType person) {
        return ofNullable(person)
            .map(PersonType::getSendedMessages)
            .map(PersonType.SendedMessages::getMessage)
            .map(List::stream)
            .orElse(Stream.empty())
            .filter(msg -> nonNull(msg.getBusinessType())
                && msg.getBusinessType().toLowerCase().contains("переезд"))
            .map(MessageType::getEno)
            .distinct()
            .collect(Collectors.joining(","));
    }
}
