package ru.croc.ugd.ssr.mq.listener;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import ru.croc.ugd.ssr.integration.config.IntegrationProperties;
import ru.croc.ugd.ssr.integration.config.IntegrationPropertyConfig;
import ru.croc.ugd.ssr.integration.mqetpmv.MqetpmvProperties;
import ru.croc.ugd.ssr.integration.service.flows.AdministrativeDocumentFlowService;
import ru.croc.ugd.ssr.integration.service.mqetpmv.flow.SsrMqetpmvFlowService;
import ru.croc.ugd.ssr.integration.util.XmlUtils;
import ru.croc.ugd.ssr.integrationflow.IntegrationFlowQueueData;
import ru.croc.ugd.ssr.integrationflow.IntegrationFlowType;
import ru.croc.ugd.ssr.model.integration.dgi.ObjectFactory;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPAdministrativeDocumentAgrMessageType;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPAdministrativeDocumentAgrSendStatusType;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPAdministrativeDocumentType;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateTaskData;
import ru.croc.ugd.ssr.model.integration.etpmv.SendTasksMessage;
import ru.croc.ugd.ssr.model.integration.etpmv.TaskDataType;
import ru.croc.ugd.ssr.model.integration.etpmv.TaskType;
import ru.reinform.cdp.mqetpmv.model.EtpInboundMessage;

import java.util.Optional;
import javax.xml.bind.JAXBElement;

/**
 * 13 поток.
 */
@Slf4j
@Service
@AllArgsConstructor
public class AdministrativeDocumentListener extends EtpQueueListener<SendTasksMessage, CoordinateTaskData> {

    @Getter
    private final XmlUtils xmlUtils;
    private final AdministrativeDocumentFlowService administrativeDocumentFlowService;
    private final IntegrationProperties integrationProperties;
    private final MqetpmvProperties mqetpmvProperties;
    private final SsrMqetpmvFlowService ssrMqetpmvFlowService;
    private final IntegrationPropertyConfig propertyConfig;

    @NotNull
    @Override
    public String getMessageType() {
        return mqetpmvProperties.getAdministrativeDocumentMessageType();
    }

    @Override
    public void receiveMessage(@NotNull final String messageType, @NotNull final EtpInboundMessage etpInboundMessage) {
        ssrMqetpmvFlowService.storeInFlowSendTasksMessage(
            messageType,
            etpInboundMessage.getMessage(),
            propertyConfig.getDefaultServiceCode(),
            getIntegrationFlowType(),
            integrationProperties.getXmlImportAdministrativeDocuments(),
            this::parseMessage,
            this::isPartOfAffairCollation,
            this::getMessageClasses
        );
    }

    @Override
    public IntegrationFlowType getIntegrationFlowType() {
        return IntegrationFlowType.DGI_TO_DGP_ADMINISTRATIVE_DOCUMENT;
    }

    @Override
    public void handle(final String message) {
        handle(message, null, null);
    }

    @Override
    public void handle(final String message, final IntegrationFlowQueueData integrationFlowQueueData) {
        final CoordinateTaskData coordinateTaskData = xmlUtils.<CoordinateTaskData>parseXml(
            integrationFlowQueueData.getEnoMessage(),
            new Class[]{CoordinateTaskData.class, SuperServiceDGPAdministrativeDocumentType.class}
        ).orElse(null);

        toSuperServiceDgpAdministrativeDocumentType(coordinateTaskData, message)
            .ifPresent(superServiceDGPAdministrativeDocumentType ->
                administrativeDocumentFlowService.receiveAdministrativeDocument(
                    integrationFlowQueueData.getEno(),
                    message,
                    superServiceDGPAdministrativeDocumentType
                )
            );
    }

    @Override
    public void handle(final String messageXml, final String affairId, final String personId) {
        final SendTasksMessage messageTask = parseMessage(messageXml);
        final String eno = getEno(messageTask);
        processMessageTask(messageTask, messageXml, eno, affairId, personId);
    }

    @Override
    public Class<?>[] getMessageClasses() {
        return new Class[]{SendTasksMessage.class};
    }

    @Override
    public boolean isPartOfAffairCollation(final CoordinateTaskData payload) {
        return ofNullable(payload)
            .map(CoordinateTaskData::getTask)
            .map(TaskType::getTaskId)
            .filter(AFFAIR_COLLATION_TASK_ID::equals)
            .isPresent();
    }

    private void processMessageTask(
        final SendTasksMessage messageTask,
        final String messageXml,
        final String eno,
        final String affairId,
        final String personId
    ) {
        messageTask
            .getCoordinateTaskDataMessages()
            .getCoordinateTaskData()
            .stream()
            .map(coordinateTaskData -> toSuperServiceDgpAdministrativeDocumentType(coordinateTaskData, messageXml))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .forEach(administrativeDocumentType ->
                handleMessage(administrativeDocumentType, eno, messageXml, affairId, personId)
            );
    }

    private String getEno(final SendTasksMessage messageTask) {
        return messageTask
            .getCoordinateTaskDataMessages()
            .getCoordinateTaskData()
            .stream()
            .map(coordinateTaskData -> coordinateTaskData.getTask().getTaskNumber())
            .findFirst()
            .orElse(null);
    }

    private void handleMessage(
        final SuperServiceDGPAdministrativeDocumentType administrativeDocumentType,
        final String eno,
        final String xmlMessage,
        final String affairId,
        final String personId
    ) {
        if ((isNull(affairId) || affairId.equals(administrativeDocumentType.getAffairId()))
            && (isNull(personId) || personId.equals(administrativeDocumentType.getPersonId()))) {
            administrativeDocumentFlowService.receiveAdministrativeDocument(
                eno,
                xmlMessage,
                administrativeDocumentType
            );
        }
    }

    private JAXBElement mapXmlToObject(String message, Object messageObj) {
        return xmlUtils.transformXmlToObject((Node) messageObj,
            getMessageClass(message), ObjectFactory.class);
    }

    private Class getMessageClass(final String message) {
        if (message.contains("SuperServiceDGPAdministrativeDocumentAgrMessage")) {
            return SuperServiceDGPAdministrativeDocumentAgrMessageType.class;
        }
        if (message.contains("SuperServiceDGPAdministrativeDocumentAgrSendStatus")) {
            return SuperServiceDGPAdministrativeDocumentAgrSendStatusType.class;
        }
        if (message.contains("SuperServiceDGPAdministrativeDocument")) {
            return SuperServiceDGPAdministrativeDocumentType.class;
        }
        return null;
    }

    private Optional<SuperServiceDGPAdministrativeDocumentType> toSuperServiceDgpAdministrativeDocumentType(
        final CoordinateTaskData coordinateTaskData, final String message
    ) {
        return ofNullable(coordinateTaskData)
            .map(CoordinateTaskData::getData)
            .map(TaskDataType::getParameter)
            .map(TaskDataType.Parameter::getAny)
            .map(messageObject -> mapXmlToObject(message, messageObject))
            .map(JAXBElement::getValue)
            .filter(object -> object instanceof SuperServiceDGPAdministrativeDocumentType)
            .map(SuperServiceDGPAdministrativeDocumentType.class::cast);
    }
}
