package ru.croc.ugd.ssr.integration.service.mqetpmv.flow;

import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.config.SystemProperties;
import ru.croc.ugd.ssr.dto.mqetpmv.flow.ExportFlowMessageDto;
import ru.croc.ugd.ssr.dto.mqetpmv.flow.FlowTypeDto;
import ru.croc.ugd.ssr.dto.mqetpmv.flow.ImportCoordinateTask;
import ru.croc.ugd.ssr.dto.mqetpmv.flow.ImportFlowMessageDto;
import ru.croc.ugd.ssr.exception.SsrException;
import ru.croc.ugd.ssr.integration.util.XmlUtils;
import ru.croc.ugd.ssr.integrationflow.IntegrationFlowStatus;
import ru.croc.ugd.ssr.integrationflow.IntegrationFlowType;
import ru.croc.ugd.ssr.integrationflow.MfrFlowData;
import ru.croc.ugd.ssr.mapper.mq.ToCoordinateStatusMessageReasonMapper;
import ru.croc.ugd.ssr.model.integration.etpmv.ArrayOfCoordinateFile;
import ru.croc.ugd.ssr.model.integration.etpmv.ArrayOfCoordinateTaskData;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateFile;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateSendTaskStatusesMessage;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateTaskData;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateTaskMessage;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateTaskStatusData;
import ru.croc.ugd.ssr.model.integration.etpmv.SendTasksMessage;
import ru.croc.ugd.ssr.model.integration.etpmv.StatusType;
import ru.croc.ugd.ssr.model.integration.etpmv.TaskType;
import ru.croc.ugd.ssr.model.integrationflow.IntegrationFlowDocument;
import ru.croc.ugd.ssr.model.integrationflow.IntegrationFlowQueueDocument;
import ru.croc.ugd.ssr.service.document.IntegrationFlowQueueDocumentService;
import ru.croc.ugd.ssr.service.integrationflow.IntegrationFlowService;
import ru.reinform.cdp.mqetpmv.api.MqetpmvRemoteService;
import ru.reinform.cdp.mqetpmv.model.EtpOutboundMessage;
import ru.reinform.cdp.utils.core.RIXmlUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Сервис по работе с поточными сообщениями, направляемыми/получаемыми через mqetpmv.
 */
@Slf4j
@Service
@AllArgsConstructor
public class DefaultSsrMqetpmvFlowService implements SsrMqetpmvFlowService {

    private final XmlUtils xmlUtils;
    private final MqetpmvRemoteService mqetpmvRemoteService;
    private final SystemProperties systemProperties;
    private final IntegrationFlowService integrationFlowService;
    private final ToCoordinateStatusMessageReasonMapper toCoordinateStatusMessageReasonMapper;
    private final IntegrationFlowQueueDocumentService integrationFlowQueueDocumentService;

    @Override
    public ExportFlowMessageDto sendFlowMessage(
        final String etpProfile,
        final String message,
        final String eno,
        final String serviceCode,
        final IntegrationFlowType integrationFlowType,
        final String flowMessageDirectory
    ) {
        return sendFlowMessage(etpProfile, message, eno, serviceCode, integrationFlowType, flowMessageDirectory, null);
    }

