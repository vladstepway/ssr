package ru.croc.ugd.ssr.integration.service.flows;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Value;
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
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPStatementStatusType;
import ru.croc.ugd.ssr.mq.interop.MqSender;
import ru.croc.ugd.ssr.mq.interop.QueueProperties;
import ru.croc.ugd.ssr.service.ChedFileService;
import ru.croc.ugd.ssr.service.PersonDocumentService;

/**
 * Отправка статуса подачи заявления на согласие/отказ. 6 поток.
 */
@Service
@RequiredArgsConstructor
public class StatementStatusService {

    @Value("${integration.ched.consentToAnEquivalentApartCode:12017}")
    private String consentToAnEquivalentApartCode;

    @Value("${integration.ched.consentToAnEquivalentApartDocType:ConsentToAnEquivalentApartment}")
    private String consentToAnEquivalentApartDocType;

    @Value("${integration.ched.refusalFromAnEquivalentApartCode:12018}")
    private String refusalFromAnEquivalentApartCode;

    @Value("${integration.ched.refusalFromAnEquivalentApartDocType:RefusalFromAnEquivalentApartment}")
    private String refusalFromAnEquivalentApartDocType;

    private static final String AGREEMENT_STATUS_TYPE = "1";

    private static final String REFUSAL_STATUS_TYPE = "2";

    private final MessageUtils messageUtils;

    private final XmlUtils xmlUtils;

    private final MqSender mqSender;

    private final QueueProperties queueProperties;

    private final IntegrationProperties integrationProperties;

    private final IntegrationPropertyConfig propertyConfig;

    private final ChedFileService chedFileService;

    private final BeanFactory beanFactory;

    private final EnoCreator enoCreator;

    private final PersonDocumentService personDocumentService;

    private final FlatResettlementStatusUpdateService flatResettlementStatusUpdateService;

    /**
     * Рассылка сведений о начале расселения.
     *
     * @param info сообщение
     * @param id   ид жителя
     */
    public void sendStatementStatus(SuperServiceDGPStatementStatusType info, String id) {
        final String guCode = info.getStatementType().equals(AGREEMENT_STATUS_TYPE)
            ? propertyConfig.getEtpMvAgreementNotificationEnoService()
            : propertyConfig.getEtpMvRefusalNotificationEnoService();

        if (info.getFileLink() != null) {
            final String apartDode = info.getStatementType().equals(AGREEMENT_STATUS_TYPE)
                ? consentToAnEquivalentApartCode
                : refusalFromAnEquivalentApartCode;

            final String apartDocType = info.getStatementType().equals(AGREEMENT_STATUS_TYPE)
                ? consentToAnEquivalentApartDocType
                : refusalFromAnEquivalentApartDocType;

            info.setFileLink(
                chedFileService.uploadFileToChed(
                    info.getFileLink(), apartDode, apartDocType
                )
            );
        }
        final String eno = enoCreator.generateEtpMvEnoNumber(
            guCode,
            EnoSequenceCode.UGD_SSR_ENO_DGI_STATEMENT_STATUS_SEQ
        );
        final String taskMessage = messageUtils
            .createCoordinateTaskMessage(eno, info);
        xmlUtils.writeXmlFile(taskMessage, integrationProperties.getXmlExportFlowSixth());
        mqSender.send(queueProperties.getStatementStatusRequest(), taskMessage);

        // Найдем жителя и добавим запись об отправке потока
        PersonDocument personDocument = personDocumentService.fetchDocument(id);
        PersonType personData = personDocument.getDocument().getPersonData();
        final IntegrationLogBuilder logBuilder = beanFactory.getBean(IntegrationLogBuilder.class);
        logBuilder
            .addEno(eno)
            .addEventId("SuperServiceDGPStatementStatus")
            .addFileId(xmlUtils.saveXmlToAlfresco(taskMessage, personDocument.getFolderId()));
        if (personData.getIntegrationLog() == null) {
            personData.setIntegrationLog(new IntegrationLog());
        }
        personData.getIntegrationLog().getItem().add(logBuilder.build());

        personDocumentService.updateDocument(personDocument.getId(), personDocument, true, true, "");
        flatResettlementStatusUpdateService.updateFlatResettlementStatus(personData, "4");
    }
}
