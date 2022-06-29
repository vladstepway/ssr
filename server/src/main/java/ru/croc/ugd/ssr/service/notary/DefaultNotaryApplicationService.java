package ru.croc.ugd.ssr.service.notary;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static ru.croc.ugd.ssr.service.utils.IcsUtils.generateIcsFileName;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.croc.ugd.ssr.RealEstate;
import ru.croc.ugd.ssr.dto.notary.NotaryApplicationFlowStatus;
import ru.croc.ugd.ssr.exception.SsrException;
import ru.croc.ugd.ssr.exception.notaryapplication.NotaryApplicationNotFound;
import ru.croc.ugd.ssr.integration.service.notification.NotaryApplicationElkNotificationService;
import ru.croc.ugd.ssr.integration.util.MessageUtils;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.RealEstateDocument;
import ru.croc.ugd.ssr.model.integration.etpmv.ArrayOfCoordinateFile;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateData;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateMessage;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateStatusMessage;
import ru.croc.ugd.ssr.model.integration.etpmv.RequestServiceForSign;
import ru.croc.ugd.ssr.model.notary.NotaryApplicationDocument;
import ru.croc.ugd.ssr.model.notary.NotaryDocument;
import ru.croc.ugd.ssr.mq.listener.notary.EtpNotaryApplicationMapper;
import ru.croc.ugd.ssr.notary.Apartments;
import ru.croc.ugd.ssr.notary.NotaryAddress;
import ru.croc.ugd.ssr.notary.NotaryApplicantFile;
import ru.croc.ugd.ssr.notary.NotaryApplication;
import ru.croc.ugd.ssr.notary.NotaryApplicationApplicant;
import ru.croc.ugd.ssr.notary.NotaryApplicationType;
import ru.croc.ugd.ssr.notary.NotaryType;
import ru.croc.ugd.ssr.service.CapitalConstructionObjectService;
import ru.croc.ugd.ssr.service.CatalogSearchService;
import ru.croc.ugd.ssr.service.ChedFileService;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.RealEstateDocumentService;
import ru.croc.ugd.ssr.service.bpm.BpmService;
import ru.croc.ugd.ssr.service.document.NotaryApplicationDocumentService;
import ru.croc.ugd.ssr.service.document.NotaryDayScheduleDocumentService;
import ru.croc.ugd.ssr.service.document.NotaryDocumentService;
import ru.croc.ugd.ssr.service.ics.IcsFileService;
import ru.croc.ugd.ssr.utils.RealEstateUtils;
import ru.mos.gu.service._084001.EtpApartment;
import ru.mos.gu.service._084001.ServiceProperties;
import ru.reinform.cdp.bpm.model.FormProperty;
import ru.reinform.cdp.bpm.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * DefaultNotaryApplicationService.
 */
@Slf4j
@Service
@AllArgsConstructor
public class DefaultNotaryApplicationService implements NotaryApplicationService {

    private static final String PROCESS_KEY_CREATE_DRAFT_CONTRACT = "ugdssrNotary_сreateDraftContract";
    private static final String PROCESS_KEY_SET_UP_APPOINTMENT = "ugdssrNotary_setUpAppointment";

    private static final String BOOKING_CLOSURE_DATE_TIME_VAR = "bookingClosureDateTime";
    private static final String CANCELLATION_CLOSURE_DATE_TIME_VAR = "cancellationClosureDateTime";
    private static final String REFUSAL_DATE_TIME_VAR = "refusalDateTime";
    private static final String REMINDER_DATE_TIME_VAR = "reminderDateTime";
    private static final String IS_CREATED_BY_NOTARY_VAR = "isCreatedByNotary";
    private static final String IS_BOOKING_SUCCESSFUL_VAR = "isBookingSuccessful";
    private static final String IS_APPLICATION_CLOSED_VAR = "isApplicationClosed";
    private static final String IS_APPLICATION_ACCEPTED_VAR = "isApplicationAccepted";
    private static final String IS_DOCUMENTS_PREPARATION_FINISHED_VAR = "isDocumentsPreparationFinished";
    private static final String IS_CONTRACT_READY_VAR = "isContractReady";

    private static final Duration NOTARY_APPOINTMENT_DURATION = Duration.ofHours(1);

