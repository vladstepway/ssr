package ru.croc.ugd.ssr.service.ssrcco;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.ApartmentEliminationDefectType;
import ru.croc.ugd.ssr.ApartmentInspection;
import ru.croc.ugd.ssr.ApartmentInspectionType;
import ru.croc.ugd.ssr.DelayReasonData;
import ru.croc.ugd.ssr.Person;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.dto.CcoInfo;
import ru.croc.ugd.ssr.enums.SsrCcoOrganizationType;
import ru.croc.ugd.ssr.exception.SsrException;
import ru.croc.ugd.ssr.model.ApartmentInspectionDocument;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.ssrcco.SsrCcoDocument;
import ru.croc.ugd.ssr.model.trade.TradeAdditionDocument;
import ru.croc.ugd.ssr.service.ApartmentInspectionService;
import ru.croc.ugd.ssr.service.CapitalConstructionObjectService;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.UserService;
import ru.croc.ugd.ssr.service.document.SsrCcoDocumentService;
import ru.croc.ugd.ssr.service.document.TradeAdditionDocumentService;
import ru.croc.ugd.ssr.ssrcco.SsrCco;
import ru.croc.ugd.ssr.ssrcco.SsrCcoData;
import ru.croc.ugd.ssr.ssrcco.SsrCcoEmployee;
import ru.croc.ugd.ssr.trade.BuyInStatus;
import ru.croc.ugd.ssr.trade.CompensationStatus;
import ru.croc.ugd.ssr.trade.EstateInfoType;
import ru.croc.ugd.ssr.trade.TradeAddition;
import ru.croc.ugd.ssr.trade.TradeAdditionType;
import ru.croc.ugd.ssr.utils.ApartmentInspectionUtils;
import ru.croc.ugd.ssr.utils.PersonUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class DefaultSsrCcoService implements SsrCcoService {

    private final SsrCcoDocumentService ssrCcoDocumentService;
    private final UserService userService;
    private final CapitalConstructionObjectService capitalConstructionObjectService;
    private final ApartmentInspectionService apartmentInspectionService;
    private final PersonDocumentService personDocumentService;
    private final TradeAdditionDocumentService tradeAdditionDocumentService;

    public DefaultSsrCcoService(
        final SsrCcoDocumentService ssrCcoDocumentService,
        final UserService userService,
        final CapitalConstructionObjectService capitalConstructionObjectService,
        @Lazy final ApartmentInspectionService apartmentInspectionService,
        final PersonDocumentService personDocumentService,
        final TradeAdditionDocumentService tradeAdditionDocumentService
    ) {
        this.ssrCcoDocumentService = ssrCcoDocumentService;
        this.userService = userService;
        this.capitalConstructionObjectService = capitalConstructionObjectService;
        this.apartmentInspectionService = apartmentInspectionService;
        this.personDocumentService = personDocumentService;
        this.tradeAdditionDocumentService = tradeAdditionDocumentService;
    }

    @Override
    public boolean existsCurrentUserAsEmployee(final String unom, final SsrCcoOrganizationType ssrCcoOrganizationType) {
        final String currentUserLogin = userService.getCurrentUserLogin();
        return retrieveActiveEmployees(unom).stream()
            .filter(ssrCcoEmployee -> Objects.equals(ssrCcoEmployee.getType(), ssrCcoOrganizationType.getTypeCode()))
            .anyMatch(ssrCcoEmployee -> Objects.equals(ssrCcoEmployee.getLogin(), currentUserLogin));
    }

    private List<SsrCcoEmployee> retrieveActiveEmployees(final String unom) {
        return ssrCcoDocumentService.fetchByUnom(unom)
            .map(SsrCcoDocument::getDocument)
            .map(SsrCco::getSsrCcoData)
            .map(SsrCcoData::getEmployees)
            .map(List::stream)
            .orElse(Stream.empty())
            .filter(this::isEmployeeActive)
            .collect(Collectors.toList());
    }

    private boolean isEmployeeActive(final SsrCcoEmployee ssrCcoEmployee) {
        return (isNull(ssrCcoEmployee.getPeriodFrom())
            || !ssrCcoEmployee.getPeriodFrom().isAfter(LocalDate.now()))
            && (isNull(ssrCcoEmployee.getPeriodTo())
            || !ssrCcoEmployee.getPeriodTo().isBefore(LocalDate.now()));
    }

    private static class FlatInfo {
        boolean isMfr;
        boolean hasAgreement;
        boolean hasContract;
        boolean hasKeyIssue;
        boolean hasApartmentInspection;
    }

    private FlatInfo getFlatInfo(final Map<String, FlatInfo> flats, final String flatNum) {
        FlatInfo flatInfo = flats.get(flatNum);
        if (flatInfo == null) {
            flatInfo = new FlatInfo();
            flats.put(flatNum, flatInfo);
        }
        return flatInfo;
    }

    @Override
    public void calculateDefectActTotalsData() {
        ssrCcoDocumentService.fetchAll().forEach(this::calculateDefectActTotalsData);
    }

    private void calculateDefectActTotalsData(final SsrCcoDocument ssrCcoDocument) {
        try {
            final SsrCcoData ssrCco = ssrCcoDocument.getDocument().getSsrCcoData();
            final String ccoDocumentId = ssrCco.getPsDocumentId();

            final CcoInfo ccoInfo = capitalConstructionObjectService
                .getCcoInfoByDocumentId(ccoDocumentId)
                .orElseThrow(() -> new SsrException("Unable to find oks " + ccoDocumentId));

            final String ccoUnom = ccoInfo.getUnom();

            final List<ApartmentInspectionType> apartmentInspections = apartmentInspectionService
                .fetchByUnom(ccoUnom)
                .stream()
                .map(ApartmentInspectionDocument::getDocument)
                .map(ApartmentInspection::getApartmentInspectionData)
                .filter(Objects::nonNull)
                .filter(a -> !a.isPending() && !a.isIsRemoved())
                .collect(Collectors.toList());

            final List<PersonType> persons = personDocumentService
                .getPersonByCcoUnom(ccoUnom)
                .stream()
                .map(PersonDocument::getDocument)
                .map(Person::getPersonData)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

            final List<TradeAdditionType> tradeAdditions = tradeAdditionDocumentService
                .fetchByNewEstateUnom(ccoUnom)
                .stream()
                .map(TradeAdditionDocument::getDocument)
                .map(TradeAddition::getTradeAdditionTypeData)
                .filter(Objects::nonNull)
                .filter(TradeAdditionType::isIndexed)
                .filter(TradeAdditionType::isConfirmed)
                .collect(Collectors.toList());

            final Map<String, FlatInfo> flats = new HashMap<>();

            int expiredActCount = 0;
            int actInWorkCount = 0;
            int defectCount = 0;
            int fixDefectCount = 0;
            int expiredDefectCount = 0;

            for (ApartmentInspectionType apartmentInspection : apartmentInspections) {
                final List<ApartmentEliminationDefectType> defects = apartmentInspection
                    .getApartmentDefects()
                    .stream()
                    .map(ApartmentInspectionType.ApartmentDefects::getApartmentDefectData)
                    .collect(Collectors.toList());

                defectCount += defects.size();

                if (Objects.equals(apartmentInspection.isHasConsent(), true)
                        || nonNull(apartmentInspection.getAcceptedDefectsDate())) {
                    fixDefectCount += defects.size();
                } else {
                    fixDefectCount += defects
                        .stream()
                        .filter(ApartmentEliminationDefectType::isIsEliminated)
                        .count();

                    getFlatInfo(flats, apartmentInspection.getFlat()).hasApartmentInspection = true;

                    actInWorkCount++;

                    final LocalDate lastDelayDate = ApartmentInspectionUtils.getLastDelayDate(apartmentInspection)
                        .orElse(LocalDate.now());
                    if (lastDelayDate.isBefore(LocalDate.now())) {
                        expiredActCount++;
                        expiredDefectCount += defects
                            .stream()
                            .filter(defect -> !defect.isIsEliminated())
                            .count();
                    }
                }
            }

            for (PersonType person : persons) {
                person.getNewFlatInfo().getNewFlat()
                    .stream()
                    .filter(f -> nonNull(f.getCcoUnom()) && Objects.equals(ccoUnom, f.getCcoUnom().toString()))
                    .filter(f -> nonNull(f.getCcoFlatNum()))
                    .forEach(newFlat -> {
                        final FlatInfo flatInfo = getFlatInfo(flats, newFlat.getCcoFlatNum());
                        if (personDocumentService.getContractSignDate(person, newFlat).isPresent()) {
                            flatInfo.hasAgreement = true;
                            flatInfo.hasContract = true;
                            flatInfo.hasKeyIssue =
                                nonNull(person.getKeysIssue()) && nonNull(person.getKeysIssue().getActNum());
                        } else {
                            PersonType.OfferLetters.OfferLetter lastOfferLetter =
                                PersonUtils.getLastOfferLetter(person).orElse(null);
                            flatInfo.hasAgreement = PersonUtils.getAcceptedAgreement(person, lastOfferLetter)
                                .isPresent();
                        }
                    });
            }

            for (TradeAdditionType tradeAddition : tradeAdditions) {
                tradeAddition.getNewEstates()
                    .stream()
                    .filter(e -> ccoUnom.equals(e.getUnom()))
                    .map(EstateInfoType::getFlatNumber)
                    .filter(Objects::nonNull)
                    .forEach(flatNum -> {
                        final FlatInfo flatInfo = getFlatInfo(flats, flatNum);
                        if (nonNull(tradeAddition.getContractSignedDate())) {
                            flatInfo.isMfr = true;
                            flatInfo.hasAgreement = true;
                            flatInfo.hasContract = true;
                            flatInfo.hasKeyIssue = tradeAddition.getBuyInStatus() == BuyInStatus.KEYS_ISSUED
                                || tradeAddition.getCompensationStatus() == CompensationStatus.KEYS_ISSUED
                                || tradeAddition.getCompensationStatus() == CompensationStatus.APARTMENT_VACATED;
                        } else if (tradeAddition.getBuyInStatus() == BuyInStatus.REQUEST_APPLIED
                            || tradeAddition.getBuyInStatus() == BuyInStatus.POSITIVE_DECISION_OF_COMMISSION
                            || tradeAddition.getBuyInStatus() == BuyInStatus.AUCTION
                            || tradeAddition.getBuyInStatus() == BuyInStatus.AUCTION_WON
                            || tradeAddition.getBuyInStatus() == BuyInStatus.CONTRACT_PROVIDED
                            || tradeAddition.getCompensationStatus() == CompensationStatus.AGREEMENT_APPLIED
                            || tradeAddition.getCompensationStatus() == CompensationStatus.CONTRACT_PROVIDED) {
                            flatInfo.isMfr = true;
                            flatInfo.hasAgreement = true;
                        }
                    });
            }

            final int flatWithoutOpenActCount = (int) flats.values().stream()
                .filter(f -> !f.hasApartmentInspection).count();
            final int flatWithAgreementCount = (int) flats.values().stream()
                .filter(f -> f.hasAgreement).count();
            final int flatWithContractCount = (int) flats.values().stream()
                .filter(f -> f.hasContract).count();
            final int flatWithKeyIssueCount = (int) flats.values().stream()
                .filter(f -> f.hasKeyIssue).count();
            final int flatDgiCount = (int) flats.values().stream()
                .filter(f -> !f.isMfr).count();
            final int flatMfrCount = (int) flats.values().stream()
                .filter(f -> f.isMfr).count();

            ssrCco.setActCount(apartmentInspections.size());
            ssrCco.setExpiredActCount(expiredActCount);
            ssrCco.setActInWorkCount(actInWorkCount);
            ssrCco.setDefectCount(defectCount);
            ssrCco.setFixDefectCount(fixDefectCount);
            ssrCco.setExpiredDefectCount(expiredDefectCount);

            ssrCco.setFlatCount(ccoInfo.getFlatCount());

            ssrCco.setFlatWithoutOpenActCount(flatWithoutOpenActCount);
            ssrCco.setFlatWithAgreementCount(flatWithAgreementCount);
            ssrCco.setFlatWithContractCount(flatWithContractCount);
            ssrCco.setFlatWithKeyIssueCount(flatWithKeyIssueCount);
            ssrCco.setFlatToResettleCount(flats.size() - flatWithKeyIssueCount);
            ssrCco.setFlatDgiCount(flatDgiCount);
            ssrCco.setFlatMfrCount(flatMfrCount);

            ssrCcoDocumentService.updateDocument(ssrCcoDocument, "calculateDefectActTotalsData");
        } catch (Exception e) {
            log.error(
                "Unable to calculate defect act total data for ssrCcoDocumentId = {}: {}",
                ssrCcoDocument.getId(),
                e.getMessage(),
                e
            );
        }
    }
}
