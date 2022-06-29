package ru.croc.ugd.ssr.service.notary;

import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.dto.notary.NotaryApplicationFlowStatus;
import ru.croc.ugd.ssr.dto.notary.RestNotaryApplicationCreateUpdateRequestDto;
import ru.croc.ugd.ssr.dto.notary.RestNotaryApplicationResponseDto;
import ru.croc.ugd.ssr.integration.eno.EnoCreator;
import ru.croc.ugd.ssr.integration.service.notification.NotaryApplicationElkNotificationService;
import ru.croc.ugd.ssr.mapper.NotaryApplicationMapper;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.notary.NotaryApplicationDocument;
import ru.croc.ugd.ssr.notary.NotaryApplicantFiles;
import ru.croc.ugd.ssr.notary.NotaryApplication;
import ru.croc.ugd.ssr.notary.NotaryApplicationApplicant;
import ru.croc.ugd.ssr.notary.NotaryApplicationType;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.SsrFilestoreService;
import ru.croc.ugd.ssr.service.UserService;
import ru.croc.ugd.ssr.service.document.NotaryApplicationDocumentService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

/**
 * DefaultRestNotaryApplicationService.
 */
@Slf4j
@Service
@AllArgsConstructor
public class DefaultRestNotaryApplicationService implements RestNotaryApplicationService {

    private static final String NOTARY_SERVICE_NUMBER = "084001";

    private final EnoCreator enoCreator;
    private final NotaryApplicationDocumentService notaryApplicationDocumentService;
    private final NotaryApplicationService notaryApplicationService;
    private final RestNotaryService restNotaryService;
    private final NotaryApplicationMapper notaryApplicationMapper;
    private final PersonDocumentService personDocumentService;
    private final NotaryApplicationElkNotificationService notaryApplicationElkNotificationService;
    private final UserService userService;
    private final SsrFilestoreService ssrFilestoreService;

    /**
     * Создать заявку.
     *
     * @param body тело запроса
     */
    @Override
    public RestNotaryApplicationResponseDto create(final RestNotaryApplicationCreateUpdateRequestDto body) {
        final PersonDocument personDocument = personDocumentService.fetchDocument(body.getApplicant().getId());

        final String eno = enoCreator.generateEtpMvNotificationEnoNumber(NOTARY_SERVICE_NUMBER);
        final NotaryApplicationDocument notaryApplicationDocument = notaryApplicationMapper
            .toNotaryApplicationDocument(personDocument, body, eno);
        notaryApplicationDocumentService.createDocument(notaryApplicationDocument, true, null);

        final NotaryApplicationType notaryApplication = notaryApplicationDocument
            .getDocument()
            .getNotaryApplicationData();
        final boolean isCreatedByNotary = isCreatedByNotary(notaryApplicationDocument);
        if (isCreatedByNotary) {
            notaryApplication.setStatusId(NotaryApplicationFlowStatus.REGISTERED.getId());
            notaryApplicationDocument.addHistoryEvent(NotaryApplicationFlowStatus.REGISTERED.getId());

            notaryApplicationElkNotificationService
                .sendStatus(NotaryApplicationFlowStatus.REGISTERED, notaryApplicationDocument);
        } else {
            notaryApplication.setStatusId(NotaryApplicationFlowStatus.ACCEPTED.getId());
            notaryApplicationDocument.addHistoryEvent(NotaryApplicationFlowStatus.ACCEPTED.getId());
        }

        notaryApplicationService
            .createContractPreparationProcess(notaryApplicationDocument, isCreatedByNotary)
            .ifPresent(notaryApplication::setProcessInstanceId);

        notaryApplicationDocumentService.updateDocument(notaryApplicationDocument);

        return notaryApplicationMapper.toRestNotaryApplicationResponseDto(
            notaryApplicationDocument,
            personDocumentService.getOtherFlatOwners(personDocument, true),
            restNotaryService
                .fetchNotaryInfo(notaryApplicationDocument.getDocument().getNotaryApplicationData().getNotaryId()),
            personDocument
        );
    }

