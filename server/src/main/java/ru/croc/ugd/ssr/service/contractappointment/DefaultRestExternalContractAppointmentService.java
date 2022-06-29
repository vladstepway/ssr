package ru.croc.ugd.ssr.service.contractappointment;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.croc.ugd.ssr.contractappointment.ContractAppointmentData;
import ru.croc.ugd.ssr.dayschedule.SlotType;
import ru.croc.ugd.ssr.dayschedule.WindowType;
import ru.croc.ugd.ssr.dayschedule.contractappointment.ContractAppointmentDaySchedule;
import ru.croc.ugd.ssr.dayschedule.contractappointment.ContractAppointmentDayScheduleData;
import ru.croc.ugd.ssr.exception.AlreadyBookedForEno;
import ru.croc.ugd.ssr.exception.BookedSlotsNotFound;
import ru.croc.ugd.ssr.exception.InvalidDataInput;
import ru.croc.ugd.ssr.exception.MissedBookingIdentifier;
import ru.croc.ugd.ssr.exception.MissedSlotIdentifier;
import ru.croc.ugd.ssr.exception.SlotsNotFound;
import ru.croc.ugd.ssr.exception.SsrException;
import ru.croc.ugd.ssr.generated.dto.contractappointment.ExternalRestContractAppointmentBookRequestDto;
import ru.croc.ugd.ssr.generated.dto.contractappointment.ExternalRestContractAppointmentDto;
import ru.croc.ugd.ssr.generated.dto.contractappointment.ExternalRestContractAppointmentPersonCheckDto;
import ru.croc.ugd.ssr.generated.dto.contractappointment.ExternalRestContractAppointmentPrebookRequestDto;
import ru.croc.ugd.ssr.generated.dto.contractappointment.ExternalRestContractAppointmentPrebookResponseDto;
import ru.croc.ugd.ssr.generated.dto.contractappointment.ExternalRestContractAppointmentSlotDto;
import ru.croc.ugd.ssr.generated.dto.contractappointment.ExternalRestContractAppointmentTimetableDto;
import ru.croc.ugd.ssr.mapper.ContractAppointmentMapper;
import ru.croc.ugd.ssr.model.CipDocument;
import ru.croc.ugd.ssr.model.contractappointment.ContractAppointmentDayScheduleDocument;
import ru.croc.ugd.ssr.model.contractappointment.ContractAppointmentDocument;
import ru.croc.ugd.ssr.service.cip.CipService;
import ru.croc.ugd.ssr.service.document.ContractAppointmentDayScheduleDocumentService;
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
public class DefaultRestExternalContractAppointmentService implements RestExternalContractAppointmentService {

    private final CipService cipService;
    private final ContractAppointmentService contractAppointmentService;
    private final ContractAppointmentMapper contractAppointmentMapper;
    private final ContractAppointmentDayScheduleDocumentService contractAppointmentDayScheduleDocumentService;
    private final ContractAppointmentCheckService contractAppointmentCheckService;

    @Override
    public ExternalRestContractAppointmentDto fetchByEno(final String eno) {
        final ContractAppointmentDocument contractAppointmentDocument = contractAppointmentService
            .fetchByEno(eno);
        final ContractAppointmentData contractAppointment = contractAppointmentDocument
            .getDocument()
            .getContractAppointmentData();

        final CipDocument cip = ofNullable(contractAppointment.getCipId())
            .flatMap(cipService::fetchById)
            .orElse(null);

        return contractAppointmentMapper.toExternalRestContractAppointmentDto(
            contractAppointment, cip
        );
    }

