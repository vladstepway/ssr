package ru.croc.ugd.ssr.integration.service.flows;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.croc.ugd.ssr.IntegrationLog;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.builder.IntegrationLogBuilder;
import ru.croc.ugd.ssr.integration.config.IntegrationProperties;
import ru.croc.ugd.ssr.integration.config.IntegrationPropertyConfig;
import ru.croc.ugd.ssr.integration.eno.EnoCreator;
import ru.croc.ugd.ssr.integration.eno.EnoSequenceCode;
import ru.croc.ugd.ssr.integration.mqetpmv.MqetpmvProperties;
import ru.croc.ugd.ssr.integration.service.mqetpmv.flow.SsrMqetpmvFlowService;
import ru.croc.ugd.ssr.integration.util.MessageUtils;
import ru.croc.ugd.ssr.integration.util.XmlUtils;
import ru.croc.ugd.ssr.integrationflow.IntegrationFlowType;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPTradeAdditionType;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.trade.PersonInfoType;
import ru.croc.ugd.ssr.trade.TradeAdditionType;

/**
 *  Сведения по докупке и компенсации. Поток 17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TradeAdditionFlowsService {
    @Value("${integration.etp-mv.trade-addition.enable:false}")
    private Boolean enableFlow;

    private final MessageUtils messageUtils;
    private final IntegrationPropertyConfig propertyConfig;
    private final IntegrationProperties integrationProperties;
    private final XmlUtils xmlUtils;
    private final PersonDocumentService personDocumentService;
    private final BeanFactory beanFactory;
    private final EnoCreator enoCreator;
    private final MqetpmvProperties mqetpmvProperties;
    private final SsrMqetpmvFlowService ssrMqetpmvFlowService;

    public void sendTradeAddition(final SuperServiceDGPTradeAdditionType tradeAdditionPayload) {
        if (!enableFlow) {
            return;
        }
        Assert.notNull(tradeAdditionPayload, "tradeAdditionPayload is null");

        final String eno = enoCreator.generateEtpMvEnoNumber(
            propertyConfig.getTradeAddition(),
            EnoSequenceCode.UGD_SSR_ENO_DGI_TRADE_ADDITION_SEQ
        );
        final String taskMessage = messageUtils.createCoordinateTaskMessage(eno, tradeAdditionPayload);
        ssrMqetpmvFlowService.sendFlowMessage(
            mqetpmvProperties.getTradeAdditionFlowTaskOutProfile(),
            taskMessage,
            eno,
            propertyConfig.getTradeAddition(),
            IntegrationFlowType.DGP_TO_DGI_TRADE_ADDITION_INFO,
            integrationProperties.getXmlExportFlowTradeAddition()
        );

        tradeAdditionPayload.getTradeAdditionInfo()
            .forEach(tradeAdditions -> logEventToPersons(eno, taskMessage, tradeAdditions.getTradeAdditionTypeData()));
    }

    private void logEventToPersons(
        final String eno,
        final String taskMessage,
        final TradeAdditionType tradeAdditionType
    ) {
        final String affairId = tradeAdditionType.getAffairId();
        tradeAdditionType.getPersonsInfo()
            .stream()
            .map(PersonInfoType::getPersonId)
            .forEach(personId ->
                personDocumentService.fetchOneByPersonIdAndAffairId(personId, affairId)
                    .ifPresent(personDocument -> logEventToPerson(eno, taskMessage, personDocument)));
    }

    private void logEventToPerson(
        final String eno,
        final String taskMessage,
        final PersonDocument personDocument
    ) {
        final PersonType personData = personDocument.getDocument().getPersonData();
        final IntegrationLogBuilder logBuilder = beanFactory.getBean(IntegrationLogBuilder.class);
        logBuilder.addEno(eno)
            .addEventId("SuperServiceDGPTradeAddition")
            .addFileId(xmlUtils.saveXmlToAlfresco(taskMessage, personDocument.getFolderId()));
        if (personData.getIntegrationLog() == null) {
            personData.setIntegrationLog(new IntegrationLog());
        }
        personData.getIntegrationLog().getItem().add(logBuilder.build());
        personDocumentService.updateDocument(personDocument, "17 поток");
    }
}
