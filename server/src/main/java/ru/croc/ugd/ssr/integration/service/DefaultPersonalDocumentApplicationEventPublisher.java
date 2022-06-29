package ru.croc.ugd.ssr.integration.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.integration.command.PersonalDocumentApplicationPublishReasonCommand;
import ru.croc.ugd.ssr.mapper.mq.ToCoordinateStatusMessageReasonMapper;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateStatusMessage;
import ru.croc.ugd.ssr.mq.interop.QueueProperties;

/**
 * Формирование и отправка сообщений для заявления на предоставление документов в очередь.
 */
@Slf4j
@Component
public class DefaultPersonalDocumentApplicationEventPublisher implements PersonalDocumentApplicationEventPublisher {

    private final EtpService etpService;
    private final ToCoordinateStatusMessageReasonMapper toCoordinateStatusMessageReasonMapper;
    private final QueueProperties queueProperties;

    public DefaultPersonalDocumentApplicationEventPublisher(
        @Qualifier("etpServiceShipping") final EtpService etpService,
        final ToCoordinateStatusMessageReasonMapper toCoordinateStatusMessageReasonMapper,
        final QueueProperties queueProperties
    ) {
        this.etpService = etpService;
        this.toCoordinateStatusMessageReasonMapper = toCoordinateStatusMessageReasonMapper;
        this.queueProperties = queueProperties;
    }

    @Override
    public void publishStatus(final PersonalDocumentApplicationPublishReasonCommand publishReasonCommand) {
        final CoordinateStatusMessage coordinateStatusMessage
            = toCoordinateStatusMessageReasonMapper.toCoordinateStatusMessage(publishReasonCommand);
        log.info("publish personal document application status eno:{} status:{},"
                + " statusDescription:{}, statusText:{}",
            publishReasonCommand.getPersonalDocumentApplication().getEno(),
            publishReasonCommand.getStatus().getId(),
            publishReasonCommand.getStatus().getDescription(),
            publishReasonCommand.getElkStatusText()
        );
        etpService.exportMessage(
            coordinateStatusMessage,
            queueProperties.getPersonalDocumentApplicationStatusOut(),
            CoordinateStatusMessage.class
        );
    }

    @Override
    public void publishToBk(final String message) {
        etpService.exportXml(message, queueProperties.getPersonalDocumentApplicationBk());
    }
}