    private boolean isCreatedByNotary(final NotaryApplicationDocument notaryApplicationDocument) {
        final String currentLogin = userService.getCurrentUserLogin();

        return notaryApplicationService.getNotaryLogin(notaryApplicationDocument)
            .filter(notaryLogin -> notaryLogin.equals(currentLogin))
            .isPresent();
    }

    /**
     * Обновить заявку.
     *
     * @param applicationId ИД заявления
     * @param body тело запроса
     * @return заявление
     */
    @Override
    public RestNotaryApplicationResponseDto update(
        final String applicationId, final RestNotaryApplicationCreateUpdateRequestDto body
    ) {
        final NotaryApplicationDocument notaryApplicationDocument = notaryApplicationDocumentService
            .fetchDocument(applicationId);

        final NotaryApplicationDocument updatedNotaryApplicationDocument = notaryApplicationMapper
            .updateNotaryApplicationDocument(notaryApplicationDocument, body);
        notaryApplicationDocumentService
            .updateDocument(applicationId, updatedNotaryApplicationDocument, true, true, null);

        final PersonDocument personDocument = personDocumentService.fetchDocument(body.getApplicant().getId());
        final NotaryApplicationType notaryApplication = updatedNotaryApplicationDocument
            .getDocument()
            .getNotaryApplicationData();

        return notaryApplicationMapper.toRestNotaryApplicationResponseDto(
            updatedNotaryApplicationDocument,
            personDocumentService.getOtherFlatOwners(personDocument, true),
            restNotaryService.fetchNotaryInfo(notaryApplication.getNotaryId()),
            personDocument
        );
    }

    /**
     * Получить карточку заявления.
     *
     * @param applicationId Ид заявки
     * @return карточка
     */
    @Override
    public RestNotaryApplicationResponseDto fetchById(final String applicationId) {
        final NotaryApplicationDocument notaryApplicationDocument = notaryApplicationDocumentService
            .fetchDocument(applicationId);

        final PersonDocument personDocument = of(notaryApplicationDocument)
            .map(NotaryApplicationDocument::getDocument)
            .map(NotaryApplication::getNotaryApplicationData)
            .map(NotaryApplicationType::getApplicant)
            .map(NotaryApplicationApplicant::getPersonId)
            .map(personDocumentService::fetchDocument)
            .orElse(null);
        final NotaryApplicationType notaryApplication = notaryApplicationDocument
            .getDocument()
            .getNotaryApplicationData();

        return notaryApplicationMapper.toRestNotaryApplicationResponseDto(
            notaryApplicationDocument,
            personDocumentService.getOtherFlatOwners(personDocument, true),
            restNotaryService.fetchNotaryInfo(notaryApplication.getNotaryId()),
            personDocument
        );
    }

    /**
     * Принять в работу.
     *
     * @param applicationId Ид заявки
     * @param comment коментарий
     */
    @Override
    public void accept(final String applicationId, final String comment) {
        final NotaryApplicationDocument notaryApplicationDocument = notaryApplicationDocumentService
            .fetchDocument(applicationId);

        final NotaryApplicationType notaryApplication = notaryApplicationDocument
            .getDocument()
            .getNotaryApplicationData();
        notaryApplication.setStatusId(NotaryApplicationFlowStatus.REGISTERED.getId());
        notaryApplicationDocument.addHistoryEvent(NotaryApplicationFlowStatus.REGISTERED.getId(), comment);

        notaryApplicationService.closeAcceptApplicationTask(notaryApplication.getProcessInstanceId(), true);

        notaryApplicationDocumentService.updateDocument(notaryApplicationDocument);

        notaryApplicationElkNotificationService
            .sendStatus(NotaryApplicationFlowStatus.REGISTERED, notaryApplicationDocument);
    }

