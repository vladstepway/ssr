package ru.croc.ugd.ssr.scheduler.apartmentinspection;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import ru.croc.ugd.ssr.db.dao.ApartmentInspectionDao;
import ru.croc.ugd.ssr.service.ApartmentInspectionService;
import ru.croc.ugd.ssr.service.DocumentWithFolder;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class ApartmentInspectionScheduler {

    private final ApartmentInspectionDao apartmentInspectionDao;
    private final ApartmentInspectionService apartmentInspectionService;

    @Scheduled(cron = "${schedulers.apartment-inspection-cleaner.cron:0 0 1 ? * SUN}")
    public void cleanPendingApartmentInspections() {
        final List<String> actsForDeletion = apartmentInspectionDao.fetchPendingActIdsForRemoval();
        if (!CollectionUtils.isEmpty(actsForDeletion)) {
            log.info("Clean pending APARTMENT-INSPECTION documents: " + String.join(",", actsForDeletion));
            actsForDeletion
                .forEach(actDocumentId -> ((DocumentWithFolder) apartmentInspectionService)
                    .deleteDocument(actDocumentId, true, null));
        }
    }

    @Scheduled(cron = "${schedulers.apartment-inspection-actualize-task-candidates.cron: 0 1 0 * * ?}")
    public void actualizeTaskCandidates() {
        log.info("Start actualizeTaskCandidatesBySsrCco for apartment inspection");
        apartmentInspectionService.actualizeTaskCandidatesBySsrCco();
    }
}
