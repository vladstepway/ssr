package ru.croc.ugd.ssr.service.document;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.dayschedule.SlotType;
import ru.croc.ugd.ssr.dayschedule.WindowType;
import ru.croc.ugd.ssr.dayschedule.notary.NotaryDayScheduleType;
import ru.croc.ugd.ssr.db.dao.NotaryDayScheduleDao;
import ru.croc.ugd.ssr.model.NotaryDayScheduleDocument;
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
 * NotaryDayScheduleDocumentService.
 */
@AllArgsConstructor
@Service
@Slf4j
public class NotaryDayScheduleDocumentService extends AbstractDayScheduleDocumentService<NotaryDayScheduleDocument> {

    private final NotaryDayScheduleDao notaryDayScheduleDao;
    private final DocumentConverterService documentConverterService;

    @Nonnull
    @Override
    public DocumentType<NotaryDayScheduleDocument> getDocumentType() {
        return SsrDocumentTypes.NOTARY_DAY_SCHEDULE;
    }

    public List<NotaryDayScheduleDocument> fetchAllByNotaryIdAndDate(
        final String notaryId, final LocalDate startFrom, final Integer days, final boolean isFreeOnly
    ) {
        final LocalDate dateFrom = ofNullable(startFrom).orElseGet(LocalDate::now);
        final LocalDate dateTo = dateFrom.plus(days, ChronoUnit.DAYS);

        return fetchAllByDateAndParentDocumentId(dateFrom, dateTo, notaryId, isFreeOnly);
    }

    @Override
    public List<NotaryDayScheduleDocument> fetchAllByDateAndParentDocumentId(
        final LocalDate from, final LocalDate to, final String parentDocumentId, final boolean isFreeOnly
    ) {
        return notaryDayScheduleDao.findAllByDatesAndNotary(from, to, parentDocumentId)
            .stream()
            .map(document -> documentConverterService.parseDocumentData(document, NotaryDayScheduleDocument.class))
            .map(document -> filterFreeSlotsIfNeeded(document, isFreeOnly))
            .collect(Collectors.toList());
    }

    private NotaryDayScheduleDocument filterFreeSlotsIfNeeded(
        final NotaryDayScheduleDocument document, final boolean isFreeOnly
    ) {
        if (!isFreeOnly) {
            return document;
        }

        final NotaryDayScheduleType daySchedule = document
            .getDocument()
            .getNotaryDayScheduleData();

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

    public List<NotaryDayScheduleDocument> fetchAllByPrebookingId(final String preBookingId) {
        return notaryDayScheduleDao.findAllByPreBookingId(preBookingId)
            .stream()
            .map(document -> documentConverterService.parseDocumentData(document, NotaryDayScheduleDocument.class))
            .collect(Collectors.toList());
    }

    public boolean isAlreadyBooked(final String preBookingId) {
        return notaryDayScheduleDao.isAlreadyBooked(preBookingId);
    }

    public Optional<NotaryDayScheduleDocument> fetchBySlotId(final String slotId) {
        return notaryDayScheduleDao.findBySlotId(slotId)
            .map(document -> documentConverterService.parseDocumentData(document, NotaryDayScheduleDocument.class));
    }

    public Optional<NotaryDayScheduleDocument> fetchByPreBookingIdAndEno(final String preBookingId, final String eno) {
        return notaryDayScheduleDao.findByPreBookingIdAndEno(preBookingId, eno)
            .map(document -> documentConverterService.parseDocumentData(document, NotaryDayScheduleDocument.class));
    }

    public Optional<NotaryDayScheduleDocument> findBookedByPreBookingId(final String preBookingId) {
        return notaryDayScheduleDao.findBookedByPreBookingId(preBookingId)
            .map(document -> documentConverterService.parseDocumentData(document, NotaryDayScheduleDocument.class));
    }

    public void cancelBooking(final String bookingId) {
        findBookedByPreBookingId(bookingId)
            .ifPresent(notaryDayScheduleDocument -> cancelBooking(notaryDayScheduleDocument, bookingId));
    }

    private void cancelBooking(final NotaryDayScheduleDocument notaryDayScheduleDocument, final String bookingId) {
        final NotaryDayScheduleType notaryDaySchedule = notaryDayScheduleDocument
            .getDocument()
            .getNotaryDayScheduleData();

        ofNullable(notaryDaySchedule.getSlots())
            .map(NotaryDayScheduleType.Slots::getSlot)
            .map(List::stream)
            .orElse(Stream.empty())
            .filter(slot -> isFutureSlot(slot, notaryDaySchedule.getDate()))
            .map(SlotType::getWindows)
            .map(SlotType.Windows::getWindow)
            .flatMap(List::stream)
            .filter(window -> Objects.equals(bookingId, window.getPreBookingId()))
            .forEach(this::cancelWindowBooking);

        updateDocument(notaryDayScheduleDocument.getId(), notaryDayScheduleDocument, true, true, null);
    }

    private void cancelWindowBooking(final WindowType window) {
        window.setEno(null);
        window.setPreBookingId(null);
    }
}

