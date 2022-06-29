package ru.croc.ugd.ssr.integration.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.integration.command.ContractDigitalSignPublishNotificationReasonCommand;
import ru.croc.ugd.ssr.integration.command.ContractDigitalSignPublishReasonCommand;
import ru.croc.ugd.ssr.integration.config.IntegrationPropertyConfig;
import ru.croc.ugd.ssr.mapper.mq.ToCoordinateStatusMessageReasonMapper;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateStatusMessage;
import ru.croc.ugd.ssr.mq.interop.QueueProperties;

/**
 * Формирование и Отправка сообщений для заявления на запись на подписание договора с помощью УКЭП в очередь.
 */
@Slf4j
@Component
public class DefaultContractDigitalSignEventPublisher implements ContractDigitalSignEventPublisher {

    private final EtpService etpService;
    private final ToCoordinateStatusMessageReasonMapper toCoordinateStatusMessageReasonMapper;
    private final QueueProperties queueProperties;
    private final IntegrationPropertyConfig config;

    public DefaultContractDigitalSignEventPublisher(
        @Qualifier("etpServiceShipping") final EtpService etpService,
        final ToCoordinateStatusMessageReasonMapper toCoordinateStatusMessageReasonMapper,
        final QueueProperties queueProperties,
        final IntegrationPropertyConfig config
    ) {
        this.etpService = etpService;
        this.toCoordinateStatusMessageReasonMapper = toCoordinateStatusMessageReasonMapper;
        this.queueProperties = queueProperties;
        this.config = config;
    }

    @Override
    public void publishStatus(
        final ContractDigitalSignPublishReasonCommand publishReasonCommand
    ) {
        final CoordinateStatusMessage coordinateStatusMessage
            = toCoordinateStatusMessageReasonMapper.toCoordinateStatusMessage(publishReasonCommand);
        log.info("publish contract digital sign status eno:{}, status:{}, statusDescription:{}, statusText:{}",
            publishReasonCommand.getContractAppointment().getEno(),
            publishReasonCommand.getStatus().getId(),
            publishReasonCommand.getStatus().getDescription(),
            publishReasonCommand.getElkStatusText()
        );
        etpService.exportMessage(
            coordinateStatusMessage,
            queueProperties.getContractAppointmentStatusOut(),
            CoordinateStatusMessage.class
        );
    }

    @Override
    public void publishNotificationStatus(
        final ContractDigitalSignPublishNotificationReasonCommand publishNotificationReasonCommand
    ) {
        final CoordinateStatusMessage coordinateStatusMessage
            = toCoordinateStatusMessageReasonMapper.toCoordinateStatusMessage(publishNotificationReasonCommand);
        log.info(
            "publish contract digital sign notification status eno:{}, status:{},"
                + " reasonCode:{}, statusDescription:{}, statusText:{}",
            publishNotificationReasonCommand.getEno(),
            publishNotificationReasonCommand.getStatus().getId(),
            publishNotificationReasonCommand.getReasonCode(),
            publishNotificationReasonCommand.getStatus().getDescription(),
            publishNotificationReasonCommand.getElkStatusText()
        );
        etpService.exportMessage(
            coordinateStatusMessage,
            config.getElkMsNotificationStatusQueueName(),
            CoordinateStatusMessage.class
        );
    }
}
