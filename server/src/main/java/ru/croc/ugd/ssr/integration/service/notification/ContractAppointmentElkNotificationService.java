package ru.croc.ugd.ssr.integration.service.notification;

import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.contractappointment.ContractAppointmentData;
import ru.croc.ugd.ssr.dto.contractappointment.ContractAppointmentFlowStatus;
import ru.croc.ugd.ssr.model.contractappointment.ContractAppointmentDocument;
import ru.croc.ugd.ssr.service.contractappointment.unsinged.ContractDigitalSignCancellationReason;

import java.util.Map;

/**
 * Сервис отправки сообщений в ЭЛК для записи на подписание договора.
 */
public interface ContractAppointmentElkNotificationService {

    /**
     * Отправка сообщений в ЭЛК для записи на подписание договора.
     *
     * @param status status
     * @param document document
     */
    void sendStatus(
        final ContractAppointmentFlowStatus status, final ContractAppointmentDocument document
    );

    /**
     * Отправка сообщений в ЭЛК для записи на подписание договора.
     *
     * @param status status
     * @param eno eno
     */
    void sendStatus(final ContractAppointmentFlowStatus status, final String eno);

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
     * Отправка статуса по 1080.3 и 1080.4
     */
    void sendUnsignedContractStatus(final ContractDigitalSignCancellationReason reason);

    /**
     * Заполнение дополнительных параметров для формирования текста уведомления.
     * @param contractAppointment заявление на заключение договора
     * @param requesterPersonType данные заявителя
     * @param ownerPersonType данные правообладателя
     * @return параметры шаблона
     */
    Map<String, String> getExtraTemplateParams(
        final ContractAppointmentData contractAppointment,
        final PersonType requesterPersonType,
        final PersonType ownerPersonType
    );

    /**
     * Формирование текста статуса.
     * @param statusTemplate statusTemplate
     * @param contractAppointment contractAppointment
     * @return текст статуса
     */
    String retrieveContractSignedStatusText(
        final String statusTemplate, final ContractAppointmentData contractAppointment
    );
}