    @Override
    public ExportFlowMessageDto sendFlowMessage(
        final String etpProfile,
        final String message,
        final String eno,
        final String serviceCode,
        final IntegrationFlowType integrationFlowType,
        final String flowMessageDirectory,
        final MfrFlowData mfrFlowData
    ) {
        final FlowTypeDto flowTypeDto = FlowTypeDto.builder()
            .serviceCode(serviceCode)
            .integrationFlowType(integrationFlowType)
            .build();
        final IntegrationFlowDocument integrationFlowDocument = integrationFlowService.createDocument(
            eno, flowTypeDto, message, IntegrationFlowStatus.PREPARED, mfrFlowData
        );
        log.info(
            "Prepared {} flow: eno = {}, integrationFlowDocumentId = {}, etpProfile = {}, message = {}",
            integrationFlowType,
            eno,
            integrationFlowDocument.getId(),
            etpProfile,
            message
        );
        xmlUtils.writeXmlFile(message, flowMessageDirectory);
        final EtpOutboundMessage etpOutboundMessage = createEtpOutboundMessage(
            integrationFlowDocument.getId(), etpProfile, message
        );

        final String response = mqetpmvRemoteService.sendMessage(etpOutboundMessage);
        log.info(
            "Sent {} flow using mqetpmv: eno = {}, integrationFlowDocumentId = {}, response = {}",
            integrationFlowType,
            eno,
            integrationFlowDocument.getId(),
            response
        );
        final String etpMessageId = retrieveEtpMessageId(response);
        integrationFlowService.updateSentDocument(integrationFlowDocument, etpMessageId);
        return ExportFlowMessageDto.builder()
            .etpMessageId(etpMessageId)
            .response(response)
            .integrationFlowDocumentId(integrationFlowDocument.getId())
            .build();
    }

    private String retrieveEtpMessageId(final String response) {
        return response.matches("ID:.+") ? response.replaceFirst("ID:", "") : null;
    }

    private EtpOutboundMessage createEtpOutboundMessage(
        final String integrationFlowDocumentId, final String etpProfile, final String message
    ) {
        final EtpOutboundMessage etpOutboundMessage = new EtpOutboundMessage();
        etpOutboundMessage.setCorrelationID(integrationFlowDocumentId);
        etpOutboundMessage.setSystemCode(systemProperties.getSystem());
        etpOutboundMessage.setEtpProfile(etpProfile);
        etpOutboundMessage.setMessage(message);
        return etpOutboundMessage;
    }

    @Override
    public ExportFlowMessageDto sendFlowStatusMessage(
        final IntegrationFlowDocument integrationFlowDocument,
        final String etpProfile,
        final String eno,
        final Integer statusCode,
        final String statusTitle,
        final String flowStatusMessageDirectory
    ) {
        final CoordinateSendTaskStatusesMessage coordinateSendTaskStatusesMessage =
            toCoordinateStatusMessageReasonMapper.toCoordinateSendTaskStatusesMessage(eno, statusCode, statusTitle);
        final String message = RIXmlUtils.marshal(coordinateSendTaskStatusesMessage);

        integrationFlowService.savePreparedStatus(integrationFlowDocument, message, statusCode.toString(), statusTitle);

        log.info(
            "Prepared flow status using mqetpmv: eno = {}, integrationFlowDocumentId = {}, statusCode = {}, "
                + "etpProfile = {}, message = {}",
            eno,
            integrationFlowDocument.getId(),
            statusCode,
            etpProfile,
            message
        );
        xmlUtils.writeXmlFile(message, flowStatusMessageDirectory);
        final EtpOutboundMessage etpOutboundMessage = createEtpOutboundMessage(
            integrationFlowDocument.getId(), etpProfile, message
        );

        final String response = mqetpmvRemoteService.sendMessage(etpOutboundMessage);
        log.info(
            "Sent flow status using mqetpmv: eno = {}, integrationFlowDocumentId = {}, statusCode = {}, response = {}",
            eno,
            integrationFlowDocument.getId(),
            statusCode,
            response
        );
        final String etpMessageId = retrieveEtpMessageId(response);

        integrationFlowService.updateDocumentWithSentStatus(
            integrationFlowDocument, statusCode.toString(), etpMessageId
        );
        return ExportFlowMessageDto.builder()
            .etpMessageId(etpMessageId)
            .response(response)
            .integrationFlowDocumentId(integrationFlowDocument.getId())
            .build();
    }

