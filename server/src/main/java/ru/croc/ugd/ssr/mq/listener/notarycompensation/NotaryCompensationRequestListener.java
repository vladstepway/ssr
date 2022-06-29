package ru.croc.ugd.ssr.mq.listener.notarycompensation;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.exception.SsrException;
import ru.croc.ugd.ssr.integration.config.IntegrationProperties;
import ru.croc.ugd.ssr.integration.mqetpmv.MqetpmvProperties;
import ru.croc.ugd.ssr.integration.util.XmlUtils;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateMessage;
import ru.croc.ugd.ssr.service.notarycompensation.NotaryCompensationService;
import ru.mos.gu.service._091201.ServiceProperties;
import ru.reinform.cdp.mqetpmv.api.EtpMessageListener;
import ru.reinform.cdp.mqetpmv.model.EtpInboundMessage;

@Component
@AllArgsConstructor
@Slf4j
public class NotaryCompensationRequestListener implements EtpMessageListener {

    private final MqetpmvProperties mqetpmvProperties;
    private final XmlUtils xmlUtils;
    private final IntegrationProperties integrationProperties;
    private final NotaryCompensationService notaryApplicationService;

    @NotNull
    @Override
    public String getMessageType() {
        return mqetpmvProperties.getNotaryCompensationRequestIncMessageType();
    }

    @Override
    public void receiveMessage(@NotNull final String messageType, @NotNull final EtpInboundMessage etpInboundMessage) {
        final String message = etpInboundMessage.getMessage();
        log.debug(
            "CreateNotaryCompensation message received: messageType: {}, message: {}",
            messageType,
            message
        );
        xmlUtils.writeXmlFile(message, integrationProperties.getXmlImportNotaryCompensationFlow());

        final CoordinateMessage coordinateMessage = parseCoordinateMessage(message);
        notaryApplicationService.processRegistration(coordinateMessage);
    }

    private CoordinateMessage parseCoordinateMessage(final String messagePayload) {
        try {
            return xmlUtils.<CoordinateMessage>parseXml(
                messagePayload,
                new Class[]{CoordinateMessage.class, ServiceProperties.class}
            ).orElseThrow(() -> new SsrException("Invalid CoordinateMessage"));
        } catch (Exception e) {
            log.error("Unable to process request with notary compensation: {}", e.getMessage());
            throw e;
        }
    }
}
