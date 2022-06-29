package ru.croc.ugd.ssr.mq.interop;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Настройки очередей.
 */
@ConfigurationProperties(prefix = QueueProperties.PREFIX)
@Getter
@Setter
public class QueueProperties {

    /**
     * Префикс свойств mq.
     */
    public static final String PREFIX = "ugd.ssr.queue";

    /**
     * Очередь для запросов сведений о жителях.
     */
    private String personRequest = "RENOVATION.ETP.PERSONS_REQ";

    /**
     * Очередь для передачи сведений о начале переселения.
     */
    private String personBuildingsRequest = "RENOVATION.ETP.BUILDINGS_REQ";

    /**
     * Очередь для отправки статусов по отправленным письмам в ДГИ.
     */
    private String offerLetterElkStatusRequest = "RENOVATION.ETP.LETTERS_DEL_REQ";

    /**
     * Очередь для отправки статусов о просмотре квартир в ДГИ.
     */
    private String flatViewStatusRequest = "RENOVATION.ETP.APT_VIEW";

    /**
     * Очередь для Сведения о выдаче ключей от новой квартиры в АИС РСМ.
     */
    private String keyIssueRequest = "RENOVATION.ETP.APT_KEYS";

    /**
     * Очередь для отправки статусов подачи заявления на согласие/отказ.
     */
    private String statementStatusRequest = "RENOVATION.ETP.APT_CONCENT";

    /**
     * Очередь для отправки сведений об освобождении квартиры.
     */
    private String flatFreeRequest = "RENOVATION.ETP.APT_RELEASE";

    /**
     * Данные о статусе доставки уведомления в ЕЛК о готовности проекта договора.
     */
    private String contractReadyElkStatusRequest = "RENOVATION.ETP.APT_DRAFT_DEL";

    /**
     * Данные о статусе доставки уведомления в ЕЛК о подписанном договоре.
     */
    private String signedContractElkStatusRequest = "RENOVATION.ETP.APT_COURT_DEL";

    /**
     * Данные о подписании договора.
     */
    private String signedContract = "RENOVATION.ETP.APT_COURT_DGP";

    /**
     * Очередь для приема заявок на комиссионный осмотр.
     */
    private String commissionInspectionRequest;

    /**
     * BK Очередь для заявок на комиссионный осмотр.
     */
    private String commissionInspectionBk;

    /**
     * Очередь для дополнительных действий с МПГУ.
     */
    private String commissionInspectionStatusInc;

    /**
     * Очередь для отправки статусов на МПГУ.
     */
    private String commissionInspectionStatusOut;

    /**
     * BK очередь для статусов по комиссионному осмотру.
     */
    private String commissionInspectionStatusBk;

    /**
     * Очередь для приема заявок на приём к нотариусу.
     */
    private String notaryApplicationRequest;
    /**
     * BK Очередь для заявок на приём к нотариусу.
     */
    private String notaryApplicationBk;
    /**
     * Очередь для дополнительных действий с МПГУ.
     */
    private String notaryApplicationStatusInc;
    /**
     * Очередь для отправки статусов на МПГУ.
     */
    private String notaryApplicationStatusOut;
    /**
     * BK очередь для статусов заявок на приём к нотариусу.
     */
    private String notaryApplicationStatusBk;

    /**
     * Данные о подписании акта.
     */
    private String signedAct = "RENOVATION.ETP.APT_FREEDOM";

    /**
     * Очередь для приема заявок на запись на заключение договора.
     */
    private String contractAppointmentRequest;
    /**
     * Очередь для дополнительных действий с МПГУ.
     */
    private String contractAppointmentStatusInc;
    /**
     * Очередь для отправки статусов на МПГУ.
     */
    private String contractAppointmentStatusOut;
    /**
     * BK очередь для статусов заявок на запись на заключение договора.
     */
    private String contractAppointmentStatusBk;
    /**
     * BK Очередь для заявок на запись на заключение договора.
     */
    private String contractAppointmentBk;

    /**
     * Очередь для приема заявок на запись на осмотр квартиры.
     */
    private String flatAppointmentRequest;
    /**
     * Очередь для дополнительных действий с МПГУ.
     */
    private String flatAppointmentStatusInc;
    /**
     * Очередь для отправки статусов на МПГУ.
     */
    private String flatAppointmentStatusOut;
    /**
     * BK очередь для статусов заявок на запись на осмотр квартиры.
     */
    private String flatAppointmentStatusBk;
    /**
     * BK Очередь для заявок на запись на осмотр квартиры.
     */
    private String flatAppointmentBk;

    /**
     * Очередь для приема заявлений на предоставление документов.
     */
    private String personalDocumentApplicationRequest;
    /**
     * BK Очередь для заявлений на предоставление документов.
     */
    private String personalDocumentApplicationBk;
    /**
     * Очередь для отправки статусов на МПГУ.
     */
    private String personalDocumentApplicationStatusOut;

    /**
     * Данные о статусе доставки уведомления в ЕЛК о распорядительном документе.
     */
    private String administrativeDocumentsElkStatusRequest;

    /**
     * Очередь для приема заявок помощи о переселении.
     */
    private String shippingRequests;

    private String shippingRequestsBk;

    /**
     * Очередь для отправки статусов о переселении.
     */
    private String shippingStatusesOut;

    /**
     * Очередь для приема статусов о переселении.
     */
    private String shippingStatusesInc;

    private String shippingStatusesIncBk;

    /**
     * Данные об актах об устранении дефектов.
     */
    private String defectEliminationActRequest;
    /**
     * Данные о назначении/переносе плановой даты устранения дефектов.
     */
    private String defectEliminationTransferRequest;
    /**
     * Данные об устранении дефектов/согласиях жителей с устранением дефектов.
     */
    private String defectEliminationAgreementRequest;
}
