package ru.croc.ugd.ssr.integration.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.integration.command.NotaryCompensationPublishReasonCommand;
import ru.croc.ugd.ssr.integration.mqetpmv.MqetpmvProperties;
import ru.croc.ugd.ssr.integration.service.mqetpmv.mpgu.SsrMqetpmvMpguService;
import ru.croc.ugd.ssr.mapper.mq.ToCoordinateStatusMessageReasonMapper;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateStatusMessage;

/**
 * Формирование и Отправка сообщений для заявлений на возмещение оплаты услуг нотариуса.
 */
@Slf4j
@Component
@AllArgsConstructor
public class DefaultNotaryCompensationEventPublisher implements NotaryCompensationEventPublisher {

    private final ToCoordinateStatusMessageReasonMapper toCoordinateStatusMessageReasonMapper;
    private final MqetpmvProperties mqetpmvProperties;
    private final SsrMqetpmvMpguService ssrMqetpmvMpguService;

    @Override
    public void publishStatus(final NotaryCompensationPublishReasonCommand publishReasonCommand) {
        final CoordinateStatusMessage coordinateStatusMessage
            = toCoordinateStatusMessageReasonMapper.toCoordinateStatusMessage(publishReasonCommand);
        log.info("publish notary compensation status eno:{} status:{}, statusDescription:{}, statusText:{}",
            publishReasonCommand.getNotaryCompensation().getEno(),
            publishReasonCommand.getStatus().getId(),
            publishReasonCommand.getStatus().getDescription(),
            publishReasonCommand.getElkStatusText()
        );
        //TODO Konstantin rewrite MqEtpMvService
        ssrMqetpmvMpguService.exportMessage(
            coordinateStatusMessage,
            mqetpmvProperties.getNotaryCompensationStatusOutProfile(),
            CoordinateStatusMessage.class
        );
    }

}
