package ru.croc.ugd.ssr.mq.listener.rsm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.dto.mqetpmv.flow.ImportCoordinateTask;
import ru.croc.ugd.ssr.dto.mqetpmv.flow.ImportFlowMessageDto;
import ru.croc.ugd.ssr.dto.rsm.RsmRequestDto;
import ru.croc.ugd.ssr.integration.config.IntegrationProperties;
import ru.croc.ugd.ssr.integration.config.IntegrationPropertyConfig;
import ru.croc.ugd.ssr.integration.mqetpmv.MqetpmvProperties;
import ru.croc.ugd.ssr.integration.service.mqetpmv.flow.SsrMqetpmvFlowService;
import ru.croc.ugd.ssr.integration.util.XmlUtils;
import ru.croc.ugd.ssr.integrationflow.IntegrationFlowType;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateTaskMessage;
import ru.croc.ugd.ssr.rsm.RsmObjectRequest;
import ru.croc.ugd.ssr.service.rsm.RsmObjectService;
import ru.reinform.cdp.mqetpmv.api.EtpMessageListener;
import ru.reinform.cdp.mqetpmv.model.EtpInboundMessage;

/**
 * RsmObjectRequestListener.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RsmObjectRequestListener implements EtpMessageListener {

    private final MqetpmvProperties mqetpmvProperties;
    private final XmlUtils xmlUtils;
    private final IntegrationProperties integrationProperties;
    private final IntegrationPropertyConfig propertyConfig;
    private final RsmObjectService rsmObjectService;
    private final SsrMqetpmvFlowService ssrMqetpmvFlowService;

    @NotNull
    @Override
    public String getMessageType() {
        return mqetpmvProperties.getRsmObjectRequestMessageType();
    }

    /**
     * Чтение из очереди.
     *
     * @param messageType       messageType
     * @param etpInboundMessage входящее сообщение с запросом ОКС
     */
    @Override
    public void receiveMessage(@NotNull final String messageType, @NotNull final EtpInboundMessage etpInboundMessage) {
        final ImportFlowMessageDto importFlowMessageDto =
            ssrMqetpmvFlowService.receiveFlowCoordinateTaskMessage(
                messageType,
                etpInboundMessage.getMessage(),
                propertyConfig.getRsmObjectRequest(),
                IntegrationFlowType.DGI_TO_DGP_RSM_OBJECT_REQUEST,
                integrationProperties.getXmlImportRsmObject(),
                this::parseCoordinateTaskMessage
            );

        try {
            importFlowMessageDto.getImportCoordinateTasks()
                .stream()
                .map(receivedCoordinateTask -> prepareRsmRequestDto(
                    receivedCoordinateTask, etpInboundMessage.getMessageID()
                ))
                .forEach(rsmObjectService::processRequest);
        } catch (Exception e) {
            log.error("Unable to process RsmObjectRequest: {}", e.getMessage());
            throw e;
        }
    }

    private CoordinateTaskMessage parseCoordinateTaskMessage(final String message) {
        return xmlUtils.transformXmlToObject(
            message, CoordinateTaskMessage.class, RsmObjectRequest.class
        );
    }

    private RsmRequestDto prepareRsmRequestDto(
        final ImportCoordinateTask importCoordinateTask, final String etpMessageId
    ) {
        final RsmObjectRequest rsmObjectRequest = (RsmObjectRequest) importCoordinateTask.getCoordinateTaskData()
            .getData()
            .getParameter()
            .getAny();

        return RsmRequestDto.builder()
            .etpmvMessageId(etpMessageId)
            .requestEno(importCoordinateTask.getEno())
            .rsmObjectRequest(rsmObjectRequest)
            .build();
    }
}
