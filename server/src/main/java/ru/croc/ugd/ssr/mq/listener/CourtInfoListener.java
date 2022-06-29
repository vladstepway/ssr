package ru.croc.ugd.ssr.mq.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.dto.mqetpmv.flow.ImportCoordinateTask;
import ru.croc.ugd.ssr.dto.mqetpmv.flow.ImportFlowMessageDto;
import ru.croc.ugd.ssr.integration.config.IntegrationProperties;
import ru.croc.ugd.ssr.integration.config.IntegrationPropertyConfig;
import ru.croc.ugd.ssr.integration.mqetpmv.MqetpmvProperties;
import ru.croc.ugd.ssr.integration.service.flows.CourtInfoFlowService;
import ru.croc.ugd.ssr.integration.service.mqetpmv.flow.SsrMqetpmvFlowService;
import ru.croc.ugd.ssr.integration.util.XmlUtils;
import ru.croc.ugd.ssr.integrationflow.IntegrationFlowType;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPCourtInfoType;
import ru.croc.ugd.ssr.model.integration.etpmv.SendTasksMessage;
import ru.reinform.cdp.mqetpmv.api.EtpMessageListener;
import ru.reinform.cdp.mqetpmv.model.EtpInboundMessage;

/**
 * Слушатель очереди приема сведений по судам.21 поток.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CourtInfoListener implements EtpMessageListener {

    private final MqetpmvProperties mqetpmvProperties;
    private final XmlUtils xmlUtils;
    private final IntegrationProperties integrationProperties;
    private final CourtInfoFlowService courtInfoFlowService;
    private final SsrMqetpmvFlowService ssrMqetpmvFlowService;
    private final IntegrationPropertyConfig propertyConfig;

    @NotNull
    @Override
    public String getMessageType() {
        return mqetpmvProperties.getCourtFlowTaskIncMessageType();
    }

    /**
     * Чтение из очереди.
     *
     * @param messageType       messageType
     * @param etpInboundMessage входящее сообщение со сведениями по судам
     */
    @Override
    public void receiveMessage(@NotNull final String messageType, @NotNull final EtpInboundMessage etpInboundMessage) {
        final ImportFlowMessageDto importFlowMessageDto = ssrMqetpmvFlowService.receiveFlowSendTasksMessage(
            messageType,
            etpInboundMessage.getMessage(),
            propertyConfig.getCourtInfo(),
            IntegrationFlowType.DGP_TO_DGI_COURT_INFO,
            integrationProperties.getXmlImportCourtInfo(),
            this::parseMessage
        );
        try {
            importFlowMessageDto.getImportCoordinateTasks()
                .forEach(this::processMessage);
        } catch (Exception e) {
            log.error("Unable to process SuperServiceDGPCourtInfoType: {}", e.getMessage());
            throw e;
        }
    }

    private void processMessage(final ImportCoordinateTask importCoordinateTask) {
        final SuperServiceDGPCourtInfoType superServiceDgpCourtInfoType =
            (SuperServiceDGPCourtInfoType) importCoordinateTask.getCoordinateTaskData()
                .getData()
                .getParameter()
                .getAny();

        courtInfoFlowService.processMessage(
            importCoordinateTask.getEno(),
            superServiceDgpCourtInfoType,
            importCoordinateTask.getIntegrationFlowDocument()
        );
    }

    private SendTasksMessage parseMessage(final String message) {
        return xmlUtils.transformXmlToObject(message, SendTasksMessage.class, SuperServiceDGPCourtInfoType.class);
    }
}
