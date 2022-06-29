package ru.croc.ugd.ssr.mq.listener.commissioninspection;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus;
import ru.croc.ugd.ssr.exception.SsrException;
import ru.croc.ugd.ssr.exception.UnknownStatusCodeReceived;
import ru.croc.ugd.ssr.integration.config.IntegrationProperties;
import ru.croc.ugd.ssr.integration.service.notification.CommissionInspectionElkNotificationService;
import ru.croc.ugd.ssr.integration.util.MessageUtils;
import ru.croc.ugd.ssr.integration.util.XmlUtils;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateMessage;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateStatusMessage;
import ru.croc.ugd.ssr.mq.interop.QueueProperties;
import ru.croc.ugd.ssr.service.commissioninspection.CommissionInspectionService;
import ru.mos.gu.service._0834.ServiceProperties;
import ru.mos.gu.service._0834.ServicePropertiesStatus;

/**
 * CommissionInspectionListener.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "ugd.ssr.listener.commission-inspection.enabled")
public class CommissionInspectionRequestListener {

    private final QueueProperties queueProperties;
    private final XmlUtils xmlUtils;
    private final MessageUtils messageUtils;
    private final IntegrationProperties integrationProperties;
    private final CommissionInspectionService commissionInspectionService;
    private final CommissionInspectionElkNotificationService commissionInspectionElkNotificationService;

    /**
     * Чтение из очереди.
     *
     * @param commissionInspectionApplication commissionInspectionApplication
     */
    @JmsListener(
        destination = "#{@'ugd.ssr.queue-ru.croc.ugd.ssr.mq.interop.QueueProperties'.getCommissionInspectionRequest()}",
        containerFactory = "guoListenerFactory"
    )
    public void handleCoordinateMessage(final Message<String> commissionInspectionApplication) {
        final String messagePayload = commissionInspectionApplication.getPayload();

        log.debug(
            "Message received queue: {}, payload: {}",
            queueProperties.getCommissionInspectionRequest(),
            messagePayload
        );
        xmlUtils.writeXmlFile(messagePayload, integrationProperties.getXmlImportCommissionInspectionFlow());

        final CoordinateMessage coordinateMessage = parseCoordinateMessage(messagePayload);
        commissionInspectionService.processRegistration(coordinateMessage);
    }

    private CoordinateMessage parseCoordinateMessage(final String messagePayload) {
        try {
            return xmlUtils.<CoordinateMessage>parseXml(
                messagePayload,
                new Class[]{CoordinateMessage.class, ServiceProperties.class}
            ).orElseThrow(() -> new SsrException("Invalid CoordinateMessage"));
        } catch (Exception e) {
            log.error("Unable to process request with commission inspection application: {}", e.getMessage());
            commissionInspectionElkNotificationService.sendToBk(messagePayload);
            throw e;
        }
    }

    /**
     * Чтение статусов из очереди.
     *
     * @param commissionInspectionStatus commissionInspectionStatus
     */
    @JmsListener(
        destination =
            "#{@'ugd.ssr.queue-ru.croc.ugd.ssr.mq.interop.QueueProperties'.getCommissionInspectionStatusInc()}",
        containerFactory = "guoListenerFactory"
    )
    public void handleCoordinateStatusMessage(final Message<String> commissionInspectionStatus) {
        final String messagePayload = commissionInspectionStatus.getPayload();

        log.debug(
            "Message received queue: {}, payload: {}",
            queueProperties.getCommissionInspectionStatusInc(),
            messagePayload
        );
        xmlUtils.writeXmlFile(messagePayload, integrationProperties.getXmlImportCommissionInspectionFlow());

        final CoordinateStatusMessage coordinateStatus = parseCoordinateStatusMessage(messagePayload);

        final CommissionInspectionFlowStatus flowStatus = CommissionInspectionFlowStatus.of(
            messageUtils.retrieveStatusCode(coordinateStatus).orElse(null),
            messageUtils.retrieveStatusReasonCode(coordinateStatus).orElse(null)
        );
        processFlowStatus(flowStatus, coordinateStatus, messagePayload);
    }

    private CoordinateStatusMessage parseCoordinateStatusMessage(final String messagePayload) {
        try {
            return xmlUtils.<CoordinateStatusMessage>parseXml(
                messagePayload, new Class[]{CoordinateStatusMessage.class, ServicePropertiesStatus.class}
            ).orElseThrow(() -> new SsrException("Invalid CoordinateStatusMessage"));
        } catch (Exception e) {
            log.error("Unable to process commission inspection flow status: {}", e.getMessage());
            commissionInspectionElkNotificationService.sendStatusToBk(messagePayload);
            throw e;
        }
    }

    private void processFlowStatus(
        final CommissionInspectionFlowStatus flowStatus,
        final CoordinateStatusMessage coordinateStatus,
        final String messagePayload
    ) {
        if (isNull(flowStatus)) {
            processUnknownFlowStatus(null, messagePayload);
        } else {
            switch (flowStatus) {
                case WITHDRAW_REQUEST: {
                    commissionInspectionService.withdraw(coordinateStatus);
                    break;
                }
                case MOVE_DATE_FIRST_VISIT_REQUEST:
                case MOVE_DATE_SECOND_VISIT_REQUEST: {
                    commissionInspectionService.processMoveDateRequest(coordinateStatus);
                    break;
                }
                case DEFECTS_CHANGE_REQUEST:
                    commissionInspectionService.processChangeDefectsRequest(coordinateStatus);
                    break;
                default:
                    processUnknownFlowStatus(flowStatus, messagePayload);
            }
        }
    }

    private void processUnknownFlowStatus(
        final CommissionInspectionFlowStatus flowStatus,
        final String messagePayload
    ) {
        final String flowStatusText = ofNullable(flowStatus)
            .map(CommissionInspectionFlowStatus::getId)
            .orElse(null);
        log.warn("Received unknown commission inspection flow status {}.", flowStatusText);
        commissionInspectionElkNotificationService.sendStatusToBk(messagePayload);
        throw new UnknownStatusCodeReceived();
    }

}
