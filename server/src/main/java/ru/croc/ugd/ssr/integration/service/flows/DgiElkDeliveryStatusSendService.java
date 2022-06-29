package ru.croc.ugd.ssr.integration.service.flows;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.IntegrationLog;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.builder.IntegrationLogBuilder;
import ru.croc.ugd.ssr.integration.config.IntegrationProperties;
import ru.croc.ugd.ssr.integration.config.IntegrationPropertyConfig;
import ru.croc.ugd.ssr.integration.eno.EnoCreator;
import ru.croc.ugd.ssr.integration.eno.EnoSequenceCode;
import ru.croc.ugd.ssr.integration.util.MessageUtils;
import ru.croc.ugd.ssr.integration.util.XmlUtils;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPAdministrativeDocumentAgrSendStatusType;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPContractHandStatusType;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPLetterHandStatusType;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPSignAgrSendStatusType;
import ru.croc.ugd.ssr.mq.interop.MqSender;
import ru.croc.ugd.ssr.mq.interop.QueueProperties;
import ru.croc.ugd.ssr.service.PersonDocumentService;

/**
 * Сервис по отправке статусов из ЕЛК в ДГИ.
 */
@Service
@RequiredArgsConstructor
public class DgiElkDeliveryStatusSendService {

    private final IntegrationProperties integrationProperties;

    private final IntegrationPropertyConfig propertyConfig;

    private final QueueProperties queueProperties;

    private final MessageUtils messageUtils;

    private final MqSender mqSender;

    private final XmlUtils xmlUtils;

    private final BeanFactory beanFactory;

    private final EnoCreator enoCreator;

    private final PersonDocumentService personDocumentService;

    /**
     * Отправить статус в ДГИ по письму с предложением.
     *
     * @param personDocument айди жителя
     * @param letterId       айди письма
     * @param sendStatus     статус
     */
    public void sendElkOfferLetterStatusToDgi(PersonDocument personDocument, String letterId, String sendStatus) {
        SuperServiceDGPLetterHandStatusType letterHandStatus = new SuperServiceDGPLetterHandStatusType();
        letterHandStatus.setLetterId(letterId);
        letterHandStatus.setSendStatus(sendStatus);

        final String eno = enoCreator.generateEtpMvEnoNumber(
            propertyConfig.getEtpMvOfferLetterNotificationEnoService(),
            EnoSequenceCode.UGD_SSR_ENO_DGI_LETTERS_DEL_REQ
        );
        String taskMessage = messageUtils.createCoordinateTaskMessage(eno, letterHandStatus);

        xmlUtils.writeXmlFile(taskMessage, integrationProperties.getXmlExportFlowSecond());
        mqSender.send(queueProperties.getOfferLetterElkStatusRequest(), taskMessage);

        PersonType personData = personDocument.getDocument().getPersonData();
        // Добавим запись об отправке потока
        final IntegrationLogBuilder logBuilder = beanFactory.getBean(IntegrationLogBuilder.class);
        logBuilder
            .addEno(eno)
            .addEventId("SuperServiceDGPLetterHandStatus")
            .addFileId(xmlUtils.saveXmlToAlfresco(taskMessage, personDocument.getFolderId()));
        if (personData.getIntegrationLog() == null) {
            personData.setIntegrationLog(new IntegrationLog());
        }
        personData.getIntegrationLog().getItem().add(logBuilder.build());

        personDocumentService.updateDocument(
            personDocument.getId(), personDocument, true, true, "Отправка статуса 2 уведомления"
        );
    }

    /**
     * Отправить статус в ДГИ о подписанном договоре.
     *
     * @param personDocument айди жителя
     * @param orderId        айди договора
     * @param sendStatus     статус
     */
    public void sendElkSignedContractStatusToDgi(PersonDocument personDocument, String orderId, String sendStatus) {
        PersonType personData = personDocument.getDocument().getPersonData();
        SuperServiceDGPSignAgrSendStatusType signAgrSendStatus = new SuperServiceDGPSignAgrSendStatusType();
        signAgrSendStatus.setAffairId(personData.getAffairId());
        signAgrSendStatus.setOrderId(orderId);
        signAgrSendStatus.setPersonId(personData.getPersonID());
        signAgrSendStatus.setSendStatus(sendStatus);

        final String eno = enoCreator.generateEtpMvEnoNumber(
            propertyConfig.getReleaseFlat(),
            EnoSequenceCode.UGD_SSR_ENO_DGI_APT_COURT_DEL
        );
        String taskMessage = messageUtils.createCoordinateTaskMessage(eno, signAgrSendStatus);

        xmlUtils.writeXmlFile(taskMessage, integrationProperties.getXmlExportFlowEighth());
        mqSender.send(queueProperties.getSignedContractElkStatusRequest(), taskMessage);

        // Добавим запись об отправке потока
        final IntegrationLogBuilder logBuilder = beanFactory.getBean(IntegrationLogBuilder.class);
        logBuilder
            .addEno(eno)
            .addEventId("SuperServiceDGPSignAgrSendStatus")
            .addFileId(xmlUtils.saveXmlToAlfresco(taskMessage, personDocument.getFolderId()));
        if (personData.getIntegrationLog() == null) {
            personData.setIntegrationLog(new IntegrationLog());
        }
        personData.getIntegrationLog().getItem().add(logBuilder.build());

        personDocumentService.updateDocument(
            personDocument.getId(), personDocument, true, true, "Отправка статуса 8 уведомления"
        );
    }

