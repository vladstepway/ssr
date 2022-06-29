package ru.croc.ugd.ssr.integration.service.flows;

import static java.util.Objects.nonNull;

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
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPKeyIssueType;
import ru.croc.ugd.ssr.mq.interop.MqSender;
import ru.croc.ugd.ssr.mq.interop.QueueProperties;
import ru.croc.ugd.ssr.service.ChedFileService;
import ru.croc.ugd.ssr.service.PersonDocumentService;

/**
 * Сведения о выдаче ключей от новой квартиры в АИС РСМ.
 */
@Service
@RequiredArgsConstructor
public class KeyIssueFlowService {

    @Value("${integration.ched.keysForTheNewApartmentCode:12163}")
    private String keysForTheNewApartmentCode;

    @Value("${integration.ched.keysForTheNewApartmentDocType:CertificateOfIssuanceOfKeysForTheNewApartmentOfTheMCN}")
    private String keysForTheNewApartmentDocType;

    private final ChedFileService chedFileService;

    private final MessageUtils messageUtils;

    private final XmlUtils xmlUtils;

    private final MqSender mqSender;

    private final QueueProperties queueProperties;

    private final IntegrationProperties integrationProperties;

    private final IntegrationPropertyConfig propertyConfig;

    private final BeanFactory beanFactory;

    private final EnoCreator enoCreator;

    private final PersonDocumentService personDocumentService;

    private final FlatResettlementStatusUpdateService flatResettlementStatusUpdateService;

    private final RemovableStatusUpdateService removableStatusUpdateService;

    /**
     * Сведения о выдаче ключей от новой квартиры в АИС РСМ.
     *
     * @param info сообщение
     * @param id   ид жителя
     */
    public void sendKeyIssue(SuperServiceDGPKeyIssueType info, String id) {
        removableStatusUpdateService.updateStatusFlat(info);

        final String eno = enoCreator.generateEtpMvEnoNumber(
            propertyConfig.getReleaseFlat(),
            EnoSequenceCode.UGD_SSR_ENO_DGI_KEY_ISSUE_SEQ
        );
        if (nonNull(info.getActLink())) {
            info.setActLink(
                chedFileService.uploadFileToChed(
                    info.getActLink(), keysForTheNewApartmentCode, keysForTheNewApartmentDocType
                )
            );
        }
        final String taskMessage = messageUtils.createCoordinateTaskMessage(eno, info);
        xmlUtils.writeXmlFile(taskMessage, integrationProperties.getXmlExportFlowNinth());
        mqSender.send(queueProperties.getKeyIssueRequest(), taskMessage);

        // Найдем жителя и добавим запись об отправке потока
        PersonDocument personDocument = personDocumentService.fetchDocument(id);
        PersonType personData = personDocument.getDocument().getPersonData();
        final IntegrationLogBuilder logBuilder = beanFactory.getBean(IntegrationLogBuilder.class);
        logBuilder
            .addEno(eno)
            .addEventId("SuperServiceDGPKeyIssue")
            .addFileId(xmlUtils.saveXmlToAlfresco(taskMessage, personDocument.getFolderId()));
        if (personData.getIntegrationLog() == null) {
            personData.setIntegrationLog(new IntegrationLog());
        }
        personData.getIntegrationLog().getItem().add(logBuilder.build());

        personDocumentService.updateDocument(personDocument.getId(), personDocument, true, true, "");
        flatResettlementStatusUpdateService.updateFlatResettlementStatus(personData, "7");
    }

}
