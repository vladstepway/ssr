package ru.croc.ugd.ssr.mq.listener.notary;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.dto.notary.NotaryApplicationFlowStatus;
import ru.croc.ugd.ssr.exception.SsrException;
import ru.croc.ugd.ssr.exception.UnknownStatusCodeReceived;
import ru.croc.ugd.ssr.integration.config.IntegrationProperties;
import ru.croc.ugd.ssr.integration.service.notification.NotaryApplicationElkNotificationService;
import ru.croc.ugd.ssr.integration.util.MessageUtils;
import ru.croc.ugd.ssr.integration.util.XmlUtils;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateMessage;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateStatusMessage;
import ru.croc.ugd.ssr.mq.interop.QueueProperties;
import ru.croc.ugd.ssr.service.notary.NotaryApplicationService;
import ru.mos.gu.service._084001.ServiceProperties;

/**
 * NotaryApplicationRequestListener.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "ugd.ssr.listener.notary-application.enabled")
public class NotaryApplicationRequestListener {

    private final QueueProperties queueProperties;
    private final XmlUtils xmlUtils;
    private final IntegrationProperties integrationProperties;
    private final NotaryApplicationService notaryApplicationService;
    private final MessageUtils messageUtils;
    private final NotaryApplicationElkNotificationService notaryApplicationElkNotificationService;

    /**
     * Чтение из очереди.
     *
     * @param notaryApplication тело заявление на приём к нотариусу
     */
    @JmsListener(
        destination = "#{@'ugd.ssr.queue-ru.croc.ugd.ssr.mq.interop.QueueProperties'.getNotaryApplicationRequest()}",
        containerFactory = "etpmvFactory"
    )
    public void handleCoordinateMessage(final Message<String> notaryApplication) {
        final String messagePayload = notaryApplication.getPayload();

        log.debug(
            "Message received queue: {}, payload: {}",
            queueProperties.getNotaryApplicationRequest(),
            messagePayload
        );
        xmlUtils.writeXmlFile(messagePayload, integrationProperties.getXmlImportNotaryApplicationFlow());

        final CoordinateMessage coordinateMessage = parseCoordinateMessage(messagePayload);
        notaryApplicationService.processRegistration(coordinateMessage);
    }

    private CoordinateMessage parseCoordinateMessage(final String messagePayload) {
        try {
            return xmlUtils.<CoordinateMessage>parseXml(
                messagePayload,
                new Class[]{CoordinateMessage.class, ServiceProperties.class}
            ).orElseThrow(() -> new SsrException("Invalid CoordinateMessage"));
        } catch (Exception e) {
            log.error("Unable to process request with notary application: {}", e.getMessage());
            notaryApplicationElkNotificationService.sendToBk(messagePayload);
            throw e;
        }
    }

    /**
     * Чтение статусов из очереди.
     *
     * @param notaryApplicationStatus действия
     */
    @JmsListener(
        destination =
            "#{@'ugd.ssr.queue-ru.croc.ugd.ssr.mq.interop.QueueProperties'.getNotaryApplicationStatusInc()}",
        containerFactory = "etpmvFactory"
    )
    public void handleCoordinateStatusMessage(final Message<String> notaryApplicationStatus) {
        final String messagePayload = notaryApplicationStatus.getPayload();

        log.debug(
            "Message received queue: {}, payload: {}",
            queueProperties.getNotaryApplicationStatusInc(),
            messagePayload
        );
        xmlUtils.writeXmlFile(messagePayload, integrationProperties.getXmlImportNotaryApplicationFlow());

        final CoordinateStatusMessage coordinateStatus = parseCoordinateStatusMessage(messagePayload);

        final NotaryApplicationFlowStatus flowStatus = NotaryApplicationFlowStatus.of(
            messageUtils.retrieveStatusCode(coordinateStatus).orElse(null),
            messageUtils.retrieveStatusReasonCode(coordinateStatus).orElse(null)
        );
        processFlowStatus(flowStatus, coordinateStatus, messagePayload);
    }

    private CoordinateStatusMessage parseCoordinateStatusMessage(final String messagePayload) {
        try {
            return xmlUtils.<CoordinateStatusMessage>parseXml(
                messagePayload, new Class[]{CoordinateStatusMessage.class}
            ).orElseThrow(() -> new SsrException("Invalid CoordinateStatusMessage"));
        } catch (Exception e) {
            log.error("Unable to process notary application flow status: {}", e.getMessage());
            log.error(e.getMessage(), e);
            notaryApplicationElkNotificationService.sendStatusToBk(messagePayload);
            throw e;
        }
    }

    private void processFlowStatus(
        final NotaryApplicationFlowStatus flowStatus,
        final CoordinateStatusMessage coordinateStatus,
        final String messagePayload
    ) {
        if (isNull(flowStatus)) {
            processUnknownFlowStatus(null, messagePayload);
        } else {
            switch (flowStatus) {
                case DOCUMENTS_TRANSFERRED: {
                    notaryApplicationService.processReceivedDocuments(coordinateStatus);
                    break;
                }
                case BOOKING_SENT: {
                    notaryApplicationService.processBookingRequest(coordinateStatus);
                    break;
                }
                case CANCEL_BY_APPLICANT_REQUEST:
                    notaryApplicationService.processCancelByApplicantRequest(coordinateStatus);
                    break;
                default:
                    processUnknownFlowStatus(flowStatus, messagePayload);
            }
        }
    }

    private void processUnknownFlowStatus(
        final NotaryApplicationFlowStatus flowStatus,
        final String messagePayload
    ) {
        final String flowStatusText = ofNullable(flowStatus)
            .map(NotaryApplicationFlowStatus::getId)
            .orElse(null);
        log.warn("Received unknown notary application flow status {}.", flowStatusText);
        notaryApplicationElkNotificationService.sendStatusToBk(messagePayload);
        throw new UnknownStatusCodeReceived();
    }

}
