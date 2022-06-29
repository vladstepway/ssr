package ru.croc.ugd.ssr.service.contractappointment;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static ru.croc.ugd.ssr.service.contractdigitalsign.DefaultRestContractDigitalSignService.CONTRACT_DIGITAL_SIGN_DURATION;
import static ru.croc.ugd.ssr.service.utils.IcsUtils.generateIcsFileName;

import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.croc.ugd.ssr.Cip;
import ru.croc.ugd.ssr.PersonType.Contracts.Contract;
import ru.croc.ugd.ssr.PersonType.Contracts.Contract.Files.File;
import ru.croc.ugd.ssr.contractappointment.Applicant;
import ru.croc.ugd.ssr.contractappointment.ContractAppointmentData;
import ru.croc.ugd.ssr.contractdigitalsign.ContractDigitalSign;
import ru.croc.ugd.ssr.contractdigitalsign.ContractDigitalSignData;
import ru.croc.ugd.ssr.contractdigitalsign.ContractDigitalSignFile;
import ru.croc.ugd.ssr.contractdigitalsign.Employee;
import ru.croc.ugd.ssr.contractdigitalsign.Owner;
import ru.croc.ugd.ssr.contractdigitalsign.SignData;
import ru.croc.ugd.ssr.dto.contractappointment.ContractAppointmentFlowStatus;
import ru.croc.ugd.ssr.dto.contractdigitalsign.ContractDigitalSignFlowStatus;
import ru.croc.ugd.ssr.dto.contractdigitalsign.ContractDigitalSignNotificationStatus;
import ru.croc.ugd.ssr.enums.ContractAppointmentSignType;
import ru.croc.ugd.ssr.enums.ContractFileType;
import ru.croc.ugd.ssr.exception.SsrException;
import ru.croc.ugd.ssr.exception.contractappointment.ContractAppointmentNotFound;
import ru.croc.ugd.ssr.integration.service.notification.ContractAppointmentElkNotificationService;
import ru.croc.ugd.ssr.integration.service.notification.ContractDigitalSignElkNotificationService;
import ru.croc.ugd.ssr.integration.util.MessageUtils;
import ru.croc.ugd.ssr.mapper.ContractAppointmentMapper;
import ru.croc.ugd.ssr.mapper.ContractDigitalSignMapper;
import ru.croc.ugd.ssr.model.CipDocument;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.contractDigitalSign.ContractDigitalSignDocument;
import ru.croc.ugd.ssr.model.contractappointment.ContractAppointmentDocument;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateMessage;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateStatusMessage;
import ru.croc.ugd.ssr.mq.listener.contractappointment.EtpContractAppointmentMapper;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.bpm.BpmService;
import ru.croc.ugd.ssr.service.cip.CipService;
import ru.croc.ugd.ssr.service.contractappointment.unsinged.ContractDigitalSignCancellationReason;
import ru.croc.ugd.ssr.service.contractappointment.unsinged.UnsignedByEmployeeCancellationReason;
import ru.croc.ugd.ssr.service.contractappointment.unsinged.UnsignedByOwnersCancellationReason;
import ru.croc.ugd.ssr.service.document.ContractAppointmentDayScheduleDocumentService;
import ru.croc.ugd.ssr.service.document.ContractAppointmentDocumentService;
import ru.croc.ugd.ssr.service.document.ContractDigitalSignDocumentService;
import ru.croc.ugd.ssr.service.ics.IcsFileService;
import ru.croc.ugd.ssr.utils.CipUtils;
import ru.croc.ugd.ssr.utils.ContractUtils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * DefaultContractAppointmentService.
 */
@Slf4j
@Service
@AllArgsConstructor
public class DefaultContractAppointmentService implements ContractAppointmentService {

    private static final String PROCESS_CONTRACT_APPOINTMENT_KEY = "ugdssrContract_processAppointment";
    private static final String OPEN_APPOINTMENT_TASK_AT_VAR = "openAppointmentTaskAt";
    private static final String CLOSE_CANCELLATION_AT_VAR = "closeCancellationAt";

    private static final long DAY_NUMBER_BEFORE_APPOINTMENT_TO_OPEN_TASK = 3;

