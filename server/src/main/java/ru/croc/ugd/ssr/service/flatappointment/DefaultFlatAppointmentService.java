package ru.croc.ugd.ssr.service.flatappointment;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static ru.croc.ugd.ssr.service.utils.IcsUtils.generateIcsFileName;

import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.croc.ugd.ssr.Cip;
import ru.croc.ugd.ssr.PersonType.FlatDemo.FlatDemoItem;
import ru.croc.ugd.ssr.dto.flatappointment.FlatAppointmentFlowStatus;
import ru.croc.ugd.ssr.exception.SsrException;
import ru.croc.ugd.ssr.exception.flatappointment.FlatAppointmentNotFound;
import ru.croc.ugd.ssr.flatappointment.FlatAppointmentData;
import ru.croc.ugd.ssr.flatappointment.FlatAppointmentParticipant;
import ru.croc.ugd.ssr.flatappointment.FlatAppointmentParticipants;
import ru.croc.ugd.ssr.integration.service.notification.FlatAppointmentElkNotificationService;
import ru.croc.ugd.ssr.integration.util.MessageUtils;
import ru.croc.ugd.ssr.mapper.FlatAppointmentMapper;
import ru.croc.ugd.ssr.model.CipDocument;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.flatAppointment.FlatAppointmentDocument;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateMessage;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateStatusMessage;
import ru.croc.ugd.ssr.mq.listener.flatappointment.EtpFlatAppointmentMapper;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.bpm.BpmService;
import ru.croc.ugd.ssr.service.cip.CipService;
import ru.croc.ugd.ssr.service.document.FlatAppointmentDayScheduleDocumentService;
import ru.croc.ugd.ssr.service.document.FlatAppointmentDocumentService;
import ru.croc.ugd.ssr.service.ics.IcsFileService;
import ru.croc.ugd.ssr.utils.CipUtils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * DefaultFlatAppointmentService.
 */
@Slf4j
@Service
@AllArgsConstructor
public class DefaultFlatAppointmentService implements FlatAppointmentService {

    private static final String PROCESS_FLAT_APPOINTMENT_KEY = "ugdssrFlat_processAppointment";
    private static final String OPEN_APPOINTMENT_TASK_AT_VAR = "openAppointmentTaskAt";
    private static final String CLOSE_CANCELLATION_AT_VAR = "closeCancellationAt";

    private static final long DAY_NUMBER_BEFORE_APPOINTMENT_TO_OPEN_TASK = 3;

    private static final Set<FlatAppointmentFlowStatus> CANCELLATION_ALLOWED_STATUSES = Sets.newHashSet(
        FlatAppointmentFlowStatus.REGISTERED,
        FlatAppointmentFlowStatus.TECHNICAL_CRASH_CANCELLATION_REJECTED
    );

    private static final Duration FLAT_APPOINTMENT_DURATION = Duration.ofHours(1);

    private final FlatAppointmentDocumentService flatAppointmentDocumentService;
    private final FlatAppointmentDayScheduleDocumentService flatAppointmentDayScheduleDocumentService;
    private final FlatAppointmentCheckService flatAppointmentCheckService;
    private final EtpFlatAppointmentMapper etpFlatAppointmentMapper;
    private final FlatAppointmentMapper flatAppointmentMapper;
    private final FlatAppointmentElkNotificationService flatAppointmentElkNotificationService;
    private final MessageUtils messageUtils;
    private final IcsFileService icsFileService;
    private final CipService cipService;
    private final BpmService bpmService;
    private final PersonDocumentService personDocumentService;
    private final FlatAppointmentEmailNotificationService flatAppointmentEmailNotificationService;

    @Override
    public FlatAppointmentDocument fetchByEno(String eno) {
        return flatAppointmentDocumentService.findByEno(eno)
            .orElseThrow(() -> new FlatAppointmentNotFound(eno));
    }

