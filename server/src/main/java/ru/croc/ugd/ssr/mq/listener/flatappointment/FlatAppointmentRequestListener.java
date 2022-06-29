package ru.croc.ugd.ssr.mq.listener.flatappointment;

import static java.util.Optional.ofNullable;
import static ru.croc.ugd.ssr.dto.flatappointment.FlatAppointmentFlowStatus.CANCEL_BY_APPLICANT_REQUEST;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.dto.flatappointment.FlatAppointmentFlowStatus;
import ru.croc.ugd.ssr.exception.SsrException;
import ru.croc.ugd.ssr.exception.UnknownStatusCodeReceived;
import ru.croc.ugd.ssr.integration.config.IntegrationProperties;
import ru.croc.ugd.ssr.integration.service.notification.FlatAppointmentElkNotificationService;
import ru.croc.ugd.ssr.integration.util.MessageUtils;
import ru.croc.ugd.ssr.integration.util.XmlUtils;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateMessage;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateStatusMessage;
import ru.croc.ugd.ssr.mq.interop.QueueProperties;
import ru.croc.ugd.ssr.service.flatappointment.FlatAppointmentService;
import ru.mos.gu.service._084901.ServiceProperties;

/**
 * FlatAppointmentRequestListener.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "ugd.ssr.listener.flat-appointment.enabled")
public class FlatAppointmentRequestListener {

    private final QueueProperties queueProperties;
    private final XmlUtils xmlUtils;
    private final IntegrationProperties integrationProperties;
    private final FlatAppointmentService flatAppointmentService;
    private final FlatAppointmentElkNotificationService flatAppointmentElkNotificationService;
    private final MessageUtils messageUtils;

    /**
     * Чтение из очереди.
     *
     * @param flatAppointment тело заявления на запись на осмотр квартиры.
     */
    @JmsListener(
        destination = "#{@'ugd.ssr.queue-ru.croc.ugd.ssr.mq.interop.QueueProperties'.getFlatAppointmentRequest()}",
        containerFactory = "etpListenerFactory"
    )
    public void handleCoordinateMessage(final Message<String> flatAppointment) {
        final String messagePayload = flatAppointment.getPayload();

        log.debug(
            "Message received queue: {}, payload: {}",
            queueProperties.getFlatAppointmentRequest(),
            messagePayload
        );
        xmlUtils.writeXmlFile(messagePayload, integrationProperties.getXmlImportFlatAppointmentFlow());

        final CoordinateMessage coordinateMessage = parseCoordinateMessage(messagePayload);
        flatAppointmentService.processRegistration(coordinateMessage);
    }

    private CoordinateMessage parseCoordinateMessage(final String messagePayload) {
        try {
            return xmlUtils.<CoordinateMessage>parseXml(
                messagePayload,
                new Class[]{CoordinateMessage.class, ServiceProperties.class}
            ).orElseThrow(() -> new SsrException("Invalid CoordinateMessage"));
        } catch (Exception e) {
            log.error("Unable to process request with flat appointment: {}", e.getMessage());
            flatAppointmentElkNotificationService.sendToBk(messagePayload);
            throw e;
        }
    }

    /**
     * Чтение статусов из очереди.
     *
     * @param flatAppointmentStatus действия
     */
    @JmsListener(
        destination = "#{@'ugd.ssr.queue-ru.croc.ugd.ssr.mq.interop.QueueProperties'.getFlatAppointmentStatusInc()}",
        containerFactory = "etpListenerFactory"
    )
    public void handleCoordinateStatusMessage(final Message<String> flatAppointmentStatus) {
        final String messagePayload = flatAppointmentStatus.getPayload();

        log.debug(
            "Message received queue: {}, payload: {}",
            queueProperties.getFlatAppointmentStatusInc(),
            messagePayload
        );
        xmlUtils.writeXmlFile(messagePayload, integrationProperties.getXmlImportFlatAppointmentFlow());

        final CoordinateStatusMessage coordinateStatus = parseCoordinateStatusMessage(messagePayload);

        final FlatAppointmentFlowStatus flowStatus = FlatAppointmentFlowStatus.of(
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
            log.error("Unable to process request with flat appointment flow status: {}", e.getMessage());
            log.error(e.getMessage(), e);
            flatAppointmentElkNotificationService.sendStatusToBk(messagePayload);
            throw e;
        }
    }

    private void processFlowStatus(
        final FlatAppointmentFlowStatus flowStatus,
        final CoordinateStatusMessage coordinateStatus,
        final String messagePayload
    ) {
        if (flowStatus == CANCEL_BY_APPLICANT_REQUEST) {
            flatAppointmentService.processCancelByApplicantRequest(coordinateStatus);
        } else {
            processUnknownFlowStatus(flowStatus, messagePayload);
        }
    }

    private void processUnknownFlowStatus(
        final FlatAppointmentFlowStatus flowStatus,
        final String messagePayload
    ) {
        final String flowStatusText = ofNullable(flowStatus)
            .map(FlatAppointmentFlowStatus::getId)
            .orElse(null);
        log.warn("Received unknown flat appointment flow status {}.", flowStatusText);
        flatAppointmentElkNotificationService.sendStatusToBk(messagePayload);
        throw new UnknownStatusCodeReceived();
    }

}
