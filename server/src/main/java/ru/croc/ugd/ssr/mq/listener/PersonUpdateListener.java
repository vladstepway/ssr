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
import ru.croc.ugd.ssr.integration.service.flows.PersonsUpdateFlowsService;
import ru.croc.ugd.ssr.integration.service.mqetpmv.flow.SsrMqetpmvFlowService;
import ru.croc.ugd.ssr.integration.util.XmlUtils;
import ru.croc.ugd.ssr.integrationflow.IntegrationFlowQueueData;
import ru.croc.ugd.ssr.integrationflow.IntegrationFlowType;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPPersonsMessage;
import ru.croc.ugd.ssr.model.integration.dto.FlowReceivedMessageDto;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateSendTaskStatusesMessage;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateTaskStatusData;
import ru.croc.ugd.ssr.model.integration.etpmv.TaskResult;
import ru.reinform.cdp.mqetpmv.model.EtpInboundMessage;

/**
 * Слушаем очередь сообщений по первому потоку.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PersonUpdateListener
    extends EtpQueueListener<CoordinateSendTaskStatusesMessage, CoordinateTaskStatusData> {

    @Getter
    private final XmlUtils xmlUtils;
    private final PersonsUpdateFlowsService personsUpdateFlowsService;
    private final IntegrationProperties integrationProperties;
    private final MqetpmvProperties mqetpmvProperties;
    private final SsrMqetpmvFlowService ssrMqetpmvFlowService;
    private final IntegrationPropertyConfig propertyConfig;

    @NotNull
    @Override
    public String getMessageType() {
        return mqetpmvProperties.getPersonUpdateMessageType();
    }

    @Override
    public void receiveMessage(@NotNull final String messageType, @NotNull final EtpInboundMessage etpInboundMessage) {
        ssrMqetpmvFlowService.storeInQueueCoordinateSendTaskStatusesMessage(
            messageType,
            etpInboundMessage.getMessage(),
            propertyConfig.getDefaultServiceCode(),
            getIntegrationFlowType(),
            integrationProperties.getXmlImportFlowFirst(),
            this::parseMessage,
            this::isPartOfAffairCollation,
            this::getMessageClasses
        );
    }

    @Override
    public IntegrationFlowType getIntegrationFlowType() {
        return IntegrationFlowType.DGI_TO_DGP_PERSON_UPDATE;
    }

    @Override
    public void handle(String message) {
        personsUpdateFlowsService.updatePersonsInfo(toFlowReceivedMessageDto(message));
    }

    @Override
    public void handle(final String message, final IntegrationFlowQueueData integrationFlowQueueData) {
        final CoordinateTaskStatusData coordinateTaskStatusData = xmlUtils.<CoordinateTaskStatusData>parseXml(
            integrationFlowQueueData.getEnoMessage(),
            new Class[]{CoordinateTaskStatusData.class, SuperServiceDGPPersonsMessage.class}
        ).orElse(null);

        ofNullable(coordinateTaskStatusData)
            .map(CoordinateTaskStatusData::getResult)
            .map(TaskResult::getXmlView)
            .map(TaskResult.XmlView::getAny)
            .map(SuperServiceDGPPersonsMessage.class::cast)
            .map(superServiceDGPPersonsMessage -> FlowReceivedMessageDto
                .<SuperServiceDGPPersonsMessage>builder()
                .eno(integrationFlowQueueData.getEno())
                .parsedMessage(superServiceDGPPersonsMessage)
                .shouldSendNotifications(!isPartOfAffairCollation(coordinateTaskStatusData))
                .build()
            )
            .ifPresent(personsUpdateFlowsService::updatePersonsInfo);
    }

    @Override
    public Class<?>[] getMessageClasses() {
        return new Class[]{CoordinateSendTaskStatusesMessage.class, SuperServiceDGPPersonsMessage.class};
    }

    @Override
    public boolean isPartOfAffairCollation(final CoordinateTaskStatusData payload) {
        return ofNullable(payload)
            .filter(p -> AFFAIR_COLLATION_TASK_ID.equals(p.getTaskId())
                || AFFAIR_COLLATION_STATUS_CODE.equals(p.getStatus().getStatusCode()))
            .isPresent();
    }

    /**
     * Метод обработки входящего сообщения ЕТП МВ по первому потоку.
     *
     * @param message
     *            входящее сообщение
     * @return сведения о жителях.
     */
    private FlowReceivedMessageDto<SuperServiceDGPPersonsMessage> toFlowReceivedMessageDto(final String message) {
        final CoordinateSendTaskStatusesMessage statusesMessage = parseMessage(message);
        final boolean shouldSendNotification = !isPartOfAffairCollation(
            statusesMessage.getCoordinateTaskStatusDataMessage()
        );

        final TaskResult.XmlView xmlView = statusesMessage
            .getCoordinateTaskStatusDataMessage()
            .getResult()
            .getXmlView();
        return FlowReceivedMessageDto
            .<SuperServiceDGPPersonsMessage>builder()
            .parsedMessage((SuperServiceDGPPersonsMessage) xmlView.getAny())
            .shouldSendNotifications(shouldSendNotification)
            .build();
    }
}
