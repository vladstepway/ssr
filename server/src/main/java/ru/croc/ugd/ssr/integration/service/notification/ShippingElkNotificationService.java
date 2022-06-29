package ru.croc.ugd.ssr.integration.service.notification;

import ru.croc.ugd.ssr.dto.shipping.ShippingFlowStatus;
import ru.croc.ugd.ssr.model.ShippingApplicationDocument;

/**
 * ShippingElkNotificationService.
 */
public interface ShippingElkNotificationService {

    /**
     * Sends shipping status to ELK.
     * @param status status
     */
    void sendStatus(final ShippingFlowStatus status);

    /**
     * Sends shipping status to ELK.
     * @param status status
     * @param document document
     */
    void sendStatus(final ShippingFlowStatus status, final ShippingApplicationDocument document);

    /**
     * Отправляет сообщение в очередь BK.
     * @param message Сообщение.
     */
    void sendStatusToBk(final String message);

    /**
     * Отправляет сообщение в очередь BK.
     * @param message Сообщение.
     */
    void sendToBk(final String message);
}
