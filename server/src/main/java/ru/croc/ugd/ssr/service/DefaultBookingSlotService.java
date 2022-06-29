package ru.croc.ugd.ssr.service;

import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.croc.ugd.ssr.dayschedule.BookingSlotType;
import ru.croc.ugd.ssr.db.dao.ShippingApplicationDao;
import ru.croc.ugd.ssr.db.dao.ShippingDayScheduleDao;
import ru.croc.ugd.ssr.dto.shipping.BookingRequestDto;
import ru.croc.ugd.ssr.dto.shipping.BookingResultDto;
import ru.croc.ugd.ssr.dto.shipping.BookingSlotDto;
import ru.croc.ugd.ssr.dto.shipping.BookingSlotListApartmentFromDto;
import ru.croc.ugd.ssr.dto.shipping.BookingSlotListApartmentToDto;
import ru.croc.ugd.ssr.dto.shipping.BookingSlotListDto;
import ru.croc.ugd.ssr.dto.shipping.BookingSlotListRequestDto;
import ru.croc.ugd.ssr.dto.shipping.CopingDaySchedulesRequestDto;
import ru.croc.ugd.ssr.dto.shipping.DeletingDaySchedulesRequestDto;
import ru.croc.ugd.ssr.dto.shipping.PreBookingRequestDto;
import ru.croc.ugd.ssr.dto.shipping.PreBookingResultDto;
import ru.croc.ugd.ssr.dto.shipping.PrebookingApartmentFromDto;
import ru.croc.ugd.ssr.dto.shipping.ShippingBookingTotalDto;
import ru.croc.ugd.ssr.dto.shipping.ValidateShippingDateRequestDto;
import ru.croc.ugd.ssr.dto.shipping.ValidateShippingDateResponseDto;
import ru.croc.ugd.ssr.exception.BookedSlotsNotFound;
import ru.croc.ugd.ssr.exception.DeleteScheduleWhenSlotWasBooked;
import ru.croc.ugd.ssr.exception.InvalidDataInput;
import ru.croc.ugd.ssr.exception.MissedBookingIdentifier;
import ru.croc.ugd.ssr.exception.MissedSlotIdentifier;
import ru.croc.ugd.ssr.exception.MissedSnils;
import ru.croc.ugd.ssr.exception.MissedUnom;
import ru.croc.ugd.ssr.exception.ScheduleDayAlreadyExisted;
import ru.croc.ugd.ssr.exception.ScheduleDayNotFound;
import ru.croc.ugd.ssr.exception.SlotsAlreadyBooked;
import ru.croc.ugd.ssr.exception.SlotsNotFound;
import ru.croc.ugd.ssr.exception.SsrException;
import ru.croc.ugd.ssr.exception.UpdateScheduleWhenSLotWasBooked;
import ru.croc.ugd.ssr.generated.dto.RestBookingSlotListApartmentFromDto;
import ru.croc.ugd.ssr.generated.dto.RestBookingSlotListApartmentToDto;
import ru.croc.ugd.ssr.generated.dto.RestInternalBookingSlotListRequestDto;
import ru.croc.ugd.ssr.generated.dto.RestInternalPrebookingRequestDto;
import ru.croc.ugd.ssr.generated.dto.RestPrebookingApartmentFromDto;
import ru.croc.ugd.ssr.model.ShippingDayScheduleDocument;
import ru.croc.ugd.ssr.service.document.ShippingDayScheduleDocumentService;
import ru.croc.ugd.ssr.shipping.BrigadeType;
import ru.croc.ugd.ssr.shipping.ShippingDaySchedule;
import ru.croc.ugd.ssr.shipping.ShippingDayScheduleType;
import ru.croc.ugd.ssr.utils.PeriodUtils;
import ru.croc.ugd.ssr.utils.StreamUtils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Default implementation of BookingSlotService.
 */
