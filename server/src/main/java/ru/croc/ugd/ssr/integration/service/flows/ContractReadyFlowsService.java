package ru.croc.ugd.ssr.integration.service.flows;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.IntegrationLog;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.builder.IntegrationLogBuilder;
import ru.croc.ugd.ssr.builder.ResettlementHistoryBuilder;
import ru.croc.ugd.ssr.integration.service.flows.mfr.dgptomfr.ContractProjectInfoMfrFlowService;
import ru.croc.ugd.ssr.integration.service.notification.ElkUserNotificationService;
import ru.croc.ugd.ssr.integration.util.XmlUtils;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPContractIssueStatusType;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPContractReadyType;
import ru.croc.ugd.ssr.model.integration.dto.FlowReceivedMessageDto;
import ru.croc.ugd.ssr.service.ChedFileService;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.flowerrorreport.FlowType;
import ru.croc.ugd.ssr.utils.PersonUtils;
import ru.reinform.cdp.security.utils.RiAuthenticationUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Обновление данных о готовых договорах. Седьмой поток.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContractReadyFlowsService {

    private final ChedFileService chedFileService;

    private final ElkUserNotificationService elkUserNotificationService;

    private final PersonDocumentService personDocumentService;

    private final FlatResettlementStatusUpdateService flatResettlementStatusUpdateService;

    private final RiAuthenticationUtils riAuthenticationUtils;

    private final BeanFactory beanFactory;

    private final XmlUtils xmlUtils;

    private final ContractProjectInfoMfrFlowService contractProjectInfoMfrFlowService;

    /**
     * Получение сообщения Сведения о готовности договора - 1 и 3.
     *
     * @param flowReceivedMessageDto сообщение из ЕТП
     * @param xml                 xml
     */
    public void receiveContractReadyRequest(
        final FlowReceivedMessageDto<SuperServiceDGPContractReadyType> flowReceivedMessageDto, final String xml
    ) {
        try {
            processContractReadyRequest(flowReceivedMessageDto, xml);
        } catch (Exception e) {
            log.error(
                "Unable to process contract ready request (eno = {}): {}",
                flowReceivedMessageDto.getEno(),
                e.getMessage(), e
            );
        }
    }

    private void processContractReadyRequest(
        FlowReceivedMessageDto<SuperServiceDGPContractReadyType> flowReceivedMessageDto, String xml
    ) {
        PersonDocument document = getPersonAndReportFlowErrorIfRequired(
            flowReceivedMessageDto.getParsedMessage().getAffairId(),
            flowReceivedMessageDto.getParsedMessage().getPersonId(),
            FlowType.FLOW_SEVEN,
            xml
        );

        if (isNull(document)) {
            return;
        }

        PersonType personData = document.getDocument().getPersonData();
        List<PersonType.Contracts.Contract> personContracts = personData.getContracts().getContract();
        final String receiveMessageOrderId = flowReceivedMessageDto.getParsedMessage().getOrderId();

        Optional<PersonType.Contracts.Contract> optionalPersonContract = personContracts
            .stream()
            .filter(contract -> contract.getOrderId().equals(receiveMessageOrderId))
            .findFirst();

        if (optionalPersonContract.isPresent()) {
            PersonType.Contracts.Contract contract = optionalPersonContract.get();
            if (isNull(contract.getFiles())) {
                contract.setFiles(new PersonType.Contracts.Contract.Files());
            }
            parseReceivedFiles(flowReceivedMessageDto.getParsedMessage(), document, contract);
            personDocumentService.updateDocument(document.getId(), document, true, true, "7 поток");
        } else {
            PersonType.Contracts.Contract contract = new PersonType.Contracts.Contract();
            contract.setMsgDateTime(LocalDateTime.now());
            contract.setOrderId(receiveMessageOrderId);
            contract.setContractStatus(flowReceivedMessageDto.getParsedMessage().getContractStatus());
            contract.setContractType(flowReceivedMessageDto.getParsedMessage().getContractType());
            contract.setContractNum(flowReceivedMessageDto.getParsedMessage().getContractNumber());
            contract.setLetterId(flowReceivedMessageDto.getParsedMessage().getLetterId());
            if (StringUtils.isNotBlank(flowReceivedMessageDto.getParsedMessage().getContractDateEnd())) {
                try {
                    contract.setContractDateEnd(
                        LocalDate.parse(flowReceivedMessageDto.getParsedMessage().getContractDateEnd())
                    );
                } catch (DateTimeParseException ex) {
                    log.warn(
                        "Не удалось распарсить дату от ДГИ {}",
                        flowReceivedMessageDto.getParsedMessage().getContractDateEnd()
                    );
                }
            }
            contract.setDataId(UUID.randomUUID().toString());
            contract.setFiles(new PersonType.Contracts.Contract.Files());
            parseReceivedFiles(flowReceivedMessageDto.getParsedMessage(), document, contract);
            personContracts.add(contract);

            final ResettlementHistoryBuilder historyBuilder = beanFactory.getBean(ResettlementHistoryBuilder.class);
            historyBuilder.addEventId("24")
                .addDataId(contract.getDataId())
                .addAnnotation(
                    "Получен договор для подписания. Тип: "
                        + getAnnotationByType(flowReceivedMessageDto.getParsedMessage().getContractType())
                );
            personData.getResettlementHistory().add(historyBuilder.build());

            if (flowReceivedMessageDto.getParsedMessage().getContractStatus().equals("1")) {
                personData.setRelocationStatus("7");
            } else {
                personData.setRelocationStatus("6");
            }

            final IntegrationLogBuilder logBuilder = beanFactory.getBean(IntegrationLogBuilder.class);
            logBuilder
                .addEno(flowReceivedMessageDto.getEno())
                .addEventId("SuperServiceDGPContractReady")
                .addFileId(xmlUtils.saveXmlToAlfresco(xml, document.getFolderId()));
            if (personData.getIntegrationLog() == null) {
                personData.setIntegrationLog(new IntegrationLog());
            }
            personData.getIntegrationLog().getItem().add(logBuilder.build());

            PersonDocument personDocument = personDocumentService.updateDocument(
                document.getId(), document, true, true, "7 поток"
            );

            flatResettlementStatusUpdateService.updateFlatResettlementStatus(personData, "5");

            if (flowReceivedMessageDto.isShouldSendNotifications()) {
                PersonUtils.getLastOfferLetter(personData)
                    .ifPresent(offerLetter -> {
                        try {
                            elkUserNotificationService.sendNotificationContractReady(
                                personDocument,
                                true,
                                receiveMessageOrderId,
                                offerLetter.getLetterId()
                            );
                        } catch (Exception e) {
                            log.debug(
                                "Couldn't run sendContractReadyNotification (eno = {}): {}",
                                flowReceivedMessageDto.getEno(),
                                e.getMessage(),
                                e
                            );
                        }
                    });
            } else {
                log.debug(
                    "Skip sendContractReadyNotification (eno = {}): message received as part of affairCollation",
                    flowReceivedMessageDto.getEno()
                );
            }
        }

        contractProjectInfoMfrFlowService.sendContractProjectInfo(personData, receiveMessageOrderId);
    }

    private void parseReceivedFiles(
        SuperServiceDGPContractReadyType parseReceiveMessage,
        PersonDocument document,
        PersonType.Contracts.Contract contract
    ) {
        if (nonNull(parseReceiveMessage.getRTFContractToSign())
            && !parseReceiveMessage.getRTFContractToSign().isEmpty()) {
            final String chedFileId = parseReceiveMessage.getRTFContractToSign().replace("{", "").replace("}", "");
            PersonType.Contracts.Contract.Files.File file = new PersonType.Contracts.Contract.Files.File();
            file.setFileLink(chedFileService.extractFileFromChedAndGetFileLink(chedFileId, document.getFolderId()));
            file.setFileType("2");
            contract.getFiles().getFile().add(file);
        }
        if (nonNull(parseReceiveMessage.getRTFActToSign()) && !parseReceiveMessage.getRTFActToSign().isEmpty()) {
            final String chedFileId = parseReceiveMessage.getRTFActToSign().replace("{", "").replace("}", "");
            PersonType.Contracts.Contract.Files.File file = new PersonType.Contracts.Contract.Files.File();
            file.setFileLink(
                chedFileService.extractFileFromChedAndGetFileLink(chedFileId, document.getFolderId()));
            file.setFileType("5");
            contract.getFiles().getFile().add(file);
        }
    }

    /**
     * Получение сообщения Сведения о выдаче проекта договора - 21 и 23.
     *
     * @param flowReceivedMessageDto сообщение из ЕТП
     * @param xml                 xml
     */
    public void receiveContractIssueRequest(
        final FlowReceivedMessageDto<SuperServiceDGPContractIssueStatusType> flowReceivedMessageDto, final String xml
    ) {
        try {
            processContractIssueRequest(flowReceivedMessageDto, xml);
        } catch (Exception e) {
            log.error(
                "Unable to process contract issue request (eno = {}): {}",
                flowReceivedMessageDto.getEno(),
                e.getMessage(),
                e
            );
        }
    }

    private void processContractIssueRequest(
        FlowReceivedMessageDto<SuperServiceDGPContractIssueStatusType> flowReceivedMessageDto, String xml
    ) {
        PersonDocument document = getPerson(
            flowReceivedMessageDto.getParsedMessage().getAffairId(),
            flowReceivedMessageDto.getParsedMessage().getPersonId()
        );
        if (isNull(document)) {
            return;
        }

        PersonType.Contracts.Contract contract = new PersonType.Contracts.Contract();
        contract.setMsgDateTime(LocalDateTime.now());
        contract.setOrderId(flowReceivedMessageDto.getParsedMessage().getOrderId());
        contract.setIssueDate(flowReceivedMessageDto.getParsedMessage().getIssueDate());
        contract.setContractStatus(flowReceivedMessageDto.getParsedMessage().getStatus());
        contract.setDataId(UUID.randomUUID().toString());
        document.getDocument().getPersonData().getContracts().getContract().add(contract);

        final ResettlementHistoryBuilder historyBuilder = beanFactory.getBean(ResettlementHistoryBuilder.class);
        historyBuilder.addEventId("13")
            .addDataId(contract.getDataId())
            .addAnnotation("Проект договора выдан жителю.");

        document.getDocument().getPersonData().getResettlementHistory().add(historyBuilder.build());

        document.getDocument().getPersonData().setRelocationStatus("8");

        final IntegrationLogBuilder logBuilder = beanFactory.getBean(IntegrationLogBuilder.class);
        logBuilder
            .addEno(flowReceivedMessageDto.getEno())
            .addEventId("SuperServiceDGPContractIssueStatus")
            .addFileId(xmlUtils.saveXmlToAlfresco(xml, document.getFolderId()));
        if (document.getDocument().getPersonData().getIntegrationLog() == null) {
            document.getDocument().getPersonData().setIntegrationLog(new IntegrationLog());
        }
        document.getDocument().getPersonData().getIntegrationLog().getItem().add(logBuilder.build());

        personDocumentService.updateDocument(document.getId(), document, true, true, "7 поток");
    }

    /**
     * Получить жителя с контрактами.
     *
     * @param affairId ид семьи
     * @param personId ид жителя
     * @return житель
     */
    public PersonDocument getPerson(String affairId, String personId) {
        return getPersonAndReportFlowErrorIfRequired(affairId, personId, null, null);
    }

    public PersonDocument getPersonAndReportFlowErrorIfRequired(
        String affairId, String personId, FlowType flowType, String xml
    ) {
        riAuthenticationUtils.setSecurityContextByServiceuser();

        final Optional<PersonDocument> personDocumentOptional = personDocumentService
            .fetchOneByPersonIdAndAffairIdAndReportFlowErrorIfRequired(personId, affairId, flowType, xml);

        if (!personDocumentOptional.isPresent()) {
            return null;
        }

        PersonDocument personDocument = personDocumentOptional.get();

        if (personDocument.getDocument().getPersonData().getContracts() == null) {
            personDocument.getDocument().getPersonData().setContracts(new PersonType.Contracts());
        }

        return personDocument;
    }

    /**
     * Получить аннотация по типу.
     *
     * @param type тип
     * @return аннотация
     */
    public String getAnnotationByType(String type) {
        String annotation = "";
        switch (type) {
            case "39":
                annotation = "Переход права собственности на равнозначное жилое помещение";
                break;
            case "41":
                annotation = "Передача равнозначного жилого помещения";
                break;
            case "8":
                annotation = "Социальный найм";
                break;
            case "43":
                annotation = "Краткосрочный найм (реновация)";
                break;
            case "45":
                annotation = "Равноценное возмещение в денежной форме";
                break;
            case "46":
                annotation = "Равноценное возмещение в натуральной форме";
                break;
            default:
                break;
        }

        return annotation;
    }

}
