package ru.croc.ugd.ssr.mapper;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import ru.croc.ugd.ssr.dayschedule.SlotType;
import ru.croc.ugd.ssr.dayschedule.WindowType;
import ru.croc.ugd.ssr.dayschedule.contractappointment.ContractAppointmentDayScheduleData;
import ru.croc.ugd.ssr.dto.RestDayScheduleDto;
import ru.croc.ugd.ssr.dto.RestDayScheduleSlotDto;
import ru.croc.ugd.ssr.model.contractappointment.ContractAppointmentDayScheduleDocument;

import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mapper(componentModel = "spring")
public interface ContractAppointmentDayScheduleMapper
    extends DayScheduleMapper<ContractAppointmentDayScheduleDocument> {

    @Mapping(target = "document.contractAppointmentDayScheduleData.cipId", source = "cipId")
    @Mapping(target = "document.contractAppointmentDayScheduleData.date", source = "date")
    @Mapping(target = "document.contractAppointmentDayScheduleData.slots.slot", source = "slots",
        qualifiedByName = "toDocumentSlotList")
    ContractAppointmentDayScheduleDocument toDayScheduleDocument(final RestDayScheduleDto dto);

    @Mapping(target = "document.contractAppointmentDayScheduleData.cipId", source = "dto.cipId")
    @Mapping(target = "document.contractAppointmentDayScheduleData.date", source = "dto.date")
    void toCopy(
        @MappingTarget final ContractAppointmentDayScheduleDocument document,
        final RestDayScheduleDto dto
    );

    @Mapping(target = "id", source = "id")
    @Mapping(target = "cipId", source = "document.contractAppointmentDayScheduleData.cipId")
    @Mapping(target = "date", source = "document.contractAppointmentDayScheduleData.date")
    @Mapping(
        target = "totalWindows",
        source = "document.contractAppointmentDayScheduleData.slots",
        qualifiedByName = "toRestContractAppointmentDayScheduleTotalWindows"
    )
    @Mapping(
        target = "totalBooked",
        source = "document.contractAppointmentDayScheduleData.slots",
        qualifiedByName = "toRestContractAppointmentDayScheduleTotalBooked"
    )
    @Mapping(
        target = "slots",
        source = "document.contractAppointmentDayScheduleData.slots",
        qualifiedByName = "toContractAppointmentDayScheduleRestSlotDtoList"
    )
    RestDayScheduleDto toRestDayScheduleDto(final ContractAppointmentDayScheduleDocument document);

    @Named("toContractAppointmentDayScheduleRestSlotDtoList")
    default List<RestDayScheduleSlotDto> toRestContractAppointmentDayScheduleSlotList(
        final ContractAppointmentDayScheduleData.Slots slots
    ) {
        return Optional.ofNullable(slots)
            .map(ContractAppointmentDayScheduleData.Slots::getSlot)
            .map(List::stream)
            .orElse(Stream.empty())
            .filter(Objects::nonNull)
            .map(this::toRestDayScheduleSlotDto)
            .collect(Collectors.toList());
    }

    @Named("toRestContractAppointmentDayScheduleTotalWindows")
    default int toRestContractAppointmentDayScheduleTotalWindows(
        final ContractAppointmentDayScheduleData.Slots slots
    ) {
        return (int) Optional.ofNullable(slots)
            .map(ContractAppointmentDayScheduleData.Slots::getSlot)
            .map(List::stream)
            .orElse(Stream.empty())
            .map(SlotType::getWindows)
            .map(SlotType.Windows::getWindow)
            .flatMap(List::stream)
            .map(WindowType::getWindowId)
            .filter(Objects::nonNull)
            .count();
    }

    @Named("toRestContractAppointmentDayScheduleTotalBooked")
    default int toRestContractAppointmentDayScheduleTotalBooked(
        final ContractAppointmentDayScheduleData.Slots slots
    ) {
        return (int) Optional.ofNullable(slots)
            .map(ContractAppointmentDayScheduleData.Slots::getSlot)
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
