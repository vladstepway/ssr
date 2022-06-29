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
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPViewStatusType;
import ru.croc.ugd.ssr.mq.interop.MqSender;
import ru.croc.ugd.ssr.mq.interop.QueueProperties;
import ru.croc.ugd.ssr.service.PersonDocumentService;

import java.util.List;

/**
 * Сервис для отправки в дги сообщений по 5 потоку.
 * Статус просмотра квартиры в заселяемом доме.
 */
@Service
@RequiredArgsConstructor
public class FlatViewStatusService {

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

    public void sendFlatViewStatus(final SuperServiceDGPViewStatusType info) {
        final List<PersonDocument> personDocumentList = personDocumentService.fetchByPersonIdAndAffairId(
            info.getPersonId(), info.getAffairId()
        );

        sendFlatViewStatus(info, personDocumentList.get(0));
    }

    /**
     * Рассылка сведений о начале расселения.
     *
     * @param info сообщение
     */
    public void sendFlatViewStatus(final SuperServiceDGPViewStatusType info, final PersonDocument personDocument) {
        final String eno = enoCreator.generateEtpMvEnoNumber(
            propertyConfig.getEtpMvFlatInspectionNotificationEnoService(),
            EnoSequenceCode.UGD_SSR_ENO_DGI_FLAT_VIEW_STATUS_SEQ
        );
        final String taskMessage = messageUtils.createCoordinateTaskMessage(eno, info);
        xmlUtils.writeXmlFile(taskMessage, integrationProperties.getXmlExportFlowFifth());
        mqSender.send(queueProperties.getFlatViewStatusRequest(), taskMessage);

        final PersonType personData = personDocument.getDocument().getPersonData();
        final IntegrationLogBuilder logBuilder = beanFactory.getBean(IntegrationLogBuilder.class);
        logBuilder
            .addEno(eno)
            .addEventId("SuperServiceDGPViewStatus")
            .addFileId(xmlUtils.saveXmlToAlfresco(taskMessage, personDocument.getFolderId()));
        if (personData.getIntegrationLog() == null) {
            personData.setIntegrationLog(new IntegrationLog());
        }
        personData.getIntegrationLog().getItem().add(logBuilder.build());

        personDocumentService.updateDocument(personDocument);
        flatResettlementStatusUpdateService.updateFlatResettlementStatus(personData, "3");
    }

}
