package ru.croc.ugd.ssr.integration.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.integration.command.FlatAppointmentPublishReasonCommand;
import ru.croc.ugd.ssr.mapper.mq.ToCoordinateStatusMessageReasonMapper;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateStatusMessage;
import ru.croc.ugd.ssr.mq.interop.QueueProperties;

/**
 * Формирование и Отправка сообщений для заявления на запись на осмотр квартиры в очередь.
 */
@Slf4j
@Component
public class DefaultFlatAppointmentEventPublisher implements FlatAppointmentEventPublisher {

    private final EtpService etpService;
    private final ToCoordinateStatusMessageReasonMapper toCoordinateStatusMessageReasonMapper;
    private final QueueProperties queueProperties;

    public DefaultFlatAppointmentEventPublisher(
        @Qualifier("etpServiceShipping") final EtpService etpService,
        final ToCoordinateStatusMessageReasonMapper toCoordinateStatusMessageReasonMapper,
        final QueueProperties queueProperties
    ) {
        this.etpService = etpService;
        this.toCoordinateStatusMessageReasonMapper = toCoordinateStatusMessageReasonMapper;
        this.queueProperties = queueProperties;
    }

    @Override
    public void publishStatus(
        final FlatAppointmentPublishReasonCommand publishReasonCommand
    ) {
        final CoordinateStatusMessage coordinateStatusMessage
            = toCoordinateStatusMessageReasonMapper.toCoordinateStatusMessage(publishReasonCommand);
        log.info("publish flat appointment status eno:{} status:{},"
                + " statusDescription:{}, statusText:{}",
            publishReasonCommand.getFlatAppointment().getEno(),
            publishReasonCommand.getStatus().getId(),
            publishReasonCommand.getStatus().getDescription(),
            publishReasonCommand.getElkStatusText()
        );
        etpService.exportMessage(
            coordinateStatusMessage,
            queueProperties.getFlatAppointmentStatusOut(),
            CoordinateStatusMessage.class
        );
    }

    @Override
    public void publishStatusToBk(final String message) {
        etpService.exportXml(message, queueProperties.getFlatAppointmentStatusBk());
    }

    @Override
    public void publishToBk(final String message) {
        etpService.exportXml(message, queueProperties.getFlatAppointmentBk());
    }
}
