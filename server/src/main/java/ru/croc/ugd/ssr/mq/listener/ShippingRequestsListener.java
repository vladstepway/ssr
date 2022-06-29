package ru.croc.ugd.ssr.mq.listener;

import static java.util.Optional.ofNullable;
import static ru.croc.ugd.ssr.dto.shipping.ShippingFlowStatus.DECLINE_SHIPPING_REQUEST;
import static ru.croc.ugd.ssr.dto.shipping.ShippingFlowStatus.TECHNICAL_CRASH_SEND;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.db.dao.ShippingApplicationDao;
import ru.croc.ugd.ssr.dto.shipping.ShippingFlowStatus;
import ru.croc.ugd.ssr.exception.SsrException;
import ru.croc.ugd.ssr.integration.config.IntegrationProperties;
import ru.croc.ugd.ssr.integration.service.notification.ShippingElkNotificationService;
import ru.croc.ugd.ssr.integration.util.XmlUtils;
import ru.croc.ugd.ssr.mapper.ShippingMapper;
import ru.croc.ugd.ssr.model.ShippingApplicationDocument;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateMessage;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateStatusData;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateStatusMessage;
import ru.croc.ugd.ssr.model.integration.etpmv.StatusType;
import ru.croc.ugd.ssr.model.integration.shipping.ServiceProperties;
import ru.croc.ugd.ssr.mq.interop.QueueProperties;
import ru.croc.ugd.ssr.service.bpm.BpmService;
import ru.croc.ugd.ssr.service.document.ShippingApplicationDocumentService;
import ru.croc.ugd.ssr.service.shipping.ShippingService;
import ru.reinform.cdp.exception.RIException;

import java.util.Collections;
import java.util.Optional;

