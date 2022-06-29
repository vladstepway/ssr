package ru.croc.ugd.ssr.integration.service;

import ru.croc.ugd.ssr.integration.command.CommissionInspectionPublishReasonCommand;

/**
 * Формирование и отправка сообщений для комиссионного осмотра в очередь.
 */
public interface CommissionInspectionEventPublisher {

    /**
     * Отправляет сообщение в очередь.
     * @param publishReasonCommand команда.
     */
    void publishStatus(final CommissionInspectionPublishReasonCommand publishReasonCommand);

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
