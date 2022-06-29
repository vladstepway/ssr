package ru.croc.ugd.ssr.integration.service.notification;

import ru.croc.ugd.ssr.dto.personaldocument.PersonalDocumentApplicationFlowStatus;
import ru.croc.ugd.ssr.model.personaldocument.PersonalDocumentApplicationDocument;

/**
 * Сервис отправки сообщений в ЭЛК для загрузки документов.
 */
public interface PersonalDocumentApplicationElkNotificationService {

    /**
     * Отправка сообщений в ЭЛК для загрузки документов.
     *
     * @param status status
     * @param document document
     */
    void sendStatus(
        final PersonalDocumentApplicationFlowStatus status, final PersonalDocumentApplicationDocument document
    );

    /**
     * Отправка сообщений в ЭЛК для загрузки документов.
     *
     * @param status status
     * @param eno eno
     */
    void sendStatus(final PersonalDocumentApplicationFlowStatus status, final String eno);

    /**
     * Отправляет сообщение в очередь BK.
     * @param message Сообщение.
     */
    void sendToBk(final String message);
}
