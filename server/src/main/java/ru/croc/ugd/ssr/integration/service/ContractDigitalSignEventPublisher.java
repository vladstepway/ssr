package ru.croc.ugd.ssr.integration.service;

import ru.croc.ugd.ssr.integration.command.ContractDigitalSignPublishNotificationReasonCommand;
import ru.croc.ugd.ssr.integration.command.ContractDigitalSignPublishReasonCommand;

/**
 * Формирование и отправка сообщений для заявления на запись на подписание договора с помощью УКЭП в очередь.
 */
public interface ContractDigitalSignEventPublisher {

    /**
     * Отправляет сообщение в очередь.
     * @param publishReasonCommand команда.
     */
    void publishStatus(final ContractDigitalSignPublishReasonCommand publishReasonCommand);

    /**
     * Отправляет сообщение со статусом уведомления в очередь.
     * @param publishNotificationReasonCommand команда.
     */
    void publishNotificationStatus(
        final ContractDigitalSignPublishNotificationReasonCommand publishNotificationReasonCommand
    );
}
