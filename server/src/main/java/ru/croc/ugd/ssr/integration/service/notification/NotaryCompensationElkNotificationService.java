package ru.croc.ugd.ssr.integration.service.notification;

import ru.croc.ugd.ssr.dto.notarycompensation.NotaryCompensationFlowStatus;
import ru.croc.ugd.ssr.model.notaryCompensation.NotaryCompensationDocument;

/**
 * Сервис отправки сообщений в ЭЛК для заявлений на возмещение оплаты услуг нотариуса.
 */
public interface NotaryCompensationElkNotificationService {

    /**
     * Отправка сообщений в ЭЛК для заявлений на возмещение оплаты услуг нотариуса.
     *
     * @param status   status
     * @param document document
     */
    void sendStatus(
        final NotaryCompensationFlowStatus status, final NotaryCompensationDocument document
    );

    /**
     * Отправка сообщений в ЭЛК для заявлений на возмещение оплаты услуг нотариуса.
     *
     * @param status status
     * @param eno    Eno
     */
    void sendStatus(final NotaryCompensationFlowStatus status, final String eno);

}
