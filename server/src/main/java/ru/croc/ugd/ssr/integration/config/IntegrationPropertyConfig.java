package ru.croc.ugd.ssr.integration.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Конфиг, инкапсулирующий все настройки по интеграции.
 */
@Component
@Getter
public class IntegrationPropertyConfig {

    //TODO перенести в IntegrationProperties

    @Value("${ibm.mq.queue.elk.out}")
    private String elkNotificationQueueName;

    @Value("${ibm.mq.queue.elk-ms.out}")
    private String elkMsNotificationQueueName;

    @Value("${ibm.mq.queue.elk-ms.statusOut}")
    private String elkMsNotificationStatusQueueName;

    @Value("${integration.mos-ru.get-sso-id.url}")
    private String url;

    @Value("${integration.mos-ru.get-sso-id.rest-url}")
    private String restUrl;

    @Value("${integration.mos-ru.get-sso-id.rest-token}")
    private String restToken;

    @Value("${integration.mos-ru.get-sso-id.token}")
    private String token;

    @Value("${integration.mos-ru.get-sso-id.system-info.from}")
    private String systemInfoFrom;

    @Value("${integration.mos-ru.get-sso-id.system-info.to}")
    private String systemInfoTo;

    @Value("${integration.asur.eno.code}")
    private String asurEnoCode;

    @Value("${integration.asur.allowed-request-initiators}")
    private String allowedRequestInitiators;

    @Value("${integration.etp-mv.eno.code}")
    private String etpMvEnoCode;

    @Value("${integration.asur.eno.service}")
    private String asurEnoService;

    @Value("${integration.etp-mv.eno.service.startRenovationNotification}")
    private String etpMvStartRenovationNotificationEnoService;

    @Value("${integration.etp-mv.eno.service.offerLetterNotification}")
    private String etpMvOfferLetterNotificationEnoService;

    @Value("${integration.etp-mv.eno.service.agreementRefusalNotification}")
    private String etpMvAgreementRefusalNotificationEnoService;

    @Value("${integration.etp-mv.eno.service.flatInspectionNotification}")
    private String etpMvFlatInspectionNotificationEnoService;

    @Value("${integration.etp-mv.eno.service.agreementNotification}")
    private String etpMvAgreementNotificationEnoService;

    @Value("${integration.etp-mv.eno.service.refusalNotification}")
    private String etpMvRefusalNotificationEnoService;

    @Value("${integration.etp-mv.eno.service.ressetelmentBegining}")
    private String ressetelmentBegining;

    @Value("${integration.etp-mv.eno.service.fixDefectsNotification}")
    private String eptFixDefectsNotificationEnoService;

    @Value("${integration.etp-mv.eno.service.keyIssue}")
    private String keyIssue;

    @Value("${integration.etp-mv.eno.service.releaseFlatNotification}")
    private String releaseFlat;

    @Value("${integration.etp-mv.eno.service.tradeAdditionNotification}")
    private String tradeAddition;

    @Value("${integration.etp-mv.eno.service.mfr.resettlementInfo}")
    private String mfrResettlementInfo;

    @Value("${integration.etp-mv.eno.service.mfr.administrativeDocuments}")
    private String mfrAdministrativeDocuments;

    @Value("${integration.etp-mv.eno.service.mfr.contracts}")
    private String mfrContracts;

    @Value("${integration.etp-mv.eno.service.mfr.contractProjects}")
    private String mfrContractProjects;

    @Value("${integration.etp-mv.eno.service.mfr.personInfo}")
    private String mfrPersonInfo;

    @Value("${integration.etp-mv.eno.service.mfr.releaseFlat}")
    private String mfrReleaseFlat;

    @Value("${integration.etp-mv.eno.service.contractReadyNotification}")
    private String etpMvContractReadyNotificationEnoService;

    @Value("${integration.etp-mv.eno.service.rsmObjectResponse}")
    private String rsmObjectResponse;

    @Value("${integration.etp-mv.eno.service.rsmObjectRequest:888007}")
    private String rsmObjectRequest;

    @Value("${integration.chedDownloadUrl}")
    private String chedDownloadUrl;

    @Value("${integration.etp-mv.eno.service.signActStatus:880086}")
    private String signActStatus;

    @Value("${integration.etp-mv.eno.service.disabledPerson}")
    private String disabledPerson;

    @Value("${integration.etp-mv.eno.service.flatLayout}")
    private String flatLayout;

    @Value("${integration.etp-mv.eno.service.affairCollation:888005}")
    private String affairCollation;

    @Value("${integration.etp-mv.eno.service.courtInfo:888006}")
    private String courtInfo;

    @Value("${integration.etp-mv.defaultServiceCode:880000}")
    private String defaultServiceCode;
}