    private static final Set<ContractAppointmentFlowStatus> CANCELLATION_ALLOWED_STATUSES = Sets.newHashSet(
        ContractAppointmentFlowStatus.REGISTERED,
        ContractAppointmentFlowStatus.TECHNICAL_CRASH_CANCELLATION_REJECTED,
        ContractAppointmentFlowStatus.MOVED_BY_OPERATOR
    );

    private static final Duration CONTRACT_APPOINTMENT_DURATION = Duration.ofMinutes(30);

    private final ContractAppointmentDocumentService contractAppointmentDocumentService;
    private final ContractAppointmentElkNotificationService contractAppointmentElkNotificationService;
    private final ContractAppointmentDayScheduleDocumentService contractAppointmentDayScheduleDocumentService;
    private final ContractAppointmentMapper contractAppointmentMapper;
    private final ContractAppointmentCheckService contractAppointmentCheckService;
    private final EtpContractAppointmentMapper etpContractAppointmentMapper;
    private final MessageUtils messageUtils;
    private final IcsFileService icsFileService;
    private final CipService cipService;
    private final BpmService bpmService;
    private final ContractDigitalSignDocumentService contractDigitalSignDocumentService;
    private final ContractDigitalSignMapper contractDigitalSignMapper;
    private final PersonDocumentService personDocumentService;
    private final ContractDigitalSignElkNotificationService contractDigitalSignElkNotificationService;
    private final ContractAppointmentEmailNotificationService contractAppointmentEmailNotificationService;

    @Override
    public ContractAppointmentDocument fetchByEno(String eno) {
        return contractAppointmentDocumentService.findByEno(eno)
            .orElseThrow(() -> new ContractAppointmentNotFound(eno));
    }

    @Override
    public void processRegistration(final CoordinateMessage coordinateMessage) {
        try {
            final ContractAppointmentDocument contractAppointmentDocument = etpContractAppointmentMapper
                .toContractAppointmentDocument(coordinateMessage);

            if (isNull(contractAppointmentDocument)) {
                throw new SsrException("Unable to parse contract appointment request: " + coordinateMessage);
            }
            if (isDuplicate(contractAppointmentDocument)) {
                log.warn("Contract appointment has been already registered: {}", coordinateMessage);
                return;
            }
            if (!contractAppointmentCheckService.canCreateApplication(contractAppointmentDocument)) {
                throw new SsrException("Failed to pass validation to create contractAppointment: " + coordinateMessage);
            }

            processRegistration(contractAppointmentDocument);
        } catch (Exception e) {
            log.warn("Unable to save contract appointment due to: {}", e.getMessage(), e);
            contractAppointmentElkNotificationService.sendStatus(
                ContractAppointmentFlowStatus.TECHNICAL_CRASH_REGISTRATION,
                messageUtils.retrieveEno(coordinateMessage).orElse(null)
            );
        }
    }

    private void processRegistration(
        final ContractAppointmentDocument contractAppointmentDocument
    ) {
        final ContractAppointmentData contractAppointment = contractAppointmentDocument
            .getDocument()
            .getContractAppointmentData();

        if (contractAppointment.getSignType() == ContractAppointmentSignType.PERSONAL.getTypeCode()
            && !StringUtils.hasText(contractAppointment.getBookingId())) {
            throw new SsrException("No booking for ContractAppointmentDocument "
                + contractAppointment.getEno());
        }

        contractAppointmentDocumentService.createDocument(
            contractAppointmentDocument, true, "contractAppointment: processRegistration"
        );

        contractAppointmentElkNotificationService.sendStatus(
            ContractAppointmentFlowStatus.ACCEPTED, contractAppointmentDocument
        );

        if (contractAppointment.getSignType() == ContractAppointmentSignType.ELECTRONIC.getTypeCode()) {
            final List<PersonDocument> owners = retrieveOwners(contractAppointment);
            final ContractDigitalSignDocument contractDigitalSignDocument = contractDigitalSignMapper
                .toContractDigitalSignDocument(contractAppointment, contractAppointmentDocument.getId(), owners);
            contractDigitalSignDocumentService.createDocument(
                contractDigitalSignDocument, true, "contractAppointment: processRegistration"
            );
        }

        final String cipAddress = ofNullable(contractAppointment.getCipId())
            .flatMap(cipService::fetchById)
            .map(CipDocument::getDocument)
            .map(Cip::getCipData)
            .map(CipUtils::getCipAddress)
            .orElse(null);

        createIcsFileInFileStore(contractAppointmentDocument, cipAddress)
            .ifPresent(contractAppointment::setIcsFileStoreId);

        ofNullable(contractAppointment.getIcsFileStoreId())
            .flatMap(icsFileService::uploadIcsFileInChed)
            .ifPresent(contractAppointment::setIcsChedFileId);

        createAppointmentProcess(contractAppointmentDocument)
            .ifPresent(contractAppointment::setProcessInstanceId);

        setAndSendActualStatus(
            contractAppointmentDocument,
            ContractAppointmentFlowStatus.REGISTERED,
            ContractDigitalSignFlowStatus.REGISTERED
        );

        contractAppointmentDocumentService.updateDocument(
            contractAppointmentDocument, "contractAppointment: processRegistration"
        );

        contractAppointmentEmailNotificationService.sendNotificationEmail(contractAppointmentDocument);
    }