    @Override
    public void processRegistration(CoordinateMessage coordinateMessage) {
        try {
            final FlatAppointmentDocument flatAppointmentDocument = etpFlatAppointmentMapper
                .toFlatAppointmentDocument(coordinateMessage, this::retrievePersonDocument);

            if (isNull(flatAppointmentDocument)) {
                throw new SsrException("Unable to parse flat appointment request: " + coordinateMessage);
            }
            if (isDuplicate(flatAppointmentDocument)) {
                log.warn("Flat appointment has been already registered: {}", coordinateMessage);
                return;
            }
            if (!flatAppointmentCheckService.canCreateApplication(flatAppointmentDocument)) {
                throw new SsrException("Failed to pass validation to create flat appointment: "
                    + coordinateMessage);
            }

            processRegistration(flatAppointmentDocument);
        } catch (Exception e) {
            log.warn("Unable to save flat appointment due to: {}", e.getMessage(), e);
            flatAppointmentElkNotificationService.sendStatus(
                FlatAppointmentFlowStatus.TECHNICAL_CRASH_REGISTRATION,
                messageUtils.retrieveEno(coordinateMessage).orElse(null)
            );
        }
    }

    private void processRegistration(final FlatAppointmentDocument flatAppointmentDocument) {
        final FlatAppointmentData flatAppointment = flatAppointmentDocument
            .getDocument()
            .getFlatAppointmentData();

        if (!StringUtils.hasText(flatAppointment.getBookingId())) {
            throw new SsrException("No booking for FlatAppointmentDocument " + flatAppointment.getEno());
        }

        flatAppointmentDocumentService.createDocument(flatAppointmentDocument, true, null);

        flatAppointmentElkNotificationService.sendStatus(
            FlatAppointmentFlowStatus.ACCEPTED, flatAppointmentDocument
        );

        flatAppointment.setStatusId(FlatAppointmentFlowStatus.REGISTERED.getId());

        final String cipAddress = ofNullable(flatAppointment.getCipId())
            .flatMap(cipService::fetchById)
            .map(CipDocument::getDocument)
            .map(Cip::getCipData)
            .map(CipUtils::getCipAddress)
            .orElse(null);

        createIcsFileInFileStore(flatAppointmentDocument, cipAddress)
            .ifPresent(flatAppointment::setIcsFileStoreId);

        ofNullable(flatAppointment.getIcsFileStoreId())
            .flatMap(icsFileService::uploadIcsFileInChed)
            .ifPresent(flatAppointment::setIcsChedFileId);

        createAppointmentProcess(flatAppointmentDocument)
            .ifPresent(flatAppointment::setProcessInstanceId);

        flatAppointmentDocumentService.updateDocument(
            flatAppointmentDocument.getId(), flatAppointmentDocument, true, true, null
        );

        flatAppointmentElkNotificationService.sendStatus(
            FlatAppointmentFlowStatus.REGISTERED, flatAppointmentDocument
        );

        flatAppointmentEmailNotificationService.sendNotificationEmail(flatAppointmentDocument);
    }

    private PersonDocument retrievePersonDocument(final String personDocumentId) {
        return personDocumentService.fetchById(personDocumentId)
            .orElse(null);
    }

    private Optional<String> createIcsFileInFileStore(
        final FlatAppointmentDocument flatAppointmentDocument,
        final String cipAddress
    ) {
        final FlatAppointmentData flatAppointment = flatAppointmentDocument
            .getDocument()
            .getFlatAppointmentData();

        return icsFileService.createIcsFileInFileStore(
            "Осмотр квартиры",
            "Осмотр квартиры",
            cipAddress,
            flatAppointment.getAppointmentDateTime(),
            FLAT_APPOINTMENT_DURATION,
            generateIcsFileName(flatAppointmentDocument.getId()),
            flatAppointmentDocument.getFolderId()
        );
    }

