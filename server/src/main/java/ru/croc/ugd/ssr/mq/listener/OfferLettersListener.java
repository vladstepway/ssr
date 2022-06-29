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
import ru.croc.ugd.ssr.integration.service.flows.OfferLettersFlowsService;
import ru.croc.ugd.ssr.integration.service.mqetpmv.flow.SsrMqetpmvFlowService;
import ru.croc.ugd.ssr.integration.util.XmlUtils;
import ru.croc.ugd.ssr.integrationflow.IntegrationFlowQueueData;
import ru.croc.ugd.ssr.integrationflow.IntegrationFlowType;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPLetterRequestType;
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

/**
 * Листенер Cведения о письмах с предложениями из ЕИС Жилище.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OfferLettersListener extends EtpQueueListener<SendTasksMessage, CoordinateTaskData> {

    @Getter
    private final XmlUtils xmlUtils;
    private final OfferLettersFlowsService offerLettersFlowsService;
    private final IntegrationProperties integrationProperties;
    private final MqetpmvProperties mqetpmvProperties;
    private final SsrMqetpmvFlowService ssrMqetpmvFlowService;
    private final IntegrationPropertyConfig propertyConfig;

    @NotNull
    @Override
    public String getMessageType() {
        return mqetpmvProperties.getOfferLetterMessageType();
    }

    @Override
    public void receiveMessage(@NotNull final String messageType, @NotNull final EtpInboundMessage etpInboundMessage) {
        ssrMqetpmvFlowService.storeInFlowSendTasksMessage(
            messageType,
            etpInboundMessage.getMessage(),
            propertyConfig.getDefaultServiceCode(),
            getIntegrationFlowType(),
            integrationProperties.getXmlImportFlowSecond(),
            this::parseMessage,
            this::isPartOfAffairCollation,
            this::getMessageClasses
        );
    }

    @Override
    public IntegrationFlowType getIntegrationFlowType() {
        return IntegrationFlowType.DGI_TO_DGP_OFFER_LETTER;
    }

    @Override
    public void handle(String message) {
        parseReceiveMessage(message)
            .forEach(mes -> offerLettersFlowsService.receiveOfferLetterRequest(
                mes, message
            ));
    }

    @Override
    public void handle(final String message, final IntegrationFlowQueueData integrationFlowQueueData) {
        final CoordinateTaskData coordinateTaskData = xmlUtils.<CoordinateTaskData>parseXml(
            integrationFlowQueueData.getEnoMessage(),
            new Class[]{CoordinateTaskData.class, SuperServiceDGPLetterRequestType.class}
        ).orElse(null);

        toFlowReceivedMessageDto(coordinateTaskData)
            .ifPresent(flowReceivedMessage -> offerLettersFlowsService.receiveOfferLetterRequest(
                flowReceivedMessage, message
            ));
    }

    @Override
    public void handle(final String message, final String affairId, final String personId) {
        parseReceiveMessage(message)
            .stream()
            .filter(mes -> Objects.equals(mes.getParsedMessage().getAffairId(), affairId)
                && Objects.equals(mes.getParsedMessage().getPersonId(), personId))
            .forEach(mes -> offerLettersFlowsService.receiveOfferLetterRequest(
                mes, message
            ));
    }

    @Override
    public Class<?>[] getMessageClasses() {
        return new Class[]{SendTasksMessage.class, SuperServiceDGPLetterRequestType.class};
    }

    /**
     * Метод обработки входящего сообщения ЕТП МВ.
     *
     * @param message входящее сообщение
     * @return сведения о жителях.
     */
    public List<FlowReceivedMessageDto<SuperServiceDGPLetterRequestType>> parseReceiveMessage(String message) {
        final SendTasksMessage tasksMessage = parseMessage(message);

        return tasksMessage.getCoordinateTaskDataMessages().getCoordinateTaskData()
            .stream()
            .map(this::toFlowReceivedMessageDto)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
    }

    @Override
    public boolean isPartOfAffairCollation(final CoordinateTaskData payload) {
        return ofNullable(payload)
            .map(CoordinateTaskData::getTask)
            .map(TaskType::getTaskId)
            .filter(AFFAIR_COLLATION_TASK_ID::equals)
            .isPresent();
    }

    private Optional<FlowReceivedMessageDto<SuperServiceDGPLetterRequestType>> toFlowReceivedMessageDto(
        final CoordinateTaskData coordinateTaskData
    ) {
        return ofNullable(coordinateTaskData)
            .map(CoordinateTaskData::getData)
            .map(TaskDataType::getParameter)
            .map(TaskDataType.Parameter::getAny)
            .map(SuperServiceDGPLetterRequestType.class::cast)
            .map(superServiceDGPLetterRequestType -> FlowReceivedMessageDto.<SuperServiceDGPLetterRequestType>builder()
                .eno(extractEno(coordinateTaskData))
                .parsedMessage(superServiceDGPLetterRequestType)
                .shouldSendNotifications(!isPartOfAffairCollation(coordinateTaskData))
                .build()
            );
    }
}
