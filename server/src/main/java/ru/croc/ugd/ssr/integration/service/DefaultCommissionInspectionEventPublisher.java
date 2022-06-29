package ru.croc.ugd.ssr.integration.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.integration.command.CommissionInspectionPublishReasonCommand;
import ru.croc.ugd.ssr.mapper.mq.ToCoordinateStatusMessageReasonMapper;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateStatusMessage;
import ru.croc.ugd.ssr.mq.interop.QueueProperties;

/**
 * Формирование и Отправка сообщений для переезда в очередь.
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "ibm.mq.guo.enabled")
public class DefaultCommissionInspectionEventPublisher implements CommissionInspectionEventPublisher {

    private final EtpService etpService;
    private final ToCoordinateStatusMessageReasonMapper toCoordinateStatusMessageReasonMapper;
    private final QueueProperties queueProperties;

    /**
     * Creates DefaultCommissionInspectionEventPublisher.
     * @param etpService etpService
     * @param toCoordinateStatusMessageReasonMapper toCoordinateStatusMessageReasonMapper
     * @param queueProperties queueProperties
     */
    public DefaultCommissionInspectionEventPublisher(
        @Qualifier("etpServiceGuo") final EtpService etpService,
        final ToCoordinateStatusMessageReasonMapper toCoordinateStatusMessageReasonMapper,
        final QueueProperties queueProperties
    ) {
        this.etpService = etpService;
        this.toCoordinateStatusMessageReasonMapper = toCoordinateStatusMessageReasonMapper;
        this.queueProperties = queueProperties;
    }

    @Override
    public void publishStatus(
        final CommissionInspectionPublishReasonCommand publishReasonCommand
    ) {
        final CoordinateStatusMessage coordinateStatusMessage =
            toCoordinateStatusMessageReasonMapper.toCoordinateStatusMessage(publishReasonCommand);
        log.info("publish commission inspection status eno:{} statusCode:{}, statusDescription:{}, statusText:{}",
            publishReasonCommand.getCommissionInspectionData().getEno(),
            publishReasonCommand.getStatus().getId(),
            publishReasonCommand.getStatus().getDescription(),
            publishReasonCommand.getElkStatusText()
        );
        etpService.exportMessage(
            coordinateStatusMessage,
            queueProperties.getCommissionInspectionStatusOut(),
            CoordinateStatusMessage.class
        );
    }

    @Override
    public void publishStatusToBk(final String message) {
        etpService.exportXml(message, queueProperties.getCommissionInspectionStatusBk());
    }

    @Override
    public void publishToBk(final String message) {
        etpService.exportXml(message, queueProperties.getCommissionInspectionBk());
    }
}