    /**
     * Отказ в регистрации заявления.
     *
     * @param applicationId Ид заявки
     * @param comment коментарий
     */
    @Override
    public void decline(final String applicationId, final String comment) {
        final NotaryApplicationDocument notaryApplicationDocument = notaryApplicationDocumentService
            .fetchDocument(applicationId);

        final NotaryApplicationType notaryApplication = notaryApplicationDocument
            .getDocument()
            .getNotaryApplicationData();
        notaryApplication.setStatusId(NotaryApplicationFlowStatus.DECLINED.getId());
        notaryApplicationDocument.addHistoryEvent(NotaryApplicationFlowStatus.DECLINED.getId(), comment);

        notaryApplicationDocumentService.updateDocument(notaryApplicationDocument);

        notaryApplicationElkNotificationService
            .sendStatus(NotaryApplicationFlowStatus.DECLINED, notaryApplicationDocument);

        notaryApplicationService.closeAcceptApplicationTask(notaryApplication.getProcessInstanceId(), false);
    }

    @Override
    public void refuse(final String applicationId, final String comment) {
        final NotaryApplicationDocument notaryApplicationDocument = notaryApplicationDocumentService
            .fetchDocument(applicationId);

        final NotaryApplicationType notaryApplication = notaryApplicationDocument
            .getDocument()
            .getNotaryApplicationData();

        notaryApplication.setCompletionDateTime(LocalDateTime.now());
        notaryApplication.setStatusId(NotaryApplicationFlowStatus.REFUSE_TO_PROVIDE_SERVICE.getId());
        notaryApplicationDocument
            .addHistoryEvent(NotaryApplicationFlowStatus.REFUSE_TO_PROVIDE_SERVICE.getId(), comment);
        notaryApplicationService.cancelBookingIfRequired(notaryApplication);

        notaryApplicationDocumentService
            .updateDocument(notaryApplicationDocument);

        notaryApplicationElkNotificationService
            .sendStatus(NotaryApplicationFlowStatus.REFUSE_TO_PROVIDE_SERVICE, notaryApplicationDocument);

        notaryApplicationService.finishBpmProcess(notaryApplication.getProcessInstanceId());

    }

    /**
     * Подтвердить готовность проекта договора.
     *  @param applicationId Ид заявки.
     * @param comment Коментарий.
     */
    @Override
    public void draftContractReady(String applicationId, String comment) {
        final NotaryApplicationDocument notaryApplicationDocument = notaryApplicationDocumentService
            .fetchDocument(applicationId);

        final NotaryApplicationType notaryApplication = notaryApplicationDocument
            .getDocument()
            .getNotaryApplicationData();
        notaryApplication.setStatusId(NotaryApplicationFlowStatus.DRAFT_CONTRACT_READY.getId());
        notaryApplicationDocument.addHistoryEvent(NotaryApplicationFlowStatus.DRAFT_CONTRACT_READY.getId(), comment);

        notaryApplicationService.closeContractPreparationTask(notaryApplication.getProcessInstanceId(), true);

        notaryApplicationService.createSetUpAppointmentProcess(notaryApplicationDocument)
            .ifPresent(notaryApplication::setProcessInstanceId);

        notaryApplicationDocumentService.updateDocument(notaryApplicationDocument);

        notaryApplicationElkNotificationService
            .sendStatus(NotaryApplicationFlowStatus.DRAFT_CONTRACT_READY, notaryApplicationDocument);
    }

    /**
     * Отмена записи к нотариусу.
     *
     * @param applicationId Ид заявки
     * @param comment коментарий
     */
    @Override
    public void cancelByNotary(final String applicationId, final String comment) {
        final NotaryApplicationDocument notaryApplicationDocument = notaryApplicationDocumentService
            .fetchDocument(applicationId);

        final NotaryApplicationType notaryApplication = notaryApplicationDocument
            .getDocument()
            .getNotaryApplicationData();
        notaryApplication.setStatusId(NotaryApplicationFlowStatus.DRAFT_CONTRACT_READY.getId());
        notaryApplicationDocument.addHistoryEvent(NotaryApplicationFlowStatus.CANCELED_BY_NOTARY.getId(), comment);

        notaryApplicationService.cancelBookingIfRequired(notaryApplication);

        ofNullable(notaryApplication.getProcessInstanceId())
            .ifPresent(notaryApplicationService::finishBpmProcess);

        notaryApplicationService.createSetUpAppointmentProcess(notaryApplicationDocument)
            .ifPresent(notaryApplication::setProcessInstanceId);

        notaryApplicationDocumentService.updateDocument(notaryApplicationDocument);

        notaryApplicationElkNotificationService
            .sendStatus(NotaryApplicationFlowStatus.CANCELED_BY_NOTARY, notaryApplicationDocument);
    }

