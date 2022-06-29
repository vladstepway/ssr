package ru.croc.ugd.ssr.mq.listener;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.integration.config.IntegrationProperties;
import ru.croc.ugd.ssr.integration.config.IntegrationPropertyConfig;
import ru.croc.ugd.ssr.integration.mqetpmv.MqetpmvProperties;
import ru.croc.ugd.ssr.integration.service.flows.ContractReadyFlowsService;
import ru.croc.ugd.ssr.integration.service.mqetpmv.flow.SsrMqetpmvFlowService;
import ru.croc.ugd.ssr.integration.util.XmlUtils;
import ru.croc.ugd.ssr.integrationflow.IntegrationFlowQueueData;
import ru.croc.ugd.ssr.integrationflow.IntegrationFlowType;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPContractIssueStatusType;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPContractReadyType;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPContractsIssueStatus;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPContractsReady;
import ru.croc.ugd.ssr.model.integration.dto.FlowReceivedMessageDto;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateTaskData;
import ru.croc.ugd.ssr.model.integration.etpmv.SendTasksMessage;
import ru.croc.ugd.ssr.model.integration.etpmv.TaskDataType;
import ru.croc.ugd.ssr.model.integration.etpmv.TaskType;
import ru.reinform.cdp.mqetpmv.model.EtpInboundMessage;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Листенер Данные о готовности договора. 7 поток.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ContractReadyListener extends EtpQueueListener<SendTasksMessage, CoordinateTaskData> {

    @Getter
    private final XmlUtils xmlUtils;
    private final ContractReadyFlowsService contractReadyFlowsService;
    private final IntegrationProperties integrationProperties;
    private final MqetpmvProperties mqetpmvProperties;
    private final SsrMqetpmvFlowService ssrMqetpmvFlowService;
    private final IntegrationPropertyConfig propertyConfig;

    @NotNull
    @Override
    public String getMessageType() {
        return mqetpmvProperties.getContractReadyMessageType();
    }

    @Override
    public void receiveMessage(@NotNull final String messageType, @NotNull final EtpInboundMessage etpInboundMessage) {
        ssrMqetpmvFlowService.storeInFlowSendTasksMessage(
            messageType,
            etpInboundMessage.getMessage(),
            propertyConfig.getDefaultServiceCode(),
            getIntegrationFlowType(),
            integrationProperties.getXmlImportFlowSeventh(),
            this::parseMessage,
            this::isPartOfAffairCollation,
            this::getMessageClasses
        );
    }

    @Override
    public IntegrationFlowType getIntegrationFlowType() {
        return IntegrationFlowType.DGI_TO_DGP_CONTRACT_READY;
    }

    @Override
    public void handle(String message) {
        final List<FlowReceivedMessageDto<SuperServiceDGPContractReadyType>> contractReadyTypes =
            toFlowReceivedMessageDtoList(message);
        handleContractReadyTypes(message, contractReadyTypes);
    }

    @Override
    public void handle(final String message, final IntegrationFlowQueueData integrationFlowQueueData) {
        final CoordinateTaskData coordinateTaskData = xmlUtils.<CoordinateTaskData>parseXml(
            integrationFlowQueueData.getEnoMessage(),
            new Class[]{CoordinateTaskData.class, SuperServiceDGPContractsReady.class}
        ).orElse(null);

        final List<FlowReceivedMessageDto<SuperServiceDGPContractReadyType>> contractReadyTypes =
            toFlowReceivedMessageDtoList(coordinateTaskData);

        handleContractReadyTypes(message, contractReadyTypes);
    }

    @Override
    public void handle(final String message, final String affairId, final String personId) {
        final List<FlowReceivedMessageDto<SuperServiceDGPContractReadyType>> flowReceivedMessageDtos =
            toFlowReceivedMessageDtoList(message);
        handleContractReadyTypes(message, flowReceivedMessageDtos, affairId, personId);
    }

    @Override
    public Class<?>[] getMessageClasses() {
        return new Class[]{SendTasksMessage.class, SuperServiceDGPContractReadyType.class};
    }

    private void handleContractReadyTypes(
        final String message,
        final List<FlowReceivedMessageDto<SuperServiceDGPContractReadyType>> contractReadyTypes
    ) {
        if (nonNull(contractReadyTypes)) {
            contractReadyTypes.forEach(
                contract -> contractReadyFlowsService.receiveContractReadyRequest(
                    contract, message
                )
            );
        } else {
            final List<FlowReceivedMessageDto<SuperServiceDGPContractIssueStatusType>> issueStatusTypes =
                toFlowReceivedMessageDtoListWithIssue(message);
            issueStatusTypes.forEach(
                contract -> contractReadyFlowsService.receiveContractIssueRequest(
                    contract, message
                )
            );
        }
    }

    private void handleContractReadyTypes(
        final String message,
        final List<FlowReceivedMessageDto<SuperServiceDGPContractReadyType>> flowReceivedMessageDtos,
        final String affairId,
        final String personId
    ) {
        if (nonNull(flowReceivedMessageDtos)) {
            flowReceivedMessageDtos.stream()
                .filter(contractReadyType ->
                    Objects.equals(contractReadyType.getParsedMessage().getAffairId(), affairId)
                        && Objects.equals(contractReadyType.getParsedMessage().getPersonId(), personId))
                .forEach(
                    contract -> contractReadyFlowsService.receiveContractReadyRequest(
                        contract, message
                    )
                );
        } else {
            final List<FlowReceivedMessageDto<SuperServiceDGPContractIssueStatusType>> issueStatusTypes =
                toFlowReceivedMessageDtoListWithIssue(message);
            issueStatusTypes.stream()
                .filter(issueStatusType -> Objects.equals(issueStatusType.getParsedMessage().getAffairId(), affairId)
                    && Objects.equals(issueStatusType.getParsedMessage().getPersonId(), personId))
                .forEach(
                    contract -> contractReadyFlowsService.receiveContractIssueRequest(
                        contract, message
                    )
                );
        }
    }

    @Override
    public boolean isPartOfAffairCollation(final CoordinateTaskData payload) {
        return ofNullable(payload)
            .map(CoordinateTaskData::getTask)
            .map(TaskType::getTaskId)
            .filter(AFFAIR_COLLATION_TASK_ID::equals)
            .isPresent();
    }

    private List<FlowReceivedMessageDto<SuperServiceDGPContractReadyType>> toFlowReceivedMessageDtoList(
        final String message
    ) {
        final SendTasksMessage tasksMessage = parseMessage(message);

        return tasksMessage.getCoordinateTaskDataMessages().getCoordinateTaskData()
            .stream()
            .map(this::toFlowReceivedMessageDtoList)
            .flatMap(List::stream)
            .collect(Collectors.toList());
    }

    private List<FlowReceivedMessageDto<SuperServiceDGPContractReadyType>> toFlowReceivedMessageDtoList(
        final CoordinateTaskData coordinateTaskData
    ) {
        return ofNullable(coordinateTaskData)
            .map(CoordinateTaskData::getData)
            .map(TaskDataType::getParameter)
            .map(TaskDataType.Parameter::getAny)
            .map(SuperServiceDGPContractsReady.class::cast)
            .map(SuperServiceDGPContractsReady::getSuperServiceDGPContractReady)
            .map(List::stream)
            .orElse(Stream.empty())
            .map(superServiceDGPContractReadyType -> FlowReceivedMessageDto.<SuperServiceDGPContractReadyType>builder()
                .eno(extractEno(coordinateTaskData))
                .parsedMessage(superServiceDGPContractReadyType)
                .shouldSendNotifications(!isPartOfAffairCollation(coordinateTaskData))
                .build())
            .collect(Collectors.toList());
    }

    private List<FlowReceivedMessageDto<SuperServiceDGPContractIssueStatusType>> toFlowReceivedMessageDtoListWithIssue(
        final String message
    ) {
        final SendTasksMessage tasksMessageWithIssues = parseMessageWithIssue(message);

        return tasksMessageWithIssues.getCoordinateTaskDataMessages().getCoordinateTaskData()
            .stream()
            .map(this::toFlowReceivedMessageDtoWithIssue)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
    }

    private Optional<FlowReceivedMessageDto<SuperServiceDGPContractIssueStatusType>> toFlowReceivedMessageDtoWithIssue(
        final CoordinateTaskData coordinateTaskData
    ) {
        return ofNullable(coordinateTaskData)
            .map(CoordinateTaskData::getData)
            .map(TaskDataType::getParameter)
            .map(TaskDataType.Parameter::getAny)
            .map(SuperServiceDGPContractsIssueStatus.class::cast)
            .map(SuperServiceDGPContractsIssueStatus::getSuperServiceDGPContractIssueStatus)
            .map(superServiceDGPContractsIssueStatus -> FlowReceivedMessageDto
                .<SuperServiceDGPContractIssueStatusType>builder()
                .eno(extractEno(coordinateTaskData))
                .parsedMessage(superServiceDGPContractsIssueStatus)
                .shouldSendNotifications(!isPartOfAffairCollation(coordinateTaskData))
                .build());
    }

    private SendTasksMessage parseMessageWithIssue(final String message) {
        return xmlUtils.transformXmlToObject(
            message, SendTasksMessage.class, SuperServiceDGPContractsIssueStatus.class
        );
    }
}