    private List<PersonDocument> retrieveOwners(final ContractAppointmentData contractAppointment) {
        return ofNullable(contractAppointment)
            .map(ContractAppointmentData::getApplicant)
            .map(Applicant::getPersonDocumentId)
            .flatMap(personDocumentService::fetchById)
            .map(personDocument -> personDocumentService.getOtherFlatOwners(personDocument, true))
            .orElse(Collections.emptyList());
    }

    private Optional<String> createIcsFileInFileStore(
        final ContractAppointmentDocument contractAppointmentDocument,
        final String cipAddress
    ) {
        final ContractAppointmentData contractAppointment = contractAppointmentDocument
            .getDocument()
            .getContractAppointmentData();

        if (contractAppointment.getSignType() == ContractAppointmentSignType.ELECTRONIC.getTypeCode()) {
            return createIcsFile(
                contractAppointmentDocument,
                contractAppointment.getAppointmentDateTime(),
                "Заключение договора с УКЭП",
                null,
                CONTRACT_DIGITAL_SIGN_DURATION
            );
        } else {
            return createIcsFile(
                contractAppointmentDocument,
                contractAppointment.getAppointmentDateTime(),
                "Заключение договора",
                cipAddress,
                CONTRACT_APPOINTMENT_DURATION
            );
        }
    }

    private Optional<String> createIcsFile(
        final ContractAppointmentDocument document,
        final LocalDateTime appointmentDateTime,
        final String summary,
        final String location,
        final Duration duration
    ) {
        return icsFileService.createIcsFileInFileStore(
            summary,
            summary,
            location,
            appointmentDateTime,
            duration,
            generateIcsFileName(document.getId()),
            document.getFolderId()
        );
    }

    private boolean isDuplicate(final ContractAppointmentDocument contractAppointmentDocument) {
        final ContractAppointmentData contractAppointment = contractAppointmentDocument
            .getDocument()
            .getContractAppointmentData();
        final boolean existsByEno = of(contractAppointment)
            .map(ContractAppointmentData::getEno)
            .filter(contractAppointmentDocumentService::existsByEno)
            .isPresent();
        final boolean existsByBookingId = of(contractAppointment)
            .filter(contractAppointmentData -> contractAppointmentData.getSignType()
                == ContractAppointmentSignType.PERSONAL.getTypeCode())
            .map(ContractAppointmentData::getBookingId)
            .filter(this::isAppointmentRegisteredByBookingId)
            .isPresent();
        return existsByEno || existsByBookingId;
    }

    @Override
    public void processCancelByApplicantRequest(final CoordinateStatusMessage coordinateStatus) {
        try {
            final ContractAppointmentDocument contractAppointmentDocument =
                getContractAppointmentDocument(coordinateStatus);
            processCancelByApplicantRequest(contractAppointmentDocument);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            contractAppointmentElkNotificationService.sendStatus(
                ContractAppointmentFlowStatus.TECHNICAL_CRASH_CANCELLATION_REJECTED,
                messageUtils.retrieveEno(coordinateStatus).orElse(null)
            );
        }
    }