    private boolean isDuplicate(final FlatAppointmentDocument flatAppointmentDocument) {
        final FlatAppointmentData flatAppointment = flatAppointmentDocument
            .getDocument()
            .getFlatAppointmentData();
        final boolean existsByEno = of(flatAppointment)
            .map(FlatAppointmentData::getEno)
            .filter(flatAppointmentDocumentService::existsByEno)
            .isPresent();
        final boolean existsByBookingId = of(flatAppointment)
            .map(FlatAppointmentData::getBookingId)
            .filter(this::isAppointmentRegisteredByBookingId)
            .isPresent();
        return existsByEno || existsByBookingId;
    }

    @Override
    public void processCancelByApplicantRequest(CoordinateStatusMessage coordinateStatus) {
        try {
            final FlatAppointmentDocument flatAppointmentDocument = getFlatAppointmentDocument(coordinateStatus);
            processCancelByApplicantRequest(flatAppointmentDocument);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            flatAppointmentElkNotificationService.sendStatus(
                FlatAppointmentFlowStatus.TECHNICAL_CRASH_CANCELLATION_REJECTED,
                messageUtils.retrieveEno(coordinateStatus).orElse(null)
            );
        }
    }

    private void processCancelByApplicantRequest(
        final FlatAppointmentDocument flatAppointmentDocument
    ) {
        final FlatAppointmentData flatAppointment = flatAppointmentDocument
            .getDocument()
            .getFlatAppointmentData();

        if (canCancelAppointment(flatAppointment, true)) {
            flatAppointment.setStatusId(FlatAppointmentFlowStatus.CANCELED_BY_APPLICANT.getId());
            flatAppointment.setCancelDate(LocalDate.now());
            //TODO set proper cancel reason from CoordinateStatusMessage

            flatAppointmentElkNotificationService.sendStatus(
                FlatAppointmentFlowStatus.CANCELED_BY_APPLICANT, flatAppointmentDocument
            );

            cancelBookingIfRequired(flatAppointmentDocument);

            ofNullable(flatAppointment.getProcessInstanceId())
                .ifPresent(this::finishBpmProcess);

            flatAppointmentDocumentService.updateDocument(
                flatAppointmentDocument.getId(), flatAppointmentDocument, true, true, null
            );
        } else {
            flatAppointmentElkNotificationService.sendStatus(
                FlatAppointmentFlowStatus.TECHNICAL_CRASH_CANCELLATION_REJECTED, flatAppointmentDocument
            );
        }
    }

    @Override
    public void processPerformedAppointment(
        final FlatAppointmentDocument flatAppointmentDocument,
        final FlatDemoItem flatDemoItem,
        final List<PersonDocument> participantDocuments
    ) {
        final FlatAppointmentData flatAppointment = flatAppointmentDocument
            .getDocument()
            .getFlatAppointmentData();

        flatAppointment.setStatusId(FlatAppointmentFlowStatus.INSPECTION_PERFORMED.getId());
        flatAppointment.setPerformed(Boolean.TRUE);
        flatAppointment.setAnnotation(flatDemoItem.getAnnotation());
        flatAppointment.setActualDemoDate(flatDemoItem.getDate());
        flatAppointment.setParticipantSummary(flatDemoItem.getParticipantSummary());
        // set dataId to be consistent with resettlement history event
        flatAppointment.setDemoId(flatDemoItem.getDataId());

        final FlatAppointmentParticipants flatAppointmentParticipants = new FlatAppointmentParticipants();
        final List<FlatAppointmentParticipant> flatAppointmentParticipantList = flatAppointmentMapper
            .toFlatAppointmentParticipantList(participantDocuments);
        flatAppointmentParticipants.getParticipants().addAll(flatAppointmentParticipantList);

        flatAppointment.setParticipants(flatAppointmentParticipants);

        flatAppointmentElkNotificationService.sendStatus(
            FlatAppointmentFlowStatus.INSPECTION_PERFORMED, flatAppointmentDocument
        );

        flatAppointmentDocumentService
            .updateDocument(flatAppointmentDocument.getId(), flatAppointmentDocument, true, true, null);

        ofNullable(flatAppointment.getProcessInstanceId())
            .ifPresent(this::finishBpmProcess);

        cancelBookingIfRequired(flatAppointmentDocument);
    }