    private final NotaryApplicationDocumentService notaryApplicationDocumentService;
    private final NotaryDocumentService notaryDocumentService;
    private final NotaryApplicationElkNotificationService notaryApplicationElkNotificationService;
    private final EtpNotaryApplicationMapper etpNotaryApplicationMapper;
    private final MessageUtils messageUtils;
    private final CatalogSearchService catalogSearchService;
    private final CapitalConstructionObjectService capitalConstructionObjectService;
    private final RealEstateDocumentService realEstateDocumentService;
    private final BpmService bpmService;
    private final ChedFileService chedFileService;
    private final PersonDocumentService personDocumentService;
    private final NotaryDayScheduleDocumentService notaryDayScheduleDocumentService;
    private final IcsFileService icsFileService;
    private final NotaryTaskDurationConfig taskDurationConfig;

    @Override
    public void processRegistration(final CoordinateMessage coordinateMessage) {
        try {
            final NotaryApplicationDocument notaryApplicationDocument = etpNotaryApplicationMapper
                .toNotaryApplicationDocument(coordinateMessage, catalogSearchService::fetchAddressByUnom);

            if (isNull(notaryApplicationDocument)) {
                throw new SsrException("Unable to parse notary application request: " + coordinateMessage);
            }
            if (!isDuplicate(notaryApplicationDocument)) {
                processRegistration(notaryApplicationDocument, coordinateMessage);
            } else {
                log.warn("Notary application has been already registered: {}", coordinateMessage);
            }
        } catch (Exception e) {
            log.warn("Unable to save notary application due to: {}", e.getMessage(), e);
            notaryApplicationElkNotificationService.sendStatus(
                NotaryApplicationFlowStatus.TECHNICAL_CRASH_REGISTRATION,
                messageUtils.retrieveEno(coordinateMessage).orElse(null)
            );
        }
    }

    private void processRegistration(
        final NotaryApplicationDocument notaryApplicationDocument,
        final CoordinateMessage coordinateMessage
    ) {
        final NotaryApplicationType notaryApplication = notaryApplicationDocument
            .getDocument()
            .getNotaryApplicationData();

        populatePersonId(notaryApplication);
        populateAddressTo(notaryApplication);
        populateAddressFrom(notaryApplication, coordinateMessage);

        notaryApplicationDocumentService.createDocument(notaryApplicationDocument, true, null);

        createContractPreparationProcess(notaryApplicationDocument, false)
            .ifPresent(notaryApplication::setProcessInstanceId);

        notaryApplicationDocumentService.updateDocument(
            notaryApplicationDocument.getId(), notaryApplicationDocument, true, true, null
        );

        notaryApplicationElkNotificationService.sendStatus(
            NotaryApplicationFlowStatus.ACCEPTED, notaryApplicationDocument
        );
    }

    private boolean isDuplicate(final NotaryApplicationDocument notaryApplicationDocument) {
        return of(notaryApplicationDocument.getDocument())
            .map(NotaryApplication::getNotaryApplicationData)
            .map(NotaryApplicationType::getEno)
            .filter(notaryApplicationDocumentService::existsByEno)
            .isPresent();
    }

    private void populatePersonId(final NotaryApplicationType notaryApplication) {
        final NotaryApplicationApplicant applicant = notaryApplication.getApplicant();

        final String personDocumentId = personDocumentService.fetchByAffairId(notaryApplication.getAffairId())
            .stream()
            .filter(person -> Objects.equals(person.getDocument().getPersonData().getSNILS(), applicant.getSnils()))
            .findFirst()
            .map(PersonDocument::getId)
            .orElseThrow(() -> new SsrException("Unable to find person by affair id and snils"));

        applicant.setPersonId(personDocumentId);
    }

    private void populateAddressTo(final NotaryApplicationType notaryApplication) {
        ofNullable(notaryApplication)
            .map(NotaryApplicationType::getApartmentTo)
            .map(Apartments::getApartment)
            .map(List::stream)
            .orElse(Stream.empty())
            .filter(apartment -> StringUtils.isEmpty(apartment.getAddress()))
            .forEach(apartment -> {
                final String address = capitalConstructionObjectService.getCcoAddressByUnom(apartment.getUnom());
                apartment.setAddress(address);
            });
    }

