package ru.croc.ugd.ssr.service.document;

import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static ru.croc.ugd.ssr.utils.StreamUtils.not;

import io.jsonwebtoken.lang.Collections;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.dayschedule.BookingSlotType;
import ru.croc.ugd.ssr.dayschedule.TimeIntervalType;
import ru.croc.ugd.ssr.db.dao.ShippingDayScheduleDao;
import ru.croc.ugd.ssr.exception.ScheduleNotFound;
import ru.croc.ugd.ssr.model.ShippingDayScheduleDocument;
import ru.croc.ugd.ssr.shipping.BrigadeType;
import ru.croc.ugd.ssr.shipping.ShippingDaySchedule;
import ru.croc.ugd.ssr.shipping.ShippingDayScheduleType;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.document.model.DocumentType;
import ru.reinform.cdp.document.service.AbstractDocumentService;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;

/**
 * ShippingDayScheduleDocumentService.
 */
@AllArgsConstructor
@Service
@Slf4j
public class ShippingDayScheduleDocumentService extends AbstractDocumentService<ShippingDayScheduleDocument> {
    private static final Duration DESIRABLE_SLOT_DURATION = Duration.ofMinutes(60 * 6);

    private final ShippingDayScheduleDao shippingDayScheduleDao;

    /**
     * Executes pre-processing before shippingDayScheduleDocument creation.
     *
     * @param shippingDayScheduleDocument shippingDayScheduleDocument
     */
    @Override
    public void beforeCreate(@NotNull ShippingDayScheduleDocument shippingDayScheduleDocument) {
        final ShippingDayScheduleType shippingDaySchedule = ofNullable(shippingDayScheduleDocument.getDocument())
            .map(ShippingDaySchedule::getShippingDayScheduleData)
            .filter(shippingDayScheduleType ->
                !isExistsForAreaAndShippingDate(shippingDayScheduleType.getArea(),
                    shippingDayScheduleType.getShippingDate())
            )
            .orElseThrow(ScheduleAlreadyExists::new);
        prePopulateNewShippingDocument(shippingDaySchedule);
    }

    /**
     * Executes pre-processing before shippingDayScheduleDocument creation.
     *
     * @param oldShippingDayScheduleDocument shippingDayScheduleDocument
     * @param newShippingDayScheduleDocument shippingDayScheduleDocument
     */
    public void beforeUpdate(@NotNull ShippingDayScheduleDocument oldShippingDayScheduleDocument,
                             @NotNull ShippingDayScheduleDocument newShippingDayScheduleDocument) {
        if (haveWorkingSlotsChanged(oldShippingDayScheduleDocument, newShippingDayScheduleDocument)) {
            final ShippingDayScheduleType shippingDaySchedule = ofNullable(newShippingDayScheduleDocument)
                .map(ShippingDayScheduleDocument::getDocument)
                .map(ShippingDaySchedule::getShippingDayScheduleData)
                .get();

            prePopulateNewShippingDocument(shippingDaySchedule, true);
        } else {
            if (hasAnyEmptyBookingSlots(newShippingDayScheduleDocument)) {
                final List<BrigadeType> oldBrigades = oldShippingDayScheduleDocument
                    .getDocument()
                    .getShippingDayScheduleData()
                    .getBrigades();
                final List<BrigadeType> newBrigades = newShippingDayScheduleDocument
                    .getDocument()
                    .getShippingDayScheduleData()
                    .getBrigades();

                newBrigades.clear();
                newBrigades.addAll(oldBrigades);
            }
        }
    }

    private static boolean hasAnyEmptyBookingSlots(final ShippingDayScheduleDocument shippingDayScheduleDocument) {
        return of(shippingDayScheduleDocument.getDocument())
            .map(ShippingDaySchedule::getShippingDayScheduleData)
            .map(ShippingDayScheduleType::getBrigades)
            .map(List::stream)
            .orElse(Stream.empty())
            .map(BrigadeType::getBookingSlots)
            .anyMatch(List::isEmpty);
    }

