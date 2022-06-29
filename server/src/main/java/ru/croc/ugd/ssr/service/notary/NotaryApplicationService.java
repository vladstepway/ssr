package ru.croc.ugd.ssr.service.notary;

import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateMessage;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateStatusMessage;
import ru.croc.ugd.ssr.model.notary.NotaryApplicationDocument;
import ru.croc.ugd.ssr.notary.NotaryApplicationType;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Сервис для работы с заявлениями на посещение нотариуса.
 */
public interface NotaryApplicationService {

    /**
     * Зарегистрировать заявление на посещение нотариуса из очереди.
     * @param coordinateMessage сообщение из очереди
     */
    void processRegistration(final CoordinateMessage coordinateMessage);

    /**
     * Получить логин сотрудника нотариальной конторы на которую подана заявка на приём.
     * @param notaryApplicationDocument заявка
     * @return логин сотрудника
     */
    Optional<String> getNotaryLogin(final NotaryApplicationDocument notaryApplicationDocument);

    /**
     * Завершить BPM процесс по идентификатору процесса.
     * @param processInstanceId processInstanceId
     */
    void finishBpmProcess(final String processInstanceId);

    /**
     * Обработка полученных документов.
     * @param coordinateStatus полученное из МПГУ сообшение
     */
    void processReceivedDocuments(final CoordinateStatusMessage coordinateStatus);

    /**
     * Обработка запроса на отмену записи по инициативе заявителя.
     * @param notaryApplicationDocument notaryApplicationDocument.
     */
    void processCancelByApplicantRequest(final NotaryApplicationDocument notaryApplicationDocument);

    /**
     * Обработка запроса на отмему записи по инициативе заявителя.
     * @param coordinateStatus полученное из МПГУ сообшение
     */
    void processCancelByApplicantRequest(final CoordinateStatusMessage coordinateStatus);

    /**
     * Обработка запроса на бронирование времени.
     * @param coordinateStatus полученное из МПГУ сообшение
     */
    void processBookingRequest(final CoordinateStatusMessage coordinateStatus);

    /**
     * Обработка запроса на бронирование времени.
     * @param eno eno
     * @param bookingId bookingId
     * @param appointmentDateTime appointmentDateTime
     * @param isSsrBooking isSsrBooking
     */
    void processBookingRequest(
        final String eno, final String bookingId, final LocalDateTime appointmentDateTime, final Boolean isSsrBooking
    );

    /**
     * Получить заявление по eno.
     * @param eno eno
     * @return notaryApplicationDocument
     */
    NotaryApplicationDocument fetchByEno(final String eno);

    /**
     * Отменить бронирование, если возможно.
     * @param notaryApplication notaryApplication
     */
    void cancelBookingIfRequired(final NotaryApplicationType notaryApplication);

    /**
     * Закрыть задачу на проверку документов.
     * @param processInstanceId processInstanceId
     * @param isDocumentsPreparationFinished isDocumentsPreparationFinished
     */
    void closeDocumentsVerificationTask(final String processInstanceId, final Boolean isDocumentsPreparationFinished);

    /**
     * Закрыть задачу на подтверждение приема заявления.
     * @param processInstanceId processInstanceId
     * @param isApplicationAccepted isApplicationAccepted
     */
    void closeAcceptApplicationTask(final String processInstanceId, final Boolean isApplicationAccepted);

    /**
     * Закрыть задачу на подготовку проекта договора.
     * @param processInstanceId processInstanceId
     * @param isContractReady isContractReady
     */
    void closeContractPreparationTask(final String processInstanceId, final Boolean isContractReady);

    /**
     * Закрыть задачу на бронирование слота.
     * @param notaryApplication notaryApplication
     * @param isBookingSuccessful isBookingSuccessful
     */
    void closeBookingTask(final NotaryApplicationType notaryApplication, final Boolean isBookingSuccessful);

    /**
     * Закрыть задачу на внесение сведений о результатах приема.
     * @param processInstanceId processInstanceId
     * @param isApplicationClosed isApplicationClosed
     */
    void closeAppointmentResultsTask(final String processInstanceId, final Boolean isApplicationClosed);

    /**
     * Закрыть задачу на подготовку повторного комплекта справок.
     * @param processInstanceId processInstanceId
     */
    void closeDocumentsRecollectionTask(final String processInstanceId);

    /**
     * Создать процесс на подготовку повторного комплекта документов.
     * @param notaryApplicationDocument notaryApplicationDocument
     * @return processInstanceId
     */
    Optional<String> createSetUpAppointmentProcess(final NotaryApplicationDocument notaryApplicationDocument);

    /**
     * Создать процесс на подготовку проекта договоров.
     * @param notaryApplicationDocument notaryApplicationDocument
     * @param isCreatedByNotary isCreatedByNotary
     * @return processInstanceId
     */
    Optional<String> createContractPreparationProcess(
        final NotaryApplicationDocument notaryApplicationDocument,
        final Boolean isCreatedByNotary
    );
}
