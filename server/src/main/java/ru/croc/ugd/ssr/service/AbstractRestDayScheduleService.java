package ru.croc.ugd.ssr.service;

import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import ru.croc.ugd.ssr.dayschedule.SlotType;
import ru.croc.ugd.ssr.dayschedule.WindowType;
import ru.croc.ugd.ssr.dto.RestDayScheduleDto;
import ru.croc.ugd.ssr.dto.RestDaySchedulePeriodDto;
import ru.croc.ugd.ssr.dto.RestDayScheduleSlotDto;
import ru.croc.ugd.ssr.exception.SsrException;
import ru.croc.ugd.ssr.mapper.DayScheduleMapper;
import ru.croc.ugd.ssr.model.DayScheduleDocumentAbstract;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * DefaultRestDayScheduleService.
 */
@AllArgsConstructor
public abstract class AbstractRestDayScheduleService<T extends DayScheduleDocumentAbstract>
    implements RestDayScheduleService<T> {

    private final AbstractDayScheduleDocumentService<T> dayScheduleDocumentService;
    private final DayScheduleMapper<T> dayScheduleMapper;

    @Override
    public List<RestDayScheduleDto> findAll(
        final LocalDate from, final LocalDate to, final String parentDocumentId, final boolean isFreeOnly
    ) {
        return dayScheduleDocumentService.fetchAllByDateAndParentDocumentId(from, to, parentDocumentId, isFreeOnly)
            .stream()
            .map(this::sortSlots)
            .map(dayScheduleMapper::toRestDayScheduleDto)
            .collect(Collectors.toList());
    }

    private T sortSlots(final T document) {
        document.getSlotList()
            .sort(Comparator.comparing(SlotType::getTimeFrom));
        return document;
    }

    @Override
    public RestDayScheduleDto create(final RestDayScheduleDto restDayScheduleDto) {
        final T dayScheduleDocument = dayScheduleMapper.toDayScheduleDocument(restDayScheduleDto);

        final RestDayScheduleDto dto = of(dayScheduleDocumentService.createDocument(dayScheduleDocument, true, null))
            .map(dayScheduleMapper::toRestDayScheduleDto)
            .orElseThrow(SsrException::new);
        afterCreate(restDayScheduleDto.getCipId(), restDayScheduleDto.getDate());
        return dto;
    }

    @Override
    public RestDayScheduleDto update(final String id, final RestDayScheduleDto restDayScheduleDto) {
        final T dayScheduleDocument = dayScheduleDocumentService.fetchDocument(id);

        if (restDayScheduleDto == null) {
            throw new SsrException("Пустой запрос на обновление");
        }

        dayScheduleMapper.toCopy(dayScheduleDocument, restDayScheduleDto);

        final List<SlotType> slotList = dayScheduleDocument.createSlotList();

        final Set<String> idList = ofNullable(restDayScheduleDto.getSlots())
            .map(List::stream)
            .orElse(Stream.empty())
            .map(RestDayScheduleSlotDto::getSlotId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        slotList.removeIf(slot -> {
            if (idList.contains(slot.getSlotId())) {
                return false;
            }

            final long bookingCount = ofNullable(slot.getWindows())
                .map(SlotType.Windows::getWindow)
                .map(List::stream)
                .orElse(Stream.empty())
                .map(this::getBookedDocumentId)
                .filter(Objects::nonNull)
                .count();

            if (bookingCount > 0) {
                throw new SsrException("Невозможно удалить слот с забронированными окнами");
            }

            return true;
        });

        ofNullable(restDayScheduleDto.getSlots())
            .map(List::stream)
            .orElse(Stream.empty())
            .filter(Objects::nonNull)
            .forEach(slotDto -> {
                final SlotType slot = slotList.stream()
                    .filter(it -> slotDto.getSlotId() != null && slotDto.getSlotId().equals(it.getSlotId()))
                    .findAny()
                    .orElse(null);

                if (slot == null) {
                    slotList.add(dayScheduleMapper.toDocumentSlot(slotDto));
                } else {
                    if (slotDto.getTimeFrom() != null) {
                        slot.setTimeFrom(slotDto.getTimeFrom());
                    }

                    if (slotDto.getTimeTo() != null) {
                        slot.setTimeTo(slotDto.getTimeTo());
                    }

                    if (slot.getTimeTo() == null && slot.getTimeFrom() != null) {
                        slot.setTimeTo(slot.getTimeFrom().plusMinutes(getSlotMinutes()));
                    }

                    if (slot.getWindows() == null) {
                        slot.setWindows(new SlotType.Windows());
                    }

                    final List<WindowType> window = slot.getWindows().getWindow();

                    while (slotDto.getTotalWindows() > window.size()) {
                        WindowType windowType = new WindowType();
                        windowType.setWindowId(UUID.randomUUID().toString());
                        window.add(windowType);
                    }

                    int currentIndex = 0;
                    while (slotDto.getTotalWindows() < window.size() && currentIndex < window.size()) {
                        if (getBookedDocumentId(window.get(currentIndex)) == null) {
                            window.remove(currentIndex);
                        } else {
                            currentIndex++;
                        }
                    }

                    if (slotDto.getTotalWindows() < window.size()) {
                        throw new SsrException("Невозможно установить количество окон"
                            + " меньше количества забронированных окон");
                    }
                }
            });

        final RestDayScheduleDto dto = of(dayScheduleDocumentService.updateDocument(
            id, dayScheduleDocument, true, true, null
        ))
            .map(dayScheduleMapper::toRestDayScheduleDto)
            .orElseThrow(SsrException::new);
        afterUpdate(restDayScheduleDto.getCipId(), restDayScheduleDto.getDate());
        return dto;
    }

    @Override
    public void delete(final String id) {
        final T dayScheduleDocument = dayScheduleDocumentService.fetchDocument(id);

        final long count = dayScheduleDocument.getBookedSlotsCount();

        if (count > 0) {
            throw new SsrException("Невозможно удалить рабочий день с забронированными окнами");
        }

        dayScheduleDocumentService.deleteDocument(id, true, null);
        afterDelete(dayScheduleDocument.getParentDocumentId(), dayScheduleDocument.getDate());
    }

    @Override
    public List<RestDayScheduleDto> copyDaySchedules(final String id, final RestDaySchedulePeriodDto periodDto) {
        final T dayScheduleDocument = dayScheduleDocumentService.fetchDocument(id);
        final RestDayScheduleDto restDayScheduleDtoToCopy = dayScheduleMapper
            .toRestDayScheduleDto(dayScheduleDocument);
        final String parentDocumentId = ofNullable(dayScheduleDocument.getParentDocumentId())
            .orElseGet(() -> this.getParentDocumentId(periodDto));

        final List<RestDayScheduleDto> existingDayScheduleList = findAll(
            periodDto.getFrom(), periodDto.getTo(), parentDocumentId, false
        );

        final Map<LocalDate, RestDayScheduleDto> existingDayScheduleToDateMap = existingDayScheduleList.stream()
            .collect(Collectors.toMap(
                RestDayScheduleDto::getDate,
                Function.identity(),
                (r1, r2) -> r1)
            );

        final Set<LocalDate> datesFromRequest = getMatchDatesFromRequest(periodDto);

        final List<RestDayScheduleDto> restDayScheduleDtoList = datesFromRequest.stream()
            .map(dateFromRequest -> {
                final RestDayScheduleDto existingDayScheduleDto = existingDayScheduleToDateMap.get(dateFromRequest);

                if (existingDayScheduleDto != null && existingDayScheduleDto.getTotalBooked() != 0) {
                    return existingDayScheduleDto;
                }

                if (existingDayScheduleDto != null && existingDayScheduleDto.getTotalBooked() == 0) {
                    dayScheduleDocumentService.deleteDocument(existingDayScheduleDto.getId(), true, null);
                }

                final T newDayScheduleDocument = dayScheduleMapper.toDayScheduleDocument(restDayScheduleDtoToCopy);
                newDayScheduleDocument.setDate(dateFromRequest);
                dayScheduleDocumentService.createDocument(newDayScheduleDocument, true, null);

                return dayScheduleMapper.toRestDayScheduleDto(newDayScheduleDocument)
                    .toBuilder()
                    .isCopied(true)
                    .build();
            })
            .collect(Collectors.toList());

        afterCopy(parentDocumentId, restDayScheduleDtoToCopy.getDate(), periodDto);

        return restDayScheduleDtoList;
    }

    @Override
    public List<RestDayScheduleDto> deleteDaySchedules(final RestDaySchedulePeriodDto periodDto) {
        final List<RestDayScheduleDto> dayScheduleList = findAll(
            periodDto.getFrom(), periodDto.getTo(), getParentDocumentId(periodDto), false
        );

        final Set<LocalDate> dayList = getMatchDatesFromRequest(periodDto);

        final List<RestDayScheduleDto> restDayScheduleWithBookedSlotDtoList = dayScheduleList.stream()
            .filter(daySchedule -> dayList.contains(daySchedule.getDate()))
            .map(daySchedule -> {
                if (daySchedule.getTotalBooked() == 0) {
                    dayScheduleDocumentService.deleteDocument(daySchedule.getId(), true, null);
                    return daySchedule.toBuilder()
                        .isRemoved(true)
                        .build();
                }

                return daySchedule;
            })
            .collect(Collectors.toList());

        afterDelete(getParentDocumentId(periodDto), periodDto);

        return restDayScheduleWithBookedSlotDtoList;
    }

    private Set<LocalDate> getMatchDatesFromRequest(final RestDaySchedulePeriodDto periodDto) {
        final Set<LocalDate> result = new HashSet<>();

        if (periodDto == null || periodDto.getFrom() == null || periodDto.getTo() == null) {
            return result;
        }

        final Set<DayOfWeek> weekDays = ofNullable(periodDto.getWeekdays())
            .map(List::stream)
            .orElse(Stream.empty())
            .filter(Objects::nonNull)
            .map(DayOfWeek::valueOf)
            .collect(Collectors.toSet());

        final LocalDate weekStart = periodDto.getFrom().minusDays(periodDto.getFrom().getDayOfWeek().getValue() - 1);

        LocalDate date = periodDto.getFrom();
        while (periodDto.getTo().compareTo(date) >= 0) {
            if (!(periodDto.getDayRepeatNumber() != null
                && periodDto.getDayRepeatNumber() > 1
                && (periodDto.getFrom().until(date, ChronoUnit.DAYS) + 1) % periodDto.getDayRepeatNumber() != 0
                || periodDto.getWeekRepeatNumber() != null
                && periodDto.getWeekRepeatNumber() > 1
                && (weekStart.until(date, ChronoUnit.WEEKS) + 1) % periodDto.getWeekRepeatNumber() != 0
                || weekDays.size() > 0
                && !weekDays.contains(date.getDayOfWeek()))
            ) {
                result.add(date);
            }

            date = date.plusDays(1);
        }

        return result;
    }

    protected void afterCreate(final String cipId, final LocalDate date) {
    }

    protected void afterUpdate(final String cipId, final LocalDate date) {
    }

    protected void afterDelete(final String cipId, final LocalDate date) {
    }

    protected void afterDelete(final String cipId, final RestDaySchedulePeriodDto periodDto) {
    }

    protected void afterCopy(final String cipId, final LocalDate date, final RestDaySchedulePeriodDto periodDto) {
    }

    protected abstract String getParentDocumentId(final RestDaySchedulePeriodDto periodDto);

    protected abstract String getBookedDocumentId(final WindowType windowType);

    protected abstract long getSlotMinutes();
}