    private static boolean haveWorkingSlotsChanged(
        final ShippingDayScheduleDocument oldShippingDayScheduleDocument,
        final ShippingDayScheduleDocument newShippingDayScheduleDocument
    ) {
        return !getBrigadesWorkingHoursMap(oldShippingDayScheduleDocument)
            .equals(getBrigadesWorkingHoursMap(newShippingDayScheduleDocument));
    }

    private static Map<String, List<ImmutablePair<LocalTime, LocalTime>>> getBrigadesWorkingHoursMap(
        final ShippingDayScheduleDocument shippingDayScheduleDocument
    ) {
        return ofNullable(shippingDayScheduleDocument.getDocument())
            .map(ShippingDaySchedule::getShippingDayScheduleData)
            .map(ShippingDayScheduleType::getBrigades)
            .map(List::stream)
            .orElse(Stream.empty())
            .collect(Collectors.toMap(
                BrigadeType::getName,
                brigadeType -> brigadeType.getWorkingIntervals()
                    .stream()
                    .map(timeInterval -> ImmutablePair.of(timeInterval.getStart(), timeInterval.getEnd()))
                    .sorted(Comparator.comparing(ImmutablePair::getLeft))
                    .collect(Collectors.toList()),
                (b1, b2) -> b1
            ));
    }

    private void prePopulateNewShippingDocument(
        @NotNull final ShippingDayScheduleType shippingDaySchedule
    ) {
        prePopulateNewShippingDocument(shippingDaySchedule, false);
    }

    private void prePopulateNewShippingDocument(
        @NotNull final ShippingDayScheduleType shippingDaySchedule,
        final boolean removeOldSlots
    ) {
        ofNullable(shippingDaySchedule.getBrigades())
            .orElseThrow(NoBrigadesAssigned::new)
            .stream()
            .filter(not(brigadeType -> brigadeType
                .getBookingSlots()
                .stream()
                .filter(bookingSlotType -> bookingSlotType.getPreBookingId() != null)
                .findAny()
                .isPresent()))
            .forEach(brigadeType -> {
                final List<BookingSlotType> bookingSlotTypes
                    = getBookingSlotsByIntervals(brigadeType.getWorkingIntervals());
                if (removeOldSlots) {
                    brigadeType.getBookingSlots().clear();
                }

                brigadeType.getBookingSlots().addAll(bookingSlotTypes);
            });
    }

    @NotNull
    @Override
    public ShippingDayScheduleDocument fetchDocument(@NotNull String id) {
        return Optional.of(super.fetchDocument(id)).orElseThrow(ScheduleNotFound::new);
    }

    private boolean isExistsForAreaAndShippingDate(final String area, final LocalDate shippingDate) {
        return shippingDayScheduleDao.existsByAreaAndShippingDate(area, shippingDate);
    }

