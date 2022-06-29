package ru.croc.ugd.ssr.integration.service.notification;

import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.contractappointment.ContractAppointmentData;
import ru.croc.ugd.ssr.contractdigitalsign.Owner;
import ru.croc.ugd.ssr.dto.ElkUserNotificationDto;
import ru.croc.ugd.ssr.dto.contractdigitalsign.ContractDigitalSignFlowStatus;
import ru.croc.ugd.ssr.dto.contractdigitalsign.ContractDigitalSignNotificationStatus;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.contractDigitalSign.ContractDigitalSignDocument;
import ru.croc.ugd.ssr.model.contractappointment.ContractAppointmentDocument;

import java.util.List;
import java.util.Map;

/**
 * Сервис отправки сообщений в ЭЛК для записи на подписание договора с помощью УКЭП.
 */
public interface ContractDigitalSignElkNotificationService {

    /**
     * Отправка сообщений в ЭЛК для записи на подписание договора с помощью УКЭП.
     *
     * @param status status
     * @param contractAppointmentDocument contractAppointmentDocument
     */
    void sendStatus(
        final ContractDigitalSignFlowStatus status,
        final ContractAppointmentDocument contractAppointmentDocument
    );

    /**
     * Отправка статуса по заявлению на запись на подписание договора с помощью УКЭП.
     *
     * @param status status
     * @param contractAppointmentDocument contractAppointmentDocument
     * @param ownerFio ownerFio
     */
    void sendStatus(
        final ContractDigitalSignFlowStatus status,
        final ContractAppointmentDocument contractAppointmentDocument,
        final String ownerFio
    );

    /**
     * Отправка уведомлений на подписание договора с помощью УКЭП.
     *
     * @param status status
     * @param contractAppointmentDocument contractAppointmentDocument
     * @param isOtherOwnerNotification isOtherOwnerNotification
     * @return данные направленных уведомлений
     */
    List<ElkUserNotificationDto> sendNotification(
        final ContractDigitalSignFlowStatus status,
        final ContractAppointmentDocument contractAppointmentDocument,
        final boolean isOtherOwnerNotification
    );

    /**
     * Заполнение дополнительных параметров для формирования текста уведомления.
     * @param contractAppointment заявление на заключение договора
     * @param requesterPerson данные заявителя
     * @return параметры шаблона
     */
    Map<String, String> getExtraTemplateParams(
        final ContractAppointmentData contractAppointment, final PersonType requesterPerson
    );

    /**
     * Отправка статусов по уведомлениям на подписание договора с помощью УКЭП.
     *
     * @param signerStatus статус, направляемый подписанту
     * @param otherOwnersStatus статус, направляемый другим правообладателям
     *                          (в том числе заявителю, если он не является подписантом)
     * @param contractDigitalSignDocument документ многостороннего подписания договора с использованием УКЭП
     * @param contractAppointmentDocument документ заявления на заключение договора
     * @param signerPersonDocumentId ИД документа подписанта
     */
    void sendNotificationStatus(
        final ContractDigitalSignNotificationStatus signerStatus,
        final ContractDigitalSignNotificationStatus otherOwnersStatus,
        final ContractDigitalSignDocument contractDigitalSignDocument,
        final ContractAppointmentDocument contractAppointmentDocument,
        final String signerPersonDocumentId
    );

    /**
     * Отправка статусов по всем уведомлениям на подписание договора с помощью УКЭП.
     *
     * @param status статус
     * @param contractDigitalSignDocument документ многостороннего подписания договора с использованием УКЭП
     * @param contractAppointmentDocument документ заявления на заключение договора
     */
    void sendNotificationStatus(
        final ContractDigitalSignNotificationStatus status,
        final ContractDigitalSignDocument contractDigitalSignDocument,
        final ContractAppointmentDocument contractAppointmentDocument
    );

    /**
     * Отправка статуса по уведомлению на подписание договора с помощью УКЭП.
     *
     * @param status статус
     * @param contractAppointmentDocument документ заявления на заключение договора
     * @param owner правообладатель, которому было направлено уведомление
     * @param signerPersonDocument подписант, инициировавший отправку статуса
     * @param reasonCode код причины
     */
    void sendNotificationStatus(
        final ContractDigitalSignNotificationStatus status,
        final ContractAppointmentDocument contractAppointmentDocument,
        final Owner owner,
        final PersonDocument signerPersonDocument,
        final Integer reasonCode
    );

    /**
     * Изменить статус уведомления.
     *
     * @param owner owner
     * @param elkUserNotificationDto elkUserNotificationDto
     * @return признак подписания договора и акта правообладателем
     */
    boolean changeElkUserNotificationStatus(final Owner owner, final ElkUserNotificationDto elkUserNotificationDto);

    /**
     * Определить код причины.
     *
     * @param status статус
     * @param owners правообладатели
     * @return код причины
     */
    Integer retrieveReasonCode(final ContractDigitalSignNotificationStatus status, final List<Owner> owners);
}
