package ru.croc.ugd.ssr.integration.service;

import ru.croc.ugd.ssr.integration.command.PublishReasonCommand;

/**
 * Формирование и Отправка сообщений для переезда в очередь.
 */
public interface ShippingEventPublisher {

    /**
     * Отправляет сообщение в очередь.
     * @param publishReasonCommand команда.
     */
    void publishCurrentShippingStatus(final PublishReasonCommand publishReasonCommand);

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