/**
 * Слушатель очереди для помощи по переезду.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "ugd.ssr.listener.shipping.enabled")
public class ShippingRequestsListener {

    private static final String PROCESS_KEY_CONFIRM_APPLICATION = "ugdssrShipping_confirmApplication";

    private final QueueProperties queueProperties;
    private final XmlUtils xmlUtils;
    private final IntegrationProperties integrationProperties;
    private final ShippingApplicationDocumentService shippingApplicationDocumentService;
    private final ShippingMapper shippingMapper;
    private final ShippingService shippingService;
    private final ShippingElkNotificationService shippingElkNotificationService;
    private final BpmService bpmService;
    private final ShippingApplicationDao shippingApplicationDao;

    /**
     * Чтение из очереди.
     *
     * @param shippingRequest shippingRequest
     */
    @JmsListener(destination = "#{@'ugd.ssr.queue-ru.croc.ugd.ssr.mq.interop.QueueProperties'.getShippingRequests()}",
        containerFactory = "etpListenerFactory")
    public void receiveShippingRequests(final Message<String> shippingRequest) {
        try {
            final CoordinateMessage parsedShippingRequest = extractAndRecordShippingRequestMessage(shippingRequest);
            handleShippingRequest(parsedShippingRequest);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            shippingElkNotificationService.sendToBk(shippingRequest.getPayload());
        }
    }

    private void handleShippingRequest(final CoordinateMessage parsedShippingRequest) {
        final ShippingApplicationDocument shippingApplicationDocument = shippingMapper
            .toShippingApplicationDocument(parsedShippingRequest);
        if (shippingApplicationDocument == null) {
            log.error("Couldn't parse booking information: {}",
                ToStringBuilder.reflectionToString(parsedShippingRequest));
            return;
        }
        try {
            handleNewShippingApplicationDocument(shippingApplicationDocument);
        } catch (RIException e) {
            log.error(e.getMessage(), e);
            shippingElkNotificationService.sendStatus(TECHNICAL_CRASH_SEND, shippingApplicationDocument);
        }
    }

    private void handleNewShippingApplicationDocument(final ShippingApplicationDocument shippingApplicationDocument) {
        final String bookingId = shippingApplicationDocument
            .getDocument()
            .getShippingApplicationData()
            .getBookingId();
        if (bookingId != null && shippingApplicationDao.existsByBookingIdAndStatus(bookingId,
            ShippingFlowStatus.RECORD_ADDED.getDescription())) {
            throw new RIException("Указанное бронирование уже занято.");
        }
        shippingApplicationDocumentService.createDocument(shippingApplicationDocument, true, null);

        log.debug("About to start BPM process for MPGU order {}",
            shippingApplicationDocument.getId());
        bpmService.startNewProcess(
            PROCESS_KEY_CONFIRM_APPLICATION,
            Collections.singletonMap(
                BpmService.PROCESS_DOCUMENT_KEY_ENTITY_ID_VAR, shippingApplicationDocument.getId()
            )
        );
    }

    private CoordinateMessage extractAndRecordShippingRequestMessage(final Message<String> shippingRequest) {
        final String payload = shippingRequest.getPayload();
        log.debug(
            "Message received queue: {}, payload: {}",
            queueProperties.getShippingRequests(),
            payload
        );
        xmlUtils.writeXmlFile(payload, integrationProperties.getXmlImportShippingFlow());

        final CoordinateMessage parsedShippingRequest = xmlUtils.<CoordinateMessage>parseXml(
            payload,
            new Class[]{CoordinateMessage.class, ServiceProperties.class}
        ).orElseThrow(() -> new SsrException("Invalid CoordinateMessage"));
        log.debug("Parsed CoordinateMessage: {}", ToStringBuilder.reflectionToString(parsedShippingRequest));
        return parsedShippingRequest;
    }

    /**
     * Чтение из очереди.
     *
     * @param shippingStatusMessage shippingStatusMessage
     */
    @JmsListener(destination =
        "#{@'ugd.ssr.queue-ru.croc.ugd.ssr.mq.interop.QueueProperties'.getShippingStatusesInc()}",
        containerFactory = "etpListenerFactory")
    public void receiveShippingStatuses(final Message<String> shippingStatusMessage) {
        try {
            final CoordinateStatusMessage receivedShippingStatus =
                extractAndRecordShippingStatusMessage(shippingStatusMessage);
            handleStatusMessage(receivedShippingStatus);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            shippingElkNotificationService.sendStatusToBk(shippingStatusMessage.getPayload());
        }
    }

    private void handleStatusMessage(final CoordinateStatusMessage receivedShippingStatus) {
        try {
            retrieveShippingStatusCode(receivedShippingStatus)
                .filter(DECLINE_SHIPPING_REQUEST.getCode()::equals)
                .ifPresent(status -> shippingService
                    .declineApplicationByApplicant(
                        retrieveEno(receivedShippingStatus), retrieveDeclineReason(receivedShippingStatus)
                    )
                );
        } catch (RIException e) {
            log.error(e.getMessage(), e);
            shippingElkNotificationService.sendStatus(TECHNICAL_CRASH_SEND);
        }
    }

    private CoordinateStatusMessage extractAndRecordShippingStatusMessage(final Message<String> shippingStatus) {
        final String payload = shippingStatus.getPayload();
        log.debug(
            "Message received queue: {}, payload: {}",
            queueProperties.getShippingStatusesInc(),
            payload
        );

        xmlUtils.writeXmlFile(payload, integrationProperties.getXmlImportShippingFlow());

        final CoordinateStatusMessage statusMessage = xmlUtils.<CoordinateStatusMessage>parseXml(
            payload, new Class[]{CoordinateStatusMessage.class}
        ).orElseThrow(() -> new SsrException("Invalid CoordinateStatusMessage"));
        log.debug("Parsed statusMessage: {}", ToStringBuilder.reflectionToString(statusMessage));
        return statusMessage;
    }

    private static Optional<Integer> retrieveShippingStatusCode(final CoordinateStatusMessage coordinateStatusMessage) {
        return ofNullable(coordinateStatusMessage)
            .map(CoordinateStatusMessage::getCoordinateStatusDataMessage)
            .map(CoordinateStatusData::getStatus)
            .map(StatusType::getStatusCode);
    }

    private static String retrieveEno(final CoordinateStatusMessage coordinateStatusMessage) {
        return ofNullable(coordinateStatusMessage)
            .map(CoordinateStatusMessage::getCoordinateStatusDataMessage)
            .map(CoordinateStatusData::getServiceNumber)
            .orElse(null);
    }

    private static String retrieveDeclineReason(final CoordinateStatusMessage coordinateStatusMessage) {
        return ofNullable(coordinateStatusMessage)
            .map(CoordinateStatusMessage::getCoordinateStatusDataMessage)
            .map(CoordinateStatusData::getNote)
            .map(note -> note.replace("Причина отзыва: ", ""))
            .orElse(null);
    }
}