    /**
     * Подтвердить комплектность документов.
     *
     * @param applicationId Ид заявки.
     */
    @Override
    public void confirmDocuments(final String applicationId) {
        final NotaryApplicationDocument notaryApplicationDocument = notaryApplicationDocumentService
            .fetchDocument(applicationId);

        final NotaryApplicationType notaryApplication = notaryApplicationDocument
            .getDocument()
            .getNotaryApplicationData();
        notaryApplication.setStatusId(NotaryApplicationFlowStatus.REGISTERED.getId());
        confirmRequestedFiles(notaryApplication);

        notaryApplicationDocumentService
            .updateDocument(notaryApplicationDocument);

        notaryApplicationService.closeDocumentsVerificationTask(notaryApplication.getProcessInstanceId(), true);
    }

    /**
     * Запросить дополнительные документы.
     *
     * @param applicationId Ид заявки.
     * @param comment Комментарий с перечнем документов.
     */
    @Override
    public void requestDocuments(final String applicationId, final String comment) {
        final NotaryApplicationDocument notaryApplicationDocument = notaryApplicationDocumentService
            .fetchDocument(applicationId);

        final NotaryApplicationType notaryApplication = notaryApplicationDocument
            .getDocument()
            .getNotaryApplicationData();
        final NotaryApplicationFlowStatus previousNotaryApplicationStatus = NotaryApplicationFlowStatus
            .of(notaryApplication.getStatusId());
        notaryApplication.setStatusId(NotaryApplicationFlowStatus.WAITING_DOCUMENTS.getId());
        notaryApplicationDocument.addHistoryEvent(NotaryApplicationFlowStatus.WAITING_DOCUMENTS.getId(), comment);
        confirmRequestedFiles(notaryApplication);

        notaryApplicationDocumentService.updateDocument(notaryApplicationDocument);

        notaryApplicationElkNotificationService
            .sendStatus(NotaryApplicationFlowStatus.WAITING_DOCUMENTS, notaryApplicationDocument);

        if (previousNotaryApplicationStatus == NotaryApplicationFlowStatus.DOCUMENTS_RECEIVED) {
            notaryApplicationService.closeDocumentsVerificationTask(notaryApplication.getProcessInstanceId(), false);
        } else {
            notaryApplicationService.closeContractPreparationTask(notaryApplication.getProcessInstanceId(), false);
        }
    }

    @Override
    public void deleteAll() {
        notaryApplicationDocumentService.fetchDocumentsPage(0, 1000)
            .forEach(document ->
                notaryApplicationDocumentService.deleteDocument(document.getId(), true, null)
            );
    }

    @Override
    public void refuseNoDocuments(final String applicationId) {
        final NotaryApplicationDocument notaryApplicationDocument = notaryApplicationDocumentService
            .fetchDocument(applicationId);
        final NotaryApplicationType notaryApplication = notaryApplicationDocument
            .getDocument()
            .getNotaryApplicationData();

        notaryApplication.setCompletionDateTime(LocalDateTime.now());
        notaryApplication.setStatusId(NotaryApplicationFlowStatus.REFUSE_NO_DOCUMENTS.getId());
        notaryApplicationDocument.addHistoryEvent(NotaryApplicationFlowStatus.REFUSE_NO_DOCUMENTS.getId());

        notaryApplicationDocumentService.updateDocument(notaryApplicationDocument);

        notaryApplicationElkNotificationService
            .sendStatus(NotaryApplicationFlowStatus.REFUSE_NO_DOCUMENTS, notaryApplicationDocument);

    }

