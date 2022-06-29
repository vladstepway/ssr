package ru.croc.ugd.ssr.integration.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.integration.command.PublishReasonCommand;
import ru.croc.ugd.ssr.mapper.mq.ToCoordinateStatusMessageReasonMapper;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateStatusMessage;
import ru.croc.ugd.ssr.mq.interop.QueueProperties;

/**
 * Формирование и Отправка сообщений для переезда в очередь.
 */
@Slf4j
@Component
public class DefaultShippingEventPublisher implements ShippingEventPublisher {

    private final EtpService etpService;
    private final ToCoordinateStatusMessageReasonMapper toCoordinateStatusMessageReasonMapper;
    private final QueueProperties queueProperties;

    /**
     * Creates DefaultShippingEventPublisher.
     * @param etpService etpService
     * @param toCoordinateStatusMessageReasonMapper toCoordinateStatusMessageReasonMapper
     * @param queueProperties queueProperties
     */
    public DefaultShippingEventPublisher(
        @Qualifier("etpServiceShipping") final EtpService etpService,
        final ToCoordinateStatusMessageReasonMapper toCoordinateStatusMessageReasonMapper,
        final QueueProperties queueProperties
    ) {
        this.etpService = etpService;
        this.toCoordinateStatusMessageReasonMapper = toCoordinateStatusMessageReasonMapper;
        this.queueProperties = queueProperties;
    }

    @Override
    public void publishCurrentShippingStatus(final PublishReasonCommand publishReasonCommand) {
        log.info("publish shipping application status eno:{}, statusDescription:{}, statusText:{}",
            publishReasonCommand.getBookingInformation().getEnoServiceNumber(),
            publishReasonCommand.getEdpResponseStatusData().getShippingFlowStatus().getDescription(),
            publishReasonCommand.getEdpResponseStatusData().getEdpResponseStatusText()
        );
        final CoordinateStatusMessage coordinateStatusMessage =
            toCoordinateStatusMessageReasonMapper.toCoordinateStatusMessage(publishReasonCommand);
        etpService.exportMessage(
            coordinateStatusMessage,
            queueProperties.getShippingStatusesOut(),
            CoordinateStatusMessage.class
        );
    }

    @Override
    public void publishStatusToBk(final String message) {
        etpService.exportXml(message, queueProperties.getShippingStatusesIncBk());
    }

    @Override
    public void publishToBk(final String message) {
        etpService.exportXml(message, queueProperties.getShippingRequestsBk());
    }
}
