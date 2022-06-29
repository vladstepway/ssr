package ru.croc.ugd.ssr.service.commissioninspection;

import ru.croc.ugd.ssr.ApartmentInspectionType;
import ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus;
import ru.croc.ugd.ssr.model.commissioninspection.CommissionInspectionDocument;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateMessage;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateStatusMessage;

/**
 * CommissionInspectionService.
 */
public interface CommissionInspectionService {

    /**
     * Завершить бпм процесс.
     * @param commissionInspectionDocument документ КО
     */
    void finishBpmProcess(final CommissionInspectionDocument commissionInspectionDocument);

    /**
     * Регистрация заявление на КО.
     * @param coordinateMessage сообщение из очереди
     */
    void processRegistration(final CoordinateMessage coordinateMessage);

    /**
     * Withdraw commission inspection.
     * @param commissionInspectionId commissionInspectionId.
     */
    void withdraw(final String commissionInspectionId, final String reason);

    /**
     * Withdraw commission inspection.
     * @param coordinateStatus coordinateStatus.
     */
    void withdraw(final CoordinateStatusMessage coordinateStatus);

    /**
     * Process change defects request.
     * @param coordinateStatus coordinateStatus
     */
    void processChangeDefectsRequest(final CoordinateStatusMessage coordinateStatus);

    /**
     * Process move date request.
     * @param coordinateStatus coordinateStatus.
     */
    void processMoveDateRequest(final CoordinateStatusMessage coordinateStatus);

    /**
     * Send move date request accepted status.
     * @param commissionInspectionDocument commissionInspectionDocument
     * @param isPrimaryInspection isPrimaryInspection
     */
    void sendMoveDateRequestAcceptedStatus(
        final CommissionInspectionDocument commissionInspectionDocument, final boolean isPrimaryInspection
    );

    /**
     * Send flow status.
     * @param commissionInspectionDocument commissionInspectionDocument
     * @param flowStatus flowStatus
     */
    void sendFlowStatus(
        final CommissionInspectionDocument commissionInspectionDocument,
        final CommissionInspectionFlowStatus flowStatus
    );

    /**
     * Send flow status with history update.
     * @param commissionInspectionDocument commissionInspectionDocument
     * @param flowStatus flowStatus
     */
    void sendFlowStatusWithHistoryUpdate(
        final CommissionInspectionDocument commissionInspectionDocument,
        final CommissionInspectionFlowStatus flowStatus
    );

    /**
     * Process defects fixed status.
     * @param commissionInspectionId commissionInspectionId
     */
    void processDefectsFixed(final String commissionInspectionId);

    /**
     * Process defects found.
     * @param commissionInspectionId commissionInspectionId
     */
    void processDefectsFound(final String commissionInspectionId);

    /**
     * Process defects not found.
     * @param commissionInspectionId commissionInspectionId
     */
    void processDefectsNotFound(final String commissionInspectionId);

    /**
     * Process act closure.
     * @param commissionInspectionId commissionInspectionId
     * @param apartmentInspection apartmentInspection
     */
    void processActClosure(
        final String commissionInspectionId,
        final ApartmentInspectionType apartmentInspection
    );

    /**
     * Process act closure without consent.
     * @param commissionInspectionId commissionInspectionId
     * @param completionReasonCode completionReasonCode
     * @param completionReason completionReason
     */
    void processActClosureWithoutConsent(
        final String commissionInspectionId, final String completionReasonCode, final String completionReason
    );

    /**
     * Process no call.
     * @param commissionInspectionDocument commissionInspectionDocument
     * @param noCallStatus noCallStatus
     */
    void processNoCall(
        final CommissionInspectionDocument commissionInspectionDocument,
        final CommissionInspectionFlowStatus noCallStatus
    );

    /**
     * Actualize task candidates.
     * @param commissionInspectionDocument commissionInspectionDocument
     */
    void actualizeTaskCandidates(final CommissionInspectionDocument commissionInspectionDocument);

    /**
     * Отправка статуса при продлении срока устранения дефектов.
     * @param commissionInspectionId commissionInspectionId
     */
    void defectsProlongation(final String commissionInspectionId);
}
