package ru.croc.ugd.ssr.scheduler.ssrcco;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.dto.CcoInfo;
import ru.croc.ugd.ssr.model.ssrcco.SsrCcoDocument;
import ru.croc.ugd.ssr.service.CapitalConstructionObjectService;
import ru.croc.ugd.ssr.service.document.SsrCcoDocumentService;
import ru.croc.ugd.ssr.service.ssrcco.RestSsrCcoService;
import ru.croc.ugd.ssr.service.ssrcco.SsrCcoService;
import ru.croc.ugd.ssr.ssrcco.SsrCco;
import ru.croc.ugd.ssr.ssrcco.SsrCcoData;
import ru.croc.ugd.ssr.ssrcco.SsrCcoOrganization;
import ru.croc.ugd.ssr.utils.StreamUtils;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SsrCcoScheduler {

    @Value("${schedulers.ssr-cco-sync.enabled:true}")
    private boolean enableScheduler;

    private final CapitalConstructionObjectService capitalConstructionObjectService;
    private final SsrCcoDocumentService ssrCcoDocumentService;
    private final RestSsrCcoService restSsrCcoService;
    private final SsrCcoService ssrCcoService;

    @Scheduled(cron = "${schedulers.ssr-cco-calculate-defect-act-totals-data.cron:0 0 0 * * ?}")
    public void calculateDefectActTotalsData() {
        ssrCcoService.calculateDefectActTotalsData();
    }

    @Scheduled(cron = "${schedulers.ssr-cco-sync.cron:0 0 0 * * ?}")
    public void dailySsrCcoSync() {
        if (enableScheduler) {
            log.info("SsrCcoSyncScheduler: Start automatic sync");
            final LocalDateTime lastUpdateDateTime = ssrCcoDocumentService
                .fetchLastUpdateDateTime()
                .orElse(LocalDateTime.now().minusDays(1));
            dailySsrCcoSync(lastUpdateDateTime);
            log.info("SsrCcoSyncScheduler: Finish automatic sync");
        } else {
            log.info("SsrCcoSyncScheduler: Scheduler is disabled");
        }
    }

    public void dailySsrCcoSync(final LocalDateTime lastUpdateDateTime) {
        try {
            log.info("SsrCcoSyncScheduler: Start sync after {}", lastUpdateDateTime);
            final List<CcoInfo> ccoInfoList = capitalConstructionObjectService
                .getAllCcoInfoAfterLastUpdate(lastUpdateDateTime);
            ccoInfoList
                .forEach(ccoInfo -> StreamUtils.OptionalConsumer
                    .of(ssrCcoDocumentService.fetchByPsDocumentId(ccoInfo.getPsDocumentId()))
                    .ifPresent(document -> syncDocument(document, ccoInfo))
                    .ifNotPresent(() ->
                        syncDocument(createEmptySsrCcoDocument(ccoInfo), ccoInfo)
                    ));
            log.info("SsrCcoSyncScheduler: Finish sync after {}", lastUpdateDateTime);
        } catch (Exception ex) {
            log.error("SsrCcoSyncScheduler: Unable to sync ssr cco: lastUpdateDateTime: {}, errorMessage: {}",
                lastUpdateDateTime, ex.getMessage(), ex
            );
        }
    }

    private SsrCcoDocument createEmptySsrCcoDocument(final CcoInfo ccoInfo) {
        final SsrCcoDocument ssrCcoDocument = new SsrCcoDocument();
        final SsrCco ssrCco = new SsrCco();
        final SsrCcoData ssrCcoData = new SsrCcoData();
        ssrCcoData.setPsDocumentId(ccoInfo.getPsDocumentId());
        ssrCco.setSsrCcoData(ssrCcoData);
        ssrCcoDocument.setDocument(ssrCco);
        return ssrCcoDocument;
    }

    private void syncDocument(final SsrCcoDocument ssrCcoDocument, final CcoInfo ccoInfo) {
        final SsrCcoData ssrCcoData = ssrCcoDocument
            .getDocument()
            .getSsrCcoData();

        ssrCcoData.setAddress(ccoInfo.getAddress());
        ssrCcoData.setUnom(ccoInfo.getUnom());
        ssrCcoData.setArea(ccoInfo.getAreas().stream().findFirst().orElse(null));
        ssrCcoData.setDistrict(ccoInfo.getDistricts().stream().findFirst().orElse(null));
        ssrCcoData.setUpdateDateTime(LocalDateTime.now());

        final List<SsrCcoOrganization> ssrCcoOrganizations = restSsrCcoService
            .fetchSsrCcoOrganizationsByUnom(ssrCcoData.getUnom());

        if (ssrCcoOrganizations.isEmpty()) {
            try {
                if (ssrCcoDocument.getId() != null) {
                    ssrCcoDocumentService.deleteDocument(ssrCcoDocument.getId(), true, "syncDocument");
                    log.info("Remove ssrCco document {} due to empty organisations", ssrCcoDocument.getId());
                }
            } catch (Exception e) {
                log.info("Unable to remove ssrCco document {} on syncDocument", ssrCcoDocument.getId());
            }
            return;
        }

        ssrCcoData.getOrganizations().clear();
        ssrCcoData.getOrganizations().addAll(ssrCcoOrganizations);

        if (ssrCcoDocument.getId() != null) {
            log.info(
                "SsrCcoSyncScheduler: Update ssrCcoDocument (ssrCcoDocumentId = {}, unom = {})",
                ssrCcoDocument.getId(),
                ccoInfo.getUnom()
            );
            ssrCcoDocumentService.updateDocument(ssrCcoDocument);
        } else {
            log.info("SsrCcoSyncScheduler: Create ssrCcoDocument (unom = {})", ccoInfo.getUnom());
            ssrCcoDocumentService.createDocument(ssrCcoDocument, true, "syncDocument");
        }
    }

    public void deleteAll() {
        ssrCcoDocumentService.fetchDocumentsPage(0, 50000)
            .forEach(document ->
                ssrCcoDocumentService.deleteDocument(document.getId(), true, "deleteAll")
            );
    }
}
