package ru.croc.ugd.ssr.service.flatappointment;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.croc.ugd.ssr.PersonType.OfferLetters.OfferLetter;
import ru.croc.ugd.ssr.dayschedule.SlotType;
import ru.croc.ugd.ssr.dayschedule.WindowType;
import ru.croc.ugd.ssr.dayschedule.flatappointment.FlatAppointmentDaySchedule;
import ru.croc.ugd.ssr.dayschedule.flatappointment.FlatAppointmentDayScheduleData;
import ru.croc.ugd.ssr.exception.AlreadyBookedForEno;
import ru.croc.ugd.ssr.exception.BookedSlotsNotFound;
import ru.croc.ugd.ssr.exception.InvalidDataInput;
import ru.croc.ugd.ssr.exception.MissedBookingIdentifier;
import ru.croc.ugd.ssr.exception.MissedSlotIdentifier;
import ru.croc.ugd.ssr.exception.SlotsNotFound;
import ru.croc.ugd.ssr.exception.SsrException;
import ru.croc.ugd.ssr.flatappointment.FlatAppointmentData;
import ru.croc.ugd.ssr.generated.dto.flatappointment.ExternalRestFlatAppointmentBookRequestDto;
import ru.croc.ugd.ssr.generated.dto.flatappointment.ExternalRestFlatAppointmentDto;
import ru.croc.ugd.ssr.generated.dto.flatappointment.ExternalRestFlatAppointmentPersonCheckDto;
import ru.croc.ugd.ssr.generated.dto.flatappointment.ExternalRestFlatAppointmentPrebookRequestDto;
import ru.croc.ugd.ssr.generated.dto.flatappointment.ExternalRestFlatAppointmentPrebookResponseDto;
import ru.croc.ugd.ssr.generated.dto.flatappointment.ExternalRestFlatAppointmentSlotDto;
import ru.croc.ugd.ssr.generated.dto.flatappointment.ExternalRestFlatAppointmentTimetableDto;
import ru.croc.ugd.ssr.mapper.FlatAppointmentMapper;
import ru.croc.ugd.ssr.model.CipDocument;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.flatAppointment.FlatAppointmentDayScheduleDocument;
import ru.croc.ugd.ssr.model.flatAppointment.FlatAppointmentDocument;
import ru.croc.ugd.ssr.service.ChedFileService;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.cip.CipService;
import ru.croc.ugd.ssr.service.document.FlatAppointmentDayScheduleDocumentService;
import ru.croc.ugd.ssr.utils.PersonUtils;
import ru.croc.ugd.ssr.utils.StreamUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class DefaultRestExternalFlatAppointmentService implements RestExternalFlatAppointmentService {

    private final CipService cipService;
    private final FlatAppointmentService flatAppointmentService;
    private final FlatAppointmentMapper flatAppointmentMapper;
    private final PersonDocumentService personDocumentService;
    private final FlatAppointmentDayScheduleDocumentService flatAppointmentDayScheduleDocumentService;
    private final FlatAppointmentCheckService flatAppointmentCheckService;
    private final ChedFileService chedFileService;

    @Override
    public ExternalRestFlatAppointmentDto fetchByEno(final String eno) {
        final FlatAppointmentDocument flatAppointmentDocument = flatAppointmentService
            .fetchByEno(eno);
        final FlatAppointmentData flatAppointment = flatAppointmentDocument
            .getDocument()
            .getFlatAppointmentData();

        final PersonDocument personDocument = personDocumentService
            .fetchDocument(flatAppointment.getApplicant().getPersonDocumentId());

        final OfferLetter letter = PersonUtils
            .getOfferLetter(personDocument, flatAppointment.getOfferLetterId())
            .orElse(null);

        final CipDocument cip = ofNullable(flatAppointment.getCipId())
            .flatMap(cipService::fetchById)
            .orElse(null);

        return flatAppointmentMapper.toExternalRestFlatAppointmentDto(
            flatAppointment, cip, letter, this::toOfferLetterChedDownloadUrl
        );
    }

    @Override
    public ExternalRestFlatAppointmentPersonCheckDto check(String snils, String ssoId) {
        return flatAppointmentCheckService.checkPerson(snils, ssoId);
    }

    @Override
    public ExternalRestFlatAppointmentTimetableDto fetchTimetable(String cipId, Integer days, LocalDate startFrom) {
        final List<FlatAppointmentDayScheduleDocument> daySchedules = flatAppointmentDayScheduleDocumentService
            .fetchAllByCipIdAndDate(cipId, startFrom, days, true);

        final List<ExternalRestFlatAppointmentSlotDto> slotDtos = new ArrayList<>();
        daySchedules
            .stream()
            .map(FlatAppointmentDayScheduleDocument::getDocument)
            .map(FlatAppointmentDaySchedule::getFlatAppointmentDayScheduleData)
            .forEach(daySchedule -> {
                final List<ExternalRestFlatAppointmentSlotDto> bookingSlotsPerDay = daySchedule
                    .getSlots()
                    .getSlot()
                    .stream()
                    .filter(StreamUtils.distinctByKey(slot ->
                        String.format("%s - %s", slot.getTimeFrom(), slot.getTimeTo())))
                    .map(slot -> {
                        final ExternalRestFlatAppointmentSlotDto slotDto = new ExternalRestFlatAppointmentSlotDto();
                        slotDto.setSlotId(slot.getSlotId());
                        slotDto.setTimeFrom(LocalDateTime.of(daySchedule.getDate(), slot.getTimeFrom()));
                        slotDto.setTimeTo(LocalDateTime.of(daySchedule.getDate(), slot.getTimeTo()));
                        return slotDto;
                    })
                    .collect(Collectors.toList());
                slotDtos.addAll(bookingSlotsPerDay);
            });

        final ExternalRestFlatAppointmentTimetableDto restTimetableResponseDto =
            new ExternalRestFlatAppointmentTimetableDto();
        restTimetableResponseDto.timetable(slotDtos);

        return restTimetableResponseDto;
    }

    @Override
    public ExternalRestFlatAppointmentPrebookResponseDto prebookSlot(
        final ExternalRestFlatAppointmentPrebookRequestDto body
    ) {
        final String slotId = ofNullable(body.getSlotId()).orElseThrow(MissedSlotIdentifier::new);
        final String letterId = ofNullable(body.getLetterId()).orElseThrow(InvalidDataInput::new);
        final int slotNumber = Integer.parseInt(ofNullable(body.getSlotNumber()).orElse("1"));

        final String preBookingId = generatePreBookingId(letterId);

        dropExistingPreBookingSlots(preBookingId);

        final FlatAppointmentDayScheduleDocument flatAppointmentDayScheduleDocument =
            flatAppointmentDayScheduleDocumentService
                .fetchBySlotId(slotId)
                .orElseThrow(SlotsNotFound::new);

        final List<SlotType> slots = flatAppointmentDayScheduleDocument
            .getDocument()
            .getFlatAppointmentDayScheduleData()
            .getSlots()
            .getSlot()
            .stream()
            .sorted(Comparator.comparing(SlotType::getTimeFrom))
            .collect(Collectors.toList());

        while (slots.size() > 0) {
            if (StringUtils.equals(slots.get(0).getSlotId(), slotId)) {
                break;
            }
            slots.remove(0);
        }

        if (slots.size() < slotNumber) {
            throw new SsrException("Выбранные слоты уже забронированы для другого заявления");
        }

        slots.stream()
            .limit(slotNumber)
            .forEach(slot -> {
                final WindowType windowFound = slot.getWindows().getWindow()
                    .stream()
                    .filter(this::isWindowFree)
                    .findFirst()
                    .orElseThrow(() -> new SsrException("Выбранные слоты уже забронированы для другого заявления"));
                setSlotBookingData(
                    letterId,
                    preBookingId,
                    windowFound
                );
            });

        flatAppointmentDayScheduleDocumentService.updateDocument(
            flatAppointmentDayScheduleDocument.getId(),
            flatAppointmentDayScheduleDocument,
            true,
            false,
            null
        );

        final ExternalRestFlatAppointmentPrebookResponseDto prebookSlotResponseDto =
            new ExternalRestFlatAppointmentPrebookResponseDto();
        prebookSlotResponseDto.setBookingId(preBookingId);
        return prebookSlotResponseDto;
    }

    @Override
    @Transactional
    public void bookSlot(
        final ExternalRestFlatAppointmentBookRequestDto body, final String flatAppointmentDocumentId
    ) {
        final String bookingId = ofNullable(body.getBookingId()).orElseThrow(MissedBookingIdentifier::new);
        final boolean isSsrMoveDate = nonNull(flatAppointmentDocumentId);
        if (!isSsrMoveDate && flatAppointmentService.isAppointmentRegisteredByBookingId(bookingId)) {
            throw new AlreadyBookedForEno();
        }

        final FlatAppointmentDayScheduleDocument flatAppointmentDayScheduleDocument =
            flatAppointmentDayScheduleDocumentService
                .findByPreBookingIdAndPreBookedUntilIsNotNull(bookingId)
                .orElseThrow(BookedSlotsNotFound::new);

        final FlatAppointmentDayScheduleData flatAppointmentDaySchedule = flatAppointmentDayScheduleDocument
            .getDocument()
            .getFlatAppointmentDayScheduleData();

        final LocalTime timeFrom = flatAppointmentDaySchedule
            .getSlots()
            .getSlot()
            .stream()
            .filter(slot -> slot
                .getWindows()
                .getWindow()
                .stream()
                .filter(window -> StringUtils.equals(bookingId, window.getPreBookingId()))
                .anyMatch(this::isPreBookingNotExpired)
            )
            .map(SlotType::getTimeFrom)
            .min(LocalTime::compareTo)
            .orElseThrow(() -> new SsrException("Выбранные слоты уже забронированы для другого заявления"));

        if (isSsrMoveDate) {
            final LocalDateTime appointmentDateTime = flatAppointmentDaySchedule.getDate().atTime(timeFrom);
            flatAppointmentService.moveAppointmentDateTime(flatAppointmentDocumentId, bookingId, appointmentDateTime);

            // cancel booking for other days if exists
            flatAppointmentDayScheduleDocumentService.cancelBooking(bookingId);

            // cancel booking for current document
            flatAppointmentDayScheduleDocumentService.cancelDayScheduleBooking(
                flatAppointmentDaySchedule, bookingId, null
            );
        }

        // and set new booking
        flatAppointmentDaySchedule
            .getSlots()
            .getSlot()
            .stream()
            .map(SlotType::getWindows)
            .map(SlotType.Windows::getWindow)
            .flatMap(Collection::stream)
            .filter(window -> StringUtils.equals(bookingId, window.getPreBookingId()))
            .filter(this::isPreBookingNotExpired)
            .forEach(window -> window.setPreBookedUntil(null));

        flatAppointmentDayScheduleDocumentService.updateDocument(
            flatAppointmentDayScheduleDocument.getId(),
            flatAppointmentDayScheduleDocument,
            true,
            true,
            null
        );
    }

    private String generatePreBookingId(final String letterId) {
        return UUID.nameUUIDFromBytes(letterId.getBytes()).toString();
    }

    private boolean isPreBookingNotExpired(final WindowType window) {
        return Objects.nonNull(window.getPreBookedUntil())
            && window.getPreBookedUntil().isAfter(LocalDateTime.now());
    }

    private boolean isWindowFree(final WindowType window) {
        return isNull(window.getPreBookingId())
            || Objects.nonNull(window.getPreBookedUntil())
            && window.getPreBookedUntil().isBefore(LocalDateTime.now());
    }

    private void dropExistingPreBookingSlots(final String preBookingId) {
        flatAppointmentDayScheduleDocumentService.fetchAllByPrebookingId(preBookingId)
            .forEach(flatAppointmentDayScheduleDocument -> {
                final FlatAppointmentDayScheduleData flatAppointmentDaySchedule = flatAppointmentDayScheduleDocument
                    .getDocument()
                    .getFlatAppointmentDayScheduleData();

                flatAppointmentDayScheduleDocumentService
                    .cancelDaySchedulePreBooking(flatAppointmentDaySchedule, preBookingId);

                flatAppointmentDayScheduleDocumentService.updateDocument(
                    flatAppointmentDayScheduleDocument.getId(),
                    flatAppointmentDayScheduleDocument,
                    true,
                    true,
                    null
                );
            });
    }

    private void setSlotBookingData(
        final String letterId,
        final String preBookingId,
        final WindowType window
    ) {
        final LocalDateTime preBookUntil = LocalDateTime.now().plus(15, ChronoUnit.MINUTES);
        window.setPreBookedUntil(preBookUntil);
        window.setPreBookingId(preBookingId);
        window.setOfferLetterId(letterId);
    }

    private String toOfferLetterChedDownloadUrl(final OfferLetter offerLetter) {
        return PersonUtils.getOfferLetterChedId(offerLetter)
            .flatMap(chedFileService::getChedFileLink)
            .orElse(null);
    }
}
