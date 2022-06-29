package ru.croc.ugd.ssr.integration.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.integration.command.NotaryApplicationPublishReasonCommand;
import ru.croc.ugd.ssr.mapper.mq.ToCoordinateStatusMessageReasonMapper;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateStatusMessage;
import ru.croc.ugd.ssr.mq.interop.QueueProperties;

/**
 * Формирование и Отправка сообщений для приёма к нотариуса в очередь.
 */
@Slf4j
@Component
@AllArgsConstructor
public class DefaultNotaryApplicationEventPublisher implements NotaryApplicationEventPublisher {

    private final EtpService etpService;
    private final ToCoordinateStatusMessageReasonMapper toCoordinateStatusMessageReasonMapper;
    private final QueueProperties queueProperties;

    @Override
    public void publishStatus(
        final NotaryApplicationPublishReasonCommand publishReasonCommand
    ) {
        final CoordinateStatusMessage coordinateStatusMessage
            = toCoordinateStatusMessageReasonMapper.toCoordinateStatusMessage(publishReasonCommand);
        log.info("publish notary application status eno:{} status:{}, statusDescription:{}, statusText:{}",
            publishReasonCommand.getNotaryApplication().getEno(),
            publishReasonCommand.getStatus().getId(),
            publishReasonCommand.getStatus().getDescription(),
            publishReasonCommand.getElkStatusText()
        );
        etpService.exportMessage(
            coordinateStatusMessage,
            queueProperties.getNotaryApplicationStatusOut(),
            CoordinateStatusMessage.class
        );
    }

    @Override
    public void publishStatusToBk(final String message) {
        etpService.exportXml(message, queueProperties.getNotaryApplicationStatusBk());
    }

    @Override
    public void publishToBk(final String message) {
        etpService.exportXml(message, queueProperties.getNotaryApplicationBk());
    }
}
