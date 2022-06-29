package ru.croc.ugd.ssr.integration.service.notification;

import java.time.LocalDateTime;

/**
 * Сервис для запуска тестовых запросов по интеграции с ЕЛК.
 */
public interface TestElkUserNotificationService {

    /**
     * Тестовый запрос в ЕЛК (начало переселения) по ssoId и id ЦИПа.
     *
     * @param ssoId ssoId
     * @param cipId cipId
     */
    void testStartRenovationElkRequest(String ssoId, String personId, String cipId);

    /**
     * Тестовый запрос в ЕЛК (письмо с предложением).
     *
     * @param ssoId ssoId
     * @param personId personId
     * @param cipId cipId
     * @param letterId letterId
     */
    void testOfferLetterElkRequest(
        final String ssoId, final String personId, final String cipId, final String letterId
    );

    /**
     * testTradeAdditionElkRequest.
     *
     * @param ssoId ssoId
     * @param cipId cipId
     */
    void testTradeAdditionElkRequest(String eventCode, String ssoId, String personDocumentId, String cipId);

    void testTradeAdditionElkRequest(String eventCode, String ssoId, String personDocumentId,
                                     String cipId, String chedId);

    /**
     * Тестовый запрос в ЕЛК (после осмотра квартиры) по ssoId и id ЦИПа.
     *
     * @param ssoId ssoId
     * @param cipId cipId
     */
    void testFlatInspectionElkRequest(String ssoId, String personId, String cipId);

    /**
     * Тестовый запрос в ЕЛК (согласие на квартиру) по ssoId и id ЦИПа.
     *
     * @param ssoId ssoId
     * @param cipId cipId
     */
    void testFlatAgreementElkRequest(String ssoId, String personId, String cipId);

    /**
     * Тестовый запрос в ЕЛК (отказ от квартиры) по ssoId и id ЦИПа.
     *
     * @param ssoId ssoId
     * @param cipId cipId
     */
    void testFlatRefusalElkRequest(String ssoId, String personId, String cipId);

    /**
     * Тестовый запрос в ЕЛК (о подписанном договоре) по ssoId и id ЦИПа.
     *
     * @param ssoId  ssoId
     * @param cipId  cipId
     * @param flatId flatId
     */
    void testSignedContractElkRequest(String ssoId, String personId, String cipId, String flatId);

    /**
     * Тестовый запрос в ЕЛК (о готовности договора) по ssoId и id ЦИПа.
     *
     * @param ssoId ssoId
     * @param cipId cipId
     */
    void testContractReadyElkRequest(String ssoId, String personId, String cipId);

    /**
     * Тестовый запрос в ЕЛК (о готовности договора) по ssoId и id ЦИПа.
     *
     * @param ssoId ssoId
     * @param cipId cipId
     */
    void sendContractReadyForAcquaintingNotification(String ssoId, String personId, String cipId);

    void sendApartmentInspectionNotification(
        final String ssoId,
        final String personDocumentId,
        final String cipId,
        final String apartmentInspectionId
    );

    void sendShippingNotification(
        final String shippingDocumentId,
        final String eventCode,
        final String ssoId,
        final String personDocumentId,
        final LocalDateTime moveDateTime,
        final String oldAddress,
        final String newAddress
    );

    void sendGuardianshipNotification(
        final boolean isAgreement,
        final String ssoId,
        final String personDocumentId,
        final String declineReason
    );

    void sendNotaryNotification(
        final String notaryApplicationDocumentId,
        final String notaryDocumentId,
        final String eventCode,
        final String ssoId,
        final String personDocumentId,
        final String notaryFullName,
        final String notaryPhones,
        final String eno,
        final String addressFrom,
        final String addressesTo,
        final LocalDateTime appointmentDateTime
    );

    void sendNotaryCompensationNotification(
        final String notaryApplicationDocumentId,
        final String notaryDocumentId,
        final String eventCode,
        final String ssoId,
        final String personDocumentId,
        final LocalDateTime appointmentDateTime,
        final String eno
    );

    void sendNotaryCompensationNotification(final String ssoId, final String personDocumentId);

    void sendContractAppointmentNotification(
        final String contactAppointmentDocumentId,
        final String eventCode,
        final String ssoId,
        final String personDocumentId,
        final String cipId,
        final String cancelReason,
        final String addressesTo,
        final LocalDateTime appointmentDateTime
    );

    void sendFlatAppointmentNotification(
        final String flatAppointmentDocumentId,
        final String eventCode,
        final String ssoId,
        final String personDocumentId,
        final String cipId,
        final String cancelReason,
        final LocalDateTime appointmentDateTime
    );

    void sendPersonalDocumentRequestNotification(
        final String ssoId, final String personDocumentId, final String personalDocumentRequestDocumentId
    );

    void sendResidencePlaceRegistrationNotification(final String personDocumentId);

    void sendResidencePlaceRegistrationNotificationByEgrn(final String egrnFlatRequestDocumentId);

    void sendContractDigitalSignRequestNotificationToApplicant(
        final String ssoId,
        final String notifiablePersonDocumentId,
        final String contractDigitalSignDocumentId
    );

    void sendContractDigitalSignRequestNotificationToOwner(
        final String ssoId,
        final String notifiablePersonDocumentId,
        final String requesterPersonDocumentId,
        final String contractDigitalSignDocumentId
    );

    void sendContractDigitalSignRequestNotification(
        final String ssoId,
        final String notifiablePersonDocumentId,
        final String requesterPersonDocumentId,
        final String contractDigitalSignDocumentId,
        final String eventCode
    );
}
