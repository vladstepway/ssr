package ru.croc.ugd.ssr.service.flowerrorreport;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import ru.croc.ugd.ssr.config.ReportProperties;
import ru.croc.ugd.ssr.config.SystemProperties;
import ru.croc.ugd.ssr.flowreportederror.FlowReportedError;
import ru.croc.ugd.ssr.flowreportederror.FlowReportedErrorData;
import ru.croc.ugd.ssr.model.FlowReportedErrorDocument;
import ru.croc.ugd.ssr.mq.listener.AdministrativeDocumentListener;
import ru.croc.ugd.ssr.mq.listener.ContractPrReadyListener;
import ru.croc.ugd.ssr.mq.listener.ContractReadyListener;
import ru.croc.ugd.ssr.mq.listener.EtpQueueListener;
import ru.croc.ugd.ssr.mq.listener.InfoSettlementListener;
import ru.croc.ugd.ssr.mq.listener.OfferLettersListener;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.document.FlowReportedErrorDocumentService;
import ru.croc.ugd.ssr.service.flowerrorreport.print.FlowErrorReportGenerator;
import ru.reinform.cdp.filestore.model.FilestoreFolderAttrs;
import ru.reinform.cdp.filestore.model.FilestoreSourceRef;
import ru.reinform.cdp.filestore.model.remote.api.CreateFolderRequest;
import ru.reinform.cdp.filestore.service.FilestoreRemoteService;
import ru.reinform.cdp.filestore.service.FilestoreV2RemoteService;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class FlowErrorReportScheduler {
    private static final String SUBSYSTEM_CODE = "UGD_SSR";

    private final FlowErrorReportGenerator flowErrorReportGenerator;
    private final FilestoreRemoteService filestoreRemoteService;
    private final FlowReportedErrorDocumentService flowReportedErrorDocumentService;
    private final SystemProperties systemProperties;
    private final FilestoreV2RemoteService remoteService;
    private final FlowErrorReportEmailService flowErrorReportEmailService;
    private final ReportProperties reportProperties;

    private final OfferLettersListener offerLettersListener;
    private final ContractReadyListener contractReadyListener;
    private final InfoSettlementListener infoSettlementListener;
    private final ContractPrReadyListener contractPrReadyListener;
    private final AdministrativeDocumentListener administrativeDocumentListener;
    private final PersonDocumentService personDocumentService;

    @Value("${app.filestore.ssr.rootPath}")
    private String rootPath;

    @Value("${ugd.ssr.flow-reported-error.programmatic-person-analysis:true}")
    private boolean isProgrammaticPersonAnalysis;

    @Scheduled(cron = "${schedulers.flow-error.notification.cron:0 0 0 * * ?}")
    public void flowErrorReportsNotificationSentOut() {
        if (reportProperties.isEnableFlowErrorSchedule()) {
            log.info("Started flowErrorReportsNotificationSentOut");
            final LocalDate reportDate = LocalDate.now().minusDays(1);
            final List<FlowReportedErrorDocument> yesterdayErrors = flowReportedErrorDocumentService
                .findFlowReportedErrorsByReportDate(reportDate);
            if (!CollectionUtils.isEmpty(yesterdayErrors)) {
                sendReports(yesterdayErrors, reportDate);
            }
            log.info("Completed flowErrorReportsNotificationSentOut");
        } else {
            log.info("flowErrorReportsNotificationSentOut was not started");
        }
    }

    @Scheduled(cron = "${schedulers.flow-error.fix-process.cron:0 0 0 * * ?}")
    public void processAvailableForFixErrors() {
        if (reportProperties.isEnableFlowErrorSchedule()) {
            log.info(
                "processAvailableForFixErrors: started (isProgrammaticPersonAnalysis = {})",
                isProgrammaticPersonAnalysis
            );
            final List<FlowReportedErrorDocument> errorsToFix = isProgrammaticPersonAnalysis
                ? flowReportedErrorDocumentService.findUnfixed()
                : flowReportedErrorDocumentService.findAvailableForFix();
            if (!CollectionUtils.isEmpty(errorsToFix)) {
                log.info(
                    "processAvailableForFixErrors: processing: {}",
                    errorsToFix
                        .stream()
                        .map(FlowReportedErrorDocument::getId)
                        .collect(Collectors.joining(", "))
                );
                fixErrors(errorsToFix);
            } else {
                log.info("processAvailableForFixErrors: skipped(no documents to process)");
            }
            log.info("processAvailableForFixErrors: completed");
        } else {
            log.info("processAvailableForFixErrors: disabled");
        }
    }

    private void fixErrors(final List<FlowReportedErrorDocument> errorsAvailableForFix) {
        errorsAvailableForFix.forEach(flowReportedErrorDocument -> {
            log.info("fixErrors: started (documentId = {})", flowReportedErrorDocument.getId());
            final FlowReportedErrorData flowReportedErrorData = flowReportedErrorDocument
                .getDocument().getFlowReportedErrorData();
            if (isAvailableForFix(flowReportedErrorData)) {
                log.info("fixErrors: processing (documentId = {})", flowReportedErrorDocument.getId());
                final String originalMessage = flowReportedErrorData.getOriginalFlowMessage();
                if (!StringUtils.isEmpty(originalMessage)) {
                    FlowType.ofFullName(flowReportedErrorData.getFlowType())
                        .map(this::getFlowServiceBasedOnFlowType)
                        .filter(Objects::nonNull)
                        .ifPresent(etpQueueListener ->
                            processErrorMessage(etpQueueListener, flowReportedErrorDocument));
                }
            } else {
                log.info("fixErrors: skipped (documentId = {})", flowReportedErrorDocument.getId());
            }
            log.info("fixErrors: completed (documentId = {})", flowReportedErrorDocument.getId());
        });
    }

    private boolean isAvailableForFix(final FlowReportedErrorData flowReportedErrorData) {
        return !isProgrammaticPersonAnalysis || ofNullable(flowReportedErrorData)
            .map(FlowReportedErrorData::getPersonFirst)
            .filter(personFirst -> nonNull(personFirst.getPersonId()) && nonNull(personFirst.getAffairId()))
            .filter(personFirst -> personDocumentService.existsOnlyOnePerson(
                personFirst.getPersonId(), personFirst.getAffairId()
            ))
            .isPresent();
    }

    private void processErrorMessage(
        final EtpQueueListener<?, ?> etpQueueListener,
        final FlowReportedErrorDocument flowReportedErrorDocument
    ) {
        final FlowReportedErrorData flowReportedErrorData = flowReportedErrorDocument
            .getDocument().getFlowReportedErrorData();
        final String originalMessage = flowReportedErrorData.getOriginalFlowMessage();
        final String affairId = flowReportedErrorData.getPersonFirst().getAffairId();
        final String personId = flowReportedErrorData.getPersonFirst().getPersonId();
        try {
            etpQueueListener.handle(originalMessage, affairId, personId);
            flowReportedErrorData.setFixed(true);
            flowReportedErrorDocumentService.updateDocument(flowReportedErrorDocument);
        } catch (Exception ex) {
            log.error("Error processAvailableForFixErrors: failed processing message from doc: "
                + flowReportedErrorDocument.getId());
        }
    }

    private void sendReports(
        final List<FlowReportedErrorDocument> yesterdayErrors,
        final LocalDate reportDate
    ) {
        final List<FlowReportedErrorData> yesterdayErrorsData = yesterdayErrors
            .stream()
            .map(FlowReportedErrorDocument::getDocument)
            .map(FlowReportedError::getFlowReportedErrorData)
            .collect(Collectors.toList());
        final byte[] report = flowErrorReportGenerator.printFlowErrorReport(yesterdayErrorsData);
        final String fileStoreId = createFile(report, createFolder());
        flowErrorReportEmailService.sendFlowErrorReportMail(fileStoreId, reportDate);
        yesterdayErrorsData.forEach(flowReportedErrorData -> {
            flowReportedErrorData.setIsPublished(true);
            flowReportedErrorData.setReportFileId(fileStoreId);
        });
        yesterdayErrors
            .forEach(flowReportedErrorDocument -> flowReportedErrorDocumentService.updateDocument(
                flowReportedErrorDocument,
                "Report sent"));
    }

    private EtpQueueListener<?, ?> getFlowServiceBasedOnFlowType(final FlowType flowType) {
        switch (flowType) {
            case FLOW_TWO:
                return offerLettersListener;
            case FLOW_SEVEN:
                return contractReadyListener;
            case FLOW_EIGHT:
                return infoSettlementListener;
            case FLOW_TWELVE:
                return contractPrReadyListener;
            case FLOW_THIRTEEN:
                return administrativeDocumentListener;
            default:
                return null;
        }
    }

    private String createFolder() {
        CreateFolderRequest request = new CreateFolderRequest();
        request.setPath(rootPath + "/flowErrorReports");
        request.setErrorIfAlreadyExists(false);
        request.setAttrs(FilestoreFolderAttrs.builder()
            .folderTypeID("-")
            .folderEntityID("-")
            .folderSourceReference(FilestoreSourceRef.SERVICE.name())
            .build());
        return remoteService.createFolder(request, systemProperties.getSystem(), SUBSYSTEM_CODE).getId();

    }

    private String createFile(final byte[] files, String folderId) {
        return filestoreRemoteService.createFile(
            UUID.randomUUID() + ".xlsx", "text/xlsx",
            files,
            folderId,
            "xlsx",
            "SSR",
            "SSR",
            "UGD"
        );
    }
}
