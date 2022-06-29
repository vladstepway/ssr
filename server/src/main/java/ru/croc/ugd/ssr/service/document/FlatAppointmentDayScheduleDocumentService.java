package ru.croc.ugd.ssr.service.document;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.dayschedule.SlotType;
import ru.croc.ugd.ssr.dayschedule.WindowType;
import ru.croc.ugd.ssr.dayschedule.flatappointment.FlatAppointmentDayScheduleData;
import ru.croc.ugd.ssr.db.dao.FlatAppointmentDayScheduleDao;
import ru.croc.ugd.ssr.model.flatAppointment.FlatAppointmentDayScheduleDocument;
import ru.croc.ugd.ssr.service.AbstractDayScheduleDocumentService;
import ru.croc.ugd.ssr.service.DocumentConverterService;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.document.model.DocumentType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;

/**
 * FlatAppointmentDayScheduleDocumentService.
 */
@AllArgsConstructor
@Service
@Slf4j
public class FlatAppointmentDayScheduleDocumentService
    extends AbstractDayScheduleDocumentService<FlatAppointmentDayScheduleDocument> {

    private final FlatAppointmentDayScheduleDao flatAppointmentDayScheduleDao;
    private final DocumentConverterService documentConverterService;

    @Nonnull
    @Override
    public DocumentType<FlatAppointmentDayScheduleDocument> getDocumentType() {
        return SsrDocumentTypes.FLAT_APPOINTMENT_DAY_SCHEDULE;
    }

    public List<FlatAppointmentDayScheduleDocument> fetchAllByCipIdAndDate(
        final String cipId, final LocalDate startFrom, final Integer days, final boolean isFreeOnly
    ) {
        final LocalDate dateFrom = ofNullable(startFrom).orElseGet(LocalDate::now);
        final LocalDate dateTo = dateFrom.plus(days, ChronoUnit.DAYS);

        return fetchAllByDateAndParentDocumentId(dateFrom, dateTo, cipId, isFreeOnly);
    }

    @Override
    public List<FlatAppointmentDayScheduleDocument> fetchAllByDateAndParentDocumentId(
        final LocalDate from, final LocalDate to, final String parentDocumentId, final boolean isFreeOnly
    ) {
        return flatAppointmentDayScheduleDao.findAllByDatesAndCip(from, to, parentDocumentId)
            .stream()
            .map(document -> documentConverterService
                .parseDocumentData(document, FlatAppointmentDayScheduleDocument.class))
            .map(document -> filterFreeSlotsIfNeeded(document, isFreeOnly))
            .collect(Collectors.toList());
    }

    private FlatAppointmentDayScheduleDocument filterFreeSlotsIfNeeded(
        final FlatAppointmentDayScheduleDocument document, final boolean isFreeOnly
    ) {
        if (!isFreeOnly) {
            return document;
        }

        final FlatAppointmentDayScheduleData daySchedule = document
            .getDocument()
            .getFlatAppointmentDayScheduleData();

        final List<SlotType> slotList = daySchedule.getSlots().getSlot();

        final List<SlotType> filteredSlotList = slotList
            .stream()
            .filter(slot -> isFutureSlot(slot, daySchedule.getDate()))
            .filter(slot -> slot
                .getWindows()
                .getWindow()
                .stream()
                .anyMatch(this::isWindowFree))
            .collect(Collectors.toList());

        slotList.clear();
        slotList.addAll(filteredSlotList);
        return document;
    }

    private boolean isFutureSlot(final SlotType slot, final LocalDate date) {
        final LocalDateTime slotDateTime = LocalDateTime.of(date, slot.getTimeFrom());
        return slotDateTime.isAfter(LocalDateTime.now());
    }

    private boolean isWindowFree(final WindowType window) {
        return Objects.isNull(window.getPreBookingId())
            || nonNull(window.getPreBookedUntil())
            && window.getPreBookedUntil().isBefore(LocalDateTime.now());
    }

    public List<FlatAppointmentDayScheduleDocument> fetchAllByPrebookingId(final String preBookingId) {
        return flatAppointmentDayScheduleDao.findAllByPreBookingId(preBookingId)
            .stream()
            .map(document -> documentConverterService
                .parseDocumentData(document, FlatAppointmentDayScheduleDocument.class))
            .collect(Collectors.toList());
    }

    public boolean isAlreadyBooked(final String preBookingId) {
        return flatAppointmentDayScheduleDao.isAlreadyBooked(preBookingId);
    }

    public Optional<FlatAppointmentDayScheduleDocument> fetchBySlotId(final String slotId) {
        return flatAppointmentDayScheduleDao.findBySlotId(slotId)
            .map(document -> documentConverterService
                .parseDocumentData(document, FlatAppointmentDayScheduleDocument.class)
            );
    }

    public Optional<FlatAppointmentDayScheduleDocument> findByPreBookingIdAndPreBookedUntilIsNotNull(
        final String preBookingId
    ) {
        return flatAppointmentDayScheduleDao
            .findByPreBookingIdAndPreBookedUntilIsNotNull(preBookingId, LocalDateTime.now())
            .map(document -> documentConverterService
                .parseDocumentData(document, FlatAppointmentDayScheduleDocument.class)
            );
    }

    public Optional<FlatAppointmentDayScheduleDocument> findByBookingIdAndPreBookedUntilIsNull(final String bookingId) {
        return flatAppointmentDayScheduleDao.findByBookingIdAndPreBookedUntilIsNull(bookingId)
            .map(document ->
                documentConverterService.parseDocumentData(document, FlatAppointmentDayScheduleDocument.class)
            );
    }

    public void cancelBooking(final String bookingId) {
        findByBookingIdAndPreBookedUntilIsNull(bookingId)
            .ifPresent(flatAppointmentDayScheduleDocument ->
                cancelDocumentBooking(flatAppointmentDayScheduleDocument, bookingId, null)
            );
    }

    public void cancelBooking(final String bookingId, final String flatAppointmentId) {
        findByBookingIdAndPreBookedUntilIsNull(bookingId)
            .ifPresent(flatAppointmentDayScheduleDocument ->
                cancelDocumentBooking(flatAppointmentDayScheduleDocument, bookingId, flatAppointmentId)
            );
    }

    private void cancelDocumentBooking(
        final FlatAppointmentDayScheduleDocument flatAppointmentDayScheduleDocument,
        final String bookingId,
        final String flatAppointmentId
    ) {
        final FlatAppointmentDayScheduleData flatAppointmentDaySchedule = flatAppointmentDayScheduleDocument
            .getDocument()
            .getFlatAppointmentDayScheduleData();

        cancelDayScheduleBooking(flatAppointmentDaySchedule, bookingId, flatAppointmentId);

        updateDocument(
            flatAppointmentDayScheduleDocument.getId(),
            flatAppointmentDayScheduleDocument,
            true,
            true,
            null
        );
    }

    public void cancelDayScheduleBooking(
        final FlatAppointmentDayScheduleData flatAppointmentDaySchedule,
        final String bookingId,
        final String flatAppointmentId
    ) {
        ofNullable(flatAppointmentDaySchedule.getSlots())
            .map(FlatAppointmentDayScheduleData.Slots::getSlot)
            .map(List::stream)
            .orElse(Stream.empty())
            .map(SlotType::getWindows)
            .map(SlotType.Windows::getWindow)
            .flatMap(List::stream)
            .filter(window -> Objects.equals(bookingId, window.getPreBookingId()))
            .filter(window -> Objects.isNull(window.getPreBookedUntil()))
            .forEach(window -> cancelWindowBooking(window, flatAppointmentId));
    }

    public void cancelDaySchedulePreBooking(
        final FlatAppointmentDayScheduleData flatAppointmentDaySchedule, final String preBookingId
    ) {
        ofNullable(flatAppointmentDaySchedule.getSlots())
            .map(FlatAppointmentDayScheduleData.Slots::getSlot)
            .map(List::stream)
            .orElse(Stream.empty())
            .map(SlotType::getWindows)
            .map(SlotType.Windows::getWindow)
            .flatMap(List::stream)
            .filter(window -> Objects.equals(preBookingId, window.getPreBookingId()))
            .filter(window -> Objects.nonNull(window.getPreBookedUntil()))
            .forEach(this::cancelWindowBooking);
    }

    private void cancelWindowBooking(final WindowType window) {
        window.setPreBookingId(null);
        window.setPreBookedUntil(null);
        window.setOfferLetterId(null);
    }

    private void cancelWindowBooking(final WindowType window, final String flatAppointmentId) {
        cancelWindowBooking(window);
        if (flatAppointmentId != null
            && window.getPrematurelyCompletedApplicationIds().stream().noneMatch(id -> id.equals(flatAppointmentId))
        ) {
            window.getPrematurelyCompletedApplicationIds().add(flatAppointmentId);
        }
    }
}
