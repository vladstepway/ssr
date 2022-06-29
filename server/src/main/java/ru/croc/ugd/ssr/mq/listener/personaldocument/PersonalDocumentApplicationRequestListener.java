package ru.croc.ugd.ssr.mq.listener.personaldocument;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.exception.SsrException;
import ru.croc.ugd.ssr.integration.config.IntegrationProperties;
import ru.croc.ugd.ssr.integration.service.notification.PersonalDocumentApplicationElkNotificationService;
import ru.croc.ugd.ssr.integration.util.XmlUtils;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateMessage;
import ru.croc.ugd.ssr.mq.interop.QueueProperties;
import ru.croc.ugd.ssr.service.personaldocument.PersonalDocumentApplicationService;
import ru.mos.gu.service._088201.ServiceProperties;

/**
 * PersonalDocumentApplicationRequestListener.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "ugd.ssr.listener.personal-document-application.enabled")
public class PersonalDocumentApplicationRequestListener {

    private final QueueProperties queueProperties;
    private final XmlUtils xmlUtils;
    private final IntegrationProperties integrationProperties;
    private final PersonalDocumentApplicationService personalDocumentApplicationService;
    private final PersonalDocumentApplicationElkNotificationService personalDocumentApplicationElkNotificationService;

    /**
     * Чтение из очереди.
     *
     * @param personalDocumentApplication тело заявления на предоставление документов
     */
    @JmsListener(
        destination =
            "#{@'ugd.ssr.queue-ru.croc.ugd.ssr.mq.interop.QueueProperties'.getPersonalDocumentApplicationRequest()}",
        containerFactory = "etpListenerFactory"
    )
    public void handleCoordinateMessage(final Message<String> personalDocumentApplication) {
        final String messagePayload = personalDocumentApplication.getPayload();

        log.debug(
            "Message received queue: {}, payload: {}",
            queueProperties.getPersonalDocumentApplicationRequest(),
            messagePayload
        );
        xmlUtils.writeXmlFile(messagePayload, integrationProperties.getXmlImportPersonalDocumentApplicationFlow());

        final CoordinateMessage coordinateMessage = parseCoordinateMessage(messagePayload);
        personalDocumentApplicationService.processRegistration(coordinateMessage);
    }

    private CoordinateMessage parseCoordinateMessage(final String messagePayload) {
        try {
            return xmlUtils.<CoordinateMessage>parseXml(
                messagePayload,
                new Class[]{CoordinateMessage.class, ServiceProperties.class}
            ).orElseThrow(() -> new SsrException("Invalid CoordinateMessage"));
        } catch (Exception e) {
            log.error("Unable to process request with personal document application: {}", e.getMessage());
            personalDocumentApplicationElkNotificationService.sendToBk(messagePayload);
            throw e;
        }
    }
}
