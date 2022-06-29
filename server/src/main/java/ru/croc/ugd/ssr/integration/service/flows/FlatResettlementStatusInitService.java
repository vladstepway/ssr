package ru.croc.ugd.ssr.integration.service.flows;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.FlatType;
import ru.croc.ugd.ssr.RealEstateDataType;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.RealEstateDocument;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.RealEstateDocumentService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Сервис инициации статусов квартир в отселяемом доме.
 */
@Service
@RequiredArgsConstructor
public class FlatResettlementStatusInitService {

    private static final Logger LOG = LoggerFactory.getLogger(FlatResettlementStatusInitService.class);
    private static final int PAGE_SIZE = 500;

    private final RealEstateDocumentService realEstateDocumentService;
    private final PersonDocumentService personDocumentService;

    /**
     * Проставляет resettlement_status квартирам отселяемых домов.
     */
    public void initFlatResettlementStatus() {
        LOG.info("Инициация статусов квартир в отселяемом доме начата");
        int page = 0;
        List<RealEstateDocument> documentPage;
        do {
            documentPage = realEstateDocumentService.fetchDocumentsPage(page, PAGE_SIZE);
            documentPage.forEach(this::handleRealEstate);
            LOG.debug("Обработано {} записей", page * PAGE_SIZE + documentPage.size());
            page++;
        }
        while (documentPage.size() > 0);
        LOG.info("Инициация статусов квартир в отселяемом доме Завершена");
    }

    private void handleRealEstate(RealEstateDocument realEstateDocument) {
        RealEstateDataType realEstateData = realEstateDocument.getDocument().getRealEstateData();
        if (Objects.isNull(realEstateData.getFlats()) || realEstateData.getFlats().getFlat().isEmpty()) {
            return;
        }

        List<PersonDocument> personDocuments = personDocumentService.fetchByRealEstateId(realEstateDocument.getId());
        personDocuments = personDocuments
            .stream()
            .filter(p -> !p.getDocument().getPersonData().isIsArchive())
            .collect(Collectors.toList());
        List<PersonDocument> personDocumentsWithIntegration = personDocuments
            .stream()
            .filter(p -> Objects.nonNull(p.getDocument().getPersonData().getIntegrationLog()))
            .filter(p -> !p.getDocument().getPersonData().getIntegrationLog().getItem().isEmpty())
            .collect(Collectors.toList());

        for (FlatType flat : realEstateData.getFlats().getFlat()) {
            if (StringUtils.isNotBlank(flat.getResettlementStatus())) {
                return; // статус уже проставлен - не обновляем
            }

            List<PersonDocument> flatPersons = personDocuments
                .stream()
                .filter(p -> flat.getFlatID().equals(p.getDocument().getPersonData().getFlatID()))
                .collect(Collectors.toList());
            List<PersonDocument> flatPersonsWithIntegration = personDocumentsWithIntegration
                .stream()
                .filter(p -> flat.getFlatID().equals(p.getDocument().getPersonData().getFlatID()))
                .collect(Collectors.toList());
            if (hasIntegrationEventId(flatPersonsWithIntegration, "SuperServiceDGPFlatFree")) {
                flat.setResettlementStatus("9");
            } else if (hasIntegrationEventId(flatPersonsWithIntegration, "SuperServiceDGPKeyIssue")) {
                flat.setResettlementStatus("7");
            } else if (hasIntegrationEventId(flatPersonsWithIntegration, "SuperServiceDGPSignAgreementStatus")) {
                flat.setResettlementStatus("6");
            } else if (hasIntegrationEventId(flatPersonsWithIntegration, "SuperServiceDGPContractReady")) {
                flat.setResettlementStatus("5");
            } else if (hasIntegrationEventId(flatPersonsWithIntegration, "SuperServiceDGPStatementStatus")) {
                flat.setResettlementStatus("4");
            } else if (hasIntegrationEventId(flatPersonsWithIntegration, "SuperServiceDGPViewStatus")) {
                flat.setResettlementStatus("3");
            } else if (hasIntegrationEventId(flatPersonsWithIntegration, "SuperServiceDGPLetterRequest")) {
                flat.setResettlementStatus("2");
            } else if (!flatPersons.isEmpty()) {
                flat.setResettlementStatus("1");
            } else {
                flat.setResettlementStatus("0");
            }
        }

        realEstateDocumentService.updateDocument(
            realEstateDocument.getId(),
            realEstateDocument,
            true,
            false,
            "init flat resettlement_status"
        );
    }

    private boolean hasIntegrationEventId(List<PersonDocument> persons, String eventId) {
        return persons.stream().anyMatch(p ->
            p.getDocument().getPersonData().getIntegrationLog().getItem()
                .stream()
                .anyMatch(i -> eventId.equals(i.getEventId())));
    }
}