    private void populateAddressFrom(
        final NotaryApplicationType notaryApplication, final CoordinateMessage coordinateMessage
    ) {
        ofNullable(notaryApplication)
            .map(NotaryApplicationType::getApartmentFrom)
            .filter(apartment -> StringUtils.isEmpty(apartment.getAddress()))
            .ifPresent(apartment -> {
                final String address = fetchAddressFromCoordinateMessage(coordinateMessage);
                apartment.setAddress(address);
            });
    }

    private String fetchAddressFromCoordinateMessage(final CoordinateMessage coordinateMessage) {
        return ofNullable(coordinateMessage)
            .map(this::castToEtpServiceProperties)
            .map(ServiceProperties::getApartmentFrom)
            .map(EtpApartment::getUnom)
            .map(realEstateDocumentService::fetchDocumentByUnom)
            .map(RealEstateDocument::getDocument)
            .map(RealEstate::getRealEstateData)
            .map(RealEstateUtils::getRealEstateAddress)
            .orElse(null);
    }

    private ServiceProperties castToEtpServiceProperties(final CoordinateMessage coordinateMessage) {
        return ofNullable(coordinateMessage)
            .map(CoordinateMessage::getCoordinateDataMessage)
            .map(CoordinateData::getSignService)
            .map(RequestServiceForSign::getCustomAttributes)
            .map(RequestServiceForSign.CustomAttributes::getAny)
            .filter(attributes -> attributes instanceof ServiceProperties)
            .map(ServiceProperties.class::cast)
            .orElse(null);
    }

    @Override
    public void finishBpmProcess(final String processInstanceId) {
        if (StringUtils.hasText(processInstanceId)) {
            try {
                bpmService.deleteProcessInstance(processInstanceId);
            } catch (Exception e) {
                log.warn("Unable to finish bpm process for notary application due to: {}", e.getMessage(), e);
            }
        }
    }

    @Override
    public void processReceivedDocuments(final CoordinateStatusMessage coordinateStatus) {
        try {
            final NotaryApplicationDocument notaryApplicationDocument = getNotaryApplicationDocument(coordinateStatus);
            processReceivedDocuments(notaryApplicationDocument, coordinateStatus);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            notaryApplicationElkNotificationService.sendStatus(
                NotaryApplicationFlowStatus.TECHNICAL_CRASH_RECEIVED_DOCUMENTS,
                messageUtils.retrieveEno(coordinateStatus).orElse(null)
            );
        }
    }

    private void processReceivedDocuments(
        final NotaryApplicationDocument notaryApplicationDocument, final CoordinateStatusMessage coordinateStatus
    ) {
        final NotaryApplicationType notaryApplication = notaryApplicationDocument
            .getDocument()
            .getNotaryApplicationData();

        notaryApplication.setStatusId(NotaryApplicationFlowStatus.DOCUMENTS_RECEIVED.getId());
        notaryApplicationDocument.addHistoryEvent(NotaryApplicationFlowStatus.DOCUMENTS_RECEIVED.getId());

        final List<NotaryApplicantFile> notaryApplicantFiles =
            retrieveFiles(coordinateStatus, notaryApplicationDocument.getFolderId());
        notaryApplicationDocument.addFiles(notaryApplicantFiles);

        notaryApplicationDocumentService.updateDocument(
            notaryApplicationDocument.getId(), notaryApplicationDocument, true, true, null);

        notaryApplicationElkNotificationService
            .sendStatus(NotaryApplicationFlowStatus.DOCUMENTS_RECEIVED, notaryApplicationDocument);

        closeDocumentsAwaitingTask(notaryApplication.getProcessInstanceId());
    }

    @Override
    public void processCancelByApplicantRequest(CoordinateStatusMessage coordinateStatus) {
        try {
            final NotaryApplicationDocument notaryApplicationDocument = getNotaryApplicationDocument(coordinateStatus);
            processCancelByApplicantRequest(notaryApplicationDocument);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            notaryApplicationElkNotificationService.sendStatus(
                NotaryApplicationFlowStatus.TECHNICAL_CRASH_CANCEL_BY_APPLICANT,
                messageUtils.retrieveEno(coordinateStatus).orElse(null)
            );
        }
    }

