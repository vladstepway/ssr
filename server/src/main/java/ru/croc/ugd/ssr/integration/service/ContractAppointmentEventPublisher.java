package ru.croc.ugd.ssr.integration.service;

import ru.croc.ugd.ssr.integration.command.ContractAppointmentPublishReasonCommand;

/**
 * Формирование и отправка сообщений для заявления на запись на подписание договора в очередь.
 */
public interface ContractAppointmentEventPublisher {

    /**
     * Отправляет сообщение в очередь.
     * @param publishReasonCommand команда.
     */
    void publishStatus(final ContractAppointmentPublishReasonCommand publishReasonCommand);

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
