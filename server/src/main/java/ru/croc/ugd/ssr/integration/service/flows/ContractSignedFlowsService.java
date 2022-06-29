package ru.croc.ugd.ssr.integration.service.flows;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.IntegrationLog;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.PersonType.Contracts;
import ru.croc.ugd.ssr.builder.IntegrationLogBuilder;
import ru.croc.ugd.ssr.builder.ResettlementHistoryBuilder;
import ru.croc.ugd.ssr.dto.RealEstateDataAndFlatInfoDto;
import ru.croc.ugd.ssr.integration.config.IntegrationProperties;
import ru.croc.ugd.ssr.integration.config.IntegrationPropertyConfig;
import ru.croc.ugd.ssr.integration.eno.EnoCreator;
import ru.croc.ugd.ssr.integration.eno.EnoSequenceCode;
import ru.croc.ugd.ssr.integration.service.flows.mfr.dgptomfr.ContractInfoMfrFlowService;
import ru.croc.ugd.ssr.integration.service.notification.ElkUserNotificationService;
import ru.croc.ugd.ssr.integration.util.MessageUtils;
import ru.croc.ugd.ssr.integration.util.XmlUtils;
import ru.croc.ugd.ssr.model.CipDocument;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPCourtAndMoneyType;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPSettleFlatInfoType;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPSignAgreementStatusType;
import ru.croc.ugd.ssr.model.integration.dto.FlowReceivedMessageDto;
import ru.croc.ugd.ssr.mq.interop.MqSender;
import ru.croc.ugd.ssr.mq.interop.QueueProperties;
import ru.croc.ugd.ssr.service.CapitalConstructionObjectService;
import ru.croc.ugd.ssr.service.FlatService;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.cip.CipService;
import ru.croc.ugd.ssr.service.flowerrorreport.FlowType;
import ru.croc.ugd.ssr.utils.RealEstateUtils;
import ru.reinform.cdp.exception.RINotFoundException;
import ru.reinform.cdp.utils.rest.utils.SendRestUtils;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Сведения о подписании договора. Восьмой поток.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContractSignedFlowsService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final String EVENT = "0";
    private static final String REMOVABLE_STATUS = "6";
    private static final String CONTRACT_NOT_FOUND = "Для жителя personId {0}, Не найден договор orderId {1}";

    private final MessageUtils messageUtils;
    private final IntegrationPropertyConfig propertyConfig;
    private final IntegrationProperties integrationProperties;
    private final XmlUtils xmlUtils;
    private final MqSender mqSender;
    private final QueueProperties queueProperties;
    private final PersonDocumentService personDocumentService;
    private final FlatResettlementStatusUpdateService flatResettlementStatusUpdateService;
    private final BeanFactory beanFactory;
    private final SendRestUtils restUtils;
    private final ElkUserNotificationService elkUserNotificationService;
    private final CipService cipService;
    private final EnoCreator enoCreator;
    private final CapitalConstructionObjectService ccoService;
    private final FlatService flatService;
    private final ContractInfoMfrFlowService contractInfoMfrFlowService;
    private final RemovableStatusUpdateService removableStatusUpdateService;

    @Value("${common.playground.url}")
    private String playgroundUrl;

    /**
     * Отправка сведений о статусе подписания договора.
     *
     * @param personId ид подписавшего договор.
     * @param info     сведения о подписании договора.
     */
    public void sendSignedContract(final String personId, final SuperServiceDGPSignAgreementStatusType info) {
        final PersonDocument personDocument = personDocumentService
            .fetchOneByPersonIdAndAffairId(personId, info.getAffairId())
            .orElseThrow(() -> getRiNotFoundException("Person document doesnt found personId {0} affairId {1}",
                personId,
                info.getAffairId()));

        sendSignedContract(personDocument, info);
    }

    public void sendSignedContract(
        final PersonDocument personDocument, final SuperServiceDGPSignAgreementStatusType info
    ) {
        final String personId = personDocument.getId();

        final PersonType personData = personDocument.getDocument().getPersonData();
        final Contracts.Contract contract = Optional.ofNullable(personData.getContracts())
            .map(Contracts::getContract)
            .map(contracts -> contracts.stream()
                .filter(contract1 -> info.getOrderId().equals(contract1.getOrderId()))
                .findAny()
                .orElseThrow(() -> getRiNotFoundException(CONTRACT_NOT_FOUND, personId, info.getOrderId())))
            .orElseThrow(() -> getRiNotFoundException(CONTRACT_NOT_FOUND, personId, info.getOrderId()));

        final String eno = enoCreator.generateEtpMvEnoNumber(propertyConfig.getReleaseFlat(),
            EnoSequenceCode.UGD_SSR_ENO_DGI_CONTRACT_SIGNED_SEQ);
        final String taskMessage = messageUtils.createCoordinateTaskMessage(eno, info);
        xmlUtils.writeXmlFile(taskMessage, integrationProperties.getXmlExportFlowEighth());
        mqSender.send(queueProperties.getSignedContract(), taskMessage);

        final LocalDate contractSignDate = contract.getContractSignDate();
        if (nonNull(contractSignDate)) {
            final String formattedContractSignDate = FORMATTER.format(contractSignDate);

            personDocument.addResettlementHistory(
                "14",
                contract.getDataId(),
                "Житель подписал договор. Дата подписания: " + formattedContractSignDate
            );
        }

        final IntegrationLogBuilder logBuilder = beanFactory.getBean(IntegrationLogBuilder.class);
        logBuilder.addEno(eno)
            .addEventId("SuperServiceDGPSignAgreementStatus")
            .addFileId(xmlUtils.saveXmlToAlfresco(taskMessage, personDocument.getFolderId()));
        if (personData.getIntegrationLog() == null) {
            personData.setIntegrationLog(new IntegrationLog());
        }
        personData.getIntegrationLog().getItem().add(logBuilder.build());

        personDocumentService.updateDocument(personDocument, "8 поток");
        flatResettlementStatusUpdateService.updateFlatResettlementStatus(personData, "6");

        contractInfoMfrFlowService.sendContractInfo(personDocument.getDocument().getPersonData(), info.getOrderId());
    }

    @NotNull
    private RINotFoundException getRiNotFoundException(String mesPattern, String personId, String orderId) {
        return new RINotFoundException(mesPattern, personId, orderId);
    }

    /**
     * Получение сведений о заселяемой квартире. Сведения о заселяемой квартире - 6 и 8.
     *
     * @param flowReceivedMessageDto заселяемые квартиры.
     * @param xml            xml
     */
    public void receiveSettleFlatInfo(
        final FlowReceivedMessageDto<SuperServiceDGPSettleFlatInfoType> flowReceivedMessageDto, final String xml
    ) {
        try {
            removableStatusUpdateService.updateStatusFlat(flowReceivedMessageDto.getParsedMessage());
            processSettleFlatInfo(flowReceivedMessageDto, xml);
        } catch (Exception e) {
            log.error(
                "Unable to process settle flat info (eno = {}): {}",
                flowReceivedMessageDto.getEno(),
                e.getMessage(),
                e
            );
        }
    }

    private void processSettleFlatInfo(
        final FlowReceivedMessageDto<SuperServiceDGPSettleFlatInfoType> flowReceivedMessageDto, final String xml
    ) {
        final ResettlementHistoryBuilder historyBuilder = beanFactory.getBean(ResettlementHistoryBuilder.class);
        historyBuilder.addEventId("18");
        Optional<PersonDocument> personDocumentOptional =
            personDocumentService.fetchOneByPersonIdAndAffairIdAndReportFlowErrorIfRequired(
                flowReceivedMessageDto.getParsedMessage().getPersonId(),
                flowReceivedMessageDto.getParsedMessage().getAffairId(),
                FlowType.FLOW_EIGHT,
                xml
            );
        if (personDocumentOptional.isPresent()) {
            final PersonDocument document = personDocumentOptional.get();

            final PersonType.NewFlatInfo.NewFlat newFlat = new PersonType.NewFlatInfo.NewFlat();
            final String dataId = initNewFlat(newFlat, flowReceivedMessageDto.getParsedMessage());
            final List<PersonType.NewFlatInfo.NewFlat> existingFlats = Optional
                .ofNullable(document.getDocument().getPersonData().getNewFlatInfo())
                .orElseGet(() -> {
                    final PersonType.NewFlatInfo newFlatInfo = new PersonType.NewFlatInfo();
                    document.getDocument().getPersonData().setNewFlatInfo(newFlatInfo);
                    return newFlatInfo;
                }).getNewFlat();

            final Optional<PersonType.NewFlatInfo.NewFlat> matchedExistingFlatOpt = existingFlats
                .stream()
                .filter(existingFlat -> Objects.equals(existingFlat.getCcoUnom(), newFlat.getCcoUnom())
                    && Objects.equals(existingFlat.getCcoFlatNum(), newFlat.getCcoFlatNum()))
                .findAny();
            if (!matchedExistingFlatOpt.isPresent()) {
                existingFlats.add(newFlat);
            } else {
                final PersonType.NewFlatInfo.NewFlat matchedExistingFlat = matchedExistingFlatOpt.get();
                initNewFlat(matchedExistingFlat, flowReceivedMessageDto.getParsedMessage());
            }

            historyBuilder.addDataId(dataId)
                .addAnnotation("Житель переезжает в дом УНОМ: "
                    + newFlat.getCcoUnom()
                    + " и квартиру: "
                    + newFlat.getCcoFlatNum());

            PersonType personData = document.getDocument().getPersonData();
            document.getDocument().getPersonData().getResettlementHistory().add(historyBuilder.build());

            final IntegrationLogBuilder logBuilder = beanFactory.getBean(IntegrationLogBuilder.class);
            logBuilder.addEno(flowReceivedMessageDto.getEno())
                .addEventId("SuperServiceDGPSettleFlatInfo")
                .addFileId(xmlUtils.saveXmlToAlfresco(xml, document.getFolderId()));
            if (personData.getIntegrationLog() == null) {
                personData.setIntegrationLog(new IntegrationLog());
            }
            personData.getIntegrationLog().getItem().add(logBuilder.build());

            personDocumentService.updateDocument(document.getId(), document, true, true, "8 поток");

            restUtils.sendJsonRequest(playgroundUrl + integrationProperties.getPsCreateIfNotExistFlatController(),
                HttpMethod.POST,
                null,
                String.class,
                newFlat.getCcoUnom(),
                newFlat.getCcoFlatNum(),
                REMOVABLE_STATUS);

            final String newFlatAddress =
                ccoService.getAddressByCcoUnom(
                    newFlat.getCcoUnom());

            String ccoIdByUnom = ccoService.getCcoIdByUnom(newFlat.getCcoUnom().toString());
            if (isNull(ccoIdByUnom)) {
                log.error("Не найден ОКС по УНОМ: {}, уведомление жителю не отправлено.",
                    newFlat.getCcoUnom().toString());
                return;
            }
            final CipDocument cip = cipService.fetchOneByCcoId(ccoIdByUnom);

            final RealEstateDataAndFlatInfoDto realEstateDataAndFlatInfoDto = flatService
                .fetchRealEstateAndFlat(personData.getFlatID());

            final String realEstateAddress = RealEstateUtils.getRealEstateAddress(realEstateDataAndFlatInfoDto);
            final String oldFlatAddress = RealEstateUtils.getFlatAddress(realEstateDataAndFlatInfoDto);

            // найдем последнее письмо с прдложением
            PersonType.OfferLetters.OfferLetter letter = personData.getOfferLetters().getOfferLetter().get(
                personData.getOfferLetters().getOfferLetter().size() - 1
            );
            if (flowReceivedMessageDto.isShouldSendNotifications()) {
                try {
                    elkUserNotificationService.sendNotificationSignedContract(
                        document,
                        true,
                        cip.getId(),
                        realEstateAddress,
                        newFlatAddress,
                        oldFlatAddress,
                        newFlat.getOrderId(),
                        letter.getLetterId()
                    );
                } catch (Exception e) {
                    log.debug(
                        "Couldn't run sendSignedContractNotification (eno = {}): {}",
                        flowReceivedMessageDto.getEno(),
                        e.getMessage(),
                        e
                    );
                }
            } else {
                log.debug(
                    "Skip sendSignedContractNotification (eno = {}): message received as part of affairCollation",
                    flowReceivedMessageDto.getEno()
                );
            }
        }
    }

    private String initNewFlat(final PersonType.NewFlatInfo.NewFlat newFlat,
                               SuperServiceDGPSettleFlatInfoType settleFlatInfo) {
        final String dataId = UUID.randomUUID().toString();

        newFlat.setMsgDateTime(LocalDateTime.now());
        newFlat.setOrderId(settleFlatInfo.getOrderId());
        newFlat.setEvent(EVENT);
        if (settleFlatInfo.getNewCadnum() != null) {
            newFlat.setCcoCadNum(settleFlatInfo.getNewCadnum());
        }
        newFlat.setCcoUnom(new BigInteger(settleFlatInfo.getNewUnom()));
        newFlat.setCcoAddress(ccoService.getCcoAddressByUnom(settleFlatInfo.getNewUnom()));
        newFlat.setCcoFlatNum(settleFlatInfo.getFlatNumber());
        newFlat.setDataId(dataId);
        return dataId;
    }

    /**
     * Сведения о переселении по решению суда и равноценном возмещении - 26 и 28.
     *
     * @param flowReceivedMessageDtos решение суда.
     * @param xml                      xml
     * @param relocationStatus         тип решения
     */
    public void receiveJudgementResolutionAndContractRegister(
        List<FlowReceivedMessageDto<SuperServiceDGPCourtAndMoneyType>> flowReceivedMessageDtos,
        String xml,
        String relocationStatus
    ) {
        final ResettlementHistoryBuilder historyBuilder = beanFactory.getBean(ResettlementHistoryBuilder.class);

        flowReceivedMessageDtos.forEach(infoJudgementResolution -> {
            try {
                processJudgementResolutionAndContractRegister(
                    infoJudgementResolution, xml, relocationStatus, historyBuilder
                );
            } catch (Exception e) {
                log.error(
                    "Unable to process judgement resolution (eno = {}): {}",
                    infoJudgementResolution.getEno(),
                    e.getMessage(),
                    e
                );
            }
        });
    }

    private void processJudgementResolutionAndContractRegister(
        final FlowReceivedMessageDto<SuperServiceDGPCourtAndMoneyType> flowReceivedMessageDto,
        final String xml,
        final String relocationStatus,
        final ResettlementHistoryBuilder historyBuilder
    ) {
        Optional<PersonDocument> personDocumentOptional =
            personDocumentService.fetchOneByPersonIdAndAffairId(
                flowReceivedMessageDto.getParsedMessage().getPersonId(),
                flowReceivedMessageDto.getParsedMessage().getAffairId()
            );
        if (personDocumentOptional.isPresent()) {
            final PersonDocument document = personDocumentOptional.get();
            final PersonType data = document.getDocument().getPersonData();
            if (data.getContracts() != null) {
                data.getContracts()
                    .getContract()
                    .stream()
                    .filter(contract -> flowReceivedMessageDto.getParsedMessage().getOrderId()
                        .equals(contract.getOrderId()))
                    .forEach(contract -> {
                        if ("1".equals(flowReceivedMessageDto.getParsedMessage().getAgrEvent())) {
                            data.setRelocationStatus("4");
                            contract.setContractStatus("4");
                        } else if ("2".equals(flowReceivedMessageDto.getParsedMessage().getAgrEvent())) {
                            data.setRelocationStatus("5");
                            contract.setContractStatus("5");
                        }
                    });
            }

            if (data.getNewFlatInfo() == null) {
                data.setNewFlatInfo(new PersonType.NewFlatInfo());
            }
            final Optional<PersonType.NewFlatInfo.NewFlat> flatOptional = data.getNewFlatInfo()
                .getNewFlat()
                .stream()
                .filter(newFlat -> newFlat.getCcoUnom().toString()
                    .equals(flowReceivedMessageDto.getParsedMessage().getNewUnom())
                    && newFlat.getCcoCadNum().equals(flowReceivedMessageDto.getParsedMessage().getNewCadnum())
                    && newFlat.getCcoFlatNum().equals(flowReceivedMessageDto.getParsedMessage().getFlatNumber()))
                .findAny();

            final String dataId;
            final PersonType.NewFlatInfo.NewFlat flat;

            if (flatOptional.isPresent()) {
                flat = flatOptional.get();
                dataId = flat.getDataId();
                setFlatProperties(flowReceivedMessageDto.getParsedMessage(), flat);
            } else {
                flat = new PersonType.NewFlatInfo.NewFlat();
                dataId = UUID.randomUUID().toString();
                flat.setDataId(dataId);
                setFlatProperties(flowReceivedMessageDto.getParsedMessage(), flat);
                data.getNewFlatInfo().getNewFlat().add(flat);
            }

            historyBuilder.addDataId(dataId);

            if ("1".equals(flowReceivedMessageDto.getParsedMessage().getAgrEvent())) {
                historyBuilder.addEventId("16");
                historyBuilder.addAnnotation("Получено уведомление о заключении договора с ДГИ");
            } else if ("2".equals(flowReceivedMessageDto.getParsedMessage().getAgrEvent())) {
                historyBuilder.addEventId("17");
                historyBuilder.addAnnotation("Дата судебного решения: " + flat.getCourtDate());
            }

            data.getResettlementHistory().add(historyBuilder.build());

            final IntegrationLogBuilder logBuilder = beanFactory.getBean(IntegrationLogBuilder.class);
            logBuilder.addEno(flowReceivedMessageDto.getEno())
                .addEventId("SuperServiceDGPCourtAndMoney")
                .addFileId(xmlUtils.saveXmlToAlfresco(xml, document.getFolderId()));
            if (data.getIntegrationLog() == null) {
                data.setIntegrationLog(new IntegrationLog());
            }
            data.getIntegrationLog().getItem().add(logBuilder.build());
            data.setRelocationStatus(relocationStatus);

            personDocumentService.updateDocument(document.getId(), document, true, true, "8 поток");
        }
    }

    private void setFlatProperties(SuperServiceDGPCourtAndMoneyType info, PersonType.NewFlatInfo.NewFlat newFlat) {
        newFlat.setMsgDateTime(LocalDateTime.now());
        newFlat.setOrderId(info.getOrderId());
        newFlat.setEvent(info.getAgrEvent());
        newFlat.setCcoCadNum(info.getNewCadnum());
        final String newUnom = info.getNewUnom();
        if (nonNull(newUnom)) {
            newFlat.setCcoUnom(new BigInteger(newUnom));
            newFlat.setCcoAddress(ccoService.getCcoAddressByUnom(newUnom));
        }
        newFlat.setCcoFlatNum(info.getFlatNumber());
        newFlat.setAgrDate(info.getAgrData());
        newFlat.setAgrType(info.getAgrType());
        newFlat.setAgrNum(info.getAgrNum());
        newFlat.setCourtDate(info.getCourtData());
        newFlat.setSignedAgrFile(info.getSignedAgrFile());
    }
}
