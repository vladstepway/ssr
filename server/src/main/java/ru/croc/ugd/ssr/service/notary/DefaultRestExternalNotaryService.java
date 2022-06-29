package ru.croc.ugd.ssr.service.notary;

import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.croc.ugd.ssr.dayschedule.SlotType;
import ru.croc.ugd.ssr.dayschedule.WindowType;
import ru.croc.ugd.ssr.dayschedule.notary.NotaryDaySchedule;
import ru.croc.ugd.ssr.dayschedule.notary.NotaryDayScheduleType;
import ru.croc.ugd.ssr.exception.AlreadyBookedForEno;
import ru.croc.ugd.ssr.exception.BookedSlotsNotFound;
import ru.croc.ugd.ssr.exception.InvalidDataInput;
import ru.croc.ugd.ssr.exception.MissedBookingIdentifier;
import ru.croc.ugd.ssr.exception.MissedSlotIdentifier;
import ru.croc.ugd.ssr.exception.SlotsNotFound;
import ru.croc.ugd.ssr.exception.SsrException;
import ru.croc.ugd.ssr.generated.dto.notary.RestNotaryApplicationDto;
import ru.croc.ugd.ssr.generated.dto.notary.RestNotaryBookSlotRequestDto;
import ru.croc.ugd.ssr.generated.dto.notary.RestNotaryBookingSlotDto;
import ru.croc.ugd.ssr.generated.dto.notary.RestNotaryListDto;
import ru.croc.ugd.ssr.generated.dto.notary.RestNotaryPersonCheckDto;
import ru.croc.ugd.ssr.generated.dto.notary.RestNotaryPrebookSlotRequestDto;
import ru.croc.ugd.ssr.generated.dto.notary.RestNotaryPrebookSlotResponseDto;
import ru.croc.ugd.ssr.generated.dto.notary.RestNotaryTimetableDto;
import ru.croc.ugd.ssr.mapper.NotaryApplicationMapper;
import ru.croc.ugd.ssr.mapper.NotaryMapper;
import ru.croc.ugd.ssr.model.NotaryDayScheduleDocument;
import ru.croc.ugd.ssr.model.notary.NotaryApplicationDocument;
import ru.croc.ugd.ssr.model.notary.NotaryDocument;
import ru.croc.ugd.ssr.notary.NotaryApplicationType;
import ru.croc.ugd.ssr.service.document.NotaryDayScheduleDocumentService;
import ru.croc.ugd.ssr.service.document.NotaryDocumentService;
import ru.croc.ugd.ssr.utils.StreamUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class DefaultRestExternalNotaryService implements RestExternalNotaryService {

    private final NotaryDocumentService notaryDocumentService;
    private final NotaryApplicationService notaryApplicationService;
    private final NotaryApplicationMapper notaryApplicationMapper;
    private final NotaryDayScheduleDocumentService notaryDayScheduleDocumentService;
    private final NotaryMapper notaryMapper;
    private final NotaryApplicationCheckService notaryApplicationCheckService;

    @Override
    public RestNotaryListDto fetchAll() {
        final List<NotaryDocument> notaries = notaryDocumentService.findAll(null, null, false);
        return notaryMapper.toRestNotaryListDto(notaries);
    }

    @Override
    public RestNotaryApplicationDto fetchNotaryApplication(final String eno) {
        final NotaryApplicationDocument notaryApplicationDocument = notaryApplicationService.fetchByEno(eno);
        final NotaryApplicationType notaryApplication = notaryApplicationDocument
            .getDocument()
            .getNotaryApplicationData();

        final String notaryId = notaryApplication.getNotaryId();
        final NotaryDocument notaryDocument = notaryDocumentService.fetchDocument(notaryId);

        return notaryApplicationMapper.toExternalRestNotaryApplicationResponseDto(
            notaryApplicationDocument, notaryDocument
        );
    }

    @Override
    public RestNotaryPersonCheckDto check(final String snils, final String ssoId) {
        return notaryApplicationCheckService.getExternalCheckResult(snils, ssoId);
    }

    @Override
    public RestNotaryTimetableDto fetchTimetable(final String notaryId, final LocalDate startFrom, final Integer days) {
        final String nonBlankNotaryId = ofNullable(notaryId)
            .filter(StringUtils::isNotBlank)
            .orElseThrow(InvalidDataInput::new);
        final List<NotaryDayScheduleDocument> notaryDaySchedules =
            notaryDayScheduleDocumentService.fetchAllByNotaryIdAndDate(nonBlankNotaryId, startFrom, days, true);

        final List<RestNotaryBookingSlotDto> slotDtos = new ArrayList<>();
        notaryDaySchedules
            .stream()
            .map(NotaryDayScheduleDocument::getDocument)
            .map(NotaryDaySchedule::getNotaryDayScheduleData)
            .forEach(notaryDaySchedule -> {
                final List<RestNotaryBookingSlotDto> bookingSlotsPerDay = notaryDaySchedule
                    .getSlots()
                    .getSlot()
                    .stream()
                    .filter(StreamUtils.distinctByKey(bookingSlotType ->
                        String.format("%s - %s", bookingSlotType.getTimeFrom(), bookingSlotType.getTimeTo())))
                    .map(bookingSlot -> {
                        final RestNotaryBookingSlotDto slotDto = new RestNotaryBookingSlotDto();
                        slotDto.setSlotId(bookingSlot.getSlotId());
                        slotDto.setTimeFrom(LocalDateTime.of(notaryDaySchedule.getDate(), bookingSlot.getTimeFrom()));
                        slotDto.setTimeTo(LocalDateTime.of(notaryDaySchedule.getDate(), bookingSlot.getTimeTo()));
                        return slotDto;
                    })
                    .collect(Collectors.toList());
                slotDtos.addAll(bookingSlotsPerDay);
            });

        final RestNotaryTimetableDto restTimetableResponseDto = new RestNotaryTimetableDto();
        restTimetableResponseDto.timetable(slotDtos);

        return restTimetableResponseDto;
    }

    @Override
    public RestNotaryPrebookSlotResponseDto prebookSlot(final RestNotaryPrebookSlotRequestDto body) {
        final String slotId = ofNullable(body.getSlotId())
            .orElseThrow(MissedSlotIdentifier::new);
        final String eno = ofNullable(body.getEno())
            .filter(StringUtils::isNotBlank)
            .orElseThrow(InvalidDataInput::new);

        final String preBookingId = generatePreBookingId(eno);

        dropExistingPreBookingSlots(preBookingId);

        if (notaryDayScheduleDocumentService.isAlreadyBooked(preBookingId)) {
            throw new AlreadyBookedForEno();
        }

        final NotaryDayScheduleDocument notaryDayScheduleDocument = notaryDayScheduleDocumentService
            .fetchBySlotId(slotId)
            .orElseThrow(SlotsNotFound::new);

        notaryDayScheduleDocument
            .getDocument()
            .getNotaryDayScheduleData()
            .getSlots()
            .getSlot()
            .stream()
            .filter(slotType -> StringUtils.equals(slotType.getSlotId(), slotId))
            .findFirst()
            .ifPresent(slotType -> {
                final WindowType windowFound = slotType.getWindows().getWindow()
                    .stream()
                    .filter(this::isWindowFree)
                    .findFirst()
                    .orElseThrow(() -> new SsrException("Выбранный слот уже забронирован для другого заявления"));
                setSlotBookingData(
                    body.getEno(),
                    preBookingId,
                    windowFound
                );
            });
        notaryDayScheduleDocumentService.updateDocument(
            notaryDayScheduleDocument.getId(),
            notaryDayScheduleDocument,
            true,
            false,
            null
        );
        final RestNotaryPrebookSlotResponseDto prebookSlotResponseDto = new RestNotaryPrebookSlotResponseDto();
        prebookSlotResponseDto.setBookingId(preBookingId);

        return prebookSlotResponseDto;
    }

    @Override
    @Transactional
    public void bookSlot(final RestNotaryBookSlotRequestDto body, final Boolean isSsrBooking) {
        final String eno = ofNullable(body.getEno()).orElseThrow(InvalidDataInput::new);
        final String bookingId = ofNullable(body.getBookingId()).orElseThrow(MissedBookingIdentifier::new);
        if (notaryDayScheduleDocumentService.isAlreadyBooked(bookingId)) {
            throw new AlreadyBookedForEno();
        }
        final NotaryDayScheduleDocument notaryDayScheduleDocument = notaryDayScheduleDocumentService
            .fetchByPreBookingIdAndEno(bookingId, eno)
            .orElseThrow(BookedSlotsNotFound::new);

        final NotaryDayScheduleType notaryDaySchedule = notaryDayScheduleDocument
            .getDocument()
            .getNotaryDayScheduleData();

        final SlotType slot = notaryDaySchedule
            .getSlots()
            .getSlot()
            .stream()
            .filter(slotType -> isFutureSlot(slotType, notaryDaySchedule.getDate()))
            .filter(slotType -> slotType
                .getWindows()
                .getWindow()
                .stream()
                .filter(window -> StringUtils.equals(bookingId, window.getPreBookingId()))
                .filter(window -> StringUtils.equals(eno, window.getEno()))
                .anyMatch(this::isPreBookingNotExpired)
            )
            .findFirst()
            .orElseThrow(() -> new SsrException("Выбранный слот уже забронирован для другого заявления"));

        slot.getWindows()
            .getWindow()
            .stream()
            .filter(window -> StringUtils.equals(bookingId, window.getPreBookingId()))
            .filter(window -> StringUtils.equals(eno, window.getEno()))
            .filter(this::isPreBookingNotExpired)
            .findFirst()
            .ifPresent(window -> {
                window.setPreBookedUntil(null);

                final LocalDateTime appointmentDateTime = notaryDaySchedule.getDate().atTime(slot.getTimeFrom());
                notaryApplicationService.processBookingRequest(eno, bookingId, appointmentDateTime, isSsrBooking);
            });

        notaryDayScheduleDocumentService.updateDocument(
            notaryDayScheduleDocument.getId(),
            notaryDayScheduleDocument,
            true,
            true,
            null
        );
    }

    private void dropExistingPreBookingSlots(final String preBookingId) {
        notaryDayScheduleDocumentService.fetchAllByPrebookingId(preBookingId)
            .forEach(doc -> {
                doc.getDocument()
                    .getNotaryDayScheduleData()
                    .getSlots()
                    .getSlot()
                    .forEach(slotType -> slotType
                        .getWindows()
                        .getWindow()
                        .stream()
                        .filter(window -> preBookingId.equals(window.getPreBookingId())
                            && Objects.nonNull(window.getPreBookedUntil()))
                        .forEach(window -> {
                            window.setPreBookingId(null);
                            window.setPreBookedUntil(null);
                            window.setEno(null);
                        }));
                notaryDayScheduleDocumentService.updateDocument(
                    doc.getId(),
                    doc,
                    true,
                    true,
                    null
                );
            });
    }

    private void setSlotBookingData(
        final String eno,
        final String preBookingId,
        final WindowType window
    ) {
        final LocalDateTime preBookUntil = LocalDateTime.now().plus(15, ChronoUnit.MINUTES);
        window.setEno(eno);
        window.setPreBookingId(preBookingId);
        window.setPreBookedUntil(preBookUntil);
    }

    private boolean isFutureSlot(final SlotType slot, final LocalDate date) {
        final LocalDateTime slotDateTime = LocalDateTime.of(date, slot.getTimeFrom());
        return slotDateTime.isAfter(LocalDateTime.now());
    }

    private boolean isPreBookingNotExpired(final WindowType window) {
        return Objects.nonNull(window.getPreBookedUntil())
            && window.getPreBookedUntil().isAfter(LocalDateTime.now());
    }

    private boolean isWindowFree(final WindowType window) {
        return Objects.isNull(window.getPreBookingId())
            || Objects.nonNull(window.getPreBookedUntil())
            && window.getPreBookedUntil().isBefore(LocalDateTime.now());
    }

    private String generatePreBookingId(final String eno) {
        return UUID.nameUUIDFromBytes(eno.getBytes()).toString();
    }

}
