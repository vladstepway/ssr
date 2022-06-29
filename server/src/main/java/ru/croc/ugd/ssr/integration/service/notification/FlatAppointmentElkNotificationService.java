package ru.croc.ugd.ssr.integration.service.notification;

import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.dto.flatappointment.FlatAppointmentFlowStatus;
import ru.croc.ugd.ssr.flatappointment.FlatAppointmentData;
import ru.croc.ugd.ssr.model.flatAppointment.FlatAppointmentDocument;

import java.util.Map;

/**
 * Сервис отправки сообщений в ЭЛК для записи на осмотр квартиры.
 */
public interface FlatAppointmentElkNotificationService {

    /**
     * Отправка сообщений в ЭЛК для записи на осмотр квартиры.
     *
     * @param status status
     * @param document document
     */
    void sendStatus(
        final FlatAppointmentFlowStatus status, final FlatAppointmentDocument document
    );

    /**
     * Отправка сообщений в ЭЛК для записи на осмотр квартиры.
     *
     * @param status status
     * @param eno eno
     */
    void sendStatus(final FlatAppointmentFlowStatus status, final String eno);

    /**
     * Отправляет сообщение в очередь BK.
     * @param message Сообщение.
     */
    void sendToBk(final String message);

    /**
     * Отправляет сообщение в очередь BK.
     * @param message Сообщение.
     */
    void sendStatusToBk(final String message);

    /**
     * Заполнение дополнительных параметров для формирования текста уведомления.
     * @param flatAppointment заявление на осмотр квартиры
     * @param requesterPersonType данные заявителя
     * @param ownerPersonType данные правообладателя
     * @return параметры шаблона
     */
    Map<String, String> getExtraTemplateParams(
        final FlatAppointmentData flatAppointment,
        final PersonType requesterPersonType,
        final PersonType ownerPersonType
    );
}
