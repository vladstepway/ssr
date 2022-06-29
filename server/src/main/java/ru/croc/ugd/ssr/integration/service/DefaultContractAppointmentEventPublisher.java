package ru.croc.ugd.ssr.integration.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.integration.command.ContractAppointmentPublishReasonCommand;
import ru.croc.ugd.ssr.mapper.mq.ToCoordinateStatusMessageReasonMapper;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateStatusMessage;
import ru.croc.ugd.ssr.mq.interop.QueueProperties;

/**
 * Формирование и Отправка сообщений для заявления на запись на подписание договора в очередь.
 */
@Slf4j
@Component
public class DefaultContractAppointmentEventPublisher implements ContractAppointmentEventPublisher {

    private final EtpService etpService;
    private final ToCoordinateStatusMessageReasonMapper toCoordinateStatusMessageReasonMapper;
    private final QueueProperties queueProperties;

    public DefaultContractAppointmentEventPublisher(
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
        final ContractAppointmentPublishReasonCommand publishReasonCommand
    ) {
        final CoordinateStatusMessage coordinateStatusMessage
            = toCoordinateStatusMessageReasonMapper.toCoordinateStatusMessage(publishReasonCommand);
        log.info("publish contract appointment status eno:{} status:{},"
                + " statusDescription:{}, statusText:{}",
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
    public void publishStatusToBk(final String message) {
        etpService.exportXml(message, queueProperties.getContractAppointmentStatusBk());
    }

    @Override
    public void publishToBk(final String message) {
        etpService.exportXml(message, queueProperties.getContractAppointmentBk());
    }
}