    @Override
    public void processNotPerformedAppointment(FlatAppointmentDocument flatAppointmentDocument) {
        final FlatAppointmentData flatAppointment = flatAppointmentDocument
            .getDocument()
            .getFlatAppointmentData();

        flatAppointment.setStatusId(FlatAppointmentFlowStatus.INSPECTION_NOT_PERFORMED.getId());
        flatAppointment.setPerformed(Boolean.FALSE);

        flatAppointmentElkNotificationService.sendStatus(
            FlatAppointmentFlowStatus.INSPECTION_NOT_PERFORMED, flatAppointmentDocument
        );

        flatAppointmentDocumentService
            .updateDocument(flatAppointmentDocument.getId(), flatAppointmentDocument, true, true, null);

        ofNullable(flatAppointment.getProcessInstanceId())
            .ifPresent(this::finishBpmProcess);

        cancelBookingIfRequired(flatAppointmentDocument);
    }

    @Override
    public void cancelBookingIfRequired(final FlatAppointmentDocument flatAppointmentDocument) {
        final FlatAppointmentData flatAppointment = flatAppointmentDocument
            .getDocument()
            .getFlatAppointmentData();

        if (nonNull(flatAppointment.getBookingId())
            && hasAppointmentDateTimeInFuture(flatAppointment)
        ) {
            flatAppointmentDayScheduleDocumentService.cancelBooking(flatAppointment.getBookingId(),
                flatAppointmentDocument.getId());

            flatAppointment.setBookingId(null);
            flatAppointment.setAppointmentDateTime(null);
        }
    }

    private static boolean hasAppointmentDateTimeInFuture(final FlatAppointmentData flatAppointmentData) {
        return ofNullable(flatAppointmentData.getAppointmentDateTime())
            .filter(appointmentDateTime -> appointmentDateTime.isAfter(LocalDateTime.now()))
            .isPresent();
    }

    @Override
    public boolean canCancelAppointment(final FlatAppointmentData flatAppointmentData, final boolean isMpgu) {
        final FlatAppointmentFlowStatus currentFlowStatus = FlatAppointmentFlowStatus
            .of(flatAppointmentData.getStatusId());

        return CANCELLATION_ALLOWED_STATUSES.contains(currentFlowStatus)
            && (isMpgu && hasAppointmentDateTimeInFuture(flatAppointmentData)
            || isAppointmentDateTimeOneDayInAdvance(flatAppointmentData));
    }

    private static boolean isAppointmentDateTimeOneDayInAdvance(final FlatAppointmentData flatAppointmentData) {
        return ofNullable(flatAppointmentData.getAppointmentDateTime())
            .filter(appointmentDateTime -> appointmentDateTime.isAfter(LocalDateTime.now().plusDays(1)))
            .isPresent();
    }

    private FlatAppointmentDocument getFlatAppointmentDocument(final CoordinateStatusMessage coordinateStatus) {
        final String eno = messageUtils.retrieveEno(coordinateStatus).orElse(null);
        return flatAppointmentDocumentService
            .findByEno(eno)
            .orElseThrow(() -> new SsrException("Unable to find FlatAppointmentDocument by eno " + eno));
    }

