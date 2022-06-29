package ru.croc.ugd.ssr.service.document;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.dayschedule.SlotType;
import ru.croc.ugd.ssr.dayschedule.WindowType;
import ru.croc.ugd.ssr.dayschedule.contractappointment.ContractAppointmentDayScheduleData;
import ru.croc.ugd.ssr.db.dao.ContractAppointmentDayScheduleDao;
import ru.croc.ugd.ssr.model.contractappointment.ContractAppointmentDayScheduleDocument;
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
 * ContractAppointmentDayScheduleDocumentService.
 */
@AllArgsConstructor
@Service
@Slf4j
public class ContractAppointmentDayScheduleDocumentService
    extends AbstractDayScheduleDocumentService<ContractAppointmentDayScheduleDocument> {

    private final ContractAppointmentDayScheduleDao contractAppointmentDayScheduleDao;
    private final DocumentConverterService documentConverterService;

    @Nonnull
    @Override
    public DocumentType<ContractAppointmentDayScheduleDocument> getDocumentType() {
        return SsrDocumentTypes.CONTRACT_APPOINTMENT_DAY_SCHEDULE;
    }

    public List<ContractAppointmentDayScheduleDocument> fetchAllByCipIdAndDate(
        final String cipId, final LocalDate startFrom, final Integer days, final boolean isFreeOnly
    ) {
        final LocalDate dateFrom = ofNullable(startFrom).orElseGet(LocalDate::now);
        final LocalDate dateTo = dateFrom.plus(days, ChronoUnit.DAYS);

        return fetchAllByDateAndParentDocumentId(dateFrom, dateTo, cipId, isFreeOnly);
    }

    @Override
    public List<ContractAppointmentDayScheduleDocument> fetchAllByDateAndParentDocumentId(
        final LocalDate from, final LocalDate to, final String parentDocumentId, final boolean isFreeOnly
    ) {
        return contractAppointmentDayScheduleDao.findAllByDatesAndCip(from, to, parentDocumentId)
            .stream()
            .map(document -> documentConverterService
                .parseDocumentData(document, ContractAppointmentDayScheduleDocument.class))
            .map(document -> filterFreeSlotsIfNeeded(document, isFreeOnly))
            .collect(Collectors.toList());
    }

    private ContractAppointmentDayScheduleDocument filterFreeSlotsIfNeeded(
        final ContractAppointmentDayScheduleDocument document, final boolean isFreeOnly
    ) {
        if (!isFreeOnly) {
            return document;
        }

        final ContractAppointmentDayScheduleData daySchedule = document
            .getDocument()
            .getContractAppointmentDayScheduleData();

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

    public List<ContractAppointmentDayScheduleDocument> fetchAllByPrebookingId(final String preBookingId) {
        return contractAppointmentDayScheduleDao.findAllByPreBookingId(preBookingId)
            .stream()
            .map(document -> documentConverterService.parseDocumentData(document,
                ContractAppointmentDayScheduleDocument.class))
            .collect(Collectors.toList());
    }

    public boolean isAlreadyBooked(final String preBookingId) {
        return contractAppointmentDayScheduleDao.isAlreadyBooked(preBookingId);
    }

    public Optional<ContractAppointmentDayScheduleDocument> fetchBySlotId(final String slotId) {
        return contractAppointmentDayScheduleDao.findBySlotId(slotId)
            .map(document -> documentConverterService.parseDocumentData(document,
                ContractAppointmentDayScheduleDocument.class));
    }

    public Optional<ContractAppointmentDayScheduleDocument> findByPreBookingIdAndPreBookedUntilIsNotNull(
        final String preBookingId
    ) {
        return contractAppointmentDayScheduleDao
            .findByPreBookingIdAndPreBookedUntilIsNotNull(preBookingId, LocalDateTime.now())
            .map(document -> documentConverterService.parseDocumentData(document,
                ContractAppointmentDayScheduleDocument.class));
    }

    public Optional<ContractAppointmentDayScheduleDocument> findByBookingIdAndPreBookedUntilIsNull(
        final String bookingId
    ) {
        return contractAppointmentDayScheduleDao.findByBookingIdAndPreBookedUntilIsNull(bookingId)
            .map(document -> documentConverterService.parseDocumentData(document,
                ContractAppointmentDayScheduleDocument.class));
    }

    public void cancelBooking(final String bookingId) {
        findByBookingIdAndPreBookedUntilIsNull(bookingId)
            .ifPresent(contractAppointmentDayScheduleDocument ->
                cancelDocumentBooking(contractAppointmentDayScheduleDocument, bookingId, null)
            );
    }

    public void cancelBooking(final String bookingId, final String contractAppointmentId) {
        findByBookingIdAndPreBookedUntilIsNull(bookingId)
            .ifPresent(contractAppointmentDayScheduleDocument ->
                cancelDocumentBooking(contractAppointmentDayScheduleDocument, bookingId, contractAppointmentId)
            );
    }

    private void cancelDocumentBooking(
        final ContractAppointmentDayScheduleDocument contractAppointmentDayScheduleDocument,
        final String bookingId,
        final String contractAppointmentId
    ) {
        final ContractAppointmentDayScheduleData contractAppointmentDaySchedule = contractAppointmentDayScheduleDocument
            .getDocument()
            .getContractAppointmentDayScheduleData();

        cancelDayScheduleBooking(contractAppointmentDaySchedule, bookingId, contractAppointmentId);

        updateDocument(contractAppointmentDayScheduleDocument.getId(),
            contractAppointmentDayScheduleDocument, true, true, null);
    }

    public void cancelDayScheduleBooking(
        final ContractAppointmentDayScheduleData contractAppointmentDaySchedule,
        final String bookingId,
        final String contractAppointmentId
    ) {
        ofNullable(contractAppointmentDaySchedule.getSlots())
            .map(ContractAppointmentDayScheduleData.Slots::getSlot)
            .map(List::stream)
            .orElse(Stream.empty())
            .map(SlotType::getWindows)
            .map(SlotType.Windows::getWindow)
            .flatMap(List::stream)
            .filter(window -> Objects.equals(bookingId, window.getPreBookingId()))
            .filter(window -> Objects.isNull(window.getPreBookedUntil()))
            .forEach(window -> cancelWindowBooking(window, contractAppointmentId));
    }

    public void cancelDaySchedulePreBooking(
        final ContractAppointmentDayScheduleData contractAppointmentDaySchedule, final String preBookingId
    ) {
        ofNullable(contractAppointmentDaySchedule.getSlots())
            .map(ContractAppointmentDayScheduleData.Slots::getSlot)
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
        window.setContractOrderId(null);
        window.setPreBookedUntil(null);
        window.setPreBookingId(null);
    }

    private void cancelWindowBooking(final WindowType window, final String contractAppointmentId) {
        cancelWindowBooking(window);
        if (contractAppointmentId != null
            && window.getPrematurelyCompletedApplicationIds().stream().noneMatch(id -> id.equals(contractAppointmentId))
        ) {
            window.getPrematurelyCompletedApplicationIds().add(contractAppointmentId);
        }
    }
}