    private void processCancelByApplicantRequest(
        final ContractAppointmentDocument contractAppointmentDocument
    ) {
        final ContractAppointmentData contractAppointment = contractAppointmentDocument
            .getDocument()
            .getContractAppointmentData();

        if (canCancelAppointment(contractAppointment, true)) {
            contractAppointment.setCancelDate(LocalDate.now());
            //TODO set proper cancel reason from CoordinateStatusMessage

            setAndSendActualStatus(
                contractAppointmentDocument,
                ContractAppointmentFlowStatus.CANCELED_BY_APPLICANT,
                ContractDigitalSignFlowStatus.CANCELED_BY_APPLICANT
            );

            cancelBookingIfRequired(contractAppointmentDocument);

            ofNullable(contractAppointment.getProcessInstanceId())
                .ifPresent(this::finishBpmProcess);

            contractAppointmentDocumentService.updateDocument(
                contractAppointmentDocument.getId(), contractAppointmentDocument, true, true, null
            );
        } else {
            contractAppointmentElkNotificationService.sendStatus(
                ContractAppointmentFlowStatus.TECHNICAL_CRASH_CANCELLATION_REJECTED, contractAppointmentDocument
            );
        }
    }

    @Override
    public void cancelBookingIfRequired(final ContractAppointmentDocument contractAppointmentDocument) {
        final ContractAppointmentData contractAppointment = contractAppointmentDocument
            .getDocument()
            .getContractAppointmentData();

        if (nonNull(contractAppointment.getBookingId())
            && hasAppointmentDateTimeInFuture(contractAppointment)
        ) {
            contractAppointmentDayScheduleDocumentService.cancelBooking(contractAppointment.getBookingId());

            contractAppointment.setBookingId(null);
            contractAppointment.setAppointmentDateTime(null);
        }
    }

    private static boolean hasAppointmentDateTimeInFuture(final ContractAppointmentData contractAppointmentData) {
        return ofNullable(contractAppointmentData.getAppointmentDateTime())
            .filter(appointmentDateTime -> appointmentDateTime.isAfter(LocalDateTime.now()))
            .isPresent();
    }

    @Override
    public boolean canCancelAppointment(final ContractAppointmentData contractAppointmentData, final boolean isMpgu) {
        final ContractAppointmentFlowStatus currentFlowStatus = ContractAppointmentFlowStatus
            .of(contractAppointmentData.getStatusId());

        return CANCELLATION_ALLOWED_STATUSES.contains(currentFlowStatus)
            && (isMpgu && hasAppointmentDateTimeInFuture(contractAppointmentData)
            || isAppointmentDateTimeOneDayInAdvance(contractAppointmentData));
    }

    private static boolean isAppointmentDateTimeOneDayInAdvance(final ContractAppointmentData contractAppointmentData) {
        return ofNullable(contractAppointmentData.getAppointmentDateTime())
            .filter(appointmentDateTime -> appointmentDateTime.isAfter(LocalDateTime.now().plusDays(1)))
            .isPresent();
    }

    @Override
    public void moveAppointmentDateTime(
        final String contractAppointmentDocumentId, final String bookingId, final LocalDateTime appointmentDateTime
    ) {
        final ContractAppointmentDocument contractAppointmentDocument = contractAppointmentDocumentService
            .fetchDocument(contractAppointmentDocumentId);
        final ContractAppointmentData contractAppointment = contractAppointmentDocument
            .getDocument()
            .getContractAppointmentData();

        if (!isAppointmentDateTimeOneDayInAdvance(contractAppointment)) {
            throw new SsrException("Нельзя перенести запись менее чем за сутки до приема");
        }

        contractAppointment.setBookingId(bookingId);
        contractAppointment.setAppointmentDateTime(appointmentDateTime);
        contractAppointment.setStatusId(ContractAppointmentFlowStatus.REGISTERED.getId());

        final String cipAddress = ofNullable(contractAppointment.getCipId())
            .flatMap(cipService::fetchById)
            .map(CipDocument::getDocument)
            .map(Cip::getCipData)
            .map(CipUtils::getCipAddress)
            .orElse(null);

        createIcsFileInFileStore(contractAppointmentDocument, cipAddress)
            .ifPresent(contractAppointment::setIcsFileStoreId);

        ofNullable(contractAppointment.getIcsFileStoreId())
            .flatMap(icsFileService::uploadIcsFileInChed)
            .ifPresent(contractAppointment::setIcsChedFileId);

        ofNullable(contractAppointment.getProcessInstanceId())
            .ifPresent(this::finishBpmProcess);
        createAppointmentProcess(contractAppointmentDocument)
            .ifPresent(contractAppointment::setProcessInstanceId);

        contractAppointmentDocumentService
            .updateDocument(contractAppointmentDocument.getId(), contractAppointmentDocument, true, true, null);
        contractAppointmentElkNotificationService
            .sendStatus(ContractAppointmentFlowStatus.MOVED_BY_OPERATOR, contractAppointmentDocument);
    }

