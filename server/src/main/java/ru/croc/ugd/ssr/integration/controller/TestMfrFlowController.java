package ru.croc.ugd.ssr.integration.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.integration.mqetpmv.MqetpmvProperties;
import ru.croc.ugd.ssr.integration.service.flows.mfr.dgptomfr.AdministrativeDocumentsInfoMfrFlowService;
import ru.croc.ugd.ssr.integration.service.flows.mfr.dgptomfr.ContractInfoMfrFlowService;
import ru.croc.ugd.ssr.integration.service.flows.mfr.dgptomfr.ContractProjectInfoMfrFlowService;
import ru.croc.ugd.ssr.integration.service.flows.mfr.dgptomfr.PersonInfoMfrFlowService;
import ru.croc.ugd.ssr.integration.service.flows.mfr.dgptomfr.ReleaseFlatInfoMfrFlowService;
import ru.croc.ugd.ssr.integration.service.flows.mfr.dgptomfr.ResettlementInfoMfrFlowService;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.ResettlementRequestDocument;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPAdministrativeDocumentType.NewFlats.NewFlat;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPFlatFreeType;
import ru.croc.ugd.ssr.mq.listener.mfr.MfrToDgpMessageListener;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.ResettlementRequestDocumentService;
import ru.reinform.cdp.mqetpmv.model.EtpInboundMessage;

import java.time.LocalDate;

@RestController
@AllArgsConstructor
@RequestMapping("/mfr-flows")
public class TestMfrFlowController {

    private final MfrToDgpMessageListener mfrToDgpMessageListener;
    private final ResettlementInfoMfrFlowService resettlementInfoMfrFlowService;
    private final ResettlementRequestDocumentService resettlementRequestDocumentService;
    private final AdministrativeDocumentsInfoMfrFlowService administrativeDocumentsInfoMfrFlowService;
    private final PersonDocumentService personDocumentService;
    private final ContractProjectInfoMfrFlowService contractProjectInfoMfrFlowService;
    private final ContractInfoMfrFlowService contractInfoMfrFlowService;
    private final ReleaseFlatInfoMfrFlowService releaseFlatInfoMfrFlowService;
    private final PersonInfoMfrFlowService personInfoMfrFlowService;
    private final MqetpmvProperties mqetpmvProperties;

    @PostMapping(value = "/receive-mft-to-dgp-flow")
    public void receiveMftToDgpFlow(@RequestBody final String message) {
        final EtpInboundMessage etpInboundMessage = new EtpInboundMessage();
        etpInboundMessage.setMessage(message);
        mfrToDgpMessageListener.receiveMessage(mqetpmvProperties.getMfrFlowTaskIncMessageType(), etpInboundMessage);
    }

    @PostMapping(value = "/send-1-flow")
    public void send1FlowResettlementInfo(
        @RequestParam("resettlementRequestDocumentId") final String resettlementRequestDocumentId
    ) {
        final ResettlementRequestDocument resettlementRequestDocument =
            resettlementRequestDocumentService.fetchDocument(resettlementRequestDocumentId);
        resettlementInfoMfrFlowService.sendResettlementInfo(resettlementRequestDocument.getDocument().getMain());
    }

    @PostMapping(value = "/send-2-flow")
    public void send2FlowAdministrativeDocumentsInfo(
        @RequestParam("personDocumentId") final String personDocumentId,
        @RequestParam("letterId") final String letterId,
        @RequestParam("unom") final String unom,
        @RequestParam("flatNumber") final String flatNumber
    ) {
        final PersonDocument personDocument = personDocumentService.fetchDocument(personDocumentId);

        final NewFlat newFlat = new NewFlat();
        newFlat.setUnom(unom);
        newFlat.setFlatNumber(flatNumber);
        administrativeDocumentsInfoMfrFlowService.sendAdministrativeDocumentsInfo(
            personDocument.getDocument().getPersonData(), letterId, newFlat, true
        );
    }

    @PostMapping(value = "/send-3-flow")
    public void send3FlowContractProjectInfo(
        @RequestParam("personDocumentId") final String personDocumentId,
        @RequestParam("contractOrderId") final String contractOrderId
    ) {
        final PersonDocument personDocument = personDocumentService.fetchDocument(personDocumentId);
        contractProjectInfoMfrFlowService.sendContractProjectInfo(
            personDocument.getDocument().getPersonData(), contractOrderId, true
        );
    }

    @PostMapping(value = "/send-4-flow")
    public void send4FlowContractInfo(
        @RequestParam("personDocumentId") final String personDocumentId,
        @RequestParam("contractOrderId") final String contractOrderId
    ) {
        final PersonDocument personDocument = personDocumentService.fetchDocument(personDocumentId);
        contractInfoMfrFlowService.sendContractInfo(
            personDocument.getDocument().getPersonData(), contractOrderId, true
        );
    }

    @PostMapping(value = "/send-8-flow")
    public void send8FlowPersonInfo(
        @RequestParam("affairId") final String affairId,
        @RequestParam("sellId") final String sellId
    ) {
        personInfoMfrFlowService.sendPersonInfo(affairId, sellId);
    }

    @PostMapping(value = "/send-18-flow")
    public void send18FlowReleaseFlatInfo(
        @RequestParam("affairId") final String affairId,
        @RequestParam("actNumber") final String actNumber,
        @RequestParam("actDate") final String actDate
    ) {
        final SuperServiceDGPFlatFreeType info = new SuperServiceDGPFlatFreeType();
        info.setAffairId(affairId);
        info.setActNumber(actNumber);
        info.setActData(LocalDate.parse(actDate));
        releaseFlatInfoMfrFlowService.sendReleaseFlatInfo(info);
    }
}
