package ru.croc.ugd.ssr.mapper;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import ru.croc.ugd.ssr.dayschedule.SlotType;
import ru.croc.ugd.ssr.dayschedule.WindowType;
import ru.croc.ugd.ssr.dayschedule.flatappointment.FlatAppointmentDayScheduleData;
import ru.croc.ugd.ssr.dto.RestDayScheduleDto;
import ru.croc.ugd.ssr.dto.RestDayScheduleSlotDto;
import ru.croc.ugd.ssr.model.flatAppointment.FlatAppointmentDayScheduleDocument;

import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mapper(componentModel = "spring")
public interface FlatAppointmentDayScheduleMapper
    extends DayScheduleMapper<FlatAppointmentDayScheduleDocument> {

    @Mapping(target = "document.flatAppointmentDayScheduleData.cipId", source = "cipId")
    @Mapping(target = "document.flatAppointmentDayScheduleData.date", source = "date")
    @Mapping(target = "document.flatAppointmentDayScheduleData.slots.slot", source = "slots",
        qualifiedByName = "toDocumentSlotList")
    FlatAppointmentDayScheduleDocument toDayScheduleDocument(final RestDayScheduleDto dto);

    @Mapping(target = "document.flatAppointmentDayScheduleData.cipId", source = "dto.cipId")
    @Mapping(target = "document.flatAppointmentDayScheduleData.date", source = "dto.date")
    void toCopy(
        @MappingTarget final FlatAppointmentDayScheduleDocument document,
        final RestDayScheduleDto dto
    );

    @Mapping(target = "id", source = "id")
    @Mapping(target = "cipId", source = "document.flatAppointmentDayScheduleData.cipId")
    @Mapping(target = "date", source = "document.flatAppointmentDayScheduleData.date")
    @Mapping(
        target = "totalWindows",
        source = "document.flatAppointmentDayScheduleData.slots",
        qualifiedByName = "toRestFlatAppointmentDayScheduleTotalWindows"
    )
    @Mapping(
        target = "totalBooked",
        source = "document.flatAppointmentDayScheduleData.slots",
        qualifiedByName = "toRestFlatAppointmentDayScheduleTotalBooked"
    )
    @Mapping(
        target = "slots",
        source = "document.flatAppointmentDayScheduleData.slots",
        qualifiedByName = "toFlatAppointmentDayScheduleSlotDtoList"
    )
    RestDayScheduleDto toRestDayScheduleDto(final FlatAppointmentDayScheduleDocument document);

    @Named("toFlatAppointmentDayScheduleSlotDtoList")
    default List<RestDayScheduleSlotDto> toRestFlatAppointmentayScheduleSlotDtoList(
        final FlatAppointmentDayScheduleData.Slots slots
    ) {
        return Optional.ofNullable(slots)
            .map(FlatAppointmentDayScheduleData.Slots::getSlot)
            .map(List::stream)
            .orElse(Stream.empty())
            .filter(Objects::nonNull)
            .map(this::toRestDayScheduleSlotDto)
            .collect(Collectors.toList());
    }

    @Named("toRestFlatAppointmentDayScheduleTotalWindows")
    default int toRestFlatAppointmentDayScheduleTotalWindows(
        final FlatAppointmentDayScheduleData.Slots slots
    ) {
        return (int) Optional.ofNullable(slots)
            .map(FlatAppointmentDayScheduleData.Slots::getSlot)
            .map(List::stream)
            .orElse(Stream.empty())
            .map(SlotType::getWindows)
            .map(SlotType.Windows::getWindow)
            .flatMap(List::stream)
            .map(WindowType::getWindowId)
            .filter(Objects::nonNull)
            .count();
    }

    @Named("toRestFlatAppointmentDayScheduleTotalBooked")
    default int toRestFlatAppointmentDayScheduleTotalBooked(
        final FlatAppointmentDayScheduleData.Slots slots
    ) {
        return (int) Optional.ofNullable(slots)
            .map(FlatAppointmentDayScheduleData.Slots::getSlot)
            .map(List::stream)
            .orElse(Stream.empty())
            .map(SlotType::getWindows)
            .map(SlotType.Windows::getWindow)
            .flatMap(List::stream)
            .filter(window -> nonNull(window.getPreBookingId())
                && isNull(window.getPreBookedUntil())
                || !window.getPrematurelyCompletedApplicationIds().isEmpty()
            )
            .count();
    }

    default RestDayScheduleSlotDto toRestDayScheduleSlotDto(final SlotType slot) {
        return RestDayScheduleSlotDto
            .builder()
            .slotId(slot.getSlotId())
            .timeFrom(slot.getTimeFrom())
            .timeTo(slot.getTimeTo())
            .totalWindows(
                (int) Optional.ofNullable(slot.getWindows())
                    .map(SlotType.Windows::getWindow)
                    .map(List::stream)
                    .orElse(Stream.empty())
                    .filter(Objects::nonNull)
                    .count()
            )
            .totalBooked(
                (int) Optional.ofNullable(slot.getWindows())
                    .map(SlotType.Windows::getWindow)
                    .map(List::stream)
                    .orElse(Stream.empty())
                    .filter(window -> nonNull(window.getPreBookingId())
                        && isNull(window.getPreBookedUntil())
                        || !window.getPrematurelyCompletedApplicationIds().isEmpty()
                    )
                    .count()
            )
            .build();
    }

    @Named("toDocumentSlotList")
    default List<SlotType> toDocumentSlotList(
        final List<RestDayScheduleSlotDto> slots
    ) {
        return Optional.ofNullable(slots)
            .map(List::stream)
            .orElse(Stream.empty())
            .filter(Objects::nonNull)
            .map(this::toDocumentSlot)
            .collect(Collectors.toList());
    }

    default SlotType toDocumentSlot(final RestDayScheduleSlotDto slot) {
        final SlotType slotType = new SlotType();
        final SlotType.Windows windows = new SlotType.Windows();
        final List<WindowType> window = windows.getWindow();

        while (slot.getTotalWindows() > window.size()) {
            WindowType windowType = new WindowType();
            windowType.setWindowId(UUID.randomUUID().toString());
            window.add(windowType);
        }

        final LocalTime to = Optional.ofNullable(slot.getTimeTo())
            .orElse(
                Optional.ofNullable(slot.getTimeFrom())
                    .map(from -> from.plusMinutes(30))
                    .orElse(null)
            );
        slotType.setSlotId(UUID.randomUUID().toString());
        slotType.setTimeFrom(slot.getTimeFrom());
        slotType.setTimeTo(to);
        slotType.setWindows(windows);
        return slotType;
    }
}
