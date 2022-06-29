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
import ru.croc.ugd.ssr.integration.service.flows.mfr.dgptomfr.ReleaseFlatInfoMfrFlowService;
import ru.croc.ugd.ssr.integration.util.MessageUtils;
import ru.croc.ugd.ssr.integration.util.XmlUtils;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPFlatFreeType;
import ru.croc.ugd.ssr.mq.interop.MqSender;
import ru.croc.ugd.ssr.mq.interop.QueueProperties;
import ru.croc.ugd.ssr.service.PersonDocumentService;

/**
 * Сервис 10 потока. Сведения об освобождении квартир.
 */
@Service
@RequiredArgsConstructor
public class FlatFreeFlowsService {

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

    private final ReleaseFlatInfoMfrFlowService releaseFlatInfoMfrFlowService;

    /**
     * Рассылка сведений о начале расселения.
     *
     * @param info сообщение
     * @param id   ид жителя
     */
    public void sendFlatFree(SuperServiceDGPFlatFreeType info, String id) {
        final String eno = enoCreator.generateEtpMvEnoNumber(
            propertyConfig.getReleaseFlat(),
            EnoSequenceCode.UGD_SSR_ENO_DGI_FLAT_FREE_SEQ
        );
        final String taskMessage = messageUtils.createCoordinateTaskMessage(eno, info);
        xmlUtils.writeXmlFile(taskMessage, integrationProperties.getXmlExportFlowTenth());
        mqSender.send(queueProperties.getFlatFreeRequest(), taskMessage);

        // Найдем жителя и добавим запись об отправке потока
        PersonDocument personDocument = personDocumentService.fetchDocument(id);
        PersonType personData = personDocument.getDocument().getPersonData();
        final IntegrationLogBuilder logBuilder = beanFactory.getBean(IntegrationLogBuilder.class);
        logBuilder
            .addEno(eno)
            .addEventId("SuperServiceDGPFlatFree")
            .addFileId(xmlUtils.saveXmlToAlfresco(taskMessage, personDocument.getFolderId()));
        if (personData.getIntegrationLog() == null) {
            personData.setIntegrationLog(new IntegrationLog());
        }
        personData.getIntegrationLog().getItem().add(logBuilder.build());

        personDocumentService.updateDocument(personDocument.getId(), personDocument, true, true, "");
        flatResettlementStatusUpdateService.updateFlatResettlementStatus(personData, "9");

        releaseFlatInfoMfrFlowService.sendReleaseFlatInfo(info);
    }
}
