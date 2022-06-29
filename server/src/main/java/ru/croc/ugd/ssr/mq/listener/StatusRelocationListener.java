package ru.croc.ugd.ssr.mq.listener;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.integration.config.IntegrationProperties;
import ru.croc.ugd.ssr.integration.config.IntegrationPropertyConfig;
import ru.croc.ugd.ssr.integration.mqetpmv.MqetpmvProperties;
import ru.croc.ugd.ssr.integration.service.flows.RemovableStatusUpdateService;
import ru.croc.ugd.ssr.integration.service.mqetpmv.flow.SsrMqetpmvFlowService;
import ru.croc.ugd.ssr.integration.util.XmlUtils;
import ru.croc.ugd.ssr.integrationflow.IntegrationFlowQueueData;
import ru.croc.ugd.ssr.integrationflow.IntegrationFlowType;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPRemovalStatusType;
import ru.croc.ugd.ssr.model.integration.dto.FlowReceivedMessageDto;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateTaskData;
import ru.croc.ugd.ssr.model.integration.etpmv.SendTasksMessage;
import ru.croc.ugd.ssr.model.integration.etpmv.TaskDataType;
import ru.croc.ugd.ssr.model.integration.etpmv.TaskType;
import ru.reinform.cdp.mqetpmv.model.EtpInboundMessage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Листенер Cведения о письмах с предложениями из ЕИС Жилище.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StatusRelocationListener extends EtpQueueListener<SendTasksMessage, CoordinateTaskData> {

    @Getter
    private final XmlUtils xmlUtils;
    private final RemovableStatusUpdateService removableStatusUpdateService;
    private final IntegrationProperties integrationProperties;
    private final MqetpmvProperties mqetpmvProperties;
    private final SsrMqetpmvFlowService ssrMqetpmvFlowService;
    private final IntegrationPropertyConfig propertyConfig;

    @NotNull
    @Override
    public String getMessageType() {
        return mqetpmvProperties.getStatusRelocationMessageType();
    }

    @Override
    public void receiveMessage(@NotNull final String messageType, @NotNull final EtpInboundMessage etpInboundMessage) {
        ssrMqetpmvFlowService.storeInFlowSendTasksMessage(
            messageType,
            etpInboundMessage.getMessage(),
            propertyConfig.getDefaultServiceCode(),
            getIntegrationFlowType(),
            integrationProperties.getXmlImportFlowFourth(),
            this::parseMessage,
            this::isPartOfAffairCollation,
            this::getMessageClasses
        );
    }

    @Override
    public IntegrationFlowType getIntegrationFlowType() {
        return IntegrationFlowType.DGI_TO_DGP_STATUS_RELOCATION;
    }

    @Override
    public void handle(String message) {
        final List<FlowReceivedMessageDto<SuperServiceDGPRemovalStatusType>> statusTypes = parseReceiveMessage(message);
        statusTypes.forEach(removableStatusUpdateService::receiveStatusFlat);
    }

    @Override
    public void handle(final String message, final IntegrationFlowQueueData integrationFlowQueueData) {
        final CoordinateTaskData coordinateTaskData = xmlUtils.<CoordinateTaskData>parseXml(
            integrationFlowQueueData.getEnoMessage(),
            new Class[]{CoordinateTaskData.class, SuperServiceDGPRemovalStatusType.class}
        ).orElse(null);

        toFlowReceivedMessageDto(coordinateTaskData)
            .ifPresent(removableStatusUpdateService::receiveStatusFlat);
    }

    @Override
    public Class<?>[] getMessageClasses() {
        return new Class[]{SendTasksMessage.class, SuperServiceDGPRemovalStatusType.class};
    }

    @Override
    public boolean isPartOfAffairCollation(final CoordinateTaskData payload) {
        return Optional.ofNullable(payload)
            .map(CoordinateTaskData::getTask)
            .map(TaskType::getTaskId)
            .filter(AFFAIR_COLLATION_TASK_ID::equals)
            .isPresent();
    }

    /**
     * Метод обработки входящего сообщения ЕТП МВ.
     *
     * @param message
     *            входящее сообщение
     * @return сведения о жителях.
     */
    public List<FlowReceivedMessageDto<SuperServiceDGPRemovalStatusType>> parseReceiveMessage(String message) {
        final SendTasksMessage tasksMessage = parseMessage(message);

        return tasksMessage.getCoordinateTaskDataMessages().getCoordinateTaskData()
            .stream()
            .map(this::toFlowReceivedMessageDto)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
    }

    private Optional<FlowReceivedMessageDto<SuperServiceDGPRemovalStatusType>> toFlowReceivedMessageDto(
        final CoordinateTaskData coordinateTaskData
    ) {
        return Optional.ofNullable(coordinateTaskData)
            .map(CoordinateTaskData::getData)
            .map(TaskDataType::getParameter)
            .map(TaskDataType.Parameter::getAny)
            .map(SuperServiceDGPRemovalStatusType.class::cast)
            .map(superServiceDGPRemovalStatusType -> FlowReceivedMessageDto.<SuperServiceDGPRemovalStatusType>builder()
                .eno(extractEno(coordinateTaskData))
                .parsedMessage(superServiceDGPRemovalStatusType)
                .shouldSendNotifications(!isPartOfAffairCollation(coordinateTaskData))
                .build());
    }
}