    @Override
    public ExternalRestContractAppointmentTimetableDto fetchTimetable(
        final String cipId, final Integer days, final LocalDate startFrom
    ) {
        final List<ContractAppointmentDayScheduleDocument> daySchedules = contractAppointmentDayScheduleDocumentService
            .fetchAllByCipIdAndDate(cipId, startFrom, days, true);

        final List<ExternalRestContractAppointmentSlotDto> slotDtos = new ArrayList<>();
        daySchedules.stream()
            .map(ContractAppointmentDayScheduleDocument::getDocument)
            .map(ContractAppointmentDaySchedule::getContractAppointmentDayScheduleData)
            .forEach(daySchedule -> {
                final List<ExternalRestContractAppointmentSlotDto> bookingSlotsPerDay = daySchedule
                    .getSlots()
                    .getSlot()
                    .stream()
                    .filter(StreamUtils.distinctByKey(slot ->
                        String.format("%s - %s", slot.getTimeFrom(), slot.getTimeTo())))
                    .map(slot -> {
                        final ExternalRestContractAppointmentSlotDto slotDto =
                            new ExternalRestContractAppointmentSlotDto();
                        slotDto.setSlotId(slot.getSlotId());
                        slotDto.setTimeFrom(LocalDateTime.of(daySchedule.getDate(), slot.getTimeFrom()));
                        slotDto.setTimeTo(LocalDateTime.of(daySchedule.getDate(), slot.getTimeTo()));
                        return slotDto;
                    })
                    .collect(Collectors.toList());
                slotDtos.addAll(bookingSlotsPerDay);
            });

        final ExternalRestContractAppointmentTimetableDto restTimetableResponseDto =
            new ExternalRestContractAppointmentTimetableDto();
        restTimetableResponseDto.timetable(slotDtos);

        return restTimetableResponseDto;
    }

    @Override
    public ExternalRestContractAppointmentPrebookResponseDto prebookSlot(
        ExternalRestContractAppointmentPrebookRequestDto body
    ) {
        final String slotId = ofNullable(body.getSlotId()).orElseThrow(MissedSlotIdentifier::new);
        final String contractOrderId = ofNullable(body.getContractOrderId()).orElseThrow(InvalidDataInput::new);

        final String preBookingId = generatePreBookingId(contractOrderId);

        dropExistingPreBookingSlots(preBookingId);

        final ContractAppointmentDayScheduleDocument contractAppointmentDayScheduleDocument =
            contractAppointmentDayScheduleDocumentService
                .fetchBySlotId(slotId)
                .orElseThrow(SlotsNotFound::new);

        contractAppointmentDayScheduleDocument
            .getDocument()
            .getContractAppointmentDayScheduleData()
            .getSlots()
            .getSlot()
            .stream()
            .filter(slot -> StringUtils.equals(slot.getSlotId(), slotId))
            .findFirst()
            .ifPresent(slotType -> {
                final WindowType windowFound = slotType.getWindows().getWindow()
                    .stream()
                    .filter(this::isWindowFree)
                    .findFirst()
                    .orElseThrow(() -> new SsrException("Выбранный слот уже забронирован для другого заявления"));
                setSlotBookingData(
                    contractOrderId,
                    preBookingId,
                    windowFound);
            });
        contractAppointmentDayScheduleDocumentService.updateDocument(
            contractAppointmentDayScheduleDocument.getId(),
            contractAppointmentDayScheduleDocument,
            true,
            false,
            null
        );
        final ExternalRestContractAppointmentPrebookResponseDto prebookSlotResponseDto =
            new ExternalRestContractAppointmentPrebookResponseDto();
        prebookSlotResponseDto.setBookingId(preBookingId);

        return prebookSlotResponseDto;
    }

