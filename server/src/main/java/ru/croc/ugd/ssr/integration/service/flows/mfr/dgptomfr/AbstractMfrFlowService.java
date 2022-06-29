package ru.croc.ugd.ssr.integration.service.flows.mfr.dgptomfr;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.croc.ugd.ssr.integration.config.IntegrationProperties;
import ru.croc.ugd.ssr.integration.config.IntegrationPropertyConfig;
import ru.croc.ugd.ssr.integration.eno.EnoCreator;
import ru.croc.ugd.ssr.integration.mqetpmv.MqetpmvProperties;
import ru.croc.ugd.ssr.integration.service.mqetpmv.flow.SsrMqetpmvFlowService;
import ru.croc.ugd.ssr.integration.util.MessageUtils;
import ru.croc.ugd.ssr.integrationflow.IntegrationFlowType;
import ru.croc.ugd.ssr.integrationflow.MfrFlowData;

@Slf4j
public abstract class AbstractMfrFlowService<T> {

    private MessageUtils messageUtils;
    private EnoCreator enoCreator;
    private SsrMqetpmvFlowService ssrMqetpmvFlowService;
    private MqetpmvProperties mqetpmvProperties;
    protected IntegrationPropertyConfig propertyConfig;
    protected IntegrationProperties integrationProperties;

    @Autowired
    public void setMessageUtils(final MessageUtils messageUtils) {
        this.messageUtils = messageUtils;
    }

    @Autowired
    public void setEnoCreator(final EnoCreator enoCreator) {
        this.enoCreator = enoCreator;
    }

    @Autowired
    public void setPropertyConfig(final IntegrationPropertyConfig propertyConfig) {
        this.propertyConfig = propertyConfig;
    }

    @Autowired
    public void setIntegrationProperties(final IntegrationProperties integrationProperties) {
        this.integrationProperties = integrationProperties;
    }

    @Autowired
    public void setMqetpmvService(final SsrMqetpmvFlowService ssrMqetpmvFlowService) {
        this.ssrMqetpmvFlowService = ssrMqetpmvFlowService;
    }

    @Autowired
    public void setMqetpmvProperties(final MqetpmvProperties mqetpmvProperties) {
        this.mqetpmvProperties = mqetpmvProperties;
    }

    protected abstract String getEnoSequenceCode();

    protected abstract String getGuNumber();

    protected abstract String getExportXmlDirectory();

    protected abstract int getFlowNumber();

    protected abstract IntegrationFlowType getIntegrationFlowType();

    private String generateEno() {
        return enoCreator.generateEtpMvEnoNumber(
            getGuNumber(),
            getEnoSequenceCode()
        );
    }

    public final void sendToMfr(final T requestType) {
        sendToMfr(requestType, null);
    }

    public final void sendToMfr(final T requestType, final MfrFlowData mfrFlowData) {
        final String eno = generateEno();
        final String serviceCode = getGuNumber();
        final String message = messageUtils.createCoordinateTaskMessage(
            eno,
            requestType,
            serviceCode
        );
        ssrMqetpmvFlowService.sendFlowMessage(
            mqetpmvProperties.getMfrFlowTaskOutProfile(),
            message,
            eno,
            serviceCode,
            getIntegrationFlowType(),
            getExportXmlDirectory(),
            mfrFlowData
        );
    }
}