    private ContractAppointmentDocument getContractAppointmentDocument(final CoordinateStatusMessage coordinateStatus) {
        final String eno = messageUtils.retrieveEno(coordinateStatus).orElse(null);
        return contractAppointmentDocumentService
            .findByEno(eno)
            .orElseThrow(() -> new SsrException("Unable to find ContractAppointmentDocument by eno " + eno));
    }

    @Override
    public void finishBpmProcess(final String processInstanceId) {
        if (StringUtils.hasText(processInstanceId)) {
            try {
                bpmService.deleteProcessInstance(processInstanceId);
            } catch (Exception e) {
                log.warn("Unable to finish bpm process for contract appointment due to: {}", e.getMessage(), e);
            }
        }
    }

    @Override
    public void processSignedContract(final String contractAppointmentId, final Contract signedContract) {
        processContractSign(contractAppointmentId, true, signedContract, null);
    }

    @Override
    public void processUnsignedContract(final String contractAppointmentId, final String refuseReason) {
        processContractSign(contractAppointmentId, false, null, refuseReason);
    }

    @Override
    public boolean isAppointmentRegisteredByBookingId(final String bookingId) {
        return contractAppointmentDocumentService.isAppointmentRegisteredByBookingId(bookingId);
    }

    @Override
    public void processAutoCancellation(ContractAppointmentDocument contractAppointmentDocument) {
        final ContractAppointmentData contractAppointment = contractAppointmentDocument
            .getDocument()
            .getContractAppointmentData();

        contractAppointment.setStatusId(ContractAppointmentFlowStatus.CANCELED_BY_OPERATOR.getId());
        contractAppointment.setCancelDate(LocalDate.now());
        contractAppointment.setCancelReason("Неявка");

        contractAppointmentElkNotificationService
            .sendStatus(ContractAppointmentFlowStatus.CANCELED_BY_OPERATOR, contractAppointmentDocument);

        ofNullable(contractAppointment.getProcessInstanceId())
            .ifPresent(this::finishBpmProcess);

        contractAppointmentDocumentService
            .updateDocument(contractAppointmentDocument.getId(), contractAppointmentDocument, true, true, null);
    }

    @Override
    public void processUnsignedByEmployeeCancellation(final ContractAppointmentDocument contractAppointmentDocument) {
        processUnsignedCancellation(contractAppointmentDocument, true);
    }

    @Override
    public void processUnsignedByOwnersCancellation(final ContractAppointmentDocument contractAppointmentDocument) {
        processUnsignedCancellation(contractAppointmentDocument, false);
    }

    @Override
    public void processUnsignedCancellation(final ContractAppointmentDocument contractAppointmentDocument) {
        processUnsignedCancellation(contractAppointmentDocument, null);
    }

    public void processUnsignedCancellation(
        final ContractAppointmentDocument contractAppointmentDocument, final Boolean checkForEmployee
    ) {
        try {
            final ContractAppointmentData contractAppointment = contractAppointmentDocument
                .getDocument()
                .getContractAppointmentData();

            final ContractDigitalSignCancellationReason cancellationReason = determineUnsignedCancellationReason(
                contractAppointmentDocument, checkForEmployee
            );
            contractAppointment.setStatusId(cancellationReason.getFlowStatus().getId());
            contractAppointment.setCancelDate(LocalDate.now());
            contractAppointment.setCancelReason(cancellationReason.getStatusText());

            contractAppointmentDocumentService.updateDocument(
                contractAppointmentDocument, "processUnsignedCancellation"
            );
            contractAppointmentElkNotificationService.sendUnsignedContractStatus(cancellationReason);
        } catch (Exception e) {
            log.warn("unable to cancel unsigned contract appointment: {} due to {}",
                contractAppointmentDocument.getId(),
                e.getMessage(),
                e
            );
        }
    }

