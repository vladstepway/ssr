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
import ru.croc.ugd.ssr.integration.service.flows.ContractSignedFlowsService;
import ru.croc.ugd.ssr.integration.service.mqetpmv.flow.SsrMqetpmvFlowService;
import ru.croc.ugd.ssr.integration.util.XmlUtils;
import ru.croc.ugd.ssr.integrationflow.IntegrationFlowQueueData;
import ru.croc.ugd.ssr.integrationflow.IntegrationFlowType;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPCourtAndMoneyType;
import ru.croc.ugd.ssr.model.integration.dto.FlowReceivedMessageDto;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateTaskData;
import ru.croc.ugd.ssr.model.integration.etpmv.SendTasksMessage;
import ru.croc.ugd.ssr.model.integration.etpmv.TaskDataType;
import ru.croc.ugd.ssr.model.integration.etpmv.TaskType;
import ru.reinform.cdp.mqetpmv.model.EtpInboundMessage;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Листенер о заселяемых квартирах.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ContractSignedListener extends EtpQueueListener<SendTasksMessage, CoordinateTaskData> {
    private static final String TYPE = "11";

    @Getter
    private final XmlUtils xmlUtils;
    private final IntegrationProperties integrationProperties;
    private final ContractSignedFlowsService contractSignedFlowsService;
    private final MqetpmvProperties mqetpmvProperties;
    private final SsrMqetpmvFlowService ssrMqetpmvFlowService;
    private final IntegrationPropertyConfig propertyConfig;

    @NotNull
    @Override
    public String getMessageType() {
        return mqetpmvProperties.getContractSignedMessageType();
    }

    @Override
    public void receiveMessage(@NotNull final String messageType, @NotNull final EtpInboundMessage etpInboundMessage) {
        ssrMqetpmvFlowService.storeInFlowSendTasksMessage(
            messageType,
            etpInboundMessage.getMessage(),
            propertyConfig.getDefaultServiceCode(),
            getIntegrationFlowType(),
            integrationProperties.getXmlImportFlowEighth(),
            this::parseMessage,
            this::isPartOfAffairCollation,
            this::getMessageClasses
        );
    }

    @Override
    public IntegrationFlowType getIntegrationFlowType() {
        return IntegrationFlowType.DGI_TO_DGP_CONTRACT_SIGNED;
    }

    @Override
    public void handle(String message) {
        contractSignedFlowsService.receiveJudgementResolutionAndContractRegister(
            toFlowReceivedMessageDtoList(message), message, TYPE
        );
    }

    @Override
    public void handle(final String message, final IntegrationFlowQueueData integrationFlowQueueData) {
        final CoordinateTaskData coordinateTaskData = xmlUtils.<CoordinateTaskData>parseXml(
            integrationFlowQueueData.getEnoMessage(),
            new Class[]{CoordinateTaskData.class, SuperServiceDGPCourtAndMoneyType.class}
        ).orElse(null);

        toFlowReceivedMessageDto(coordinateTaskData)
            .ifPresent(flowReceivedMessageDto ->
                contractSignedFlowsService.receiveJudgementResolutionAndContractRegister(
                    Collections.singletonList(flowReceivedMessageDto), message, TYPE
                )
            );
    }

    public void handle(final String message, final String affairId, final String personId) {
        List<FlowReceivedMessageDto<SuperServiceDGPCourtAndMoneyType>> resolutions =
            toFlowReceivedMessageDtoList(message).stream()
                .filter(resolution -> Objects.equals(resolution.getParsedMessage().getAffairId(), affairId)
                    && Objects.equals(resolution.getParsedMessage().getPersonId(), personId))
                .collect(Collectors.toList());

        contractSignedFlowsService.receiveJudgementResolutionAndContractRegister(
            resolutions, message, TYPE
        );
    }

    @Override
    public Class<?>[] getMessageClasses() {
        return new Class[]{SendTasksMessage.class, SuperServiceDGPCourtAndMoneyType.class};
    }

    @Override
    public boolean isPartOfAffairCollation(final CoordinateTaskData payload) {
        return Optional.ofNullable(payload)
            .map(CoordinateTaskData::getTask)
            .map(TaskType::getTaskId)
            .filter(AFFAIR_COLLATION_TASK_ID::equals)
            .isPresent();
    }

    private List<FlowReceivedMessageDto<SuperServiceDGPCourtAndMoneyType>> toFlowReceivedMessageDtoList(
        String payload
    ) {
        final SendTasksMessage tasksMessage = parseMessage(payload);

        return tasksMessage.getCoordinateTaskDataMessages().getCoordinateTaskData()
            .stream()
            .map(this::toFlowReceivedMessageDto)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
    }

    private Optional<FlowReceivedMessageDto<SuperServiceDGPCourtAndMoneyType>> toFlowReceivedMessageDto(
        final CoordinateTaskData coordinateTaskData
    ) {
        return ofNullable(coordinateTaskData)
            .map(CoordinateTaskData::getData)
            .map(TaskDataType::getParameter)
            .map(TaskDataType.Parameter::getAny)
            .map(SuperServiceDGPCourtAndMoneyType.class::cast)
            .map(superServiceDGPCourtAndMoneyType -> FlowReceivedMessageDto.<SuperServiceDGPCourtAndMoneyType>builder()
                .eno(extractEno(coordinateTaskData))
                .parsedMessage(superServiceDGPCourtAndMoneyType)
                .shouldSendNotifications(!isPartOfAffairCollation(coordinateTaskData))
                .build()
            );
    }
}
