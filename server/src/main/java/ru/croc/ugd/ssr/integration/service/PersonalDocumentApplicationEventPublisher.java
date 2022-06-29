package ru.croc.ugd.ssr.integration.service;

import ru.croc.ugd.ssr.integration.command.PersonalDocumentApplicationPublishReasonCommand;

/**
 * Формирование и отправка сообщений для заявления на предоставление документов в очередь.
 */
public interface PersonalDocumentApplicationEventPublisher {

    /**
     * Отправляет сообщение в очередь.
     * @param publishReasonCommand команда.
     */
    void publishStatus(final PersonalDocumentApplicationPublishReasonCommand publishReasonCommand);

    /**
     * Отправляет сообщение в очередь BK.
     * @param message Сообщение.
     */
    void publishToBk(final String message);
}