    @Override
    public ImportFlowMessageDto receiveFlowCoordinateTaskMessage(
        final String messageType,
        final String message,
        final String serviceCode,
        final IntegrationFlowType integrationFlowType,
        final String flowMessageDirectory,
        final Function<String, CoordinateTaskMessage> parseCoordinateTaskMessage
    ) {
        return receiveFlowCoordinateTaskMessage(
            messageType,
            message,
            serviceCode,
            integrationFlowType,
            flowMessageDirectory,
            parseCoordinateTaskMessage,
            null
        );
    }

    @Override
    public ImportFlowMessageDto receiveFlowCoordinateTaskMessage(
        final String messageType,
        final String message,
        final String flowMessageDirectory,
        final Function<String, CoordinateTaskMessage> parseCoordinateTaskMessage,
        final Function<CoordinateTaskData, FlowTypeDto> retrieveFlowTypeDto
    ) {
        return receiveFlowCoordinateTaskMessage(
            messageType, message, null, null, flowMessageDirectory, parseCoordinateTaskMessage, retrieveFlowTypeDto
        );
    }

    private ImportFlowMessageDto receiveFlowCoordinateTaskMessage(
        final String messageType,
        final String message,
        final String serviceCode,
        final IntegrationFlowType integrationFlowType,
        final String directory,
        final Function<String, CoordinateTaskMessage> parseCoordinateTaskMessage,
        final Function<CoordinateTaskData, FlowTypeDto> retrieveFlowTypeDto
    ) {
        log.info("Received flow using mqetpmv: type = {}, message = {}", messageType, message);
        xmlUtils.writeXmlFile(message, directory);
        try {
            final CoordinateTaskMessage coordinateTaskMessage = parseCoordinateTaskMessage.apply(message);
            final CoordinateTaskData coordinateTaskData = ofNullable(coordinateTaskMessage)
                .map(CoordinateTaskMessage::getCoordinateTaskDataMessage)
                .orElse(null);
            final FlowTypeDto flowTypeDto = ofNullable(retrieveFlowTypeDto)
                .map(function -> function.apply(coordinateTaskData))
                .orElseGet(() -> FlowTypeDto.builder()
                    .integrationFlowType(integrationFlowType)
                    .serviceCode(serviceCode)
                    .build());
            final ImportCoordinateTask importCoordinateTask = createImportCoordinateTask(
                message, flowTypeDto, coordinateTaskData
            );
            final List<CoordinateFile> coordinateFiles = ofNullable(coordinateTaskMessage)
                .map(CoordinateTaskMessage::getFiles)
                .map(ArrayOfCoordinateFile::getCoordinateFile)
                .orElse(Collections.emptyList());
            return ImportFlowMessageDto.builder()
                .importCoordinateTasks(Collections.singletonList(importCoordinateTask))
                .coordinateFiles(coordinateFiles)
                .build();
        } catch (Exception e) {
            log.error("Unable to process request with flow: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public ImportFlowMessageDto receiveFlowSendTasksMessage(
        final String messageType,
        final String message,
        final String serviceCode,
        final IntegrationFlowType integrationFlowType,
        final String flowMessageDirectory,
        final Function<String, SendTasksMessage> parseSendTasksMessage
    ) {
        log.info("Received flow using mqetpmv: type = {}, message = {}", messageType, message);
        xmlUtils.writeXmlFile(message, flowMessageDirectory);
        try {
            final SendTasksMessage sendTasksMessage = parseSendTasksMessage.apply(message);
            final FlowTypeDto flowTypeDto = FlowTypeDto.builder()
                .serviceCode(serviceCode)
                .integrationFlowType(integrationFlowType)
                .build();
            final List<ImportCoordinateTask> importCoordinateTasks = ofNullable(sendTasksMessage)
                .map(SendTasksMessage::getCoordinateTaskDataMessages)
                .map(ArrayOfCoordinateTaskData::getCoordinateTaskData)
                .map(Collection::stream)
                .orElse(Stream.empty())
                .map(coordinateTaskData -> createImportCoordinateTask(message, flowTypeDto, coordinateTaskData))
                .collect(Collectors.toList());
            final List<CoordinateFile> coordinateFiles = ofNullable(sendTasksMessage)
                .map(SendTasksMessage::getFiles)
                .map(ArrayOfCoordinateFile::getCoordinateFile)
                .orElse(Collections.emptyList());
            return ImportFlowMessageDto.builder()
                .importCoordinateTasks(importCoordinateTasks)
                .coordinateFiles(coordinateFiles)
                .build();
        } catch (Exception e) {
            log.error("Unable to process request with flow: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void storeInFlowSendTasksMessage(
        final String messageType,
        final String message,
        final String serviceCode,
        final IntegrationFlowType integrationFlowType,
        final String flowMessageDirectory,
        final Function<String, SendTasksMessage> parseSendTasksMessage,
        final Function<CoordinateTaskData, Boolean> isPartOfAffairCollation,
        final Supplier<Class<?>[]> messageClassesSupplier
    ) {
        final ImportFlowMessageDto importFlowMessageDto = receiveFlowSendTasksMessage(
            messageType, message, serviceCode, integrationFlowType, flowMessageDirectory, parseSendTasksMessage
        );

        importFlowMessageDto.getImportCoordinateTasks()
            .forEach(importCoordinateTask -> integrationFlowQueueDocumentService
                .createDocument(
                    IntegrationFlowQueueDocument.of(
                        importCoordinateTask.getIntegrationFlowDocument(),
                        isPartOfAffairCollation.apply(importCoordinateTask.getCoordinateTaskData()),
                        xmlUtils.transformObjectToXmlString(
                            importCoordinateTask.getCoordinateTaskData(),
                            messageClassesSupplier.get()
                        )
                    ),
                    false,
                    "storeInFlowSendTasksMessage"
                ));
    }

    @Override
    public void storeInQueueCoordinateSendTaskStatusesMessage(
        final String messageType,
        final String message,
        final String serviceCode,
        final IntegrationFlowType integrationFlowType,
        final String flowMessageDirectory,
        final Function<String, CoordinateSendTaskStatusesMessage> parseCoordinateSendTaskStatusesMessage,
        final Function<CoordinateTaskStatusData, Boolean> isPartOfAffairCollation,
        final Supplier<Class<?>[]> messageClassesSupplier
    ) {
        log.info("Received flow using mqetpmv: type = {}, message = {}", messageType, message);
        xmlUtils.writeXmlFile(message, flowMessageDirectory);
        try {
            final CoordinateSendTaskStatusesMessage coordinateSendTaskStatusesMessage =
                parseCoordinateSendTaskStatusesMessage.apply(message);
            final FlowTypeDto flowTypeDto = FlowTypeDto.builder()
                .serviceCode(serviceCode)
                .integrationFlowType(integrationFlowType)
                .build();
            ofNullable(coordinateSendTaskStatusesMessage)
                .map(CoordinateSendTaskStatusesMessage::getCoordinateTaskStatusDataMessage)
                .ifPresent(coordinateTaskStatusData -> {
                    final IntegrationFlowDocument integrationFlowDocument = integrationFlowService.createDocument(
                        coordinateTaskStatusData.getTaskId(), flowTypeDto, message, IntegrationFlowStatus.RECEIVED
                    );

                    integrationFlowQueueDocumentService.createDocument(
                        IntegrationFlowQueueDocument.of(
                            integrationFlowDocument,
                            isPartOfAffairCollation.apply(
                                coordinateSendTaskStatusesMessage.getCoordinateTaskStatusDataMessage()
                            ),
                            xmlUtils.transformObjectToXmlString(
                                coordinateSendTaskStatusesMessage.getCoordinateTaskStatusDataMessage(),
                                messageClassesSupplier.get()
                            )
                        ),
                        false,
                        "storeInQueueCoordinateSendTaskStatusesMessage"
                    );
                });
        } catch (Exception e) {
            log.error("Unable to process request with flow: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void storeInQueueCoordinateTaskMessage(
        final String messageType,
        final String message,
        final String serviceCode,
        final IntegrationFlowType integrationFlowType,
        final String flowMessageDirectory,
        final Function<String, CoordinateTaskMessage> parseCoordinateTaskMessage,
        final Supplier<Class<?>[]> messageClassesSupplier
    ) {
        final ImportFlowMessageDto importFlowMessageDto = receiveFlowCoordinateTaskMessage(
            messageType, message, serviceCode, integrationFlowType, flowMessageDirectory, parseCoordinateTaskMessage
        );

        importFlowMessageDto.getImportCoordinateTasks()
            .forEach(importCoordinateTask -> integrationFlowQueueDocumentService
                .createDocument(
                    IntegrationFlowQueueDocument.of(
                        importCoordinateTask.getIntegrationFlowDocument(),
                        false,
                        xmlUtils.transformObjectToXmlString(
                            importCoordinateTask.getCoordinateTaskData(),
                            messageClassesSupplier.get()
                        )
                    ),
                    false,
                    "storeInQueueCoordinateTaskMessage"
                ));
    }

    @Override
    public void receiveFlowStatusMessage(
        final String messageType, final String message, final String flowStatusMessageDirectory
    ) {
        log.info("Received flow status using mqetpmv: type = {}, message = {}", messageType, message);
        xmlUtils.writeXmlFile(message, flowStatusMessageDirectory);
        try {
            final CoordinateSendTaskStatusesMessage coordinateSendTaskStatusesMessage =
                parseCoordinateSendTaskStatusesMessage(message);
            processImportStatusMessage(coordinateSendTaskStatusesMessage, message);
        } catch (Exception e) {
            log.error("Unable to process request with flow status: {}", e.getMessage(), e);
            throw e;
        }
    }

    private void processImportStatusMessage(
        final CoordinateSendTaskStatusesMessage coordinateSendTaskStatusesMessage, final String message
    ) {
        final String eno = coordinateSendTaskStatusesMessage.getCoordinateTaskStatusDataMessage().getTaskId();
        final StatusType statusType = coordinateSendTaskStatusesMessage.getCoordinateTaskStatusDataMessage()
            .getStatus();
        final String statusCode = ofNullable(statusType)
            .map(StatusType::getStatusCode)
            .map(String::valueOf)
            .orElse(null);
        final String statusTitle = ofNullable(statusType)
            .map(StatusType::getStatusTitle)
            .orElse(null);
        integrationFlowService.saveReceivedStatus(eno, message, statusCode, statusTitle);
    }

    private ImportCoordinateTask createImportCoordinateTask(
        final String message, final FlowTypeDto flowTypeDto, final CoordinateTaskData coordinateTaskData
    ) {
        final TaskType taskType = ofNullable(coordinateTaskData)
            .map(CoordinateTaskData::getTask)
            .orElse(null);
        final String eno = ofNullable(taskType)
            .map(TaskType::getServiceNumber)
            .filter(StringUtils::isNotBlank)
            .orElseGet(() -> ofNullable(taskType)
                .map(TaskType::getTaskNumber)
                .orElse(null));
        final IntegrationFlowDocument integrationFlowDocument = integrationFlowService.createDocument(
            eno, flowTypeDto, message, IntegrationFlowStatus.RECEIVED
        );
        return ImportCoordinateTask.builder()
            .eno(eno)
            .coordinateTaskData(coordinateTaskData)
            .integrationFlowDocument(integrationFlowDocument)
            .flowMessageProcessorBeanName(flowTypeDto.getFlowMessageProcessorBeanName())
            .build();
    }

    private CoordinateSendTaskStatusesMessage parseCoordinateSendTaskStatusesMessage(final String message) {
        return xmlUtils.<CoordinateSendTaskStatusesMessage>parseXml(
            message,
            new Class[]{CoordinateSendTaskStatusesMessage.class}
        ).orElseThrow(() -> new SsrException("Invalid CoordinateSendTaskStatusesMessage"));
    }
}