    @Override
    public void setAndSendActualStatus(
        final ContractAppointmentDocument contractAppointmentDocument,
        final ContractAppointmentFlowStatus contractAppointmentFlowStatus,
        final ContractDigitalSignFlowStatus contractDigitalSignFlowStatus
    ) {
        final ContractAppointmentData contractAppointment = contractAppointmentDocument.getDocument()
            .getContractAppointmentData();

        if (contractAppointment.getSignType() == ContractAppointmentSignType.ELECTRONIC.getTypeCode()) {
            contractAppointment.setStatusId(contractDigitalSignFlowStatus.getId());
            contractDigitalSignElkNotificationService.sendStatus(
                contractDigitalSignFlowStatus, contractAppointmentDocument
            );
        } else {
            contractAppointment.setStatusId(contractAppointmentFlowStatus.getId());
            contractAppointmentElkNotificationService.sendStatus(
                contractAppointmentFlowStatus, contractAppointmentDocument
            );
        }
    }

    private ContractDigitalSignCancellationReason determineUnsignedCancellationReason(
        final ContractAppointmentDocument contractAppointmentDocument, final Boolean checkForEmployee
    ) {
        final ContractDigitalSignDocument contractDigitalSignDocument = contractDigitalSignDocumentService
            .findByContractAppointmentId(contractAppointmentDocument.getId()).orElseThrow(() ->
                new SsrException(String.format(
                    "Failed to fetch contractDigitalSignDocument by contractAppointmentId: %s",
                    contractAppointmentDocument.getId()
                ))
            );
        final List<String> unsignedPersonNames = of(contractDigitalSignDocument)
            .map(ContractDigitalSignDocument::getDocument)
            .map(ContractDigitalSign::getContractDigitalSignData)
            .map(ContractDigitalSignData::getOwners)
            .map(ContractDigitalSignData.Owners::getOwner)
            .orElse(Collections.emptyList())
            .stream()
            .filter(owner -> !isContractSignedByOwner(owner))
            .map(Owner::getFullName)
            .collect(Collectors.toList());
        if ((isNull(checkForEmployee) || !checkForEmployee)
            && !unsignedPersonNames.isEmpty()) { //at least one owner did not provide signature
            contractDigitalSignElkNotificationService.sendNotificationStatus(
                ContractDigitalSignNotificationStatus.SIGNING_PERIOD_EXPIRED,
                contractDigitalSignDocument,
                contractAppointmentDocument
            );
            return UnsignedByOwnersCancellationReason.builder()
                .flowStatus(ContractDigitalSignFlowStatus.REFUSE_TO_PROVIDE_SERVICE_DUE_TO_OWNER)
                .contractAppointmentDocument(contractAppointmentDocument)
                .personNames(unsignedPersonNames)
                .build();
        }
        if ((isNull(checkForEmployee) || checkForEmployee)
            && !isContractSignedByEmployee(contractDigitalSignDocument)) { //employee did not provide signature
            return UnsignedByEmployeeCancellationReason.builder()
                .flowStatus(ContractDigitalSignFlowStatus.REFUSE_TO_PROVIDE_SERVICE_DUE_TO_EMPLOYEE)
                .contractAppointmentDocument(contractAppointmentDocument)
                .build();
        }
        if (isNull(checkForEmployee)) {
            //this will never happen
            throw new SsrException(
                "Failed to determine reason for unsigned contract appointment with id %s",
                contractAppointmentDocument.getId()
            );
        } else {
            return null;
        }
    }

    private boolean isContractSignedByOwner(final Owner owner) {
        return ofNullable(owner)
            .map(Owner::getContractFile)
            .map(ContractDigitalSignFile::getSignData)
            .map(SignData::isIsVerified)
            .orElse(false);
    }

    private boolean isContractSignedByEmployee(final ContractDigitalSignDocument contractDigitalSignDocument) {
        return ofNullable(contractDigitalSignDocument)
            .map(ContractDigitalSignDocument::getDocument)
            .map(ContractDigitalSign::getContractDigitalSignData)
            .map(ContractDigitalSignData::getEmployee)
            .map(Employee::getContractFile)
            .map(ContractDigitalSignFile::getSignData)
            .map(SignData::isIsVerified)
            .orElse(false);
    }