    /**
     * Creates booking slots based on working intervals. Default booking interval values is defined as 6 hours.
     * If break takes part of current interval it expands to break duration. Break is considered as a duration between
     * current interval and the next one if exists.
     *
     * @param workingIntervals initial working intervals
     * @return booking slots.
     */
    public List<BookingSlotType> getBookingSlotsByIntervals(final List<TimeIntervalType> workingIntervals) {
        if (Collections.isEmpty(workingIntervals)) {
            return java.util.Collections.emptyList();
        }

        final List<BookingSlotType> bookingSlots = new ArrayList<>();
        BookingSlotType newBookingSlot = new BookingSlotType();
        boolean containsMultipleIntervalsInSlot = false;

        for (final TimeIntervalType workingInterval : workingIntervals) {
            containsMultipleIntervalsInSlot = false;
            final LocalTime intervalStart = workingInterval.getStart();
            final LocalTime intervalEnd = workingInterval.getEnd();

            if (newBookingSlot.getStart() == null) {
                newBookingSlot.setStart(intervalStart);
            }

            if (newBookingSlot.getEnd() != null && intervalStart.compareTo(newBookingSlot.getEnd()) > 0) {
                final Duration slotDuration = Duration
                    .between(newBookingSlot.getStart(), newBookingSlot.getEnd());
                if (slotDuration.compareTo(DESIRABLE_SLOT_DURATION) > 0) {
                    final List<BookingSlotType> bookingSlotsToAdd = splitSlot(newBookingSlot);
                    bookingSlots.addAll(bookingSlotsToAdd);
                } else {
                    bookingSlots.add(newBookingSlot);
                }

                newBookingSlot = new BookingSlotType();
                newBookingSlot.setStart(intervalStart);
            }

            final Duration durationBetweenSlotStartAndIntervalEnd
                = Duration.between(newBookingSlot.getStart(), intervalEnd);
            final boolean isDesirableToTheEnd
                = durationBetweenSlotStartAndIntervalEnd.compareTo(DESIRABLE_SLOT_DURATION) == 0;
            final boolean isLessThenDesirableToTheEnd
                = durationBetweenSlotStartAndIntervalEnd.compareTo(DESIRABLE_SLOT_DURATION) < 0;
            final boolean isMoreThenDesirableToTheEnd
                = durationBetweenSlotStartAndIntervalEnd.compareTo(DESIRABLE_SLOT_DURATION) > 0;
            if (isDesirableToTheEnd) {
                newBookingSlot.setEnd(intervalEnd);
                bookingSlots.add(newBookingSlot);
                newBookingSlot = new BookingSlotType();
            }
            if (isLessThenDesirableToTheEnd) {
                newBookingSlot.setEnd(intervalEnd);

                containsMultipleIntervalsInSlot = true;
            }
            if (isMoreThenDesirableToTheEnd) {
                final LocalTime desirableBookingSlotEnd = newBookingSlot.getStart().plus(DESIRABLE_SLOT_DURATION);
                newBookingSlot.setEnd(desirableBookingSlotEnd);
                bookingSlots.add(newBookingSlot);

                newBookingSlot = new BookingSlotType();
                newBookingSlot.setStart(desirableBookingSlotEnd);
                newBookingSlot.setEnd(intervalEnd);

                containsMultipleIntervalsInSlot = true;
            }
        }

        if (containsMultipleIntervalsInSlot) {
            final List<BookingSlotType> bookingSlotsToAdd = splitSlot(newBookingSlot);
            bookingSlots.addAll(bookingSlotsToAdd);
        }

        bookingSlots.forEach(this::setRandomUidToSlot);
        return bookingSlots;
    }

    private List<BookingSlotType> splitSlot(BookingSlotType bookingSlot) {
        final List<BookingSlotType> bookingSlots = new ArrayList<>();

        Duration slotDuration = Duration.between(bookingSlot.getStart(), bookingSlot.getEnd());

        while (slotDuration.compareTo(DESIRABLE_SLOT_DURATION) > 0) {
            final LocalTime currentBookingSlotEnd = bookingSlot.getEnd();
            final LocalTime desirableBookingSlotEnd = bookingSlot.getStart().plus(DESIRABLE_SLOT_DURATION);

            bookingSlot.setEnd(desirableBookingSlotEnd);
            bookingSlots.add(bookingSlot);

            bookingSlot = new BookingSlotType();
            bookingSlot.setStart(desirableBookingSlotEnd);
            bookingSlot.setEnd(currentBookingSlotEnd);

            slotDuration = Duration.between(bookingSlot.getStart(), bookingSlot.getEnd());
        }

        bookingSlots.add(bookingSlot);

        return bookingSlots;
    }

    private void setRandomUidToSlot(final BookingSlotType randomUidToSlot) {
        randomUidToSlot.setSlotId(UUID.randomUUID().toString());
    }

    @Nonnull
    @Override
    public DocumentType<ShippingDayScheduleDocument> getDocumentType() {
        return SsrDocumentTypes.SHIPPING_DAY_SCHEDULE;
    }
}
