package ru.croc.ugd.ssr.mq.listener;

import static java.util.Optional.ofNullable;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.integration.config.IntegrationProperties;
import ru.croc.ugd.ssr.integration.config.IntegrationPropertyConfig;
import ru.croc.ugd.ssr.integration.mqetpmv.MqetpmvProperties;
import ru.croc.ugd.ssr.integration.service.flows.ContractPrReadyFlowsService;
import ru.croc.ugd.ssr.integration.service.mqetpmv.flow.SsrMqetpmvFlowService;
import ru.croc.ugd.ssr.integration.util.XmlUtils;
import ru.croc.ugd.ssr.integrationflow.IntegrationFlowQueueData;
import ru.croc.ugd.ssr.integrationflow.IntegrationFlowType;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPContractPrReadyType;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPContractsPrReady;
import ru.croc.ugd.ssr.model.integration.dto.FlowReceivedMessageDto;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateTaskData;
import ru.croc.ugd.ssr.model.integration.etpmv.SendTasksMessage;
import ru.croc.ugd.ssr.model.integration.etpmv.TaskDataType;
import ru.croc.ugd.ssr.model.integration.etpmv.TaskType;
import ru.reinform.cdp.mqetpmv.model.EtpInboundMessage;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Листенер Данные о готовности договора. 12 поток.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ContractPrReadyListener extends EtpQueueListener<SendTasksMessage, CoordinateTaskData> {

    @Getter
    private final XmlUtils xmlUtils;
    private final ContractPrReadyFlowsService contractPrReadyFlowsService;
    private final IntegrationProperties integrationProperties;
    private final MqetpmvProperties mqetpmvProperties;
    private final SsrMqetpmvFlowService ssrMqetpmvFlowService;
    private final IntegrationPropertyConfig propertyConfig;

    @NotNull
    @Override
    public String getMessageType() {
        return mqetpmvProperties.getContractPrReadyMessageType();
    }

    @Override
    public void receiveMessage(@NotNull final String messageType, @NotNull final EtpInboundMessage etpInboundMessage) {
        ssrMqetpmvFlowService.storeInFlowSendTasksMessage(
            messageType,
            etpInboundMessage.getMessage(),
            propertyConfig.getDefaultServiceCode(),
            getIntegrationFlowType(),
            integrationProperties.getXmlImportFlowTwelfth(),
            this::parseMessage,
            this::isPartOfAffairCollation,
            this::getMessageClasses
        );
    }

    @Override
    public IntegrationFlowType getIntegrationFlowType() {
        return IntegrationFlowType.DGI_TO_DGP_CONTRACT_PR_READY;
    }

    @Override
    public void handle(String message) {
        final List<FlowReceivedMessageDto<SuperServiceDGPContractPrReadyType>> contractReadyTypes =
            toFlowReceivedMessageDtoList(message);
        contractReadyTypes.forEach(
            contract -> contractPrReadyFlowsService.receiveContractPrReadyRequest(
                contract, message
            )
        );
    }

    @Override
    public void handle(final String message, final IntegrationFlowQueueData integrationFlowQueueData) {
        final CoordinateTaskData coordinateTaskData = xmlUtils.<CoordinateTaskData>parseXml(
            integrationFlowQueueData.getEnoMessage(),
            new Class[]{CoordinateTaskData.class, SuperServiceDGPContractsPrReady.class}
        ).orElse(null);

        toFlowReceivedMessageDtoList(coordinateTaskData)
            .forEach(flowReceivedMessage ->
                contractPrReadyFlowsService.receiveContractPrReadyRequest(flowReceivedMessage, message)
            );
    }

    public void handle(final String message, final String affairId, final String personId) {
        final List<FlowReceivedMessageDto<SuperServiceDGPContractPrReadyType>> contractReadyTypes =
            toFlowReceivedMessageDtoList(message);
        contractReadyTypes.stream()
            .filter(contract -> Objects.equals(contract.getParsedMessage().getAffairId(), affairId)
                && Objects.equals(contract.getParsedMessage().getPersonId(), personId))
            .forEach(
                contract -> contractPrReadyFlowsService.receiveContractPrReadyRequest(
                    contract, message
                )
            );
    }

    @Override
    public Class<?>[] getMessageClasses() {
        return new Class[]{SendTasksMessage.class, SuperServiceDGPContractPrReadyType.class};
    }

    @Override
    public boolean isPartOfAffairCollation(final CoordinateTaskData payload) {
        return ofNullable(payload)
            .map(CoordinateTaskData::getTask)
            .map(TaskType::getTaskId)
            .filter(AFFAIR_COLLATION_TASK_ID::equals)
            .isPresent();
    }

    private List<FlowReceivedMessageDto<SuperServiceDGPContractPrReadyType>> toFlowReceivedMessageDtoList(
        String message
    ) {
        final SendTasksMessage tasksMessage = parseMessage(message);

        return tasksMessage.getCoordinateTaskDataMessages()
            .getCoordinateTaskData()
            .stream()
            .map(this::toFlowReceivedMessageDtoList)
            .flatMap(List::stream)
            .collect(Collectors.toList());
    }

    private List<FlowReceivedMessageDto<SuperServiceDGPContractPrReadyType>> toFlowReceivedMessageDtoList(
        final CoordinateTaskData coordinateTaskData
    ) {
        return ofNullable(coordinateTaskData)
            .map(CoordinateTaskData::getData)
            .map(TaskDataType::getParameter)
            .map(TaskDataType.Parameter::getAny)
            .map(SuperServiceDGPContractsPrReady.class::cast)
            .map(SuperServiceDGPContractsPrReady::getSuperServiceDGPContractPrReady)
            .map(List::stream)
            .orElse(Stream.empty())
            .map(superServiceDGPContractsPrReadyType -> FlowReceivedMessageDto
                .<SuperServiceDGPContractPrReadyType>builder()
                .eno(extractEno(coordinateTaskData))
                .parsedMessage(superServiceDGPContractsPrReadyType)
                .shouldSendNotifications(!isPartOfAffairCollation(coordinateTaskData))
                .build())
            .collect(Collectors.toList());
    }
}