    private void processContractSign(
        final String contractAppointmentId,
        final boolean isContractEntered,
        final Contract signedContract,
        final String refuseReason
    ) {
        contractAppointmentDocumentService.fetchById(contractAppointmentId)
            .ifPresent(contractAppointmentDocument ->
                processContractSign(contractAppointmentDocument, isContractEntered, signedContract, refuseReason)
            );
    }

    private void processContractSign(
        final ContractAppointmentDocument contractAppointmentDocument,
        final boolean isContractEntered,
        final Contract signedContract,
        final String refuseReason
    ) {
        final ContractAppointmentData contractAppointment = contractAppointmentDocument
            .getDocument()
            .getContractAppointmentData();

        contractAppointment.setIsContractEntered(isContractEntered);

        if (isContractEntered && nonNull(signedContract)) {
            setSignContractData(contractAppointment, signedContract);
            setSignActData(contractAppointment, signedContract);
        }

        if (isContractEntered) {
            setAndSendActualStatus(
                contractAppointmentDocument,
                ContractAppointmentFlowStatus.CONTRACT_SIGNED,
                ContractDigitalSignFlowStatus.CONTRACT_SIGNED
            );
        } else {
            contractAppointment.setStatusId(ContractAppointmentFlowStatus.REFUSE_TO_PROVIDE_SERVICE.getId());
            contractAppointment.setRefuseReason(refuseReason);
            contractAppointment.setCancelDate(LocalDate.now());
            contractAppointmentElkNotificationService.sendStatus(
                ContractAppointmentFlowStatus.REFUSE_TO_PROVIDE_SERVICE, contractAppointmentDocument
            );
        }

        contractAppointmentDocumentService
            .updateDocument(contractAppointmentDocument.getId(), contractAppointmentDocument, true, true, null);

        finishBpmProcess(contractAppointment.getProcessInstanceId());

        cancelBookingIfRequired(contractAppointmentDocument);
    }

    private Optional<String> createAppointmentProcess(final ContractAppointmentDocument contractAppointmentDocument) {
        final String contractAppointmentId = contractAppointmentDocument.getId();

        final ContractAppointmentData contractAppointment = contractAppointmentDocument
            .getDocument()
            .getContractAppointmentData();

        final LocalDateTime appointmentDateTime = contractAppointment.getAppointmentDateTime();
        if (isNull(appointmentDateTime)) {
            log.warn(
                "Unable to create bpm process for contract appointment {} without appointment date and time",
                contractAppointmentId
            );
            return Optional.empty();
        }

        final LocalDateTime openAppointmentTaskAt = appointmentDateTime
            .minusDays(DAY_NUMBER_BEFORE_APPOINTMENT_TO_OPEN_TASK);

        final Map<String, String> variablesMap = new HashMap<>();
        variablesMap.put(BpmService.PROCESS_DOCUMENT_KEY_ENTITY_ID_VAR, contractAppointmentId);
        variablesMap.put(OPEN_APPOINTMENT_TASK_AT_VAR, openAppointmentTaskAt.toString());
        variablesMap.put(CLOSE_CANCELLATION_AT_VAR, appointmentDateTime.toString());

        return ofNullable(bpmService.startNewProcess(PROCESS_CONTRACT_APPOINTMENT_KEY, variablesMap));
    }

    private void setSignContractData(final ContractAppointmentData contractAppointment, final Contract contract) {
        final File contractFile = ContractUtils
            .getContractFileByFileType(contract, ContractFileType.SIGNED_CONTRACT.getStringValue())
            .orElse(null);
        final LocalDate contractSignDate = ofNullable(contract)
            .map(Contract::getContractSignDate)
            .orElse(null);

        contractAppointment.setContractFile(contractAppointmentMapper.toContractAppointmentFile(contractFile));
        contractAppointment.setContractSignDate(contractSignDate);
    }

    private void setSignActData(final ContractAppointmentData contractAppointment, final Contract contract) {
        final File actFile = ContractUtils
            .getContractFileByFileType(contract, ContractFileType.SIGNED_ACT.getStringValue())
            .orElse(null);
        final LocalDate actSignDate = ofNullable(contract)
            .map(Contract::getActSignDate)
            .orElse(null);

        contractAppointment.setActFile(contractAppointmentMapper.toContractAppointmentFile(actFile));
        contractAppointment.setActSignDate(actSignDate);
    }
}
