package ru.croc.ugd.ssr.integration.service.notification;

import ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus;
import ru.croc.ugd.ssr.model.commissioninspection.CommissionInspectionDocument;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Сервис отправки сообщений в ЭЛК для КО.
 */
public interface CommissionInspectionElkNotificationService {

    /**
     * Sends commission inspection status to ELK asynchronously.
     *
     * @param status status
     * @param eno Eno
     */
    void sendStatusAsync(final CommissionInspectionFlowStatus status, final String eno);

    /**
     * Sends commission inspection status to ELK asynchronously.
     *
     * @param status status
     * @param document document
     */
    void sendStatusAsync(
        final CommissionInspectionFlowStatus status, final CommissionInspectionDocument document
    );

    /**
     * Sends commission inspection statuses to ELK asynchronously.
     * @param statuses statuses
     * @param document document
     * @return completableFuture
     */
    CompletableFuture<Void> sendStatusAsync(
        final List<CommissionInspectionFlowStatus> statuses, final CommissionInspectionDocument document
    );

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
