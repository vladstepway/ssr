package ru.croc.ugd.ssr.service;

import ru.reinform.cdp.document.model.DocumentAbstract;

/**
 * Интерфейс для сервисов отправки уведомлений по email.
 */
public interface EmailNotificationService<T extends DocumentAbstract> {

    /**
     * Отправка уведомления на Email.
     *
     * @param document документ.
     */
    void sendNotificationEmail(final T document);
}