    @Override
    public void processCancelByApplicantRequest(final NotaryApplicationDocument notaryApplicationDocument) {
        final NotaryApplicationType notaryApplication = notaryApplicationDocument
            .getDocument()
            .getNotaryApplicationData();

        if (canCancelApplication(notaryApplication)) {
            notaryApplication.setStatusId(NotaryApplicationFlowStatus.DRAFT_CONTRACT_READY.getId());
            notaryApplicationDocument.addHistoryEvent(NotaryApplicationFlowStatus.CANCEL_BY_APPLICANT_REQUEST.getId());

            cancelBookingIfRequired(notaryApplication);

            ofNullable(notaryApplication.getProcessInstanceId())
                .ifPresent(this::finishBpmProcess);

            createSetUpAppointmentProcess(notaryApplicationDocument)
                .ifPresent(notaryApplication::setProcessInstanceId);

            notaryApplicationDocumentService.updateDocument(
                notaryApplicationDocument.getId(), notaryApplicationDocument, true, true, null
            );

            notaryApplicationElkNotificationService
                .sendStatus(NotaryApplicationFlowStatus.CANCELED_BY_APPLICANT, notaryApplicationDocument);
        } else {
            notaryApplicationElkNotificationService
                .sendStatus(NotaryApplicationFlowStatus.UNABLE_TO_CANCEL, notaryApplicationDocument);
        }
    }

    @Override
    public void processBookingRequest(final CoordinateStatusMessage coordinateStatus) {
        try {
            final NotaryApplicationDocument notaryApplicationDocument = getNotaryApplicationDocument(coordinateStatus);
            processBookingRequest(notaryApplicationDocument);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            notaryApplicationElkNotificationService.sendStatus(
                NotaryApplicationFlowStatus.TECHNICAL_CRASH_BOOKING,
                messageUtils.retrieveEno(coordinateStatus).orElse(null)
            );
        }
    }

    @Override
    public void processBookingRequest(
        final String eno, final String bookingId, final LocalDateTime appointmentDateTime, final Boolean isSsrBooking
    ) {
        final NotaryApplicationDocument notaryApplicationDocument = fetchByEno(eno);
        final NotaryApplicationType notaryApplication = notaryApplicationDocument
            .getDocument()
            .getNotaryApplicationData();

        notaryApplication.setBookingId(bookingId);
        notaryApplication.setAppointmentDateTime(appointmentDateTime);

        if (Boolean.TRUE.equals(isSsrBooking)) {
            processBookingRequest(notaryApplicationDocument);
        } else {
            notaryApplicationDocumentService
                .updateDocument(notaryApplicationDocument.getId(), notaryApplicationDocument, true, true, null);
        }
    }

    private void processBookingRequest(final NotaryApplicationDocument notaryApplicationDocument) {
        final NotaryApplicationType notaryApplication = notaryApplicationDocument
            .getDocument()
            .getNotaryApplicationData();

        if (!StringUtils.hasText(notaryApplication.getBookingId())) {
            throw new SsrException("No booking for NotaryApplicationDocument " + notaryApplication.getEno());
        }

        notaryApplication.setStatusId(NotaryApplicationFlowStatus.BOOKED.getId());
        notaryApplicationDocument.addHistoryEvent(
            NotaryApplicationFlowStatus.BOOKED.getId(),
            notaryApplication.getAppointmentDateTime()
        );

        closeBookingTask(notaryApplication, true);

        final NotaryDocument notaryDocument = notaryDocumentService.fetchDocument(notaryApplication.getNotaryId());

        createIcsFileInFileStore(notaryApplicationDocument, notaryDocument)
            .ifPresent(notaryApplication::setIcsFileStoreId);

        ofNullable(notaryApplication.getIcsFileStoreId())
            .flatMap(icsFileService::uploadIcsFileInChed)
            .ifPresent(notaryApplication::setIcsChedFileId);

        notaryApplicationDocumentService.updateDocument(
            notaryApplicationDocument.getId(), notaryApplicationDocument, true, true, null
        );

        notaryApplicationElkNotificationService
            .sendStatus(NotaryApplicationFlowStatus.BOOKED, notaryApplicationDocument);
    }

