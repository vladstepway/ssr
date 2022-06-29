package ru.croc.ugd.ssr.scheduler;

import static java.util.Objects.isNull;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.ResettlementRequest;
import ru.croc.ugd.ssr.ResettlementRequestType;
import ru.croc.ugd.ssr.model.CipDocument;
import ru.croc.ugd.ssr.model.ResettlementRequestDocument;
import ru.croc.ugd.ssr.service.ResettlementRequestDocumentService;
import ru.croc.ugd.ssr.service.cip.CipService;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ResettlementProcessScheduler {

    private final CipService cipService;
    private final ResettlementRequestDocumentService resettlementRequestDocumentService;
    private final int daysBeforeSlotNumberUpdate;

    public ResettlementProcessScheduler(
        final CipService cipService,
        final ResettlementRequestDocumentService resettlementRequestDocumentService,
        @Value("${ugd.ssr.resettlement.days-before-slot-number-update:7}") final int daysBeforeSlotNumberUpdate
    ) {
        this.cipService = cipService;
        this.resettlementRequestDocumentService = resettlementRequestDocumentService;
        this.daysBeforeSlotNumberUpdate = daysBeforeSlotNumberUpdate;
    }

    @Scheduled(cron = "${schedulers.resettlement-check-slot-number.cron:0 0 0 * * ?}")
    public void checkSlotNumber() {
        final List<CipDocument> cipDocuments = cipService.fetchAll()
            .stream()
            .filter(cipDocument -> isNull(cipDocument.getDocument().getCipData().getSlotNumber()))
            .collect(Collectors.toList());

        cipDocuments.stream()
            .filter(this::isResettlementProcessInitiated)
            .forEach(this::updateCipSlotNumber);
    }

    private void updateCipSlotNumber(final CipDocument cipDocument) {
        cipDocument.getDocument().getCipData().setSlotNumber(3);
        cipService.updateDocument(cipDocument.getId(), cipDocument, true, true, null);
    }

    private boolean isResettlementProcessInitiated(final CipDocument cipDocument) {
        return resettlementRequestDocumentService.fetchAllByCipId(cipDocument.getId())
            .stream()
            .map(ResettlementRequestDocument::getDocument)
            .map(ResettlementRequest::getMain)
            .map(ResettlementRequestType::getStartResettlementDate)
            .anyMatch(startDate -> Duration
                .between(startDate.atStartOfDay(), LocalDate.now().atStartOfDay())
                .toDays() >= daysBeforeSlotNumberUpdate
            );
    }
}
