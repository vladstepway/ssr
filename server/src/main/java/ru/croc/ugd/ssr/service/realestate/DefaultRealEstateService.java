package ru.croc.ugd.ssr.service.realestate;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static ru.croc.ugd.ssr.service.RealEstateDocumentService.PAGE_SIZE;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.FlatType;
import ru.croc.ugd.ssr.HouseToSettle;
import ru.croc.ugd.ssr.Person;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.RealEstateDataType;
import ru.croc.ugd.ssr.RealEstateDataType.Flats;
import ru.croc.ugd.ssr.ResettlementRequest;
import ru.croc.ugd.ssr.ResettlementRequestType;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.RealEstateDocument;
import ru.croc.ugd.ssr.model.ResettlementRequestDocument;
import ru.croc.ugd.ssr.model.trade.TradeAdditionDocument;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.RealEstateDocumentService;
import ru.croc.ugd.ssr.service.ResettlementRequestDocumentService;
import ru.croc.ugd.ssr.service.bpm.BpmService;
import ru.croc.ugd.ssr.service.document.TradeAdditionDocumentService;
import ru.croc.ugd.ssr.trade.EstateInfoType;
import ru.croc.ugd.ssr.trade.TradeAddition;
import ru.croc.ugd.ssr.trade.TradeAdditionType;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class DefaultRealEstateService implements RealEstateService {

    private static final String PROCESS_KEY_COMPLETE_RESETTLEMENT = "ugdssrRealEstate_completeResettlement";

    private final RealEstateDocumentService realEstateDocumentService;
    private final BpmService bpmService;
    private final ResettlementRequestDocumentService resettlementRequestDocumentService;
    private final PersonDocumentService personDocumentService;
    private final TradeAdditionDocumentService tradeAdditionDocumentService;

    public DefaultRealEstateService(
        final RealEstateDocumentService realEstateDocumentService,
        final BpmService bpmService,
        final ResettlementRequestDocumentService resettlementRequestDocumentService,
        @Lazy final PersonDocumentService personDocumentService,
        final TradeAdditionDocumentService tradeAdditionDocumentService
    ) {
        this.realEstateDocumentService = realEstateDocumentService;
        this.bpmService = bpmService;
        this.resettlementRequestDocumentService = resettlementRequestDocumentService;
        this.personDocumentService = personDocumentService;
        this.tradeAdditionDocumentService = tradeAdditionDocumentService;
    }

    @Override
    public void createCompleteResettlementProcess(final BigInteger unom) {
        final RealEstateDocument realEstateDocument = ofNullable(unom)
            .map(BigInteger::toString)
            .map(realEstateDocumentService::fetchDocumentByUnom)
            .orElse(null);

        if (isNull(realEstateDocument)) {
            log.info("Unable to find real estate: unom = {}", unom);
            return;
        }

        final Map<String, String> variablesMap = new HashMap<>();
        variablesMap.put(BpmService.PROCESS_DOCUMENT_KEY_ENTITY_ID_VAR, realEstateDocument.getId());

        final String processInstanceId = bpmService.startNewProcess(PROCESS_KEY_COMPLETE_RESETTLEMENT, variablesMap);

        if (nonNull(processInstanceId)) {
            realEstateDocument.getDocument().getRealEstateData().setProcessInstanceId(processInstanceId);
            realEstateDocumentService.updateDocument(realEstateDocument, "createCompleteResettlementProcess");
        } else {
            log.info("Unable to create bpm process to complete resettlement: unom = {}", unom);
        }
    }

    @Async
    @Override
    public void calculateData() {
        log.info("Start calculate real estates data.");
        final long countDocuments = realEstateDocumentService.countDocuments();
        for (int page = 0; page <= countDocuments / PAGE_SIZE; page++) {
            realEstateDocumentService.fetchDocumentsPage(page, PAGE_SIZE)
                .forEach(realEstateDocument -> {
                    try {
                        calculateData(realEstateDocument);
                    } catch (Exception e) {
                        log.error(
                            "Unable to calculate real estate data (realEstateDocumentId = {}): {}",
                            realEstateDocument.getId(),
                            e.getMessage(),
                            e
                        );
                    }
                });
        }
        log.info("Finish calculate real estates data.");
    }

    private void calculateData(final RealEstateDocument realEstateDocument) {
        final RealEstateDataType realEstateData = realEstateDocument.getDocument().getRealEstateData();
        realEstateData.getCcoUnoms().clear();
        realEstateData.getCcoUnoms().addAll(
            calculateCcoUnoms(realEstateData.getUNOM())
        );

        final List<FlatType> flats = ofNullable(realEstateData.getFlats())
            .map(Flats::getFlat)
            .orElse(Collections.emptyList());

        final List<String> resettledFlatNumbers = flats.stream()
            .filter(flatType -> Objects.nonNull(flatType.getResettlementStatus()))
            .filter(flatType -> "9".equals(flatType.getResettlementStatus()))
            .map(FlatType::getApartmentL4VALUE)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        final List<String> activeResettlementFlatNumbers = flats.stream()
            .filter(flatType -> Objects.nonNull(flatType.getResettlementStatus()))
            .filter(flatType -> !"0".equals(flatType.getResettlementStatus())
                && !"9".equals(flatType.getResettlementStatus()))
            .map(FlatType::getApartmentL4VALUE)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        final List<String> courtFlatNumbers = ofNullable(realEstateData.getUNOM())
            .map(personDocumentService::fetchByUnom)
            .map(Collection::stream)
            .orElse(Stream.empty())
            .map(PersonDocument::getDocument)
            .map(Person::getPersonData)
            .filter(this::checkCourtMark)
            .map(PersonType::getFlatNum)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        final int resettledFlatCount = resettledFlatNumbers.size();
        final int activeResettlementFlatCount = activeResettlementFlatNumbers.size();
        final int courtFlatCount = courtFlatNumbers.size();

        final List<String> mfrFlatNumbers = ofNullable(realEstateData.getUNOM())
            .map(BigInteger::toString)
            .map(tradeAdditionDocumentService::fetchByOldEstateUnom)
            .map(Collection::stream)
            .orElse(Stream.empty())
            .map(TradeAdditionDocument::getDocument)
            .map(TradeAddition::getTradeAdditionTypeData)
            .map(TradeAdditionType::getOldEstate)
            .map(EstateInfoType::getFlatNumber)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        final int resettledFlatCountMfr = retrieveMfrFlatCount(mfrFlatNumbers, resettledFlatNumbers);
        final int activeResettlementFlatCountMfr = retrieveMfrFlatCount(mfrFlatNumbers, activeResettlementFlatNumbers);
        final int courtFlatCountMfr = retrieveMfrFlatCount(mfrFlatNumbers, courtFlatNumbers);

        realEstateData.setResettledFlatCountDgi(resettledFlatCount - resettledFlatCountMfr);
        realEstateData.setResettledFlatCountMfr(resettledFlatCountMfr);
        realEstateData.setCourtFlatCountDgi(activeResettlementFlatCount - activeResettlementFlatCountMfr);
        realEstateData.setCourtFlatCountMfr(activeResettlementFlatCountMfr);
        realEstateData.setActiveResettlementFlatCountDgi(courtFlatCount - courtFlatCountMfr);
        realEstateData.setActiveResettlementFlatCountMfr(courtFlatCountMfr);

        realEstateDocumentService.updateDocument(realEstateDocument, "calculateData");
        log.info("Finish calculate real estate data: realEstateDocumentId = {}", realEstateDocument.getId());
    }

    private int retrieveMfrFlatCount(final List<String> allMfrFlatNumbers, final List<String> flatNumbers) {
        return (int) flatNumbers.stream()
            .filter(allMfrFlatNumbers::contains)
            .count();
    }

    private boolean checkCourtMark(final PersonType personType) {
        return ofNullable(personType)
            .map(PersonType::getAddFlatInfo)
            .map(PersonType.AddFlatInfo::getInCourt)
            .filter("1"::equals)
            .isPresent();
    }

    private List<String> calculateCcoUnoms(final BigInteger unom) {
        return ofNullable(unom)
            .map(BigInteger::toString)
            .map(resettlementRequestDocumentService::fetchAllByRealEstateUnom)
            .map(Collection::stream)
            .orElse(Stream.empty())
            .map(ResettlementRequestDocument::getDocument)
            .map(ResettlementRequest::getMain)
            .map(ResettlementRequestType::getHousesToSettle)
            .flatMap(Collection::stream)
            .map(HouseToSettle::getCapitalConstructionObjectUnom)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }
}