    @Override
    @Transactional
    public void bookSlot(
        final ExternalRestContractAppointmentBookRequestDto body, final String contractAppointmentDocumentId
    ) {
        final String bookingId = ofNullable(body.getBookingId()).orElseThrow(MissedBookingIdentifier::new);
        final boolean isSsrMoveDate = nonNull(contractAppointmentDocumentId);
        if (!isSsrMoveDate && contractAppointmentService.isAppointmentRegisteredByBookingId(bookingId)) {
            throw new AlreadyBookedForEno();
        }

        final ContractAppointmentDayScheduleDocument contractAppointmentDayScheduleDocument =
            contractAppointmentDayScheduleDocumentService
                .findByPreBookingIdAndPreBookedUntilIsNotNull(bookingId)
                .orElseThrow(BookedSlotsNotFound::new);

        final ContractAppointmentDayScheduleData contractAppointmentDaySchedule = contractAppointmentDayScheduleDocument
            .getDocument()
            .getContractAppointmentDayScheduleData();

        final SlotType slotFound = contractAppointmentDaySchedule
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
            .findFirst()
            .orElseThrow(() -> new SsrException("Выбранный слот уже забронирован для другого заявления"));

        if (isSsrMoveDate) {
            final LocalDateTime appointmentDateTime =
                contractAppointmentDaySchedule.getDate().atTime(slotFound.getTimeFrom());
            contractAppointmentService
                .moveAppointmentDateTime(contractAppointmentDocumentId, bookingId, appointmentDateTime);

            // cancel booking for other days if exists
            contractAppointmentDayScheduleDocumentService.cancelBooking(bookingId);

            // cancel booking for current document
            contractAppointmentDayScheduleDocumentService
                .cancelDayScheduleBooking(contractAppointmentDaySchedule, bookingId, null);
        }

        // and set new booking
        slotFound.getWindows()
            .getWindow()
            .stream()
            .filter(window -> StringUtils.equals(bookingId, window.getPreBookingId()))
            .filter(this::isPreBookingNotExpired)
            .findFirst()
            .ifPresent(window -> window.setPreBookedUntil(null));

        contractAppointmentDayScheduleDocumentService.updateDocument(
            contractAppointmentDayScheduleDocument.getId(),
            contractAppointmentDayScheduleDocument,
            true,
            true,
            null
        );
    }

    private String generatePreBookingId(final String contractOrderId) {
        return UUID.nameUUIDFromBytes(contractOrderId.getBytes()).toString();
    }

    private boolean isPreBookingNotExpired(final WindowType window) {
        return nonNull(window.getPreBookedUntil())
            && window.getPreBookedUntil().isAfter(LocalDateTime.now());
    }

    private boolean isWindowFree(final WindowType window) {
        return Objects.isNull(window.getPreBookingId())
            || nonNull(window.getPreBookedUntil())
            && window.getPreBookedUntil().isBefore(LocalDateTime.now());
    }

    private void dropExistingPreBookingSlots(final String preBookingId) {
        contractAppointmentDayScheduleDocumentService.fetchAllByPrebookingId(preBookingId)
            .forEach(contractAppointmentDayScheduleDocument -> {
                final ContractAppointmentDayScheduleData contractAppointmentDaySchedule =
                    contractAppointmentDayScheduleDocument
                        .getDocument()
                        .getContractAppointmentDayScheduleData();

                contractAppointmentDayScheduleDocumentService
                    .cancelDaySchedulePreBooking(contractAppointmentDaySchedule, preBookingId);

                contractAppointmentDayScheduleDocumentService.updateDocument(
                    contractAppointmentDayScheduleDocument.getId(),
                    contractAppointmentDayScheduleDocument,
                    true,
                    true,
                    null
                );
            });
    }

    private void setSlotBookingData(
        final String contractOrderId,
        final String preBookingId,
        final WindowType window
    ) {
        final LocalDateTime preBookUntil = LocalDateTime.now().plus(15, ChronoUnit.MINUTES);
        window.setPreBookedUntil(preBookUntil);
        window.setPreBookingId(preBookingId);
        window.setContractOrderId(contractOrderId);
    }

    @Override
    public ExternalRestContractAppointmentPersonCheckDto check(final String snils, final String ssoId) {
        return contractAppointmentCheckService.checkPerson(snils, ssoId);
    }
}