    /**
     * Отправить статус в ДГИ о готовности договора.
     *
     * @param personDocument айди жителя
     * @param orderId        айди договора
     * @param sendStatus     статус
     */
    public void sendElkContractReadyStatusToDgi(PersonDocument personDocument, String orderId, String sendStatus) {
        PersonType personData = personDocument.getDocument().getPersonData();
        SuperServiceDGPContractHandStatusType contractHandStatus = new SuperServiceDGPContractHandStatusType();
        contractHandStatus.setAffairId(personData.getAffairId());
        contractHandStatus.setOrderId(orderId);
        contractHandStatus.setPersonId(personData.getPersonID());
        contractHandStatus.setStatus(sendStatus);

        final String eno = enoCreator.generateEtpMvEnoNumber(
            propertyConfig.getEtpMvContractReadyNotificationEnoService(),
            EnoSequenceCode.UGD_SSR_ENO_DGI_APT_DRAFT_DEL
        );
        String taskMessage = messageUtils.createCoordinateTaskMessage(eno, contractHandStatus);

        xmlUtils.writeXmlFile(taskMessage, integrationProperties.getXmlExportFlowSeventh());
        mqSender.send(queueProperties.getContractReadyElkStatusRequest(), taskMessage);

        // Добавим запись об отправке потока
        final IntegrationLogBuilder logBuilder = beanFactory.getBean(IntegrationLogBuilder.class);
        logBuilder
            .addEno(eno)
            .addEventId("SuperServiceDGPContractHandStatus")
            .addFileId(xmlUtils.saveXmlToAlfresco(taskMessage, personDocument.getFolderId()));
        if (personData.getIntegrationLog() == null) {
            personData.setIntegrationLog(new IntegrationLog());
        }
        personData.getIntegrationLog().getItem().add(logBuilder.build());

        personDocumentService.updateDocument(
            personDocument.getId(), personDocument, true, true, "Отправка статуса 7 уведомления"
        );
    }

    /**
     * Отправить статус в ДГИ по потоку РД.
     *
     * @param personDocument айди жителя
     * @param letterId        letterId
     * @param sendStatus     статус
     */
    public void sendAdStatusToDgi(PersonDocument personDocument, String letterId, String sendStatus) {
        PersonType personData = personDocument.getDocument().getPersonData();
        SuperServiceDGPAdministrativeDocumentAgrSendStatusType sendStatusType
            = new SuperServiceDGPAdministrativeDocumentAgrSendStatusType();
        sendStatusType.setAffairId(personData.getAffairId());
        sendStatusType.setLetterId(letterId);
        sendStatusType.setPersonId(personData.getPersonID());
        sendStatusType.setSendStatus(sendStatus);

        final String eno = enoCreator.generateEtpMvEnoNumber(
            propertyConfig.getEtpMvContractReadyNotificationEnoService(),
            EnoSequenceCode.UGD_SSR_ENO_DGI_APT_DRAFT_DEL
        );
        String taskMessage = messageUtils.createCoordinateTaskMessage(eno, sendStatusType);

        xmlUtils.writeXmlFile(taskMessage, integrationProperties.getXmlExportAdministrativeDocuments());
        mqSender.send(queueProperties.getAdministrativeDocumentsElkStatusRequest(), taskMessage);

        final IntegrationLogBuilder logBuilder = beanFactory.getBean(IntegrationLogBuilder.class);
        logBuilder
            .addEno(eno)
            .addEventId("SuperServiceDGPAdministrativeDocumentAgrSendStatus")
            .addFileId(xmlUtils.saveXmlToAlfresco(taskMessage, personDocument.getFolderId()));
        if (personData.getIntegrationLog() == null) {
            personData.setIntegrationLog(new IntegrationLog());
        }
        personData.getIntegrationLog().getItem().add(logBuilder.build());

        personDocumentService.updateDocument(
            personDocument.getId(),
            personDocument,
            true,
            true,
            "Отправка статуса 13 уведомления"
        );
    }

}
