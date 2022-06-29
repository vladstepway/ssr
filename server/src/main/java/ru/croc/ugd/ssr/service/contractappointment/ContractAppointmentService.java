package ru.croc.ugd.ssr.service.contractappointment;

import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.contractappointment.ContractAppointmentData;
import ru.croc.ugd.ssr.dto.contractappointment.ContractAppointmentFlowStatus;
import ru.croc.ugd.ssr.dto.contractdigitalsign.ContractDigitalSignFlowStatus;
import ru.croc.ugd.ssr.model.contractappointment.ContractAppointmentDocument;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateMessage;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateStatusMessage;

import java.time.LocalDateTime;

/**
 * Сервис для работы с заявлениями на запись на подписание договора.
 */
public interface ContractAppointmentService {

    /**
     * Зарегистрировать заявление на запись на подписание договора из очереди.
     *
     * @param coordinateMessage сообщение из очереди
     */
    void processRegistration(final CoordinateMessage coordinateMessage);

    ContractAppointmentDocument fetchByEno(final String eno);

    /**
     * Запрос на отмену заявления.
     *
     * @param coordinateStatus сообщение из очереди
     */
    void processCancelByApplicantRequest(final CoordinateStatusMessage coordinateStatus);

    /**
     * Отмена записи.
     *
     * @param contractAppointmentDocument contractAppointmentDocument
     */
    void cancelBookingIfRequired(final ContractAppointmentDocument contractAppointmentDocument);

    /**
     * Возможность отмены записи.
     *
     * @param contractAppointmentData contractAppointmentData
     * @param isMpgu                  true - отмена заявителем, false - отмена оператором
     */
    boolean canCancelAppointment(final ContractAppointmentData contractAppointmentData, final boolean isMpgu);

    void moveAppointmentDateTime(
        final String contractAppointmentDocumentId, final String bookingId, final LocalDateTime appointmentDateTime
    );

    /**
     * Завершить BPM процесс по идентификатору процесса.
     *
     * @param processInstanceId processInstanceId
     */
    void finishBpmProcess(final String processInstanceId);

    /**
     * Закрытие заявления с подписанным контрактом.
     *
     * @param contractAppointmentId contractAppointmentId
     * @param signedContract        signedContract
     */
    void processSignedContract(final String contractAppointmentId, final PersonType.Contracts.Contract signedContract);

    /**
     * Закрытие заявления с неподписанным контрактом.
     *
     * @param contractAppointmentId contractAppointmentId
     * @param refuseReason refuseReason
     */
    void processUnsignedContract(final String contractAppointmentId, final String refuseReason);

    boolean isAppointmentRegisteredByBookingId(final String bookingId);

    void processAutoCancellation(final ContractAppointmentDocument contractAppointmentDocument);

    void processUnsignedCancellation(final ContractAppointmentDocument contractAppointmentDocument);

    void processUnsignedByEmployeeCancellation(final ContractAppointmentDocument contractAppointmentDocument);

    void processUnsignedByOwnersCancellation(final ContractAppointmentDocument contractAppointmentDocument);

    void setAndSendActualStatus(
        final ContractAppointmentDocument contractAppointmentDocument,
        final ContractAppointmentFlowStatus contractAppointmentFlowStatus,
        final ContractDigitalSignFlowStatus contractDigitalSignFlowStatus
    );
}
