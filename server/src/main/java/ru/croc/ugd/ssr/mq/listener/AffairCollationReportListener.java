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
import ru.croc.ugd.ssr.integration.service.mqetpmv.flow.SsrMqetpmvFlowService;
import ru.croc.ugd.ssr.integration.util.XmlUtils;
import ru.croc.ugd.ssr.integrationflow.IntegrationFlowQueueData;
import ru.croc.ugd.ssr.integrationflow.IntegrationFlowType;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPAffairCollationReportType;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateTaskData;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateTaskMessage;
import ru.croc.ugd.ssr.model.integration.etpmv.TaskDataType;
import ru.croc.ugd.ssr.service.affaircollation.AffairCollationService;
import ru.reinform.cdp.mqetpmv.model.EtpInboundMessage;

@Slf4j
@Component
@RequiredArgsConstructor
public class AffairCollationReportListener extends EtpQueueListener<CoordinateTaskMessage, CoordinateTaskData> {

    @Getter
    private final XmlUtils xmlUtils;
    private final MqetpmvProperties mqetpmvProperties;
    private final IntegrationProperties integrationProperties;
    private final IntegrationPropertyConfig propertyConfig;
    private final AffairCollationService affairCollationService;
    private final SsrMqetpmvFlowService ssrMqetpmvFlowService;

    @NotNull
    @Override
    public String getMessageType() {
        return mqetpmvProperties.getAffairCollationReportMessageType();
    }

    /**
     * Чтение из очереди.
     *
     * @param messageType messageType
     * @param etpInboundMessage etpInboundMessage
     */
    @Override
    public void receiveMessage(@NotNull final String messageType, @NotNull final EtpInboundMessage etpInboundMessage) {
        ssrMqetpmvFlowService.storeInQueueCoordinateTaskMessage(
            messageType,
            etpInboundMessage.getMessage(),
            propertyConfig.getAffairCollation(),
            IntegrationFlowType.DGI_TO_DGP_AFFAIR_COLLATION_REPORT_INFO,
            integrationProperties.getXmlImportAffairCollation(),
            this::parseMessage,
            this::getMessageClasses
        );
    }

    @Override
    public IntegrationFlowType getIntegrationFlowType() {
        return IntegrationFlowType.DGI_TO_DGP_AFFAIR_COLLATION_REPORT_INFO;
    }

    @Override
    public void handle(final String message) {
        throw new UnsupportedOperationException("Only queue message processing is possible");
    }

    @Override
    public void handle(final String message, final IntegrationFlowQueueData integrationFlowQueueData) {
        final CoordinateTaskData coordinateTaskData = xmlUtils.<CoordinateTaskData>parseXml(
            integrationFlowQueueData.getEnoMessage(),
            new Class[]{CoordinateTaskData.class, SuperServiceDGPAffairCollationReportType.class}
        ).orElse(null);

        processReport(coordinateTaskData);
    }

    @Override
    public Class<?>[] getMessageClasses() {
        return new Class[]{CoordinateTaskMessage.class, SuperServiceDGPAffairCollationReportType.class};
    }

    @Override
    public boolean isPartOfAffairCollation(final CoordinateTaskData payload) {
        return false;
    }

    private void processReport(final CoordinateTaskData coordinateTaskData) {
        ofNullable(coordinateTaskData)
            .map(CoordinateTaskData::getData)
            .map(TaskDataType::getParameter)
            .map(TaskDataType.Parameter::getAny)
            .map(SuperServiceDGPAffairCollationReportType.class::cast)
            .ifPresent(affairCollationReport -> affairCollationService
                .processReport(extractEno(coordinateTaskData), affairCollationReport)
            );
    }
}
