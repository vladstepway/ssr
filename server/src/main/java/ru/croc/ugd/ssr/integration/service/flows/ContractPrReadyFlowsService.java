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
import ru.croc.ugd.ssr.integration.service.notification.ElkUserNotificationService;
import ru.croc.ugd.ssr.integration.util.XmlUtils;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPContractPrReadyType;
import ru.croc.ugd.ssr.model.integration.dto.FlowReceivedMessageDto;
import ru.croc.ugd.ssr.service.ChedFileService;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.flowerrorreport.FlowType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Обновление данных о готовых проектах договора. 12 поток.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContractPrReadyFlowsService {

    private final ContractReadyFlowsService contractReadyFlowsService;

    private final ChedFileService chedFileService;

    private final PersonDocumentService personDocumentService;

    private final ElkUserNotificationService elkUserNotificationService;

    private final FlatResettlementStatusUpdateService flatResettlementStatusUpdateService;

    private final BeanFactory beanFactory;

    private final XmlUtils xmlUtils;

    /**
     * Получение сообщения Сведения о готовности проекта договора для ознакомления.
     *
     * @param flowReceivedMessageDto сообщение из ЕТП
     * @param xml                 xml
     */
    public void receiveContractPrReadyRequest(
        final FlowReceivedMessageDto<SuperServiceDGPContractPrReadyType> flowReceivedMessageDto, final String xml
    ) {
        try {
            processContractPrReadyRequest(flowReceivedMessageDto, xml);
        } catch (Exception e) {
            log.error(
                "Unable to process contract project ready request (eno = {}): {}",
                flowReceivedMessageDto.getEno(),
                e.getMessage(),
                e
            );
        }
    }

    private void processContractPrReadyRequest(
        FlowReceivedMessageDto<SuperServiceDGPContractPrReadyType> flowReceivedMessageDto, String xml
    ) {
        PersonDocument document = contractReadyFlowsService.getPersonAndReportFlowErrorIfRequired(
            flowReceivedMessageDto.getParsedMessage().getAffairId(),
            flowReceivedMessageDto.getParsedMessage().getPersonId(),
            FlowType.FLOW_TWELVE,
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

        if (flowReceivedMessageDto.isShouldSendNotifications()) {
            try {
                elkUserNotificationService.sendContractReadyForAcquaintingNotification(document);
            } catch (Exception e) {
                log.debug(
                    "Couldn't run sendContractPrReadyNotification (eno = {}): {}",
                    flowReceivedMessageDto.getEno(),
                    e.getMessage(),
                    e
                );
            }
        } else {
            log.debug(
                "Skip sendContractPrReadyNotification (eno = {}): message received as part of affairCollation",
                flowReceivedMessageDto.getEno()
            );
        }
        if (optionalPersonContract.isPresent()) {
            PersonType.Contracts.Contract contract = optionalPersonContract.get();
            if (isNull(contract.getFiles())) {
                contract.setFiles(new PersonType.Contracts.Contract.Files());
            }
            parseReceivedFiles(flowReceivedMessageDto.getParsedMessage(), document, contract);

            final ResettlementHistoryBuilder historyBuilder = beanFactory.getBean(ResettlementHistoryBuilder.class);
            historyBuilder.addEventId("10")
                .addDataId(contract.getDataId())
                .addAnnotation(
                    "Получен проект договора. Тип: " + contractReadyFlowsService
                        .getAnnotationByType(flowReceivedMessageDto.getParsedMessage().getContractType())
                );
            personData.getResettlementHistory().add(historyBuilder.build());

            final IntegrationLogBuilder logBuilder = beanFactory.getBean(IntegrationLogBuilder.class);
            logBuilder
                .addEno(flowReceivedMessageDto.getEno())
                .addEventId("SuperServiceDGPContractPrReady")
                .addFileId(xmlUtils.saveXmlToAlfresco(xml, document.getFolderId()));
            if (personData.getIntegrationLog() == null) {
                personData.setIntegrationLog(new IntegrationLog());
            }
            personData.getIntegrationLog().getItem().add(logBuilder.build());

            personDocumentService.updateDocument(document, "12 поток");
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
            historyBuilder.addEventId("10")
                .addDataId(contract.getDataId())
                .addAnnotation(
                    "Получен проект договора. Тип: " + contractReadyFlowsService
                        .getAnnotationByType(flowReceivedMessageDto.getParsedMessage().getContractType())
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
                .addEventId("SuperServiceDGPContractPrReady")
                .addFileId(xmlUtils.saveXmlToAlfresco(xml, document.getFolderId()));
            if (personData.getIntegrationLog() == null) {
                personData.setIntegrationLog(new IntegrationLog());
            }
            personData.getIntegrationLog().getItem().add(logBuilder.build());

            personDocumentService.updateDocument(document, "12 поток");

            flatResettlementStatusUpdateService.updateFlatResettlementStatus(personData, "5");
        }
    }

    private void parseReceivedFiles(
        SuperServiceDGPContractPrReadyType parseReceiveMessage,
        PersonDocument document,
        PersonType.Contracts.Contract contract
    ) {
        if (nonNull(parseReceiveMessage.getPdfContract())
            && !parseReceiveMessage.getPdfContract().isEmpty()) {
            final String chedFileId = parseReceiveMessage.getPdfContract().replace("{", "").replace("}", "");
            PersonType.Contracts.Contract.Files.File file = new PersonType.Contracts.Contract.Files.File();
            file.setFileLink(chedFileService.extractFileFromChedAndGetFileLink(chedFileId, document.getFolderId()));
            file.setFileType("1");
            contract.getFiles().getFile().add(file);
        }
        if (nonNull(parseReceiveMessage.getPdfProposal()) && !parseReceiveMessage.getPdfProposal().isEmpty()) {
            final String chedFileId = parseReceiveMessage.getPdfProposal().replace("{", "").replace("}", "");
            PersonType.Contracts.Contract.Files.File file = new PersonType.Contracts.Contract.Files.File();
            file.setFileLink(
                chedFileService.extractFileFromChedAndGetFileLink(chedFileId, document.getFolderId())
            );
            file.setFileType("3");
            contract.getFiles().getFile().add(file);
        }
    }

}
