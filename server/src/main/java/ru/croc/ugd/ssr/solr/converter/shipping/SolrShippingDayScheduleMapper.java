package ru.croc.ugd.ssr.solr.converter.shipping;

import static org.mapstruct.ReportingPolicy.ERROR;
import static ru.croc.ugd.ssr.utils.LocaleUtils.ru_RU;

import org.apache.logging.log4j.util.Strings;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.util.CollectionUtils;
import ru.croc.ugd.ssr.dayschedule.BookingSlotType;
import ru.croc.ugd.ssr.dayschedule.TimeIntervalType;
import ru.croc.ugd.ssr.shipping.BrigadeType;
import ru.croc.ugd.ssr.shipping.ShippingDayScheduleType;
import ru.croc.ugd.ssr.solr.UgdSsrShippingDaySchedule;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface SolrShippingDayScheduleMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(
        target = "ugdSsrShippingDayScheduleArea",
        source = "shippingDayScheduleData.area"
    )
    @Mapping(
        target = "ugdSsrShippingDayScheduleWorkingdate",
        source = "shippingDayScheduleData.shippingDate"
    )
    @Mapping(
        target = "ugdSsrShippingDayScheduleWeekday",
        source = "shippingDayScheduleData.shippingDate",
        qualifiedByName = "toWeekday"
    )
    @Mapping(
        target = "ugdSsrShippingDayScheduleSchedule",
        source = "shippingDayScheduleData.brigades",
        qualifiedByName = "toWorkingInterval"
    )
    @Mapping(
        target = "ugdSsrShippingDayScheduleBrigadesAmount",
        source = "shippingDayScheduleData.brigades",
        qualifiedByName = "toBrigadesAmount"
    )
    @Mapping(
        target = "ugdSsrShippingDayScheduleTotalApplied",
        source = "shippingDayScheduleData.brigades",
        qualifiedByName = "toTotalAppliedPersonNumber"
    )
    UgdSsrShippingDaySchedule toUgdSsrShippingDaySchedule(
        @MappingTarget final UgdSsrShippingDaySchedule solrShippingDaySchedule,
        final ShippingDayScheduleType shippingDayScheduleData
    );

    @Named("toWeekday")
    default String toWeekday(final LocalDate shippingDate) {
        return DateTimeFormatter.ofPattern("EEEE")
            .withLocale(ru_RU)
            .format(shippingDate);
    }

    @Named("toWorkingInterval")
    default String toWorkingInterval(final List<BrigadeType> brigades) {
        if (CollectionUtils.isEmpty(brigades)) {
            return Strings.EMPTY;
        }
        final String minDate = brigades
            .stream()
            .map(BrigadeType::getWorkingIntervals)
            .flatMap(List::stream)
            .map(TimeIntervalType::getStart)
            .min(LocalTime::compareTo)
            .map(localTime -> localTime.format(DateTimeFormatter.ofPattern("HH:mm")))
            .orElse("N/A");

        final String maxDate = brigades
            .stream()
            .map(BrigadeType::getWorkingIntervals)
            .flatMap(List::stream)
            .map(TimeIntervalType::getEnd)
            .max(LocalTime::compareTo)
            .map(localTime -> localTime.format(DateTimeFormatter.ofPattern("HH:mm")))
            .orElse("N/A");

        return String.format("%s - %s", minDate, maxDate);
    }

    @Named("toTotalAppliedPersonNumber")
    default Long toTotalAppliedPersonNumber(final List<BrigadeType> brigades) {
        return brigades.stream()
            .map(BrigadeType::getBookingSlots)
            .flatMap(List::stream)
            .filter(bookingSlot -> Objects.isNull(bookingSlot.getPreBookedUntil())
                && Objects.nonNull(bookingSlot.getPreBookingId()))
            .map(BookingSlotType::getPreBookingId)
            .distinct()
            .count();
    }

    @Named("toBrigadesAmount")
    default Long toBrigadesAmount(final List<BrigadeType> brigades) {
        return (long) brigades.size();
    }
}
