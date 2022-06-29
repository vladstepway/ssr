package ru.croc.ugd.ssr.mapper;

import static java.util.Objects.nonNull;
import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.croc.ugd.ssr.DayScheduleEvents;
import ru.croc.ugd.ssr.dto.RestDaySchedulePeriodDto;

import java.time.LocalDate;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface DayScheduleEventMapper {

    @Mapping(target = "code", source = "code")
    @Mapping(target = "date", source = "date")
    @Mapping(target = "dateFrom", ignore = true)
    @Mapping(target = "dateTo", ignore = true)
    @Mapping(target = "dayRepeatNumber", ignore = true)
    @Mapping(target = "weekRepeatNumber", ignore = true)
    @Mapping(target = "weekdays", ignore = true)
    DayScheduleEvents.DayScheduleEvent toDayScheduleEvent(
        final String code, final LocalDate date
    );

    @Mapping(target = "code", source = "code")
    @Mapping(target = "dateFrom", source = "periodDto.from")
    @Mapping(target = "dateTo", source = "periodDto.to")
    @Mapping(target = "dayRepeatNumber", source = "periodDto.dayRepeatNumber")
    @Mapping(target = "weekRepeatNumber", source = "periodDto.weekRepeatNumber")
    @Mapping(target = "weekdays", source = "periodDto.weekdays", qualifiedByName = "toWeekdays")
    @Mapping(target = "date", source = "date")
    DayScheduleEvents.DayScheduleEvent toDayScheduleEvent(
        final String code, final LocalDate date, final RestDaySchedulePeriodDto periodDto
    );

    @Named("toWeekdays")
    default DayScheduleEvents.DayScheduleEvent.Weekdays toWeekdays(final List<String> list) {
        final DayScheduleEvents.DayScheduleEvent.Weekdays weekdays =
            new DayScheduleEvents.DayScheduleEvent.Weekdays();
        if (nonNull(list)) {
            weekdays.getWeekday().addAll(list);
        }
        return weekdays;
    }
}