    private Optional<String> createIcsFileInFileStore(
        final NotaryApplicationDocument notaryApplicationDocument,
        final NotaryDocument notaryDocument
    ) {
        final NotaryApplicationType notaryApplication = notaryApplicationDocument
            .getDocument()
            .getNotaryApplicationData();

        final NotaryType notary = notaryDocument
            .getDocument()
            .getNotaryData();
        final String notaryFullName = ofNullable(notary.getFullName()).orElse("");
        final String notaryAddress = ofNullable(notary.getAddress())
            .map(NotaryAddress::getAddress)
            .orElse("");

        return icsFileService.createIcsFileInFileStore(
            "Приём у нотариуса",
            "Приём у нотариуса " + notaryFullName,
            notaryAddress,
            notaryApplication.getAppointmentDateTime(),
            NOTARY_APPOINTMENT_DURATION,
            generateIcsFileName(notaryApplicationDocument.getId()),
            notaryApplicationDocument.getFolderId()
        );
    }

    private boolean canCancelApplication(NotaryApplicationType notaryApplication) {
        return notaryApplication.getAppointmentDateTime() == null
            || !notaryApplication.getAppointmentDateTime().isBefore(LocalDateTime.now().plusDays(1));
    }

    private List<NotaryApplicantFile> retrieveFiles(
        final CoordinateStatusMessage coordinateStatus, final String folderId
    ) {
        return ofNullable(coordinateStatus.getFiles())
            .map(ArrayOfCoordinateFile::getCoordinateFile)
            .map(List::stream)
            .orElse(Stream.empty())
            .map(file -> {
                final NotaryApplicantFile notaryApplicantFile = new NotaryApplicantFile();
                notaryApplicantFile.setName(file.getFileName());
                notaryApplicantFile.setChedFileId(file.getFileIdInStore());
                notaryApplicantFile.setCreationDate(LocalDateTime.now());
                notaryApplicantFile.setFileStoreId(
                    chedFileService.extractFileFromChedAndGetFileLink(file.getFileIdInStore(), folderId)
                );
                notaryApplicantFile.setConfirmed(false);
                return notaryApplicantFile;
            })
            .collect(Collectors.toList());
    }

    @Override
    public Optional<String> getNotaryLogin(final NotaryApplicationDocument notaryApplicationDocument) {
        return of(notaryApplicationDocument.getDocument())
            .map(NotaryApplication::getNotaryApplicationData)
            .map(NotaryApplicationType::getNotaryId)
            .flatMap(notaryDocumentService::fetchNotaryLoginById);
    }

    @Override
    public NotaryApplicationDocument fetchByEno(final String eno) {
        return notaryApplicationDocumentService.findByEno(eno)
            .orElseThrow(() -> new NotaryApplicationNotFound(eno));
    }

    @Override
    public void cancelBookingIfRequired(final NotaryApplicationType notaryApplication) {
        if (nonNull(notaryApplication.getBookingId()) && hasAppointmentDateTimeInFuture(notaryApplication)) {
            notaryDayScheduleDocumentService.cancelBooking(notaryApplication.getBookingId());
        }

        notaryApplication.setBookingId(null);
        notaryApplication.setAppointmentDateTime(null);
    }

    private static boolean hasAppointmentDateTimeInFuture(final NotaryApplicationType notaryApplication) {
        return ofNullable(notaryApplication.getAppointmentDateTime())
            .filter(appointmentDateTime -> appointmentDateTime.isAfter(LocalDateTime.now()))
            .isPresent();
    }

    private void closeDocumentsAwaitingTask(final String processInstanceId) {
        closeNotaryTask(
            PROCESS_KEY_CREATE_DRAFT_CONTRACT,
            processInstanceId,
            Collections.emptyMap()
        );
    }

    @Override
    public void closeDocumentsVerificationTask(
        final String processInstanceId, final Boolean isDocumentsPreparationFinished
    ) {
        final LocalDateTime refusalDateTime =
            calculateNoDocumentsRefusalDateTime(taskDurationConfig.getRefuseNoDocuments());

        final Map<String, String> formPropertyMap = new HashMap<>();
        formPropertyMap.put(IS_DOCUMENTS_PREPARATION_FINISHED_VAR, isDocumentsPreparationFinished.toString());
        if (!isDocumentsPreparationFinished) {
            formPropertyMap.put(IS_CONTRACT_READY_VAR, "false");
            formPropertyMap.put(REFUSAL_DATE_TIME_VAR, refusalDateTime.toString());
        }

        closeNotaryTask(
            PROCESS_KEY_CREATE_DRAFT_CONTRACT,
            processInstanceId,
            formPropertyMap
        );
    }