    @Override
    public void refuseNoBooking(final String applicationId) {
        final NotaryApplicationDocument notaryApplicationDocument = notaryApplicationDocumentService
            .fetchDocument(applicationId);

        final NotaryApplicationType notaryApplication = notaryApplicationDocument
            .getDocument()
            .getNotaryApplicationData();
        notaryApplication.setCompletionDateTime(LocalDateTime.now());
        notaryApplication.setStatusId(NotaryApplicationFlowStatus.REFUSE_NO_BOOKING.getId());
        notaryApplicationDocument.addHistoryEvent(NotaryApplicationFlowStatus.REFUSE_NO_BOOKING.getId());

        notaryApplicationDocumentService.updateDocument(notaryApplicationDocument);

        notaryApplicationElkNotificationService
            .sendStatus(NotaryApplicationFlowStatus.REFUSE_NO_BOOKING, notaryApplicationDocument);
    }

    @Override
    public void bookingClosed(final String applicationId) {
        final NotaryApplicationDocument notaryApplicationDocument = notaryApplicationDocumentService
            .fetchDocument(applicationId);

        notaryApplicationDocument.addHistoryEvent(NotaryApplicationFlowStatus.BOOKING_CLOSED.getId());

        notaryApplicationDocumentService
            .updateDocument(notaryApplicationDocument.getId(), notaryApplicationDocument, true, false, null);

        notaryApplicationElkNotificationService
            .sendStatus(NotaryApplicationFlowStatus.BOOKING_CLOSED, notaryApplicationDocument);
    }

    @Override
    public void appointmentReminder(final String applicationId) {
        final NotaryApplicationDocument notaryApplicationDocument = notaryApplicationDocumentService
            .fetchDocument(applicationId);

        notaryApplicationDocument.addHistoryEvent(NotaryApplicationFlowStatus.APPOINTMENT_REMINDER.getId());

        notaryApplicationDocumentService
            .updateDocument(notaryApplicationDocument.getId(), notaryApplicationDocument, true, false, null);

        notaryApplicationElkNotificationService
            .sendStatus(NotaryApplicationFlowStatus.APPOINTMENT_REMINDER, notaryApplicationDocument);
    }

    @Override
    public void cancellationClosed(final String applicationId) {
        final NotaryApplicationDocument notaryApplicationDocument = notaryApplicationDocumentService
            .fetchDocument(applicationId);

        notaryApplicationDocument.addHistoryEvent(NotaryApplicationFlowStatus.CLOSE_APPOINTMENT_CANCELLATION.getId());

        notaryApplicationDocumentService
            .updateDocument(notaryApplicationDocument.getId(), notaryApplicationDocument, true, false, null);

        notaryApplicationElkNotificationService
            .sendStatus(NotaryApplicationFlowStatus.CLOSE_APPOINTMENT_CANCELLATION, notaryApplicationDocument);
    }

    @Override
    public void reCollectDocuments(final String applicationId) {
        final NotaryApplicationDocument notaryApplicationDocument = notaryApplicationDocumentService
            .fetchDocument(applicationId);

        final NotaryApplicationType notaryApplication = notaryApplicationDocument
            .getDocument()
            .getNotaryApplicationData();
        notaryApplication.setStatusId(NotaryApplicationFlowStatus.RECOLLECT_DOCUMENTS.getId());
        notaryApplicationDocument.addHistoryEvent(NotaryApplicationFlowStatus.RECOLLECT_DOCUMENTS.getId());

        notaryApplicationDocumentService.updateDocument(notaryApplicationDocument);

        notaryApplicationElkNotificationService
            .sendStatus(NotaryApplicationFlowStatus.RECOLLECT_DOCUMENTS, notaryApplicationDocument);

        notaryApplicationService.closeBookingTask(notaryApplication, false);
    }

