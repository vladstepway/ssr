package ru.croc.ugd.ssr.integration.service.flows;

import static java.util.Optional.ofNullable;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.IntegrationLog;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.builder.IntegrationLogBuilder;
import ru.croc.ugd.ssr.builder.ResettlementHistoryBuilder;
import ru.croc.ugd.ssr.integration.config.IntegrationProperties;
import ru.croc.ugd.ssr.integration.config.IntegrationPropertyConfig;
import ru.croc.ugd.ssr.integration.eno.EnoCreator;
import ru.croc.ugd.ssr.integration.eno.EnoSequenceCode;
import ru.croc.ugd.ssr.integration.util.MessageUtils;
import ru.croc.ugd.ssr.integration.util.XmlUtils;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPSignActStatusType;
import ru.croc.ugd.ssr.mq.interop.MqSender;
import ru.croc.ugd.ssr.mq.interop.QueueProperties;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.reinform.cdp.exception.RINotFoundException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Сервис для отправки в дги сообщений по 11 потоку.
 * Сведения о подписании Акта приема-передачи жилого помещения в АИС РСМ.
 */
@Service
@RequiredArgsConstructor
public class SignActStatusFlowService {

    private static final String CONTRACT_NOT_FOUND = "Для жителя personId {0}, Не найден договор orderId {1}";

    private final BeanFactory beanFactory;
    private final EnoCreator enoCreator;
    private final IntegrationPropertyConfig propertyConfig;
    private final IntegrationProperties integrationProperties;
    private final MessageUtils messageUtils;
    private final MqSender mqSender;
    private final PersonDocumentService personDocumentService;
    private final QueueProperties queueProperties;
    private final XmlUtils xmlUtils;

    /**
     * Отправить сведения о статусе подписания акта приема передачи жилого помещения.
     *
     * @param personId ид жителя
     * @param info сведения о статусе подписания
     */
    public void sendSignedActStatus(final String personId, final SuperServiceDGPSignActStatusType info) {
        final PersonDocument personDocument = personDocumentService.fetchDocument(personId);

        sendSignedActStatus(personDocument, info);
    }

    public void sendSignedActStatus(final PersonDocument personDocument, final SuperServiceDGPSignActStatusType info) {
        final String personId = personDocument.getId();
        final ResettlementHistoryBuilder historyBuilder = beanFactory.getBean(ResettlementHistoryBuilder.class);
        final PersonType personData = personDocument.getDocument().getPersonData();

        final String eno = enoCreator.generateEtpMvEnoNumber(propertyConfig.getSignActStatus(),
            EnoSequenceCode.UGD_SSR_ENO_DGI_ACT_SIGNED_SEQ);
        final String taskMessage = messageUtils.createCoordinateTaskMessage(eno, info);
        xmlUtils.writeXmlFile(taskMessage, integrationProperties.getXmlExportFlowEleventh());
        mqSender.send(queueProperties.getSignedAct(), taskMessage);

        final PersonType.Contracts.Contract contract = Optional.ofNullable(personData.getContracts())
            .map(PersonType.Contracts::getContract)
            .map(contracts -> contracts.stream()
                .filter(contract1 -> info.getOrderId().equals(contract1.getOrderId()))
                .findAny()
                .orElseThrow(() -> getRiNotFoundException(CONTRACT_NOT_FOUND, personId, info.getOrderId())))
            .orElseThrow(() -> getRiNotFoundException(CONTRACT_NOT_FOUND, personId, info.getOrderId()));

        historyBuilder.addEventId("25")
            .addDataId(contract.getDataId())
            .addAnnotation("Житель подписал акт приема-передачи жилого помещения. Дата подписания: "
                + formatDate(contract.getActSignDate()));
        personData.getResettlementHistory().add(historyBuilder.build());

        final IntegrationLogBuilder logBuilder = beanFactory.getBean(IntegrationLogBuilder.class);
        logBuilder.addEno(eno)
            .addEventId("SuperServiceDGPSignActStatus")
            .addFileId(xmlUtils.saveXmlToAlfresco(taskMessage, personDocument.getFolderId()));
        if (personData.getIntegrationLog() == null) {
            personData.setIntegrationLog(new IntegrationLog());
        }
        personData.getIntegrationLog().getItem().add(logBuilder.build());

        personDocumentService.updateDocument(
            personDocument.getId(), personDocument, true, true, "11 поток"
        );
    }

    private String formatDate(final LocalDate date) {
        return ofNullable(date)
            .map(e -> e.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
            .orElse("-");
    }

    @NotNull
    private RINotFoundException getRiNotFoundException(String messagePattern, String personId, String orderId) {
        return new RINotFoundException(messagePattern, personId, orderId);
    }

}
