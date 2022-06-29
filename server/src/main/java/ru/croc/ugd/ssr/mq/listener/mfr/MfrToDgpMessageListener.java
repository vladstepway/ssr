package ru.croc.ugd.ssr.mq.listener.mfr;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.dto.mqetpmv.flow.ImportCoordinateTask;
import ru.croc.ugd.ssr.dto.mqetpmv.flow.ImportFlowMessageDto;
import ru.croc.ugd.ssr.exception.SsrException;
import ru.croc.ugd.ssr.integration.config.IntegrationProperties;
import ru.croc.ugd.ssr.integration.mqetpmv.MqetpmvProperties;
import ru.croc.ugd.ssr.integration.service.flows.mfr.MfrFlowService;
import ru.croc.ugd.ssr.integration.service.flows.mfr.enums.MfrToDgpFlowDataType;
import ru.croc.ugd.ssr.integration.service.mqetpmv.flow.SsrMqetpmvFlowService;
import ru.croc.ugd.ssr.integration.util.XmlUtils;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateTaskMessage;
import ru.reinform.cdp.mqetpmv.api.EtpMessageListener;
import ru.reinform.cdp.mqetpmv.model.EtpInboundMessage;

import java.util.Arrays;
import java.util.stream.Stream;

@Slf4j
@Component
@AllArgsConstructor
public class MfrToDgpMessageListener implements EtpMessageListener {

    private final MqetpmvProperties mqetpmvProperties;
    private final XmlUtils xmlUtils;
    private final IntegrationProperties integrationProperties;
    private final MfrFlowService mfrFlowService;
    private final SsrMqetpmvFlowService ssrMqetpmvFlowService;

    @NotNull
    @Override
    public String getMessageType() {
        return mqetpmvProperties.getMfrFlowTaskIncMessageType();
    }

    @Override
    public void receiveMessage(@NotNull final String messageType, @NotNull final EtpInboundMessage etpInboundMessage) {
        receiveMessage(messageType, etpInboundMessage.getMessage());
    }

    private void receiveMessage(final String messageType, final String message) {
        final ImportFlowMessageDto importFlowMessageDto =
            ssrMqetpmvFlowService.receiveFlowCoordinateTaskMessage(
                messageType,
                message,
                integrationProperties.getXmlImportMfrMessage(),
                this::parseCoordinateTaskMessage,
                mfrFlowService::retrieveFlowTypeDto
            );

        try {
            importFlowMessageDto.getImportCoordinateTasks()
                .forEach(this::processMessage);
        } catch (Exception e) {
            log.error("Unable to process request with mfr flow message: {}", e.getMessage());
            throw e;
        }
    }

    private void processMessage(final ImportCoordinateTask importCoordinateTask) {
        mfrFlowService.processMessage(
            importCoordinateTask.getEno(),
            importCoordinateTask.getCoordinateTaskData(),
            importCoordinateTask.getIntegrationFlowDocument(),
            importCoordinateTask.getFlowMessageProcessorBeanName()
        );
    }

    private CoordinateTaskMessage parseCoordinateTaskMessage(final String message) {
        return xmlUtils.<CoordinateTaskMessage>parseXml(
            message,
            getAvailableClasses()
        ).orElseThrow(() -> new SsrException("Invalid CoordinateTaskMessage"));
    }

    private Class<?>[] getAvailableClasses() {
        return Stream.concat(
                Stream.of(CoordinateTaskMessage.class),
                Arrays.stream(MfrToDgpFlowDataType.values()).map(MfrToDgpFlowDataType::getClazz)
            )
            .toArray(Class[]::new);
    }
}
