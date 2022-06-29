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
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPSettleFlatInfoType;
import ru.croc.ugd.ssr.model.integration.dto.FlowReceivedMessageDto;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateSendTaskStatusesMessage;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateTaskStatusData;
import ru.croc.ugd.ssr.model.integration.etpmv.TaskResult;
import ru.reinform.cdp.mqetpmv.model.EtpInboundMessage;

import java.util.Objects;
import java.util.Optional;

/**
 * Листенер очереди о жилом помещении. 8 поток.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InfoSettlementListener
    extends EtpQueueListener<CoordinateSendTaskStatusesMessage, CoordinateTaskStatusData> {

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
        return mqetpmvProperties.getInfoSettlementMessageType();
    }

    @Override
    public void receiveMessage(@NotNull final String messageType, @NotNull final EtpInboundMessage etpInboundMessage) {
        ssrMqetpmvFlowService.storeInQueueCoordinateSendTaskStatusesMessage(
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
        return IntegrationFlowType.DGI_TO_DGP_INFO_SETTLEMENT;
    }

    @Override
    public void handle(String message) {
        toFlowReceivedMessageDto(message)
            .ifPresent(flowReceivedMessageDto ->
                contractSignedFlowsService.receiveSettleFlatInfo(flowReceivedMessageDto, message)
            );
    }

    @Override
    public void handle(final String message, final IntegrationFlowQueueData integrationFlowQueueData) {
        final CoordinateTaskStatusData coordinateTaskStatusData = xmlUtils.<CoordinateTaskStatusData>parseXml(
            integrationFlowQueueData.getEnoMessage(),
            new Class[]{CoordinateTaskStatusData.class, SuperServiceDGPSettleFlatInfoType.class}
        ).orElse(null);

        toFlowReceivedMessageDto(coordinateTaskStatusData)
            .ifPresent(flowReceivedMessageDto ->
                contractSignedFlowsService.receiveSettleFlatInfo(flowReceivedMessageDto, message)
            );
    }

    @Override
    public void handle(final String message, final String affairId, final String personId) {
        toFlowReceivedMessageDto(message)
            .filter(flowReceivedMessageDto ->
                Objects.equals(flowReceivedMessageDto.getParsedMessage().getAffairId(), affairId)
                    && Objects.equals(flowReceivedMessageDto.getParsedMessage().getPersonId(), personId)
            )
            .ifPresent(flowReceivedMessageDto ->
                contractSignedFlowsService.receiveSettleFlatInfo(flowReceivedMessageDto, message)
            );
    }

    @Override
    public Class<?>[] getMessageClasses() {
        return new Class[]{CoordinateSendTaskStatusesMessage.class, SuperServiceDGPSettleFlatInfoType.class};
    }

    @Override
    public boolean isPartOfAffairCollation(final CoordinateTaskStatusData payload) {
        return ofNullable(payload)
            .filter(p -> AFFAIR_COLLATION_TASK_ID.equals(p.getTaskId())
                || AFFAIR_COLLATION_STATUS_CODE.equals(p.getStatus().getStatusCode()))
            .isPresent();
    }

    private Optional<FlowReceivedMessageDto<SuperServiceDGPSettleFlatInfoType>> toFlowReceivedMessageDto(
        final String message
    ) {
        final CoordinateSendTaskStatusesMessage coordinateSendTaskStatusesMessage = parseMessage(message);

        return toFlowReceivedMessageDto(coordinateSendTaskStatusesMessage.getCoordinateTaskStatusDataMessage());
    }

    private Optional<FlowReceivedMessageDto<SuperServiceDGPSettleFlatInfoType>> toFlowReceivedMessageDto(
        final CoordinateTaskStatusData coordinateTaskStatusData
    ) {
        return ofNullable(coordinateTaskStatusData)
            .map(CoordinateTaskStatusData::getResult)
            .map(TaskResult::getXmlView)
            .map(TaskResult.XmlView::getAny)
            .map(SuperServiceDGPSettleFlatInfoType.class::cast)
            .map(superServiceDGPSettleFlatInfoType -> FlowReceivedMessageDto
                .<SuperServiceDGPSettleFlatInfoType>builder()
                .eno(coordinateTaskStatusData.getTaskId())
                .parsedMessage(superServiceDGPSettleFlatInfoType)
                .shouldSendNotifications(!isPartOfAffairCollation(coordinateTaskStatusData))
                .build()
            );
    }
}