    @Override
    public void moveAppointmentDateTime(
        final String flatAppointmentDocumentId, final String bookingId, final LocalDateTime appointmentDateTime
    ) {
        final FlatAppointmentDocument flatAppointmentDocument = flatAppointmentDocumentService
            .fetchDocument(flatAppointmentDocumentId);
        final FlatAppointmentData flatAppointment = flatAppointmentDocument
            .getDocument()
            .getFlatAppointmentData();

        if (!isAppointmentDateTimeOneDayInAdvance(flatAppointment)) {
            throw new SsrException("Нельзя перенести запись менее чем за сутки до приема");
        }

        flatAppointment.setBookingId(bookingId);
        flatAppointment.setAppointmentDateTime(appointmentDateTime);
        flatAppointment.setStatusId(FlatAppointmentFlowStatus.REGISTERED.getId());

        final String cipAddress = ofNullable(flatAppointment.getCipId())
            .flatMap(cipService::fetchById)
            .map(CipDocument::getDocument)
            .map(Cip::getCipData)
            .map(CipUtils::getCipAddress)
            .orElse(null);

        createIcsFileInFileStore(flatAppointmentDocument, cipAddress)
            .ifPresent(flatAppointment::setIcsFileStoreId);

        ofNullable(flatAppointment.getIcsFileStoreId())
            .flatMap(icsFileService::uploadIcsFileInChed)
            .ifPresent(flatAppointment::setIcsChedFileId);

        ofNullable(flatAppointment.getProcessInstanceId())
            .ifPresent(this::finishBpmProcess);
        createAppointmentProcess(flatAppointmentDocument)
            .ifPresent(flatAppointment::setProcessInstanceId);

        flatAppointmentDocumentService
            .updateDocument(flatAppointmentDocument.getId(), flatAppointmentDocument, true, true, null);
        flatAppointmentElkNotificationService
            .sendStatus(FlatAppointmentFlowStatus.MOVED_BY_OPERATOR, flatAppointmentDocument);
    }

    @Override
    public void finishBpmProcess(final String processInstanceId) {
        if (StringUtils.hasText(processInstanceId)) {
            try {
                bpmService.deleteProcessInstance(processInstanceId);
            } catch (Exception e) {
                log.warn("Unable to finish bpm process for flat appointment due to: {}", e.getMessage(), e);
            }
        }
    }

    @Override
    public boolean isAppointmentRegisteredByBookingId(final String bookingId) {
        return flatAppointmentDocumentService.isAppointmentRegisteredByBookingId(bookingId);
    }

    @Override
    public void processAutoCancellation(final FlatAppointmentDocument flatAppointmentDocument) {
        final FlatAppointmentData flatAppointment = flatAppointmentDocument
            .getDocument()
            .getFlatAppointmentData();

        flatAppointment.setStatusId(FlatAppointmentFlowStatus.CANCELED_BY_OPERATOR.getId());
        flatAppointment.setCancelDate(LocalDate.now());
        flatAppointment.setCancelReason("Отсутствует информация о проведении осмотра");

        flatAppointmentElkNotificationService
            .sendStatus(FlatAppointmentFlowStatus.CANCELED_BY_OPERATOR, flatAppointmentDocument);

        ofNullable(flatAppointment.getProcessInstanceId())
            .ifPresent(this::finishBpmProcess);

        flatAppointmentDocumentService
            .updateDocument(flatAppointmentDocument.getId(), flatAppointmentDocument, true, true, null);
    }

    private Optional<String> createAppointmentProcess(final FlatAppointmentDocument flatAppointmentDocument) {
        final String flatAppointmentId = flatAppointmentDocument.getId();

        final FlatAppointmentData flatAppointment = flatAppointmentDocument
            .getDocument()
            .getFlatAppointmentData();

        final LocalDateTime appointmentDateTime = flatAppointment.getAppointmentDateTime();
        if (isNull(appointmentDateTime)) {
            log.warn(
                "Unable to create bpm process for flat appointment {} without appointment date and time",
                flatAppointmentId
            );
            return Optional.empty();
        }

        final LocalDateTime openAppointmentTaskAt = appointmentDateTime
            .minusDays(DAY_NUMBER_BEFORE_APPOINTMENT_TO_OPEN_TASK);

        final Map<String, String> variablesMap = new HashMap<>();
        variablesMap.put(BpmService.PROCESS_DOCUMENT_KEY_ENTITY_ID_VAR, flatAppointmentId);
        variablesMap.put(OPEN_APPOINTMENT_TASK_AT_VAR, openAppointmentTaskAt.toString());
        variablesMap.put(CLOSE_CANCELLATION_AT_VAR, appointmentDateTime.toString());

        return ofNullable(bpmService.startNewProcess(PROCESS_FLAT_APPOINTMENT_KEY, variablesMap));
    }

}
