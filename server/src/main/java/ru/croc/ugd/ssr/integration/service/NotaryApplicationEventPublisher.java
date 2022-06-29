package ru.croc.ugd.ssr.integration.service;

import ru.croc.ugd.ssr.integration.command.NotaryApplicationPublishReasonCommand;

/**
 * Формирование и отправка сообщений для заявления по натариусу в очередь.
 */
public interface NotaryApplicationEventPublisher {

    /**
     * Отправляет сообщение в очередь.
     * @param publishReasonCommand команда.
     */
    void publishStatus(final NotaryApplicationPublishReasonCommand publishReasonCommand);

    /**
     * Отправляет сообщение в очередь BK.
     * @param message Сообщение.
     */
    void publishStatusToBk(final String message);

    /**
     * Отправляет сообщение в очередь BK.
     * @param message Сообщение.
     */
    void publishToBk(final String message);
}
