package ru.croc.ugd.ssr.scheduler.apartmentdefectconfirmation;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import ru.croc.ugd.ssr.db.dao.ApartmentDefectConfirmationDao;
import ru.croc.ugd.ssr.service.apartmentdefectconfirmation.ApartmentDefectConfirmationService;
import ru.croc.ugd.ssr.service.document.ApartmentDefectConfirmationDocumentService;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class ApartmentDefectConfirmationScheduler {

    private final ApartmentDefectConfirmationDao apartmentDefectConfirmationDao;
    private final ApartmentDefectConfirmationDocumentService apartmentDefectConfirmationDocumentService;
    private final ApartmentDefectConfirmationService apartmentDefectConfirmationService;

    @Scheduled(cron = "${schedulers.apartment-defect-confirmation-cleaner.cron:0 0 1 ? * SUN}")
    public void cleanPendingApartmentDefectConfirmations() {
        final List<String> documentIdsForRemoving = apartmentDefectConfirmationDao.fetchPendingDocumentIdsForRemoving();
        if (!CollectionUtils.isEmpty(documentIdsForRemoving)) {
            log.info(
                "Clean pending APARTMENT-DEFECT-CONFIRMATION documents: {}", String.join(",", documentIdsForRemoving)
            );
            documentIdsForRemoving.forEach(documentId -> apartmentDefectConfirmationDocumentService.deleteDocument(
                documentId, true, "cleanPendingApartmentDefectConfirmations"
            ));
        }
    }

    @Scheduled(cron = "${schedulers.apartment-defect-confirmation-actualize-task-candidates.cron: 0 1 0 * * ?}")
    public void actualizeTaskCandidates() {
        log.info("Start actualizeTaskCandidatesBySsrCco for apartment defect confirmation");
        apartmentDefectConfirmationService.actualizeTaskCandidatesBySsrCco();
    }
}