    @Override
    public void closeAcceptApplicationTask(final String processInstanceId, final Boolean isApplicationAccepted) {
        closeNotaryTask(
            PROCESS_KEY_CREATE_DRAFT_CONTRACT,
            processInstanceId,
            Collections.singletonMap(IS_APPLICATION_ACCEPTED_VAR, isApplicationAccepted.toString())
        );
    }

    @Override
    public void closeContractPreparationTask(final String processInstanceId, final Boolean isContractReady) {
        final LocalDateTime refusalDateTime =
            calculateNoDocumentsRefusalDateTime(taskDurationConfig.getRefuseNoDocuments());

        final Map<String, String> formPropertyMap = new HashMap<>();
        formPropertyMap.put(IS_CONTRACT_READY_VAR, isContractReady.toString());
        formPropertyMap.put(REFUSAL_DATE_TIME_VAR, refusalDateTime.toString());

        closeNotaryTask(
            PROCESS_KEY_CREATE_DRAFT_CONTRACT,
            processInstanceId,
            formPropertyMap
        );
    }

    private static LocalDateTime calculateNoDocumentsRefusalDateTime(final String refuseNoDocumentsDuration) {
        final Duration refusalDuration = Duration.parse(refuseNoDocumentsDuration);
        return LocalDateTime.now().plusMinutes(refusalDuration.toMinutes());
    }

    @Override
    public void closeBookingTask(final NotaryApplicationType notaryApplication, final Boolean isBookingSuccessful) {
        final LocalDateTime appointmentDateTime = notaryApplication.getAppointmentDateTime();

        final Map<String, String> formPropertyMap = new HashMap<>();
        formPropertyMap.put(IS_BOOKING_SUCCESSFUL_VAR, isBookingSuccessful.toString());
        if (isBookingSuccessful) {
            final LocalDateTime reminderDateTime =
                calculateReminderDateTime(taskDurationConfig.getAppointmentReminder(), appointmentDateTime);
            final LocalDateTime cancellationClosureDateTime =
                cancellationClosureDateTime(taskDurationConfig.getCancellationClosed(), appointmentDateTime);

            formPropertyMap.put(REMINDER_DATE_TIME_VAR, reminderDateTime.toString());
            formPropertyMap.put(CANCELLATION_CLOSURE_DATE_TIME_VAR, cancellationClosureDateTime.toString());
        }

        closeNotaryTask(
            PROCESS_KEY_SET_UP_APPOINTMENT,
            notaryApplication.getProcessInstanceId(),
            formPropertyMap
        );
    }

    private static LocalDateTime calculateReminderDateTime(
        final String applicationReminderDuration, final LocalDateTime appointmentDateTime
    ) {
        final Duration reminderDuration = Duration.parse(applicationReminderDuration);
        return appointmentDateTime.minusMinutes(reminderDuration.toMinutes());
    }

    private static LocalDateTime cancellationClosureDateTime(
        final String cancellationClosedDuration, final LocalDateTime appointmentDateTime
    ) {
        final Duration cancellationClosureDuration = Duration.parse(cancellationClosedDuration);
        return appointmentDateTime.minusMinutes(cancellationClosureDuration.toMinutes());
    }

    @Override
    public void closeAppointmentResultsTask(final String processInstanceId, final Boolean isApplicationClosed) {
        closeNotaryTask(
            PROCESS_KEY_SET_UP_APPOINTMENT,
            processInstanceId,
            Collections.singletonMap(IS_APPLICATION_CLOSED_VAR, isApplicationClosed.toString())
        );
    }

    @Override
    public void closeDocumentsRecollectionTask(final String processInstanceId) {
        closeNotaryTask(
            PROCESS_KEY_SET_UP_APPOINTMENT,
            processInstanceId,
            Collections.emptyMap()
        );
    }

