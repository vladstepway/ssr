package ru.croc.ugd.ssr.integration.service;

import ru.croc.ugd.ssr.integration.command.FlatAppointmentPublishReasonCommand;

/**
 * Формирование и отправка сообщений для заявления на запись на осмотр квартиры в очередь.
 */
public interface FlatAppointmentEventPublisher {

    /**
     * Отправляет сообщение в очередь.
     * @param publishReasonCommand команда.
     */
    void publishStatus(final FlatAppointmentPublishReasonCommand publishReasonCommand);

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
