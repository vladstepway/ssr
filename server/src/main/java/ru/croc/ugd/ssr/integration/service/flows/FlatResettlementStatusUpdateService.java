package ru.croc.ugd.ssr.integration.service.flows;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.FlatType;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.model.RealEstateDocument;
import ru.croc.ugd.ssr.service.RealEstateDocumentService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Сервис обновления статусов квартир в отселяемом доме.
 */
@Service
@RequiredArgsConstructor
public class FlatResettlementStatusUpdateService {

    private static final Logger LOG = LoggerFactory.getLogger(FlatResettlementStatusUpdateService.class);

    private final RealEstateDocumentService realEstateDocumentService;

    /**
     * Обновление статуса квартиры отселяемого дома по жителю.
     *
     * @param personData          житель
     * @param resettlementStatus  статус квартиры
     */
    public void updateFlatResettlementStatus(
        PersonType personData,
        String resettlementStatus
    ) {
        LOG.info(
            "Обновление статуса квартиры отселяемого дома. "
                + "UNOM = {}, flatID = {}, resettlementStatus = {}",
            personData.getUNOM(),
            personData.getFlatID(),
            resettlementStatus
        );
        if (Objects.nonNull(personData.getUNOM()) && StringUtils.isNotBlank(personData.getFlatID())) {
            RealEstateDocument realEstateDocument = realEstateDocumentService
                .fetchDocumentByUnom(personData.getUNOM().toString());
            if (Objects.nonNull(realEstateDocument)
                && Objects.nonNull(realEstateDocument.getDocument().getRealEstateData().getFlats())) {
                List<FlatType> flats = realEstateDocument.getDocument().getRealEstateData().getFlats().getFlat();
                Optional<FlatType> optFlat = flats
                    .stream()
                    .filter(f -> personData.getFlatID().equals(f.getFlatID()))
                    .findAny();
                if (optFlat.isPresent()) {
                    FlatType flat = optFlat.get();
                    flat.setResettlementStatus(resettlementStatus);
                    realEstateDocumentService.updateDocument(
                        realEstateDocument.getId(),
                        realEstateDocument,
                        true,
                        false,
                        "updated flat resettlement_status"
                    );
                }
            }
        }
    }

    /**
     * Проставляем всем квартирам дома resettlement_status = 1 (Участвует в программе реновации).
     *
     * @param unom UNOM дома
     */
    public void updateFlatResettlementStatusStartRenovation(String unom) {
        if (StringUtils.isBlank(unom)) {
            return;
        }
        LOG.info(
            "Обновление статуса всех квартир отселяемого дома resettlement_status = 1. UNOM = {}",
            unom
        );

        RealEstateDocument realEstateDocument = realEstateDocumentService
            .fetchDocumentByUnom(unom);
        if (Objects.isNull(realEstateDocument)
            || Objects.isNull(realEstateDocument.getDocument().getRealEstateData().getFlats())) {
            return;
        }

        realEstateDocument.getDocument().getRealEstateData().getFlats().getFlat()
            .forEach(f -> f.setResettlementStatus("1"));
        realEstateDocumentService.updateDocument(
            realEstateDocument.getId(),
            realEstateDocument,
            true,
            false,
            "updated flat resettlement_status"
        );
    }
}

