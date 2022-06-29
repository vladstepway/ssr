package ru.croc.ugd.ssr.integration.service.flows;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.ApartmentInspectionType;
import ru.croc.ugd.ssr.IntegrationLog;
import ru.croc.ugd.ssr.builder.IntegrationLogBuilder;
import ru.croc.ugd.ssr.integration.config.IntegrationProperties;
import ru.croc.ugd.ssr.integration.config.IntegrationPropertyConfig;
import ru.croc.ugd.ssr.integration.eno.EnoCreator;
import ru.croc.ugd.ssr.integration.util.MessageUtils;
import ru.croc.ugd.ssr.integration.util.XmlUtils;
import ru.croc.ugd.ssr.model.ApartmentInspectionDocument;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPDefectEliminationActType;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPDefectEliminationAgreementType;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPDefectEliminationTransferType;
import ru.croc.ugd.ssr.mq.interop.MqSender;
import ru.croc.ugd.ssr.mq.interop.QueueProperties;
import ru.croc.ugd.ssr.service.ApartmentInspectionService;

/**
 * Потоки по актам об устранении дефектов.
 */
@Service
public class DefectEliminationFlowsService {

    private final EnoCreator enoCreator;
    private final IntegrationPropertyConfig propertyConfig;
    private final MessageUtils messageUtils;
    private final XmlUtils xmlUtils;
    private final IntegrationProperties integrationProperties;
    private final MqSender mqSender;
    private final QueueProperties queueProperties;
    private final ApartmentInspectionService apartmentInspectionService;

    public DefectEliminationFlowsService(
        final EnoCreator enoCreator,
        final IntegrationPropertyConfig propertyConfig,
        final MessageUtils messageUtils,
        final XmlUtils xmlUtils,
        final IntegrationProperties integrationProperties,
        final MqSender mqSender,
        final QueueProperties queueProperties,
        @Lazy final ApartmentInspectionService apartmentInspectionService
    ) {
        this.enoCreator = enoCreator;
        this.propertyConfig = propertyConfig;
        this.messageUtils = messageUtils;
        this.xmlUtils = xmlUtils;
        this.integrationProperties = integrationProperties;
        this.mqSender = mqSender;
        this.queueProperties = queueProperties;
        this.apartmentInspectionService = apartmentInspectionService;
    }

    public void sendDefectEliminationAct(
        final ApartmentInspectionDocument apartmentInspectionDocument,
        final SuperServiceDGPDefectEliminationActType defectEliminationAct
    ) {
        final String eno = enoCreator.generateEtpMvNotificationEnoNumber(
            propertyConfig.getEptFixDefectsNotificationEnoService()
        );
        final String taskMessage = messageUtils.createCoordinateTaskMessage(eno, defectEliminationAct);
        xmlUtils.writeXmlFile(taskMessage, integrationProperties.getXmlExportDefectEliminationAct());
        mqSender.send(queueProperties.getDefectEliminationActRequest(), taskMessage);

        final ApartmentInspectionType apartmentInspectionData =
            apartmentInspectionDocument.getDocument().getApartmentInspectionData();
        final IntegrationLogBuilder logBuilder = new IntegrationLogBuilder()
            .addEno(eno)
            .addEventId("SuperServiceDGPDefectEliminationAct")
            .addFileId(xmlUtils.saveXmlToAlfresco(taskMessage, apartmentInspectionDocument.getFolderId()));
        if (apartmentInspectionData.getIntegrationLog() == null) {
            apartmentInspectionData.setIntegrationLog(new IntegrationLog());
        }
        apartmentInspectionData.getIntegrationLog().getItem().add(logBuilder.build());

        apartmentInspectionService.updateDocument(apartmentInspectionDocument, "SuperServiceDGPDefectEliminationAct");
    }

    public void sendDefectEliminationTransfer(
        final ApartmentInspectionDocument apartmentInspectionDocument,
        final SuperServiceDGPDefectEliminationTransferType defectEliminationTransfer
    ) {
        final String eno = enoCreator.generateEtpMvNotificationEnoNumber(
            propertyConfig.getEptFixDefectsNotificationEnoService()
        );
        final String taskMessage = messageUtils.createCoordinateTaskMessage(eno, defectEliminationTransfer);
        xmlUtils.writeXmlFile(taskMessage, integrationProperties.getXmlExportDefectEliminationTransfer());
        mqSender.send(queueProperties.getDefectEliminationTransferRequest(), taskMessage);

        final ApartmentInspectionType apartmentInspectionData =
            apartmentInspectionDocument.getDocument().getApartmentInspectionData();
        final IntegrationLogBuilder logBuilder = new IntegrationLogBuilder()
            .addEno(eno)
            .addEventId("SuperServiceDGPDefectEliminationTransfer")
            .addFileId(xmlUtils.saveXmlToAlfresco(taskMessage, apartmentInspectionDocument.getFolderId()));
        if (apartmentInspectionData.getIntegrationLog() == null) {
            apartmentInspectionData.setIntegrationLog(new IntegrationLog());
        }
        apartmentInspectionData.getIntegrationLog().getItem().add(logBuilder.build());

        apartmentInspectionService.updateDocument(
            apartmentInspectionDocument, "SuperServiceDGPDefectEliminationTransfer"
        );
    }

    public void sendDefectEliminationAgreement(
        final ApartmentInspectionDocument apartmentInspectionDocument,
        final SuperServiceDGPDefectEliminationAgreementType defectEliminationAgreement
    ) {
        final String eno = enoCreator.generateEtpMvNotificationEnoNumber(
            propertyConfig.getEptFixDefectsNotificationEnoService()
        );
        final String taskMessage = messageUtils.createCoordinateTaskMessage(eno, defectEliminationAgreement);
        xmlUtils.writeXmlFile(taskMessage, integrationProperties.getXmlExportDefectEliminationAgreement());
        mqSender.send(queueProperties.getDefectEliminationAgreementRequest(), taskMessage);

        final ApartmentInspectionType apartmentInspectionData =
            apartmentInspectionDocument.getDocument().getApartmentInspectionData();
        final IntegrationLogBuilder logBuilder = new IntegrationLogBuilder()
            .addEno(eno)
            .addEventId("SuperServiceDGPDefectEliminationAgreement")
            .addFileId(xmlUtils.saveXmlToAlfresco(taskMessage, apartmentInspectionDocument.getFolderId()));
        if (apartmentInspectionData.getIntegrationLog() == null) {
            apartmentInspectionData.setIntegrationLog(new IntegrationLog());
        }
        apartmentInspectionData.getIntegrationLog().getItem().add(logBuilder.build());

        apartmentInspectionService.updateDocument(
            apartmentInspectionDocument, "SuperServiceDGPDefectEliminationAgreement"
        );
    }
}