    @Override
    public Optional<String> createSetUpAppointmentProcess(final NotaryApplicationDocument notaryApplicationDocument) {
        final String notaryApplicationId = notaryApplicationDocument.getId();
        final Optional<String> optionalNotaryLogin = getNotaryLogin(notaryApplicationDocument);
        if (!optionalNotaryLogin.isPresent()) {
            log.warn("No employee found to create set up appointment process: applicationId {}", notaryApplicationId);
            return Optional.empty();
        }

        final String notaryLogin = optionalNotaryLogin.get();
        log.debug("Create set up appointment process: applicationId {}", notaryApplicationId);

        final LocalDateTime refusalDateTime =
            calculateNoBookingRefusalDateTime(taskDurationConfig.getRefuseNoBooking());

        final LocalDateTime bookingClosureDateTime =
            calculateBookingClosureDateTime(taskDurationConfig.getBookingClosed());

        final Map<String, String> variablesMap = new HashMap<>();
        variablesMap.put(BpmService.PROCESS_DOCUMENT_KEY_ENTITY_ID_VAR, notaryApplicationId);
        variablesMap.put(BpmService.PROCESS_DOCUMENT_KEY_CANDIDATE_USERS, notaryLogin);
        variablesMap.put(BOOKING_CLOSURE_DATE_TIME_VAR, bookingClosureDateTime.toString());
        variablesMap.put(REFUSAL_DATE_TIME_VAR, refusalDateTime.toString());

        return ofNullable(bpmService.startNewProcess(PROCESS_KEY_SET_UP_APPOINTMENT, variablesMap));
    }

    private static LocalDateTime calculateNoBookingRefusalDateTime(final String refuseNoBookingDuration) {
        final Duration refusalDuration = Duration.parse(refuseNoBookingDuration);
        return LocalDateTime.now().plusMinutes(refusalDuration.toMinutes());
    }

    private static LocalDateTime calculateBookingClosureDateTime(final String bookingClosedDuration) {
        final Duration bookingClosureDuration = Duration.parse(bookingClosedDuration);
        return LocalDateTime.now().plusMinutes(bookingClosureDuration.toMinutes());
    }

    @Override
    public Optional<String> createContractPreparationProcess(
        final NotaryApplicationDocument notaryApplicationDocument,
        final Boolean isCreatedByNotary
    ) {
        final String notaryApplicationId = notaryApplicationDocument.getId();
        final Optional<String> optionalNotaryLogin = getNotaryLogin(notaryApplicationDocument);
        if (!optionalNotaryLogin.isPresent()) {
            log.warn(
                "No employee found to create contract preparation process: applicationId {}",
                notaryApplicationId
            );
            return Optional.empty();
        }

        final String notaryLogin = optionalNotaryLogin.get();
        log.debug("Create contract preparation process: applicationId {}", notaryApplicationId);

        final Map<String, String> variablesMap = new HashMap<>();
        variablesMap.put(BpmService.PROCESS_DOCUMENT_KEY_ENTITY_ID_VAR, notaryApplicationId);
        variablesMap.put(BpmService.PROCESS_DOCUMENT_KEY_CANDIDATE_USERS, notaryLogin);
        variablesMap.put(IS_CREATED_BY_NOTARY_VAR, isCreatedByNotary.toString());

        return Optional.of(bpmService.startNewProcess(PROCESS_KEY_CREATE_DRAFT_CONTRACT, variablesMap));
    }

    private void closeNotaryTask(
        final String processDefinitionKey,
        final String processInstanceId,
        final Map<String, String> formPropertyMap
    ) {
        final Optional<Task> optionalNotaryTask = bpmService
            .getTasksByProcessId(processDefinitionKey, processInstanceId)
            .stream()
            .findFirst();

        if (!optionalNotaryTask.isPresent()) {
            log.warn("Notary task not found for process {}", processInstanceId);
            return;
        }
        final Task notaryTask = optionalNotaryTask.get();
        final List<FormProperty> formProperties = formPropertyMap.entrySet()
            .stream()
            .map(entry -> new FormProperty(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());

        bpmService.completeTaskViaForm(notaryTask.getId(), formProperties);
    }

    private NotaryApplicationDocument getNotaryApplicationDocument(final CoordinateStatusMessage coordinateStatus) {
        final String eno = messageUtils.retrieveEno(coordinateStatus).orElse(null);
        return notaryApplicationDocumentService
            .findByEno(eno)
            .orElseThrow(() -> new SsrException("Unable to find NotaryApplicationDocument by eno " + eno));
    }

}
