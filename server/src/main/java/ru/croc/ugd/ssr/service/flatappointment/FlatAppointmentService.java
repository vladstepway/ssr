package ru.croc.ugd.ssr.service.flatappointment;

import ru.croc.ugd.ssr.PersonType.FlatDemo.FlatDemoItem;
import ru.croc.ugd.ssr.flatappointment.FlatAppointmentData;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.flatAppointment.FlatAppointmentDocument;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateMessage;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateStatusMessage;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Сервис для работы с заявлениями на запись на осмотр квартиры.
 */
public interface FlatAppointmentService {

    FlatAppointmentDocument fetchByEno(final String eno);

    /**
     * Зарегистрировать заявление на запись на осмотр квартиры из очереди.
     *
     * @param coordinateMessage сообщение из очереди
     */
    void processRegistration(final CoordinateMessage coordinateMessage);

    /**
     * Запрос на отмену заявления.
     *
     * @param coordinateStatus сообщение из очереди
     */
    void processCancelByApplicantRequest(final CoordinateStatusMessage coordinateStatus);

    /**
     * Внести сведения по проведенному осмотру.
     * @param flatAppointmentDocument flatAppointmentDocument
     * @param flatDemoItem flatDemoItem
     * @param participantDocuments participantDocuments
     */
    void processPerformedAppointment(
        final FlatAppointmentDocument flatAppointmentDocument,
        final FlatDemoItem flatDemoItem,
        final List<PersonDocument> participantDocuments
    );

    /**
     * Внести сведения по непроведенному осмотру.
     * @param flatAppointmentDocument flatAppointmentDocument
     */
    void processNotPerformedAppointment(final FlatAppointmentDocument flatAppointmentDocument);

    /**
     * Отмена записи.
     *
     * @param flatAppointmentDocument flatAppointmentDocument
     */
    void cancelBookingIfRequired(final FlatAppointmentDocument flatAppointmentDocument);

    /**
     * Возможность отмены записи.
     *
     * @param flatAppointmentData flatAppointmentData
     * @param isMpgu true - отмена заявителем, false - отмена оператором
     */
    boolean canCancelAppointment(final FlatAppointmentData flatAppointmentData, final boolean isMpgu);

    void moveAppointmentDateTime(
        final String letterId,
        final String bookingId,
        final LocalDateTime appointmentDateTime
    );

    /**
     * Завершить BPM процесс по идентификатору процесса.
     * @param processInstanceId processInstanceId
     */
    void finishBpmProcess(final String processInstanceId);

    boolean isAppointmentRegisteredByBookingId(final String bookingId);

    /**
     * Автоматическая отмена записи.
     *
     * @param flatAppointmentDocument flatAppointmentDocument
     */
    void processAutoCancellation(final FlatAppointmentDocument flatAppointmentDocument);
}
