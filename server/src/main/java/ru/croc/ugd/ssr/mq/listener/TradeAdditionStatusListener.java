package ru.croc.ugd.ssr.mq.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.integration.config.IntegrationProperties;
import ru.croc.ugd.ssr.integration.mqetpmv.MqetpmvProperties;
import ru.croc.ugd.ssr.integration.service.mqetpmv.flow.SsrMqetpmvFlowService;
import ru.reinform.cdp.mqetpmv.api.EtpMessageListener;
import ru.reinform.cdp.mqetpmv.model.EtpInboundMessage;

/**
 * Слушатель очереди приема статуса по сделкам по докупке, компенсации и вне района. 17 поток.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TradeAdditionStatusListener implements EtpMessageListener {

    private final MqetpmvProperties mqetpmvProperties;
    private final IntegrationProperties integrationProperties;
    private final SsrMqetpmvFlowService ssrMqetpmvFlowService;

    @NotNull
    @Override
    public String getMessageType() {
        return mqetpmvProperties.getTradeAdditionFlowStatusIncMessageType();
    }

    @Override
    public void receiveMessage(@NotNull final String messageType, @NotNull final EtpInboundMessage etpInboundMessage) {
        ssrMqetpmvFlowService.receiveFlowStatusMessage(
            messageType,
            etpInboundMessage.getMessage(),
            integrationProperties.getXmlImportFlowTradeAddition()
        );
    }
}
