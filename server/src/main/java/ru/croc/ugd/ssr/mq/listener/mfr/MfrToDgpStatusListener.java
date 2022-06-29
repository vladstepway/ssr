package ru.croc.ugd.ssr.mq.listener.mfr;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.integration.config.IntegrationProperties;
import ru.croc.ugd.ssr.integration.mqetpmv.MqetpmvProperties;
import ru.croc.ugd.ssr.integration.service.mqetpmv.flow.SsrMqetpmvFlowService;
import ru.reinform.cdp.mqetpmv.api.EtpMessageListener;
import ru.reinform.cdp.mqetpmv.model.EtpInboundMessage;

@Slf4j
@Component
@AllArgsConstructor
public class MfrToDgpStatusListener implements EtpMessageListener {

    private final MqetpmvProperties mqetpmvProperties;
    private final IntegrationProperties integrationProperties;
    private final SsrMqetpmvFlowService ssrMqetpmvFlowService;

    @NotNull
    @Override
    public String getMessageType() {
        return mqetpmvProperties.getMfrFlowStatusIncMessageType();
    }

    @Override
    public void receiveMessage(@NotNull final String messageType, @NotNull final EtpInboundMessage etpInboundMessage) {
        ssrMqetpmvFlowService.receiveFlowStatusMessage(
            messageType,
            etpInboundMessage.getMessage(),
            integrationProperties.getXmlImportMfrStatus()
        );
    }
}