    @Override
    public void confirmRecollected(final String applicationId) {
        final NotaryApplicationDocument notaryApplicationDocument = notaryApplicationDocumentService
            .fetchDocument(applicationId);

        final NotaryApplicationType notaryApplication = notaryApplicationDocument
            .getDocument()
            .getNotaryApplicationData();
        notaryApplication.setStatusId(NotaryApplicationFlowStatus.APPOINTMENT_OPEN.getId());
        notaryApplicationDocument.addHistoryEvent(NotaryApplicationFlowStatus.APPOINTMENT_OPEN.getId());

        notaryApplicationDocumentService.updateDocument(notaryApplicationDocument);

        notaryApplicationElkNotificationService
            .sendStatus(NotaryApplicationFlowStatus.APPOINTMENT_OPEN, notaryApplicationDocument);

        notaryApplicationService.closeDocumentsRecollectionTask(notaryApplication.getProcessInstanceId());
    }

    @Override
    public void contractSigned(final String applicationId) {
        final NotaryApplicationDocument notaryApplicationDocument = notaryApplicationDocumentService
            .fetchDocument(applicationId);

        final NotaryApplicationType notaryApplication = notaryApplicationDocument
            .getDocument()
            .getNotaryApplicationData();
        notaryApplication.setCompletionDateTime(LocalDateTime.now());
        notaryApplication.setStatusId(NotaryApplicationFlowStatus.CONTRACT_SIGNED.getId());
        notaryApplicationDocument.addHistoryEvent(NotaryApplicationFlowStatus.CONTRACT_SIGNED.getId());

        notaryApplicationDocumentService.updateDocument(notaryApplicationDocument);

        notaryApplicationElkNotificationService
            .sendStatus(NotaryApplicationFlowStatus.CONTRACT_SIGNED, notaryApplicationDocument);

        notaryApplicationService.closeAppointmentResultsTask(notaryApplication.getProcessInstanceId(), true);
    }

    @Override
    public void requestReturnVisit(final String applicationId) {
        final NotaryApplicationDocument notaryApplicationDocument = notaryApplicationDocumentService
            .fetchDocument(applicationId);

        final NotaryApplicationType notaryApplication = notaryApplicationDocument
            .getDocument()
            .getNotaryApplicationData();
        notaryApplication.setStatusId(NotaryApplicationFlowStatus.DRAFT_CONTRACT_READY.getId());
        notaryApplicationDocument.addHistoryEvent(NotaryApplicationFlowStatus.RETURN_VISIT_REQUIRED.getId());
        notaryApplicationService.cancelBookingIfRequired(notaryApplication);

        notaryApplicationDocumentService.updateDocument(notaryApplicationDocument);

        notaryApplicationElkNotificationService
            .sendStatus(NotaryApplicationFlowStatus.RETURN_VISIT_REQUIRED, notaryApplicationDocument);

        notaryApplicationService.closeAppointmentResultsTask(notaryApplication.getProcessInstanceId(), false);
    }

    @Override
    public void processCancelByApplicant(final String applicationId) {
        final NotaryApplicationDocument notaryApplicationDocument = notaryApplicationDocumentService
            .fetchDocument(applicationId);

        notaryApplicationService.processCancelByApplicantRequest(notaryApplicationDocument);
    }

    @Override
    public byte[] getIcsFileContent(final String id) {
        final NotaryApplicationDocument notaryApplicationDocument = notaryApplicationDocumentService.fetchDocument(id);
        return retrieveIcsFileStoreId(notaryApplicationDocument);
    }

    private byte[] retrieveIcsFileStoreId(final NotaryApplicationDocument notaryApplicationDocument) {
        return of(notaryApplicationDocument.getDocument().getNotaryApplicationData())
            .map(NotaryApplicationType::getIcsFileStoreId)
            .map(ssrFilestoreService::getFile)
            .orElse(null);
    }

    private static void confirmRequestedFiles(final NotaryApplicationType notaryApplication) {
        ofNullable(notaryApplication.getApplicantFiles())
            .map(NotaryApplicantFiles::getFiles)
            .map(List::stream)
            .orElse(Stream.empty())
            .forEach(file -> file.setConfirmed(true));
    }
}
