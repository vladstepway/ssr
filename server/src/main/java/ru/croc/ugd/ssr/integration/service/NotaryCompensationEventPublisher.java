package ru.croc.ugd.ssr.integration.service;

import ru.croc.ugd.ssr.integration.command.NotaryCompensationPublishReasonCommand;

public interface NotaryCompensationEventPublisher {

    /**
     * Отправляет сообщение в очередь.
     *
     * @param publishReasonCommand команда.
     */
    void publishStatus(final NotaryCompensationPublishReasonCommand publishReasonCommand);

}
