package ru.croc.ugd.ssr.mq.listener.contractappointment;

import static java.util.Optional.ofNullable;
import static ru.croc.ugd.ssr.dto.contractappointment.ContractAppointmentFlowStatus.CANCEL_BY_APPLICANT_REQUEST;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.dto.contractappointment.ContractAppointmentFlowStatus;
import ru.croc.ugd.ssr.exception.SsrException;
import ru.croc.ugd.ssr.exception.UnknownStatusCodeReceived;
import ru.croc.ugd.ssr.integration.config.IntegrationProperties;
import ru.croc.ugd.ssr.integration.service.notification.ContractAppointmentElkNotificationService;
import ru.croc.ugd.ssr.integration.util.MessageUtils;
import ru.croc.ugd.ssr.integration.util.XmlUtils;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateMessage;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateStatusMessage;
import ru.croc.ugd.ssr.mq.interop.QueueProperties;
import ru.croc.ugd.ssr.service.contractappointment.ContractAppointmentService;

/**
 * ContractAppointmentRequestListener.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "ugd.ssr.listener.contract-appointment.enabled")
public class ContractAppointmentRequestListener {

    private final QueueProperties queueProperties;
    private final XmlUtils xmlUtils;
    private final IntegrationProperties integrationProperties;
    private final ContractAppointmentService contractAppointmentService;
    private final ContractAppointmentElkNotificationService contractAppointmentElkNotificationService;
    private final MessageUtils messageUtils;

    /**
     * Чтение из очереди.
     *
     * @param contractAppointment тело заявления на запись на подписание договора.
     */
    @JmsListener(
        destination = "#{@'ugd.ssr.queue-ru.croc.ugd.ssr.mq.interop.QueueProperties'.getContractAppointmentRequest()}",
        containerFactory = "etpListenerFactory"
    )
    public void handleCoordinateMessage(final Message<String> contractAppointment) {
        final String messagePayload = contractAppointment.getPayload();

        log.debug(
            "Message received queue: {}, payload: {}",
            queueProperties.getContractAppointmentRequest(),
            messagePayload
        );
        xmlUtils.writeXmlFile(messagePayload, integrationProperties.getXmlImportContractAppointmentFlow());

        final CoordinateMessage coordinateMessage = parseCoordinateMessage(messagePayload);
        contractAppointmentService.processRegistration(coordinateMessage);
    }

    private CoordinateMessage parseCoordinateMessage(final String messagePayload) {
        try {
            return xmlUtils.<CoordinateMessage>parseXml(
                messagePayload,
                new Class[]{
                    CoordinateMessage.class,
                    ru.mos.gu.service._086601.ServiceProperties.class,
                    ru.mos.gu.service._086602.ServiceProperties.class
                }
            ).orElseThrow(() -> new SsrException("Invalid CoordinateMessage"));
        } catch (Exception e) {
            log.error("Unable to process request with contract appointment: {}", e.getMessage());
            contractAppointmentElkNotificationService.sendToBk(messagePayload);
            throw e;
        }
    }

    /**
     * Чтение статусов из очереди.
     *
     * @param contractAppointmentStatus действия
     */
    @JmsListener(
        destination =
            "#{@'ugd.ssr.queue-ru.croc.ugd.ssr.mq.interop.QueueProperties'.getContractAppointmentStatusInc()}",
        containerFactory = "etpListenerFactory"
    )
    public void handleCoordinateStatusMessage(final Message<String> contractAppointmentStatus) {
        final String messagePayload = contractAppointmentStatus.getPayload();

        log.debug(
            "Message received queue: {}, payload: {}",
            queueProperties.getContractAppointmentStatusInc(),
            messagePayload
        );
        xmlUtils.writeXmlFile(messagePayload, integrationProperties.getXmlImportContractAppointmentFlow());

        final CoordinateStatusMessage coordinateStatus = parseCoordinateStatusMessage(messagePayload);

        final ContractAppointmentFlowStatus flowStatus = ContractAppointmentFlowStatus.of(
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
            log.error("Unable to process request with contract appointment flow status: {}", e.getMessage());
            log.error(e.getMessage(), e);
            contractAppointmentElkNotificationService.sendStatusToBk(messagePayload);
            throw e;
        }
    }

    private void processFlowStatus(
        final ContractAppointmentFlowStatus flowStatus,
        final CoordinateStatusMessage coordinateStatus,
        final String messagePayload
    ) {
        if (flowStatus == CANCEL_BY_APPLICANT_REQUEST) {
            contractAppointmentService.processCancelByApplicantRequest(coordinateStatus);
        } else {
            processUnknownFlowStatus(flowStatus, messagePayload);
        }
    }

    private void processUnknownFlowStatus(
        final ContractAppointmentFlowStatus flowStatus,
        final String messagePayload
    ) {
        final String flowStatusText = ofNullable(flowStatus)
            .map(ContractAppointmentFlowStatus::getId)
            .orElse(null);
        log.warn("Received unknown contract appointment flow status {}.", flowStatusText);
        contractAppointmentElkNotificationService.sendStatusToBk(messagePayload);
        throw new UnknownStatusCodeReceived();
    }

}