@Slf4j
@Service
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class DefaultBookingSlotService implements BookingSlotService {

    private static final Duration PRE_BOOKING_DURATION = Duration.ofMinutes(15);

    private static final Duration START_OF_TIMETABLE_FROM_NOW = Duration.ofDays(2);

    private static final String QUERY_BY_BOOKING_ID = "{ \"shippingDaySchedule\" : { \"shippingDayScheduleData\" :"
        + " { \"brigades\" : [{ \"bookingSlots\" : [{ \"preBookingId\" : \"%s\" }] }] } }}";

    private final ShippingDayScheduleDocumentService shippingDayScheduleDocumentService;
    private final ShippingDayScheduleDao shippingDayScheduleDao;
    private final DocumentConverterService documentConverterService;
    private final CapitalConstructionObjectService capitalConstructionObjectService;
    private final ShippingApplicationDao shippingApplicationDao;

    /**
     * Retrieve available booking slot list.
     *
     * @param bookingSlotListRequestDto booking slot list request
     * @return result
     */
    @Override
    public BookingSlotListDto getAll(final BookingSlotListRequestDto bookingSlotListRequestDto) {
        final LocalDate moveDate = ofNullable(bookingSlotListRequestDto.getFromApartment())
            .map(BookingSlotListApartmentFromDto::getMoveDate)
            .orElseThrow(InvalidDataInput::new);
        final String unomNew = ofNullable(bookingSlotListRequestDto.getToApartment())
            .map(BookingSlotListApartmentToDto::getUnomNew)
            .orElseThrow(MissedUnom::new);
        final List<ShippingDaySchedule> shippingScheduleList = retrieveShippingScheduleList(moveDate, unomNew);

        final String unomOld = ofNullable(bookingSlotListRequestDto.getFromApartment())
            .map(BookingSlotListApartmentFromDto::getUnomOld)
            .orElseThrow(MissedUnom::new);

        final String preBookingId = generatePreBookingId(
            bookingSlotListRequestDto.getSnils(),
            bookingSlotListRequestDto.getSsoId(),
            unomOld
        );
        return getBookingSlotListDto(shippingScheduleList, preBookingId);
    }

    @Override
    public BookingSlotListDto getAllInternal(final RestInternalBookingSlotListRequestDto bookingSlotListRequestDto) {
        final LocalDate moveDate = ofNullable(bookingSlotListRequestDto.getFrom())
            .map(RestBookingSlotListApartmentFromDto::getMOVEDATE)
            .orElseThrow(InvalidDataInput::new);
        final String unomNew = ofNullable(bookingSlotListRequestDto.getTo())
            .map(RestBookingSlotListApartmentToDto::getUNOMNEW)
            .orElseThrow(MissedUnom::new);
        final List<ShippingDaySchedule> shippingScheduleList = retrieveShippingScheduleList(moveDate, unomNew);

        final String unomOld = ofNullable(bookingSlotListRequestDto.getFrom())
            .map(RestBookingSlotListApartmentFromDto::getUNOMOLD)
            .orElseThrow(MissedUnom::new);

        final String preBookingId = generatePreBookingId(
            bookingSlotListRequestDto.getPersonDocumentId(),
            unomOld
        );

        return getBookingSlotListDto(shippingScheduleList, preBookingId);
    }

    private BookingSlotListDto getBookingSlotListDto(
        final List<ShippingDaySchedule> shippingScheduleList, final String preBookingId
    ) {
        final List<BookingSlotDto> bookingSlotTypes = new ArrayList<>();
        shippingScheduleList.forEach(shippingDaySchedule -> {
            final List<BookingSlotDto> bookingSlotsPerDay = shippingDaySchedule
                .getShippingDayScheduleData()
                .getBrigades()
                .stream()
                .map(BrigadeType::getBookingSlots)
                .flatMap(Collection::stream)
                .filter(bookingSlotType -> bookingSlotType.getPreBookingId() == null || (
                    StringUtils.equals(preBookingId, bookingSlotType.getPreBookingId())
                        && bookingSlotType.getPreBookedUntil() != null))
                .filter(StreamUtils.distinctByKey(bookingSlotType ->
                    String.format("%s - %s", bookingSlotType.getStart(), bookingSlotType.getEnd())))
                .filter(bookingSlotType ->
                    !shippingDaySchedule.getShippingDayScheduleData().getShippingDate().isEqual(LocalDate.now())
                        || !bookingSlotType.getStart().isBefore(LocalTime.now()))
                .map(bookingSlotType -> {
                    final LocalDateTime dateTo = LocalDateTime.of(shippingDaySchedule
                            .getShippingDayScheduleData().getShippingDate(),
                        bookingSlotType.getEnd());
                    return BookingSlotDto.builder()
                        .dateFrom(LocalDateTime.of(shippingDaySchedule
                                .getShippingDayScheduleData().getShippingDate(),
                            bookingSlotType.getStart()))
                        .dateTo(dateTo)
                        .preBookingId(bookingSlotType.getPreBookingId())
                        .uid(bookingSlotType.getSlotId())
                        .build();
                })
                .collect(Collectors.toList());
            bookingSlotTypes.addAll(bookingSlotsPerDay);
        });
        return BookingSlotListDto.builder()
            .bookingSlots(bookingSlotTypes)
            .build();
    }

    private List<ShippingDaySchedule> retrieveShippingScheduleList(final LocalDate moveDate, final String unomNew) {
        final String districtName = retrieveDistrictName(unomNew);
        final LocalDate startTimetableDate = LocalDate.now().plusDays(START_OF_TIMETABLE_FROM_NOW.toDays());

        return documentConverterService
            .parseDocumentData(
                shippingDayScheduleDao.findByMoveDateAndDistrictName(startTimetableDate, moveDate, districtName),
                ShippingDayScheduleDocument.class
            ).stream()
            .map(ShippingDayScheduleDocument::getDocument)
            .collect(Collectors.toList());
    }

    private String retrieveDistrictName(final String unomNew) {
        return ofNullable(unomNew)
            .map(capitalConstructionObjectService::getCcoDistrictsByUnom)
            .filter(StreamUtils.not(CollectionUtils::isEmpty))
            .map(districts -> districts.get(0))
            .orElse(null);
    }

    @Override
    public PreBookingResultDto preBookSlotInternal(final RestInternalPrebookingRequestDto preBookingRequestDto) {
        final String slotId = ofNullable(preBookingRequestDto.getTimetableUid())
            .orElseThrow(MissedSlotIdentifier::new);
        final String unomOld = ofNullable(preBookingRequestDto.getFrom())
            .map(RestPrebookingApartmentFromDto::getUNOMOLD)
            .orElseThrow(MissedUnom::new);
        final String preBookingId = generatePreBookingId(
            preBookingRequestDto.getPersonDocumentId(),
            unomOld
        );
        return prebookSlot(preBookingId, slotId);
    }

    /**
     * Pre-book slot for shipping.
     *
     * @param preBookingRequestDto pre-booking request
     * @return result
     */
    @Override
    public PreBookingResultDto preBookSlot(final PreBookingRequestDto preBookingRequestDto) {
        final String slotId = ofNullable(preBookingRequestDto.getTimetableUid())
            .orElseThrow(MissedSlotIdentifier::new);
        final String unomOld = ofNullable(preBookingRequestDto.getFromApartment())
            .map(PrebookingApartmentFromDto::getUnomOld)
            .orElseThrow(MissedUnom::new);
        final String preBookingId = generatePreBookingId(
            preBookingRequestDto.getSnils(),
            preBookingRequestDto.getSsoId(),
            unomOld
        );
        return prebookSlot(preBookingId, slotId);
    }

    private PreBookingResultDto prebookSlot(final String preBookingId, final String slotId) {
        dropExistingPreBookingSlots(preBookingId);

        final List<String> shippingDayJsons = shippingDayScheduleDao.findShippingDayJsonBySlotIdInside(slotId);
        if (CollectionUtils.isEmpty(shippingDayJsons)) {
            throw new SlotsNotFound();
        }
        final ShippingDayScheduleDocument shippingDayScheduleDocument = shippingDayJsons
            .stream()
            .map(document -> documentConverterService
                .parseJson(document, ShippingDayScheduleDocument.class))
            .findFirst()
            .orElseThrow(SlotsNotFound::new);
        final AtomicBoolean isPrebooked = new AtomicBoolean(false);
        shippingDayScheduleDocument
            .getDocument()
            .getShippingDayScheduleData()
            .getBrigades()
            .forEach(brigadeType -> {
                final Optional<BookingSlotType> bookingSlotTypeFound = brigadeType.getBookingSlots()
                    .stream()
                    .filter(bookingSlotType -> StringUtils.equals(bookingSlotType.getSlotId(), slotId)
                        && bookingSlotType.getPreBookingId() == null)
                    .findFirst();
                bookingSlotTypeFound.ifPresent(bookingSlotType -> {
                    isPrebooked.set(true);
                    this.setExpirationOnBookingSlot(preBookingId, bookingSlotType);
                });
            });
        if (!isPrebooked.get()) {
            throw new SlotsAlreadyBooked();
        }
        shippingDayScheduleDocumentService.updateDocument(
            shippingDayScheduleDocument.getId(),
            shippingDayScheduleDocument,
            true,
            false,
            null
        );

        return PreBookingResultDto.builder()
            .bookingUid(preBookingId)
            .build();
    }

    private void setExpirationOnBookingSlot(
        final String preBookingId, final BookingSlotType bookingSlotsForPreBooking
    ) {
        final LocalDateTime preBookUntil = LocalDateTime.now().plusMinutes(PRE_BOOKING_DURATION.toMinutes());
        bookingSlotsForPreBooking.setPreBookingId(preBookingId);
        bookingSlotsForPreBooking.setPreBookedUntil(preBookUntil);
    }

    private void dropExistingPreBookingSlots(final String preBookingId) {
        shippingDayScheduleDao.findAllByPreBookingId(preBookingId)
            .stream()
            .map(document -> documentConverterService
                .parseDocumentData(document, ShippingDayScheduleDocument.class))
            .peek(doc -> doc.getDocument()
                .getShippingDayScheduleData()
                .getBrigades()
                .stream()
                .map(BrigadeType::getBookingSlots)
                .flatMap(List::stream)
                .filter(bookingSlot -> preBookingId.equals(bookingSlot.getPreBookingId())
                    && Objects.nonNull(bookingSlot.getPreBookedUntil()))
                .forEach(bookingSlot -> {
                    bookingSlot.setPreBookingId(null);
                    bookingSlot.setPreBookedUntil(null);
                })
            )
            .forEach(document ->
                shippingDayScheduleDocumentService.updateDocument(
                    document.getId(),
                    document,
                    true,
                    true,
                    null
                ));
    }

    /**
     * Book slot for shipping.
     *
     * @param bookingRequestDto booking request
     * @return result
     */
    @Override
    public BookingResultDto bookSlot(final BookingRequestDto bookingRequestDto) {
        final String preBookingId = ofNullable(bookingRequestDto)
            .map(BookingRequestDto::getBookingUid)
            .orElseThrow(MissedBookingIdentifier::new);

        final ShippingDayScheduleDocument shippingDayScheduleDocument = findShippingDayScheduleDocument(
            bookingSlot ->
                preBookingId.equals(bookingSlot.getPreBookingId()) && isNotBooked(bookingSlot),
            BookedSlotsNotFound::new);

        shippingDayScheduleDocument
            .getDocument()
            .getShippingDayScheduleData()
            .getBrigades()
            .forEach(bookingSlots -> bookingSlots
                .getBookingSlots()
                .stream()
                .filter(slot -> preBookingId.equals(slot.getPreBookingId()))
                .forEach(slot -> {
                    if (isNotBookedOrPreBooked(slot)) {
                        changeSlotByPreBookingId(
                            bookingSlot -> {
                                bookingSlot.setPreBookingId(null);
                                bookingSlot.setPreBookedUntil(null);
                            },
                            preBookingId,
                            shippingDayScheduleDocument);
                        throw new BookedSlotsNotFound();
                    }
                }));

        changeSlotByPreBookingId(
            bookingSlot -> bookingSlot.setPreBookedUntil(null),
            preBookingId,
            shippingDayScheduleDocument
        );

        return BookingResultDto.builder()
            .bookingUid(preBookingId)
            .build();
    }

    /**
     * Coping day schedules.
     *
     * @param copingDaySchedulesRequestDto request
     */
    @Override
    @Transactional
    public void copyDaySchedules(
        final String id, final Boolean isConfirm, final CopingDaySchedulesRequestDto copingDaySchedulesRequestDto
    ) {
        final ShippingDayScheduleDocument document = shippingDayScheduleDocumentService.fetchDocument(id);
        final ShippingDayScheduleType shippingDaySchedule = document
            .getDocument()
            .getShippingDayScheduleData();

        final LocalDate startDate = copingDaySchedulesRequestDto.getStartDate();
        final LocalDate endDate = copingDaySchedulesRequestDto.getEndDate();

        final List<LocalDate> periodDates = PeriodUtils.getDatesPeriod(startDate, endDate);

        final Map<LocalDate, String> busyDates = getExistsShippingDayDates(
            periodDates,
            shippingDaySchedule.getArea()
        );

        if (busyDates.size() > 0) {
            final boolean isExistsBookedSlots = shippingDayScheduleDao.existsByShippingDatesAndNotNullPreBookingId(
                new ArrayList<>(busyDates.keySet()), shippingDaySchedule.getArea()
            );

            if (isExistsBookedSlots) {
                throw new UpdateScheduleWhenSLotWasBooked();
            }
        }

        if (!busyDates.isEmpty() && !isConfirm) {
            throw new ScheduleDayAlreadyExisted();
        }

        for (final LocalDate currentDate : periodDates) {
            if (busyDates.containsKey(currentDate)) {
                shippingDayScheduleDocumentService.deleteDocument(busyDates.get(currentDate), true, "copyDaySchedule");
            }

            shippingDaySchedule.getBrigades().forEach(brigade -> brigade.getBookingSlots().clear());
            shippingDaySchedule.setShippingDate(currentDate);
            shippingDayScheduleDocumentService.createDocument(document, true, "copyDaySchedule");
        }
    }

    @Override
    public ValidateShippingDateResponseDto validateShippingDateOrInterval(
        final ValidateShippingDateRequestDto validateShippingDateRequestDto
    ) {
        final LocalDate startDate = validateShippingDateRequestDto.getStartDate();
        final LocalDate endDate = validateShippingDateRequestDto.getEndDate();
        final List<LocalDate> periodDates = PeriodUtils.getDatesPeriod(startDate, endDate);

        final Map<LocalDate, String> busyDates = getExistsShippingDayDates(periodDates,
            validateShippingDateRequestDto.getDistrict());
        final boolean isAnyDayBusy = !busyDates.isEmpty();
        boolean isExistsBookedSlots = false;
        if (isAnyDayBusy) {
            isExistsBookedSlots = shippingDayScheduleDao.existsByShippingDatesAndNotNullPreBookingId(
                new ArrayList<>(busyDates.keySet()), validateShippingDateRequestDto.getDistrict()
            );
        }
        return ValidateShippingDateResponseDto.builder()
            .isExist(isAnyDayBusy)
            .canBeReplaced(!isExistsBookedSlots)
            .build();
    }

    @Override
    public void removeBooking(final String bookingId) {
        getShippingDayScheduleDocumentByBookingIdQuery(bookingId)
            .forEach(shippingDayScheduleDocument ->
                changeSlotByPreBookingId(
                    bookingSlot -> {
                        bookingSlot.setPreBookingId(null);
                        bookingSlot.setPreBookedUntil(null);
                    },
                    bookingId,
                    shippingDayScheduleDocument));
    }

    @Override
    public boolean checkExistsBooking(final String bookingId) {
        return !getShippingDayScheduleDocumentByBookingIdQuery(bookingId).isEmpty();
    }

    @Override
    public void removeBookedAndBookPreBooked(final String bookingId) {
        getShippingDayScheduleDocumentByBookingIdQuery(bookingId)
            .forEach(shippingDayScheduleDocument ->
                changeSlotByPreBookingId(
                    bookingSlot -> {
                        if (bookingSlot.getPreBookedUntil() == null) {
                            bookingSlot.setPreBookingId(null);
                        }
                        bookingSlot.setPreBookedUntil(null);
                    },
                    bookingId,
                    shippingDayScheduleDocument));
    }

    @Override
    public List<ShippingBookingTotalDto> fetchShippingBookingTotal(
        final LocalDate startDate, final LocalDate endDate, final String district
    ) {
        final List<LocalDate> periodDates = PeriodUtils.getDatesPeriod(startDate, endDate);

        return documentConverterService
            .parseDocumentData(
                shippingDayScheduleDao.findAllByShippingDatesAndArea(periodDates, district),
                ShippingDayScheduleDocument.class
            )
            .stream()
            .map(this::toShippingBookingTotalDto)
            .collect(Collectors.toList());
    }

    private ShippingBookingTotalDto toShippingBookingTotalDto(
        final ShippingDayScheduleDocument shippingDayScheduleDocument
    ) {
        final ShippingDayScheduleType shippingDaySchedule =
            shippingDayScheduleDocument.getDocument().getShippingDayScheduleData();
        final List<BrigadeType> brigades = shippingDaySchedule.getBrigades();

        final int totalBookings = calculateTotalBookings(brigades);
        final int totalSlots = calculateTotalSlots(brigades);

        return ShippingBookingTotalDto
            .builder()
            .shippingDate(shippingDaySchedule.getShippingDate())
            .totalBookings(totalBookings)
            .totalSlots(totalSlots)
            .build();
    }

    private int calculateTotalBookings(final List<BrigadeType> brigades) {
        return (int) brigades.stream()
            .map(BrigadeType::getBookingSlots)
            .flatMap(List::stream)
            .filter(DefaultBookingSlotService::isBooked)
            .count();
    }

    private int calculateTotalSlots(final List<BrigadeType> brigades) {
        return brigades.stream()
            .map(BrigadeType::getBookingSlots)
            .mapToInt(List::size)
            .sum();
    }

    @Override
    public List<ShippingBookingTotalDto> deleteDaySchedules(
        final DeletingDaySchedulesRequestDto requestDto, final boolean hasConfirm
    ) {
        final List<LocalDate> periodDates = PeriodUtils
            .getDatesPeriod(requestDto.getStartDate(), requestDto.getEndDate());

        final List<Pair<String, ShippingBookingTotalDto>> schedules = documentConverterService
            .parseDocumentData(
                shippingDayScheduleDao.findAllByShippingDatesAndArea(periodDates, requestDto.getDistrict()),
                ShippingDayScheduleDocument.class
            )
            .stream()
            .map(shippingDaySchedule ->
                Pair.of(shippingDaySchedule.getId(), toShippingBookingTotalDto(shippingDaySchedule)))
            .collect(Collectors.toList());

        if (schedules.isEmpty()) {
            throw new ScheduleDayNotFound();
        }

        if (!hasConfirm) {
            if (schedules.stream().anyMatch(e -> e.getRight().getTotalBookings() > 0)) {
                throw new DeleteScheduleWhenSlotWasBooked();
            }
        }

        return schedules.stream()
            .map(pair -> {
                final ShippingBookingTotalDto shippingBookingTotalDto = pair.getRight();
                if (shippingBookingTotalDto.getTotalBookings() == 0) {
                    shippingDayScheduleDocumentService.deleteDocument(pair.getLeft(), true, null);
                    return shippingBookingTotalDto.toBuilder()
                        .isRemoved(true)
                        .build();
                }

                return shippingBookingTotalDto;
            })
            .collect(Collectors.toList());
    }

    private List<ShippingDayScheduleDocument> getShippingDayScheduleDocumentByBookingIdQuery(
        final String bookingId
    ) {
        final String queryByBookingId = String.format(QUERY_BY_BOOKING_ID, bookingId);
        return shippingDayScheduleDocumentService.findDocuments(queryByBookingId);
    }

    private void changeSlotByPreBookingId(
        final Consumer<BookingSlotType> bookingSlotConsumer,
        final String preBookingId,
        final ShippingDayScheduleDocument shippingDayScheduleDocument
    ) {
        shippingDayScheduleDocument
            .getDocument()
            .getShippingDayScheduleData()
            .getBrigades().stream()
            .map(BrigadeType::getBookingSlots)
            .flatMap(List::stream)
            .filter(bookingSlot -> preBookingId.equals(bookingSlot.getPreBookingId()))
            .forEach(bookingSlotConsumer);

        shippingDayScheduleDocumentService.updateDocument(
            shippingDayScheduleDocument.getId(),
            shippingDayScheduleDocument,
            true,
            true,
            null
        );
    }

    private Map<LocalDate, String> getExistsShippingDayDates(final List<LocalDate> periodDates, final String area) {
        if (CollectionUtils.isEmpty(periodDates) || area == null) {
            return Collections.emptyMap();
        }

        return documentConverterService
            .parseDocumentData(
                shippingDayScheduleDao.findAllByShippingDatesAndArea(periodDates, area),
                ShippingDayScheduleDocument.class
            )
            .stream()
            .collect(Collectors.toMap(
                shippingDayScheduleDocument -> shippingDayScheduleDocument.getDocument()
                    .getShippingDayScheduleData()
                    .getShippingDate(),
                ShippingDayScheduleDocument::getId,
                (s1, s2) -> s1
            ));
    }

    private ShippingDayScheduleDocument findShippingDayScheduleDocument(
        final Predicate<BookingSlotType> filterPredicate,
        final Supplier<SsrException> exceptionSupplier
    ) {
        return shippingDayScheduleDao.findAll()
            .stream()
            .map(document -> documentConverterService
                .parseDocumentData(document, ShippingDayScheduleDocument.class))
            .filter(shippingDaySchedule -> ofNullable(shippingDaySchedule)
                .map(ShippingDayScheduleDocument::getDocument)
                .map(ShippingDaySchedule::getShippingDayScheduleData)
                .map(ShippingDayScheduleType::getBrigades)
                .orElse(Collections.emptyList())
                .stream()
                .map(BrigadeType::getBookingSlots)
                .flatMap(List::stream)
                .filter(Objects::nonNull)
                .anyMatch(filterPredicate))
            .findFirst()
            .orElseThrow(exceptionSupplier);
    }

    private static boolean isNotBooked(final BookingSlotType bookingSlot) {
        return Objects.nonNull(bookingSlot.getPreBookedUntil());
    }

    private static boolean isNotBookedOrPreBooked(final BookingSlotType bookingSlot) {
        return Objects.isNull(bookingSlot.getPreBookingId()) || hasPreBookingExpired(bookingSlot.getPreBookedUntil());
    }

    private static boolean isBooked(final BookingSlotType bookingSlot) {
        return Objects.isNull(bookingSlot.getPreBookedUntil()) && Objects.nonNull(bookingSlot.getPreBookingId());
    }

    private static boolean hasPreBookingExpired(final LocalDateTime preBookedUntil) {
        return Objects.nonNull(preBookedUntil) && preBookedUntil.isBefore(LocalDateTime.now());
    }

    private String generatePreBookingId(
        final String snils, final String ssoId, final String unomOld
    ) {
        validatePrebookingGenerateData(snils);
        final String uniqueUserIdentifier =
            ofNullable(snils).orElse("") + ofNullable(ssoId).orElse("") + unomOld;
        return UUID.nameUUIDFromBytes(uniqueUserIdentifier.getBytes()).toString();
    }

    private String generatePreBookingId(
        final String personDocumentId, final String unomOld
    ) {
        final String uniqueUserIdentifier = ofNullable(personDocumentId).orElse("") + unomOld;
        return UUID.nameUUIDFromBytes(uniqueUserIdentifier.getBytes()).toString();
    }

    private static void validatePrebookingGenerateData(final String snils) {
        ofNullable(snils).orElseThrow(MissedSnils::new);
    }

    @Override
    public void cleanEmptyBookings() {
        final List<String> allBookingIds = shippingApplicationDao.findAllBookingIds();
        shippingDayScheduleDao.findAll()
            .stream()
            .map(document -> documentConverterService.parseDocumentData(document, ShippingDayScheduleDocument.class))
            .forEach(shippingDayScheduleDocument -> {
                of(shippingDayScheduleDocument.getDocument())
                    .map(ShippingDaySchedule::getShippingDayScheduleData)
                    .map(ShippingDayScheduleType::getBrigades)
                    .map(List::stream)
                    .orElse(Stream.empty())
                    .map(BrigadeType::getBookingSlots)
                    .flatMap(List::stream)
                    .filter(DefaultBookingSlotService::isBooked)
                    .forEach(bookingSlot -> {
                        if (!allBookingIds.contains(bookingSlot.getPreBookingId())) {
                            log.debug(
                                "Clean empty bookings: slotId {}, shipping day schedule {}",
                                bookingSlot.getSlotId(),
                                shippingDayScheduleDocument.getId()
                            );
                            bookingSlot.setPreBookingId(null);
                        }
                    });

                shippingDayScheduleDocumentService.updateDocument(
                    shippingDayScheduleDocument.getId(),
                    shippingDayScheduleDocument,
                    true,
                    true,
                    null
                );
            });
    }

    @Override
    public void recreateBookingSlots() {
        shippingDayScheduleDao.findAll()
            .stream()
            .map(document -> documentConverterService.parseDocumentData(document, ShippingDayScheduleDocument.class))
            .forEach(shippingDayScheduleDocument -> {
                of(shippingDayScheduleDocument.getDocument())
                    .map(ShippingDaySchedule::getShippingDayScheduleData)
                    .map(ShippingDayScheduleType::getBrigades)
                    .map(List::stream)
                    .orElse(Stream.empty())
                    .filter(brigadeType -> brigadeType
                        .getBookingSlots()
                        .stream()
                        .noneMatch(DefaultBookingSlotService::isBooked)
                    )
                    .forEach(brigadeType -> {
                        log.debug(
                            "Recreate booking slots: brigade {}, shipping day schedule {}",
                            brigadeType.getName(),
                            shippingDayScheduleDocument.getId()
                        );
                        final List<BookingSlotType> bookingSlotsByIntervals = shippingDayScheduleDocumentService
                            .getBookingSlotsByIntervals(brigadeType.getWorkingIntervals());

                        brigadeType.getBookingSlots().clear();
                        brigadeType.getBookingSlots().addAll(bookingSlotsByIntervals);
                    });

                shippingDayScheduleDocumentService.updateDocument(
                    shippingDayScheduleDocument.getId(),
                    shippingDayScheduleDocument,
                    true,
                    true,
                    null
                );
            });
    }
}
