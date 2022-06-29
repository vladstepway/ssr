package ru.croc.ugd.ssr.service;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang.StringUtils.isEmpty;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.ApartmentInspectionType;
import ru.croc.ugd.ssr.FlatType;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.RealEstateDataType;
import ru.croc.ugd.ssr.dto.RealEstateDataAndFlatInfoDto;
import ru.croc.ugd.ssr.model.ApartmentInspectionDocument;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.RealEstateDocument;
import ru.croc.ugd.ssr.model.api.chess.CcoFlatInfoResponse;
import ru.croc.ugd.ssr.model.api.chess.CcoSolrResponse;
import ru.croc.ugd.ssr.model.api.chess.EntranceResponse;
import ru.croc.ugd.ssr.model.api.chess.EntrancesResponse;
import ru.croc.ugd.ssr.model.api.chess.FamilyResponse;
import ru.croc.ugd.ssr.model.api.chess.FlatInfoResponse;
import ru.croc.ugd.ssr.model.api.chess.FlatResponse;
import ru.croc.ugd.ssr.model.api.chess.FlatsResponse;
import ru.croc.ugd.ssr.model.api.chess.FullFlatResponse;
import ru.croc.ugd.ssr.model.api.chess.FullOksHousesResponse;
import ru.croc.ugd.ssr.model.api.chess.FullResHousesResponse;
import ru.croc.ugd.ssr.model.api.chess.HousesResponse;
import ru.croc.ugd.ssr.model.api.chess.OksHousesResponse;
import ru.croc.ugd.ssr.model.api.chess.PeopleResponse;
import ru.croc.ugd.ssr.model.api.chess.ResHousesResponse;
import ru.croc.ugd.ssr.model.api.chess.StatsHouseResponse;
import ru.croc.ugd.ssr.model.trade.TradeAdditionDocument;
import ru.croc.ugd.ssr.service.document.TradeAdditionDocumentService;
import ru.croc.ugd.ssr.trade.ClaimStatus;
import ru.croc.ugd.ssr.trade.EstateInfoType;
import ru.croc.ugd.ssr.trade.PersonInfoType;
import ru.croc.ugd.ssr.trade.TradeAdditionType;
import ru.croc.ugd.ssr.trade.TradeType;
import ru.croc.ugd.ssr.utils.RealEstateUtils;
import ru.reinform.cdp.exception.RINotFoundException;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Сервис по получению данных для шахматки.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChessApiService {

    private final ApartmentInspectionService apartmentInspectionService;
    private final CapitalConstructionObjectService ccoService;
    private final FlatService flatService;
    private final PersonDocumentService personDocumentService;
    private final RealEstateDocumentService realEstateDocumentService;
    private final RealEstateSolrService realEstateSolrService;
    private final TradeAdditionDocumentService tradeAdditionDocumentService;
    private final Map<String, String> flatPersonStatus = new HashMap<String, String>() {
        {
            put("1", "1");
            put("2", "2");
            put("3", "3");
            put("4", "4");
            put("5", "12");
            put("6", "5");
            put("7", "5");
            put("8", "5");
            put("9", "6");
            put("10", "6");
            put("11", "11");
            put("14", "7");
            put("15", "9");
        }
    };
    private final Map<String, String> priorityStatus = new HashMap<String, String>() {
        {
            put("0", "0");
            put("1", "1");
            put("2", "2");
            put("3", "3");
            put("4", "4");
            put("5", "6");
            put("6", "7");
            put("7", "8");
            put("8", "9");
            put("9", "10");
            put("10", "11");
            put("11", "12");
            put("12", "5");
        }
    };

    /**
     * Получение списка отселяемых домов по фрагменту адреса (подстрока).
     *
     * @param addrSubstr Подстрока для поиска в адресе
     * @param selectTop  Количество возвращаемых записей
     * @return список отселяемых домов
     */
    public ResHousesResponse getResHouses(String addrSubstr, Integer selectTop) {
        List<Map<String, Object>> solrDocs = realEstateSolrService
            .getRealEstatesFromSolrByAddrSubstr(addrSubstr, selectTop).getDocs();

        ResHousesResponse resHousesResponse = new ResHousesResponse();
        if (nonNull(solrDocs)) {
            for (Map<String, Object> doc : solrDocs) {
                resHousesResponse.getResHouses().add(solrRealEstateMapper(doc));
            }
        }

        return resHousesResponse;
    }

    /**
     * Получение списка заселяемых домов по фрагменту адреса (подстрока).
     *
     * @param addrSubstr Подстрока для поиска в адресе
     * @param selectTop  Количество возвращаемых записей
     * @return список заселяемых домов
     */
    public OksHousesResponse getOksHouses(String addrSubstr, Integer selectTop) {
        List<Map<String, Object>> solrDocs = ccoService.getCcoFromSolrByAddrSubstr(addrSubstr, selectTop).getDocs();

        OksHousesResponse oksHousesResponse = new OksHousesResponse();
        if (nonNull(solrDocs)) {
            for (Map<String, Object> doc : solrDocs) {
                oksHousesResponse.getOksHouses().add(solrOksMapper(doc));
            }
        }

        return oksHousesResponse;
    }

    /**
     * Получение данных по заселяемым домам для отселяемого дома.
     *
     * @param unom УНОМ дома
     * @return данные по заселяемому дому
     */
    public OksHousesResponse getLinkedOksHouses(String unom) {
        List<PersonType> persons = personDocumentService.fetchByUnom(unom)
            .stream()
            .map(personDocument -> personDocument.getDocument().getPersonData())
            .filter(personType -> !personType.isIsArchive())
            .collect(Collectors.toList());

        Map<String, Set<String>> ccoWithFlats = getCooWithFlatsByRePersons(persons);

        OksHousesResponse oksHousesResponse = new OksHousesResponse();

        if (!ccoWithFlats.isEmpty()) {
            List<Map<String, Object>> solrDocs = ccoService.getCcoFromSolrByUnoms(ccoWithFlats.keySet()).getDocs();

            if (nonNull(solrDocs)) {
                for (Map<String, Object> doc : solrDocs) {
                    HousesResponse housesResponse = solrOksMapper(doc);
                    housesResponse.setLink(
                        "/ugd/#/app/ps/capital-construction-object-card/" + doc.get("sys_documentId")
                    );
                    housesResponse.setFlatCount(ccoWithFlats.get((String) doc.get("ugd_ps_oks_unom")).size());
                    oksHousesResponse.getOksHouses().add(housesResponse);
                }
            }
        }

        return oksHousesResponse;
    }

    /**
     * Получение данных по отселяемым домам для заселяемого дома (ОКС).
     *
     * @param unom УНОМ дома
     * @return данные по отселяемому дому
     */
    public ResHousesResponse getLinkedResHouses(String unom) {
        List<PersonType> persons = personDocumentService.getPersonsByNewFlatCcoUnom(unom)
            .stream()
            .map(personDocument -> personDocument.getDocument().getPersonData())
            .collect(Collectors.toList());
        Set<String> realEstateUnomsHashSet = persons
            .stream()
            .map(person -> person.getUNOM().toString())
            .collect(Collectors.toCollection(HashSet::new));
        ResHousesResponse resHousesResponse = new ResHousesResponse();
        for (String realEstateUnom : realEstateUnomsHashSet) {
            RealEstateDocument realEstateDocument = realEstateDocumentService.fetchDocumentByUnom(realEstateUnom);
            RealEstateDataType realEstateData = realEstateDocument.getDocument().getRealEstateData();
            HousesResponse housesResponse = realEstateMapper(realEstateData);
            housesResponse.setLink("/ugd/ssr/#/app/template/real-estate-card/" + realEstateDocument.getId());
            Set<String> personUniqueFlat = persons
                .stream()
                .filter(person -> person.getUNOM().equals(realEstateData.getUNOM()))
                .map(PersonType::getFlatID)
                .collect(Collectors.toCollection(HashSet::new));
            housesResponse.setFlatCount(personUniqueFlat.size());
            resHousesResponse.getResHouses().add(housesResponse);
        }
        return resHousesResponse;
    }

    /**
     * Получение данных по подъездам и квартирам в отселяемом доме.
     *
     * @param unom УНОМ дома
     * @return данные по подъездам и квартирам в отселяемом доме
     */
    public EntrancesResponse getResEntranceInfo(String unom) {
        EntrancesResponse entrancesResponse = new EntrancesResponse();
        RealEstateDocument realEstateDocument = realEstateDocumentService.fetchDocumentByUnom(unom);
        if (isNull(realEstateDocument)) {
            return entrancesResponse;
        }
        RealEstateDataType.Flats realEstateFlats = realEstateDocument.getDocument().getRealEstateData().getFlats();
        if (isNull(realEstateFlats)) {
            return entrancesResponse;
        }
        List<FlatType> flats = realEstateFlats.getFlat();
        for (FlatType flat : flats) {
            EntranceResponse entranceResponse = new EntranceResponse();
            if (nonNull(flat.getEntrance())) {
                entranceResponse.setEntrance(flat.getEntrance());
            } else {
                entranceResponse.setEntrance("0");
            }
            entranceResponse.setFlatStatus(flat.getResettlementStatus());
            String flatNumber = flat.getApartmentL4VALUE();
            if (isNull(flatNumber)) {
                entranceResponse.setFlatNumber(flat.getFlatNumber());
            } else {
                entranceResponse.setFlatNumber(flatNumber);
            }
            entrancesResponse.getEntrances().add(entranceResponse);
        }

        return entrancesResponse;
    }

    /**
     * Получение данных по подъездам и квартирам в заселяемом доме.
     *
     * @param unom УНОМ дома
     * @return данные по подъездам и квартирам в заселяемом доме
     */
    public EntrancesResponse getOksEntranceInfo(String unom) {
        EntrancesResponse ccoChessFlatsByUnom = ccoService.getCcoChessFlatsByUnom(unom);

        if (
            personDocumentService.getPersonsByNewFlatCcoUnom(unom)
                .stream()
                .map(personDocument -> personDocument.getDocument().getPersonData())
                .anyMatch(personDocument -> {
                    List<PersonType.NewFlatInfo.NewFlat> newFlatInfo = personDocument
                        .getNewFlatInfo()
                        .getNewFlat();
                    PersonType.NewFlatInfo.NewFlat lastNewFlat = newFlatInfo.get(newFlatInfo.size() - 1);
                    return lastNewFlat.getCcoUnom().toString().equals(unom) && isNull(lastNewFlat.getCcoFlatNum());
                })
        ) {
            EntranceResponse entranceResponse = new EntranceResponse();
            entranceResponse.setEntrance("0");
            entranceResponse.setFlatNumber("0");
            entranceResponse.setFlatStatus("0");
            ccoChessFlatsByUnom.getEntrances().add(entranceResponse);
        }

        return ccoChessFlatsByUnom;
    }

    /**
     * Получение данных по квартирам в подъезде в отселяемом доме.
     *
     * @param unom     УНОМ дома
     * @param entrance Номер подъезда
     * @return данные по квартирам в подъезде в отселяемом доме
     */
    public FlatsResponse getResFlatInfo(String unom, String entrance) {
        FlatsResponse flatsResponse = new FlatsResponse();
        RealEstateDocument realEstateDocument = realEstateDocumentService.fetchDocumentByUnom(unom);
        if (isNull(realEstateDocument)) {
            return flatsResponse;
        }
        RealEstateDataType.Flats realEstateFlats = realEstateDocument.getDocument().getRealEstateData().getFlats();
        if (isNull(realEstateFlats)) {
            return flatsResponse;
        }
        List<FlatType> flats = realEstateFlats.getFlat();
        List<FlatType> flatsByEntrance = flats
            .stream()
            .filter(flat -> "0".equals(entrance) && isNull(flat.getEntrance())
                || nonNull(flat.getEntrance()) && flat.getEntrance().equals(entrance))
            .collect(Collectors.toList());

        List<TradeAdditionType> tradeAdditionTypes = tradeAdditionDocumentService.fetchByOldEstateUnom(unom)
            .stream()
            .map(tradeAdditionDocument -> tradeAdditionDocument.getDocument().getTradeAdditionTypeData())
            .filter(tradeAdditionType ->
                tradeAdditionType.isIndexed()
                && ClaimStatus.ACTIVE.equals(tradeAdditionType.getClaimStatus())
                && tradeAdditionType.isConfirmed())
            .collect(Collectors.toList());

        List<PersonType> personDocumentsByUnom = personDocumentService.fetchByUnom(unom)
            .stream()
            .map(personDocument -> personDocument.getDocument().getPersonData())
            .filter(personType -> !personType.isIsArchive())
            .collect(Collectors.toList());

        for (FlatType flatType : flatsByEntrance) {
            FlatResponse flatResponse = new FlatResponse();

            String flatNumber;
            if (nonNull(flatType.getApartmentL4VALUE())) {
                flatNumber = flatType.getApartmentL4VALUE();
            } else {
                flatNumber = flatType.getFlatNumber();
            }
            flatResponse.setFlatNumber(flatNumber);

            if (nonNull(flatType.getFloor())) {
                flatResponse.setFloor(flatType.getFloor());
            }
            if (nonNull(flatType.getRoomsCount())) {
                flatResponse.setRoomsCount((int) flatType.getRoomsCount().longValue());
            }
            if (nonNull(flatType.getFlatType())) {
                flatResponse.setCommunal(flatType.getFlatType().equals("Коммунальная") ? 1 : 0);
            }
            flatResponse.setLink("/ugd/ssr/#/app/template/flat-card/" + flatType.getFlatID());
            if (nonNull(flatType.getSAll())) {
                flatResponse.setTotalSq(flatType.getSAll());
            }
            if (nonNull(flatType.getSGil())) {
                flatResponse.setLivingSq(flatType.getSGil());
            }

            flatResponse.setFlatStatus(flatType.getResettlementStatus());

            List<PersonType> flatPersons = personDocumentsByUnom
                .stream()
                .filter(personDocument -> personDocument.getFlatID().equals(flatType.getFlatID()))
                .collect(Collectors.toList());

            Set<String> affairIdSet = flatPersons
                .stream()
                .map(PersonType::getAffairId)
                .collect(Collectors.toCollection(HashSet::new));
            if (affairIdSet.isEmpty() || affairIdSet.size() == 1) {
                flatResponse.setFlatStatus(flatType.getResettlementStatus());
            } else {
                flatPersons
                    .stream()
                    .filter(personDocument -> nonNull(personDocument.getRelocationStatus()))
                    .map(personDocument -> flatPersonStatus.get(personDocument.getRelocationStatus()))
                    .min(Comparator.comparing(a -> Integer.parseInt(priorityStatus.get(a))))
                    .ifPresent(flatResponse::setFlatStatus);
            }

            Optional<TradeAdditionType> optionalTradeAdditionType = tradeAdditionTypes
                .stream()
                .filter(tradeAdditionType -> tradeAdditionType.getOldEstate().getFlatNumber().equals(flatNumber))
                .findFirst();

            flatResponse.setRemovalType(getResFlatRemovalType(optionalTradeAdditionType, flatPersons));

            flatsResponse.getFlats().add(flatResponse);
        }

        return flatsResponse;
    }

    /**
     * Получение данных по квартирам в подъезде в заселяемом доме.
     *
     * @param unom     УНОМ дома
     * @param entrance Номер подъезда
     * @return данные по квартирам в подъезде в заселяемом доме
     */
    public FlatsResponse getOksFlatInfo(String unom, String entrance) {
        try {
            FlatsResponse ccoChessEntranceFlatsByUnom = ccoService.getCcoChessEntranceFlatsByUnom(unom, entrance);
            if (isNull(ccoChessEntranceFlatsByUnom)) {
                return new FlatsResponse();
            }
            List<TradeAdditionType> tradeAdditionTypes = tradeAdditionDocumentService.fetchByNewEstateUnom(unom)
                .stream()
                .map(tradeAdditionDocument -> tradeAdditionDocument.getDocument().getTradeAdditionTypeData())
                .filter(tradeAdditionType ->
                    tradeAdditionType.isIndexed()
                        && ClaimStatus.ACTIVE.equals(tradeAdditionType.getClaimStatus())
                        && tradeAdditionType.isConfirmed())
                .collect(Collectors.toList());
            List<PersonType> flatPersonsByUnom = personDocumentService.getPersonsByNewFlatCcoUnom(unom)
                .stream()
                .map(personDocument -> personDocument.getDocument().getPersonData())
                .collect(Collectors.toList());
            List<ApartmentInspectionType> apartmentInspectionTypes = apartmentInspectionService.fetchByUnom(unom)
                .stream()
                .map(apartmentInspectionDocument ->
                    apartmentInspectionDocument
                        .getDocument()
                        .getApartmentInspectionData()
                )
                .filter(inspection -> !inspection.getApartmentDefects().isEmpty())
                .collect(Collectors.toList());

            if (
                entrance.equals("0")
                    && flatPersonsByUnom
                    .stream()
                    .anyMatch(personDocument -> {
                        List<PersonType.NewFlatInfo.NewFlat> newFlatInfo = personDocument
                            .getNewFlatInfo()
                            .getNewFlat();
                        PersonType.NewFlatInfo.NewFlat lastNewFlat = newFlatInfo.get(newFlatInfo.size() - 1);
                        return lastNewFlat.getCcoUnom().toString().equals(unom) && isNull(lastNewFlat.getCcoFlatNum());
                    })
            ) {
                FlatResponse flatResponse = new FlatResponse();
                flatResponse.setFlatStatus("0");
                flatResponse.setFlatNumber("0");
                flatResponse.setFloor(0);
                flatResponse.setCommunal(0);
                flatResponse.setRemovalType(0);
                flatResponse.setLink("0");
                flatResponse.setDefect(0);
                flatResponse.setInvalid(0);
                flatResponse.setTotalSq(0);
                flatResponse.setRoomsCount(0);
                flatResponse.setLivingSq(0);
                ccoChessEntranceFlatsByUnom.getFlats().add(flatResponse);
            }

            for (FlatResponse flat : ccoChessEntranceFlatsByUnom.getFlats()) {
                if (flat.getFlatNumber().equals("0")) {
                    continue;
                }
                apartmentInspectionTypes
                    .stream()
                    .filter(
                        apartmentInspectionType -> nonNull(apartmentInspectionType.getFlat())
                            && apartmentInspectionType.getFlat().equals(flat.getFlatNumber())
                    )
                    .findFirst()
                    .ifPresent(apartmentInspectionDocument -> flat.setDefect(1));

                Optional<TradeAdditionType> additionType = tradeAdditionTypes
                    .stream()
                    .filter(
                        tradeAdditionType -> {
                            List<EstateInfoType> collect = tradeAdditionType.getNewEstates()
                                .stream()
                                .filter(estateInfoType -> estateInfoType.getFlatNumber().equals(flat.getFlatNumber()))
                                .collect(Collectors.toList());
                            return !collect.isEmpty();
                        }
                    )
                    .findFirst();

                if (additionType.isPresent()) {
                    TradeType tradeType = additionType.get().getTradeType();
                    if (nonNull(tradeType)) {
                        if (
                            tradeType.equals(TradeType.SIMPLE_TRADE)
                                || tradeType.equals(TradeType.TRADE_WITH_COMPENSATION)
                                || tradeType.equals(TradeType.TRADE_IN_TWO_YEARS)
                        ) {
                            flat.setRemovalType(2);
                        } else if (tradeType.equals(TradeType.COMPENSATION)) {
                            flat.setRemovalType(3);
                        } else {
                            flat.setRemovalType(1);
                        }
                    } else {
                        flat.setRemovalType(1);
                    }
                } else {
                    flatPersonsByUnom
                        .stream()
                        .filter(personDocument -> {
                            List<PersonType.NewFlatInfo.NewFlat> newFlatInfo = personDocument
                                .getNewFlatInfo()
                                .getNewFlat();
                            PersonType.NewFlatInfo.NewFlat lastNewFlat = newFlatInfo.get(newFlatInfo.size() - 1);
                            if (nonNull(lastNewFlat.getCcoFlatNum())) {
                                return lastNewFlat.getCcoFlatNum().equals(flat.getFlatNumber());
                            }
                            return false;
                        })
                        .filter(
                            personDocument -> nonNull(personDocument.getOfferLetters())
                                && !personDocument.getOfferLetters().getOfferLetter().isEmpty()
                        )
                        .findFirst()
                        .ifPresent(personDocument -> flat.setRemovalType(1));
                }
            }

            return ccoChessEntranceFlatsByUnom;
        } catch (Exception e) {
            log.error("Ошибка при получение данных по квартирам в подъезде в заселяемом доме", e);
            throw e;
        }
    }

    /**
     * Получение данных квартиры и жителей отселяемого дома.
     *
     * @param unom       УНОМ дома
     * @param flatNumber Номер квартиры
     * @return данные квартиры и жителей в отселяемом доме
     */
    public FlatInfoResponse getResExtFlatInfo(String unom, String flatNumber) {
        RealEstateDocument realEstateDocument = realEstateDocumentService.fetchDocumentByUnom(unom);
        if (isNull(realEstateDocument)) {
            return null;
        }
        List<FlatType> flats = realEstateDocument.getDocument().getRealEstateData().getFlats().getFlat();
        FlatInfoResponse flatInfoResponse = new FlatInfoResponse();
        flats
            .stream()
            .filter(flat -> (nonNull(flat.getFlatNumber()) && flat.getFlatNumber().equals(flatNumber))
                || flat.getApartmentL4VALUE().equals(flatNumber))
            .findFirst()
            .ifPresent(flat -> {
                if (nonNull(flat.getSKitchen())) {
                    flatInfoResponse.setKitchenSq(flat.getSKitchen());
                }
                flatInfoResponse.setFlatStatus(flat.getResettlementStatus());
                List<PersonDocument> personDocuments = personDocumentService.fetchByFlatId(flat.getFlatID())
                    .stream()
                    .filter(personDocument -> !personDocument.getDocument().getPersonData().isIsArchive())
                    .collect(Collectors.toList());
                Set<String> affairIdSet = personDocuments
                    .stream()
                    .map(personDocument -> personDocument.getDocument().getPersonData().getAffairId())
                    .collect(Collectors.toCollection(HashSet::new));
                if (affairIdSet.isEmpty() || affairIdSet.size() == 1) {
                    flatInfoResponse.setFlatStatus(flat.getResettlementStatus());
                } else {
                    personDocuments
                        .stream()
                        .filter(personDocument -> nonNull(
                            personDocument.getDocument().getPersonData().getRelocationStatus()
                        ))
                        .map(
                            personDocument -> flatPersonStatus
                                .get(personDocument.getDocument().getPersonData().getRelocationStatus())
                        )
                        .min(Comparator.comparing(a -> Integer.parseInt(priorityStatus.get(a))))
                        .ifPresent(flatInfoResponse::setFlatStatus);
                }

                Map<String, CcoSolrResponse> ccoIdByUnom = new HashMap<>();

                for (String affairId : affairIdSet) {
                    FamilyResponse familyResponse = new FamilyResponse();
                    familyResponse.setAffairId(affairId);

                    if (affairIdSet.size() > 1) {
                        personDocuments
                            .stream()
                            .filter(personDocument -> nonNull(
                                personDocument.getDocument().getPersonData().getRelocationStatus()
                            ))
                            .filter(personDocument -> nonNull(
                                personDocument.getDocument().getPersonData().getAffairId()
                            ))
                            .filter(
                                personDocument ->
                                    personDocument.getDocument().getPersonData().getAffairId().equals(affairId)
                            )
                            .map(
                                personDocument -> flatPersonStatus
                                    .get(personDocument.getDocument().getPersonData().getRelocationStatus())
                            )
                            .min(Comparator.comparing(a -> Integer.parseInt(priorityStatus.get(a))))
                            .map(Integer::parseInt)
                            .ifPresent(familyResponse::setResStatus);
                    } else if (nonNull(flat.getResettlementStatus())) {
                        familyResponse.setResStatus(Integer.parseInt(flat.getResettlementStatus()));
                    }

                    personDocuments
                        .stream()
                        .filter(
                            personDocument ->
                                nonNull(personDocument.getDocument().getPersonData().getAffairId())
                                    && personDocument.getDocument().getPersonData().getAffairId()
                                    .equals(affairId)
                        )
                        .forEach(personDocument -> {
                            PersonType personType = personDocument.getDocument().getPersonData();
                            if (
                                isNull(familyResponse.getRoomsNum())
                                    && nonNull(personType.getRoomNum())
                                    && !personType.getRoomNum().isEmpty()
                            ) {
                                familyResponse.setRoomsNum(String.join(", ", personType.getRoomNum()));
                            }
                            if (isNull(familyResponse.getAddress())) {
                                if (nonNull(personType.getNewFlatInfo())) {
                                    List<PersonType.NewFlatInfo.NewFlat> newFlat
                                        = personType.getNewFlatInfo().getNewFlat();
                                    if (nonNull(newFlat) && !newFlat.isEmpty()) {
                                        PersonType.NewFlatInfo.NewFlat newFlatInfo = newFlat.get(newFlat.size() - 1);
                                        if (nonNull(newFlatInfo.getCcoUnom())) {
                                            String newFlatUnom = newFlatInfo.getCcoUnom().toString();
                                            if (ccoIdByUnom.containsKey(newFlatUnom)) {
                                                familyResponse.setLink(
                                                    "/ugd/#/app/ps/capital-construction-object-card/"
                                                        + ccoIdByUnom.get(newFlatUnom).getId()
                                                );
                                                familyResponse.setAddress(
                                                    ccoIdByUnom.get(newFlatUnom).getAddress()
                                                        + ", квартира " + newFlatInfo.getCcoFlatNum()
                                                );
                                            } else {
                                                CcoSolrResponse ccoSolrResponseByUnom = ccoService
                                                    .getCcoSolrResponseByUnom(newFlatUnom);
                                                ccoIdByUnom.put(newFlatUnom, ccoSolrResponseByUnom);
                                                familyResponse.setLink(
                                                    "/ugd/#/app/ps/capital-construction-object-card/"
                                                        + ccoSolrResponseByUnom.getId()
                                                );
                                                familyResponse.setAddress(
                                                    ccoSolrResponseByUnom.getAddress()
                                                        + ", квартира " + newFlatInfo.getCcoFlatNum()
                                                );
                                            }
                                        }
                                    }
                                }
                            }
                            PeopleResponse peopleResponse = new PeopleResponse();
                            peopleResponse.setFio(personType.getFIO());
                            peopleResponse.setLiveStatus(personType.getStatusLiving());
                            peopleResponse.setLink("ugd/ssr/#/app/person/" + personDocument.getId());
                            familyResponse.getPeoples().add(peopleResponse);
                        });

                    flatInfoResponse.getFamily().add(familyResponse);
                }
            });

        return flatInfoResponse;
    }

    /**
     * Получение данных квартиры и жителей в ОКС.
     *
     * @param unom       УНОМ дома
     * @param flatNumber Номер квартиры
     * @return данные квартиры и жителей в заселяемом доме
     */
    public FlatInfoResponse getOksExtFlatInfo(String unom, String flatNumber, String entrance, String floor) {
        FlatInfoResponse flatInfoResponse = new FlatInfoResponse();

        List<PersonDocument> nonFilteredPersonDocuments = personDocumentService.getPersonsByNewFlatCcoUnom(unom);
        List<PersonDocument> personDocuments;

        if (flatNumber.equals("0")) {
            personDocuments = nonFilteredPersonDocuments
                .stream()
                .filter(personDocument -> {
                    List<PersonType.NewFlatInfo.NewFlat> newFlatList
                        = personDocument.getDocument().getPersonData().getNewFlatInfo().getNewFlat();
                    if (!newFlatList.isEmpty()) {
                        String ccoFlatNum = newFlatList.get(newFlatList.size() - 1).getCcoFlatNum();
                        return isNull(ccoFlatNum);
                    }
                    return false;
                })
                .collect(Collectors.toList());

            flatInfoResponse.setKitchenSq(0);
            flatInfoResponse.setFlatStatus("0");
        } else {
            CcoFlatInfoResponse ccoChessFlatInfoByUnom = ccoService
                .getCcoChessFlatInfoByUnom(unom, flatNumber, entrance, floor);

            List<ApartmentInspectionDocument> apartmentInspectionDocuments
                = apartmentInspectionService.fetchByUnomAndFlatNumber(unom, flatNumber)
                .stream()
                .filter(
                    inspection -> !inspection.getDocument().getApartmentInspectionData().getApartmentDefects().isEmpty()
                )
                .collect(Collectors.toList());

            for (ApartmentInspectionDocument apartmentInspectionDocument : apartmentInspectionDocuments) {
                apartmentInspectionDocument.getDocument().getApartmentInspectionData().getApartmentDefects().forEach(
                    apartmentDefects -> flatInfoResponse.getDefectList().add(
                        apartmentDefects.getApartmentDefectData().getDescription()
                    )
                );
            }

            personDocuments = nonFilteredPersonDocuments
                .stream()
                .filter(personDocument -> {
                    List<PersonType.NewFlatInfo.NewFlat> newFlatList
                        = personDocument.getDocument().getPersonData().getNewFlatInfo().getNewFlat();
                    if (!newFlatList.isEmpty()) {
                        String ccoFlatNum = newFlatList.get(newFlatList.size() - 1).getCcoFlatNum();
                        return nonNull(ccoFlatNum) && ccoFlatNum.equals(flatNumber);
                    }
                    return false;
                })
                .collect(Collectors.toList());

            flatInfoResponse.setKitchenSq(ccoChessFlatInfoByUnom.getKitchenSq());
            flatInfoResponse.setFlatStatus(ccoChessFlatInfoByUnom.getFlatStatus());
        }

        Set<String> affairIdSet = personDocuments
            .stream()
            .map(personDocument -> personDocument.getDocument().getPersonData().getAffairId())
            .collect(Collectors.toCollection(HashSet::new));

        for (String affairId : affairIdSet) {
            FamilyResponse familyResponse = new FamilyResponse();
            familyResponse.setAffairId(affairId);

            personDocuments
                .stream()
                .filter(
                    personDocument ->
                        personDocument.getDocument().getPersonData().getAffairId().equals(affairId)
                )
                .forEach(personDocument -> {
                    PersonType personType = personDocument.getDocument().getPersonData();
                    if (isNull(familyResponse.getAddress())) {
                        final RealEstateDataAndFlatInfoDto realEstateDataAndFlatInfoDto = flatService
                            .fetchRealEstateAndFlat(personType.getFlatID());
                        familyResponse.setAddress(RealEstateUtils.getFlatAddress(realEstateDataAndFlatInfoDto));
                        Optional.ofNullable(realEstateDataAndFlatInfoDto)
                            .map(RealEstateDataAndFlatInfoDto::getFlat)
                            .map(FlatType::getFlatID)
                            .ifPresent(flatId -> familyResponse.setLink(
                                "/ugd/ssr/#/app/flat/" + flatId + "/affairId/" + affairId
                            ));
                    }

                    PeopleResponse peopleResponse = new PeopleResponse();
                    peopleResponse.setFio(personType.getFIO());
                    peopleResponse.setLiveStatus(personType.getStatusLiving());
                    peopleResponse.setLink("ugd/ssr/#/app/person/" + personDocument.getId());
                    familyResponse.getPeoples().add(peopleResponse);
                });

            flatInfoResponse.getFamily().add(familyResponse);
        }

        // Поиск семей по данным TRADE-ADDITION
        List<TradeAdditionDocument> allTradeAdditionsForUnom = tradeAdditionDocumentService.fetchByNewEstateUnom(unom);
        // Мапа вида "ID семьи -> Коллекция номеров квартир".
        Map<String, Set<String>> affairToFlatNumberMap = allTradeAdditionsForUnom.stream()
            .map(tradeAdditionDocument -> tradeAdditionDocument.getDocument().getTradeAdditionTypeData())
            .filter(tradeAdditionType -> tradeAdditionType.isConfirmed() && tradeAdditionType.isIndexed()
                && ClaimStatus.ACTIVE.equals(tradeAdditionType.getClaimStatus()))
            .filter(tradeAdditionType -> nonNull(tradeAdditionType.getNewEstates())
                && !tradeAdditionType.getNewEstates().isEmpty())
            .collect(Collectors.toMap(
                TradeAdditionType::getAffairId,
                tradeAdditionType -> tradeAdditionType.getNewEstates()
                    .stream().map(EstateInfoType::getFlatNumber).collect(Collectors.toSet()),
                (firstFlatSet, secondFlatSet) -> {
                    firstFlatSet.addAll(secondFlatSet);
                    return firstFlatSet;
                }
            ));

        if (!affairToFlatNumberMap.isEmpty()) {
            List<PersonDocument> personsByTradeAdditions = personDocumentService
                .fetchByAffairIds(affairToFlatNumberMap.keySet());

            personsByTradeAdditions.forEach(personDocument -> {
                PeopleResponse peopleResponse = new PeopleResponse();
                peopleResponse.setFio(personDocument.getDocument().getPersonData().getFIO());
                peopleResponse.setLiveStatus(personDocument.getDocument().getPersonData().getStatusLiving());
                peopleResponse.setLink("ugd/ssr/#/app/person/" + personDocument.getId());

                String affairId = personDocument.getDocument().getPersonData().getAffairId();
                if (nonNull(affairId)) {
                    Optional<FamilyResponse> familyWithSameAffairId = flatInfoResponse.getFamily().stream()
                        .filter(familyResponse -> affairId.equals(familyResponse.getAffairId()))
                        .findFirst();
                    String flatNumberByPerson = getPersonNewFlatNumber(personDocument);
                    if (familyWithSameAffairId.isPresent()) {
                        if (familyWithSameAffairId.get().getPeoples().stream()
                            .noneMatch(person -> person.getFio().equals(peopleResponse.getFio()))
                            && (affairToFlatNumberMap.get(affairId).contains(flatNumber)
                            || flatNumber.equals(flatNumberByPerson))) {
                            familyWithSameAffairId.get().getPeoples().add(peopleResponse);
                        }
                    } else if (affairToFlatNumberMap.get(affairId).contains(flatNumber)
                        || flatNumber.equals(flatNumberByPerson)) {
                        FamilyResponse newFamilyResponse = new FamilyResponse();
                        newFamilyResponse.setAffairId(personDocument.getDocument().getPersonData().getAffairId());
                        newFamilyResponse.getPeoples().add(peopleResponse);
                        flatInfoResponse.getFamily().add(newFamilyResponse);

                        final RealEstateDataAndFlatInfoDto realEstateDataAndFlatInfoDto = flatService
                            .fetchRealEstateAndFlat(personDocument.getDocument().getPersonData().getFlatID());
                        newFamilyResponse.setAddress(RealEstateUtils.getFlatAddress(realEstateDataAndFlatInfoDto));
                    }
                }
            });
        }

        return flatInfoResponse;
    }

    /**
     * Получение статистики по отселяемому дому по УНОМ.
     *
     * @param unom УНОМ дома
     * @return статистика по отселяемому дому
     */
    public StatsHouseResponse getStatResHouses(String unom) {
        StatsHouseResponse statsHouseResponse = new StatsHouseResponse();

        RealEstateDocument realEstateDocument = realEstateDocumentService.fetchDocumentByUnom(unom);
        if (isNull(realEstateDocument)) {
            return statsHouseResponse;
        }

        statsHouseResponse.setInProgress("0");
        statsHouseResponse.setDone("0");
        statsHouseResponse.setCourt("0");

        RealEstateDataType.Flats realEstateFlats = realEstateDocument.getDocument().getRealEstateData().getFlats();
        if (nonNull(realEstateFlats)) {
            fillResStatsByFlats(statsHouseResponse, realEstateFlats.getFlat());
        }

        return statsHouseResponse;
    }

    /**
     * Получение статистики по отселяемому дому по УНОМ.
     *
     * @param unom УНОМ дома
     * @return статистика по отселяемому дому
     */
    public FullResHousesResponse getFullResHouses(String unom) {
        FullResHousesResponse fullResHousesResponse = new FullResHousesResponse();

        RealEstateDocument realEstateDocument = realEstateDocumentService.fetchDocumentByUnom(unom);
        if (isNull(realEstateDocument)) {
            return fullResHousesResponse;
        }

        StatsHouseResponse statsHouseResponse = new StatsHouseResponse();
        statsHouseResponse.setInProgress("0");
        statsHouseResponse.setDone("0");
        statsHouseResponse.setCourt("0");

        List<PersonType> persons = personDocumentService.fetchByUnom(unom)
            .stream()
            .peek(personDocument -> personDocument.getDocument().getPersonData().setCadNum(personDocument.getId()))
            .map(personDocument -> personDocument.getDocument().getPersonData())
            .filter(personType -> !personType.isIsArchive())
            .collect(Collectors.toList());

        List<TradeAdditionType> tradeAdditionTypes = tradeAdditionDocumentService.fetchByOldEstateUnom(unom)
            .stream()
            .map(tradeAdditionDocument -> tradeAdditionDocument.getDocument().getTradeAdditionTypeData())
            .filter(tradeAdditionType ->
                tradeAdditionType.isIndexed()
                    && ClaimStatus.ACTIVE.equals(tradeAdditionType.getClaimStatus())
                    && tradeAdditionType.isConfirmed())
            .collect(Collectors.toList());

        RealEstateDataType.Flats realEstateFlats = realEstateDocument.getDocument().getRealEstateData().getFlats();
        if (nonNull(realEstateFlats)) {
            List<FlatType> flats = realEstateFlats.getFlat();
            fillResStatsByFlats(statsHouseResponse, flats);

            Map<String, CcoSolrResponse> ccoIdByUnom = new HashMap<>();

            for (FlatType flat : flats) {
                FullFlatResponse fullFlatResponse = new FullFlatResponse();

                if (nonNull(flat.getEntrance())) {
                    fullFlatResponse.setEntrance(flat.getEntrance());
                } else {
                    fullFlatResponse.setEntrance("0");
                }
                String flatNumber = flat.getApartmentL4VALUE();
                if (isNull(flatNumber)) {
                    fullFlatResponse.setFlatNumber(flat.getFlatNumber());
                } else {
                    fullFlatResponse.setFlatNumber(flatNumber);
                }
                if (nonNull(flat.getFloor())) {
                    fullFlatResponse.setFloor(flat.getFloor());
                }
                if (nonNull(flat.getRoomsCount())) {
                    fullFlatResponse.setRoomsCount((int) flat.getRoomsCount().longValue());
                }
                if (nonNull(flat.getFlatType())) {
                    fullFlatResponse.setCommunal(flat.getFlatType().equals("Коммунальная") ? 1 : 0);
                }
                fullFlatResponse.setInvalid(0);
                fullFlatResponse.setLink("/ugd/ssr/#/app/template/flat-card/" + flat.getFlatID());
                if (nonNull(flat.getSAll())) {
                    fullFlatResponse.setTotalSq(flat.getSAll());
                }
                if (nonNull(flat.getSGil())) {
                    fullFlatResponse.setLivingSq(flat.getSGil());
                }
                if (nonNull(flat.getSKitchen())) {
                    fullFlatResponse.setKitchenSq(flat.getSKitchen());
                }
                fullFlatResponse.setFlatStatus(flat.getResettlementStatus());

                List<PersonType> flatPersons = persons
                    .stream()
                    .filter(personType -> personType.getFlatID().equals(flat.getFlatID()))
                    .collect(Collectors.toList());

                Optional<TradeAdditionType> optionalTradeAdditionType = tradeAdditionTypes
                    .stream()
                    .filter(tradeAdditionType -> tradeAdditionType.getOldEstate().getFlatNumber().equals(flatNumber))
                    .findFirst();
                fullFlatResponse.setRemovalType(getResFlatRemovalType(optionalTradeAdditionType, flatPersons));

                Set<String> affairIdSet = flatPersons
                    .stream()
                    .map(PersonType::getAffairId)
                    .collect(Collectors.toCollection(HashSet::new));
                if (affairIdSet.isEmpty() || affairIdSet.size() == 1) {
                    fullFlatResponse.setFlatStatus(flat.getResettlementStatus());
                } else {
                    flatPersons
                        .stream()
                        .filter(personDocument -> nonNull(personDocument.getRelocationStatus()))
                        .map(personDocument -> flatPersonStatus.get(personDocument.getRelocationStatus()))
                        .min(Comparator.comparing(a -> Integer.parseInt(priorityStatus.get(a))))
                        .ifPresent(fullFlatResponse::setFlatStatus);
                }
                for (String affairId : affairIdSet) {
                    FamilyResponse familyResponse = new FamilyResponse();
                    familyResponse.setAffairId(affairId);

                    if (affairIdSet.size() > 1) {
                        flatPersons
                            .stream()
                            .filter(personDocument -> nonNull(personDocument.getRelocationStatus()))
                            .filter(personDocument -> nonNull(personDocument.getAffairId()))
                            .filter(personDocument -> personDocument.getAffairId().equals(affairId))
                            .map(personDocument -> flatPersonStatus.get(personDocument.getRelocationStatus()))
                            .min(Comparator.comparing(a -> Integer.parseInt(priorityStatus.get(a))))
                            .map(Integer::parseInt)
                            .ifPresent(familyResponse::setResStatus);
                    } else if (nonNull(flat.getResettlementStatus())) {
                        familyResponse.setResStatus(Integer.parseInt(flat.getResettlementStatus()));
                    }

                    flatPersons
                        .stream()
                        .filter(
                            personDocument ->
                                nonNull(personDocument.getAffairId()) && personDocument.getAffairId().equals(affairId)
                        )
                        .forEach(personType -> {
                            if (
                                isNull(familyResponse.getRoomsNum())
                                    && nonNull(personType.getRoomNum())
                                    && !personType.getRoomNum().isEmpty()
                            ) {
                                familyResponse.setRoomsNum(String.join(", ", personType.getRoomNum()));
                            }
                            if (isNull(familyResponse.getAddress())) {
                                if (nonNull(personType.getNewFlatInfo())) {
                                    List<PersonType.NewFlatInfo.NewFlat> newFlat
                                        = personType.getNewFlatInfo().getNewFlat();
                                    if (nonNull(newFlat) && !newFlat.isEmpty()) {
                                        PersonType.NewFlatInfo.NewFlat newFlatInfo = newFlat.get(newFlat.size() - 1);
                                        if (nonNull(newFlatInfo.getCcoUnom())) {
                                            String newFlatUnom = newFlatInfo.getCcoUnom().toString();
                                            if (ccoIdByUnom.containsKey(newFlatUnom)) {
                                                familyResponse.setLink(
                                                    "/ugd/#/app/ps/capital-construction-object-card/"
                                                        + ccoIdByUnom.get(newFlatUnom).getId()
                                                );
                                                familyResponse.setAddress(
                                                    ccoIdByUnom.get(newFlatUnom).getAddress()
                                                        + ", квартира " + newFlatInfo.getCcoFlatNum()
                                                );
                                            } else {
                                                CcoSolrResponse ccoSolrResponseByUnom = ccoService
                                                    .getCcoSolrResponseByUnom(newFlatUnom);
                                                ccoIdByUnom.put(newFlatUnom, ccoSolrResponseByUnom);
                                                familyResponse.setLink(
                                                    "/ugd/#/app/ps/capital-construction-object-card/"
                                                        + ccoSolrResponseByUnom.getId()
                                                );
                                                familyResponse.setAddress(
                                                    ccoSolrResponseByUnom.getAddress()
                                                        + ", квартира " + newFlatInfo.getCcoFlatNum()
                                                );
                                            }
                                        }
                                    }
                                }
                            }
                            PeopleResponse peopleResponse = new PeopleResponse();
                            peopleResponse.setFio(personType.getFIO());
                            if (isNull(personType.getStatusLiving())) {
                                peopleResponse.setLiveStatus("0");
                            } else {
                                peopleResponse.setLiveStatus(personType.getStatusLiving());
                            }
                            peopleResponse.setLink("ugd/ssr/#/app/person/" + personType.getCadNum());
                            familyResponse.getPeoples().add(peopleResponse);
                        });

                    fullFlatResponse.getFamily().add(familyResponse);
                }
                fullResHousesResponse.getFlats().add(fullFlatResponse);
            }
        }
        fullResHousesResponse.setStatResHouses(statsHouseResponse);

        Map<String, Set<String>> ccoWithFlats = getCooWithFlatsByRePersons(persons);
        if (!ccoWithFlats.isEmpty()) {
            List<Map<String, Object>> solrDocs = ccoService.getCcoFromSolrByUnoms(ccoWithFlats.keySet()).getDocs();

            if (nonNull(solrDocs)) {
                for (Map<String, Object> doc : solrDocs) {
                    HousesResponse housesResponse = solrOksMapper(doc);
                    housesResponse.setLink(
                        "/ugd/#/app/ps/capital-construction-object-card/" + doc.get("sys_documentId")
                    );
                    housesResponse.setFlatCount(ccoWithFlats.get((String) doc.get("ugd_ps_oks_unom")).size());
                    fullResHousesResponse.getOksHouses().add(housesResponse);
                }
            }
        }

        return fullResHousesResponse;
    }

    /**
     * Получение статистики по отселяемому дому по УНОМ.
     *
     * @param unom УНОМ дома
     * @return статистика по отселяемому дому
     */
    public FullOksHousesResponse getFullOksHouses(String unom) {
        FullOksHousesResponse fullOksHousesResponse = ccoService.getFullCcoChessInfo(unom);

        List<PersonType> personsByCcoUnom = personDocumentService.getPersonsByNewFlatCcoUnom(unom)
            .stream()
            .peek(personDocument -> personDocument.getDocument().getPersonData().setCadNum(personDocument.getId()))
            .map(personDocument -> personDocument.getDocument().getPersonData())
            .collect(Collectors.toList());

        List<ApartmentInspectionType> apartmentInspectionsByUnom = apartmentInspectionService.fetchByUnom(unom)
            .stream()
            .map(apartmentInspectionDocument -> apartmentInspectionDocument.getDocument().getApartmentInspectionData())
            .filter(inspection -> !inspection.getApartmentDefects().isEmpty())
            .collect(Collectors.toList());

        List<TradeAdditionType> tradeAdditionTypes = tradeAdditionDocumentService.fetchByNewEstateUnom(unom)
            .stream()
            .map(tradeAdditionDocument -> tradeAdditionDocument.getDocument().getTradeAdditionTypeData())
            .filter(tradeAdditionType ->
                tradeAdditionType.isIndexed()
                    && ClaimStatus.ACTIVE.equals(tradeAdditionType.getClaimStatus())
                    && tradeAdditionType.isConfirmed())
            .collect(Collectors.toList());

        Set<String> realEstateUnomsHashSet = personsByCcoUnom
            .stream()
            .map(person -> person.getUNOM().toString())
            .collect(Collectors.toCollection(HashSet::new));
        Map<String, RealEstateDataType> realEstatesMap = new HashMap<>();
        for (String realEstateUnom : realEstateUnomsHashSet) {
            RealEstateDocument realEstateDocument = realEstateDocumentService.fetchDocumentByUnom(realEstateUnom);
            RealEstateDataType realEstateData = realEstateDocument.getDocument().getRealEstateData();
            realEstatesMap.put(realEstateUnom, realEstateData);
            HousesResponse housesResponse = realEstateMapper(realEstateData);
            housesResponse.setLink("/ugd/ssr/#/app/template/real-estate-card/" + realEstateDocument.getId());
            Set<String> personUniqueFlat = personsByCcoUnom
                .stream()
                .filter(person -> person.getUNOM().equals(realEstateData.getUNOM()))
                .map(PersonType::getFlatID)
                .collect(Collectors.toCollection(HashSet::new));
            housesResponse.setFlatCount(personUniqueFlat.size());
            fullOksHousesResponse.getResHouses().add(housesResponse);
        }

        // Мапа вида "ID семьи -> Коллекция номеров квартир".
        Map<String, Set<String>> affairToFlatNumberMap = tradeAdditionTypes.stream()
            .filter(tradeAdditionType -> nonNull(tradeAdditionType.getAffairId()))
            .filter(tradeAdditionType -> nonNull(tradeAdditionType.getNewEstates())
                && !tradeAdditionType.getNewEstates().isEmpty())
            .collect(Collectors.toMap(
                TradeAdditionType::getAffairId,
                tradeAdditionType -> tradeAdditionType.getNewEstates()
                    .stream().map(EstateInfoType::getFlatNumber).collect(Collectors.toSet()),
                (firstFlatSet, secondFlatSet) -> {
                    firstFlatSet.addAll(secondFlatSet);
                    return firstFlatSet;
                }
            ));

        List<PersonDocument> personsByTradeAdditions = new ArrayList<>();
        if (!affairToFlatNumberMap.isEmpty()) {
            personsByTradeAdditions = personDocumentService
                .fetchByAffairIds(affairToFlatNumberMap.keySet());
        }

        for (FullFlatResponse fullFlatResponse : fullOksHousesResponse.getFlats()) {
            List<ApartmentInspectionType> apartmentInspectionDocuments = apartmentInspectionsByUnom
                .stream()
                .filter(
                    apartmentInspectionDocument -> apartmentInspectionDocument
                        .getFlat().equals(fullFlatResponse.getFlatNumber())
                )
                .collect(Collectors.toList());
            for (ApartmentInspectionType apartmentInspectionDocument : apartmentInspectionDocuments) {
                apartmentInspectionDocument.getApartmentDefects().forEach(
                    apartmentDefects -> fullFlatResponse.getDefectList().add(
                        apartmentDefects.getApartmentDefectData().getDescription()
                    )
                );
            }
            if (!fullFlatResponse.getDefectList().isEmpty()) {
                fullFlatResponse.setDefect(1);
            }
            List<PersonType> personDocuments = personsByCcoUnom
                .stream()
                .filter(personDocument -> {
                    List<PersonType.NewFlatInfo.NewFlat> newFlatList
                        = personDocument.getNewFlatInfo().getNewFlat();
                    if (!newFlatList.isEmpty()) {
                        String ccoFlatNum = newFlatList.get(newFlatList.size() - 1).getCcoFlatNum();
                        return nonNull(ccoFlatNum) && ccoFlatNum.equals(fullFlatResponse.getFlatNumber());
                    }
                    return false;
                })
                .collect(Collectors.toList());
            Set<String> affairIdSet = personDocuments
                .stream()
                .map(PersonType::getAffairId)
                .collect(Collectors.toCollection(HashSet::new));
            for (String affairId : affairIdSet) {
                FamilyResponse familyResponse = getFamilyResponseByAffairId(affairId, personDocuments, realEstatesMap);

                fullFlatResponse.getFamily().add(familyResponse);
            }

            personsByTradeAdditions.forEach(personDocument -> {
                PeopleResponse peopleResponse = new PeopleResponse();
                peopleResponse.setFio(personDocument.getDocument().getPersonData().getFIO());
                peopleResponse.setLiveStatus(personDocument.getDocument().getPersonData().getStatusLiving());
                peopleResponse.setLink("ugd/ssr/#/app/person/" + personDocument.getId());

                String affairId = personDocument.getDocument().getPersonData().getAffairId();
                String flatNumberByPerson = getPersonNewFlatNumber(personDocument);
                if (nonNull(affairId)) {
                    Optional<FamilyResponse> familyWithSameAffairId = fullFlatResponse.getFamily().stream()
                        .filter(familyResponse -> affairId.equals(familyResponse.getAffairId()))
                        .findFirst();
                    if (familyWithSameAffairId.isPresent()) {
                        if (familyWithSameAffairId.get().getPeoples().stream()
                            .noneMatch(person -> person.getFio().equals(peopleResponse.getFio()))
                            && (affairToFlatNumberMap.get(affairId).contains(fullFlatResponse.getFlatNumber())
                            || fullFlatResponse.getFlatNumber().equals(flatNumberByPerson))) {
                            familyWithSameAffairId.get().getPeoples().add(peopleResponse);
                        }
                    } else if (affairToFlatNumberMap.get(affairId).contains(fullFlatResponse.getFlatNumber())
                        || fullFlatResponse.getFlatNumber().equals(flatNumberByPerson)) {
                        FamilyResponse newFamilyResponse = new FamilyResponse();
                        newFamilyResponse.setAffairId(personDocument.getDocument().getPersonData().getAffairId());
                        newFamilyResponse.getPeoples().add(peopleResponse);
                        fullFlatResponse.getFamily().add(newFamilyResponse);

                        final RealEstateDataAndFlatInfoDto realEstateDataAndFlatInfoDto = flatService
                            .fetchRealEstateAndFlat(personDocument.getDocument().getPersonData().getFlatID());
                        newFamilyResponse.setAddress(RealEstateUtils.getFlatAddress(realEstateDataAndFlatInfoDto));
                    }
                }
            });

            Optional<TradeAdditionType> additionType = tradeAdditionTypes
                .stream()
                .filter(
                    tradeAdditionType -> {
                        List<EstateInfoType> collect = tradeAdditionType.getNewEstates()
                            .stream()
                            .filter(
                                estateInfoType -> estateInfoType
                                    .getFlatNumber().equals(fullFlatResponse.getFlatNumber())
                            )
                            .collect(Collectors.toList());
                        return !collect.isEmpty();
                    }
                )
                .findFirst();

            if (additionType.isPresent()) {
                TradeType tradeType = additionType.get().getTradeType();
                if (nonNull(tradeType)) {
                    if (
                        tradeType.equals(TradeType.SIMPLE_TRADE)
                            || tradeType.equals(TradeType.TRADE_WITH_COMPENSATION)
                            || tradeType.equals(TradeType.TRADE_IN_TWO_YEARS)
                    ) {
                        fullFlatResponse.setRemovalType(2);
                    } else if (tradeType.equals(TradeType.COMPENSATION)) {
                        fullFlatResponse.setRemovalType(3);
                    } else {
                        fullFlatResponse.setRemovalType(1);
                    }
                } else {
                    fullFlatResponse.setRemovalType(1);
                }
            } else {
                personsByCcoUnom
                    .stream()
                    .filter(personDocument -> {
                        List<PersonType.NewFlatInfo.NewFlat> newFlatInfo = personDocument.getNewFlatInfo().getNewFlat();
                        PersonType.NewFlatInfo.NewFlat lastNewFlat = newFlatInfo.get(newFlatInfo.size() - 1);
                        if (nonNull(lastNewFlat.getCcoFlatNum())) {
                            return lastNewFlat.getCcoFlatNum().equals(fullFlatResponse.getFlatNumber());
                        }
                        return false;
                    })
                    .filter(
                        personDocument -> nonNull(personDocument.getOfferLetters())
                            && !personDocument.getOfferLetters().getOfferLetter().isEmpty()
                    )
                    .findFirst()
                    .ifPresent(personDocument -> fullFlatResponse.setRemovalType(1));
            }
        }

        if (
            personsByCcoUnom
                .stream()
                .anyMatch(personDocument -> {
                    List<PersonType.NewFlatInfo.NewFlat> newFlatInfo = personDocument
                        .getNewFlatInfo()
                        .getNewFlat();
                    PersonType.NewFlatInfo.NewFlat lastNewFlat = newFlatInfo.get(newFlatInfo.size() - 1);
                    return lastNewFlat.getCcoUnom().toString().equals(unom) && isNull(lastNewFlat.getCcoFlatNum());
                })
        ) {
            FullFlatResponse fullFlatResponse = new FullFlatResponse();
            fullFlatResponse.setCommunal(0);
            fullFlatResponse.setFlatStatus("0");
            fullFlatResponse.setDefect(0);
            fullFlatResponse.setFlatNumber("0");
            fullFlatResponse.setEntrance("0");
            fullFlatResponse.setFloor(0);
            fullFlatResponse.setInvalid(0);
            fullFlatResponse.setKitchenSq(0);
            fullFlatResponse.setLink("0");
            fullFlatResponse.setLivingSq(0);
            fullFlatResponse.setRemovalType(0);
            fullFlatResponse.setRoomsCount(0);
            fullFlatResponse.setTotalSq(0);

            List<PersonType> personDocuments = personsByCcoUnom
                .stream()
                .filter(personDocument -> {
                    List<PersonType.NewFlatInfo.NewFlat> newFlatList
                        = personDocument.getNewFlatInfo().getNewFlat();
                    if (!newFlatList.isEmpty()) {
                        String ccoFlatNum = newFlatList.get(newFlatList.size() - 1).getCcoFlatNum();
                        return isNull(ccoFlatNum);
                    }
                    return false;
                })
                .collect(Collectors.toList());
            Set<String> affairIdSet = personDocuments
                .stream()
                .map(PersonType::getAffairId)
                .collect(Collectors.toCollection(HashSet::new));
            for (String affairId : affairIdSet) {
                FamilyResponse familyResponse = getFamilyResponseByAffairId(affairId, personDocuments, realEstatesMap);

                fullFlatResponse.getFamily().add(familyResponse);
            }

            fullOksHousesResponse.getFlats().add(fullFlatResponse);
        }

        return fullOksHousesResponse;
    }

    private HousesResponse solrOksMapper(Map<String, Object> solrOks) {
        HousesResponse housesResponse = new HousesResponse();

        String unom = (String) solrOks.get("ugd_ps_oks_unom");
        if (nonNull(unom)) {
            housesResponse.setUnom(unom);
        }
        housesResponse.setAddress(getOksAddress(solrOks));

        return housesResponse;
    }

    private HousesResponse solrRealEstateMapper(Map<String, Object> solrRealEstate) {
        HousesResponse housesResponse = new HousesResponse();

        String unom = (String) solrRealEstate.get("ugd_ssr_real_estate_unom");
        if (nonNull(unom)) {
            housesResponse.setUnom(unom);
        }
        housesResponse.setAddress(getRealEstateAddressFromSolr(solrRealEstate));

        return housesResponse;
    }

    private HousesResponse realEstateMapper(RealEstateDataType realEstateData) {
        HousesResponse housesResponse = new HousesResponse();

        housesResponse.setAddress(getRealEstateAddress(realEstateData));
        housesResponse.setUnom(realEstateData.getUNOM().toString());

        return housesResponse;
    }

    private String getRealEstateAddress(RealEstateDataType realEstateData) {
        String address = realEstateData.getAddress();
        address += " (УНОМ: " + realEstateData.getUNOM();
        if (nonNull(realEstateData.getADMAREA()) && nonNull(realEstateData.getMunOkrugP5())) {
            address += ", " + realEstateData.getADMAREA().getName()
                + ", " + realEstateData.getMunOkrugP5().getName() + ")";
        } else {
            address += ")";
        }
        return address;
    }

    private String getOksAddress(Map<String, Object> solrOks) {
        String assignedAddress = (String) solrOks.get("ugd_ps_oks_assignedAddress");
        String buildingAddress = (String) solrOks.get("ugd_ps_oks_buildingAddress");
        String unom = (String) solrOks.get("ugd_ps_oks_unom");
        ArrayList<String> districts = (ArrayList) solrOks.get("ugd_ps_oks_districts");
        ArrayList<String> areas = (ArrayList) solrOks.get("ugd_ps_oks_areas");
        String address = "";
        if (nonNull(assignedAddress)) {
            address += assignedAddress;
        } else if (nonNull(buildingAddress)) {
            address += buildingAddress;
        }
        if (nonNull(unom)) {
            address += " (УНОМ: " + unom;
        } else if (nonNull(districts) && nonNull(areas)) {
            address += " (";
        }
        if (nonNull(districts) && nonNull(areas)) {
            if (nonNull(unom)) {
                address += ", ";
            }
            address += areas.get(0) + ", " + districts.get(0) + ")";
        } else if (nonNull(unom)) {
            address += ")";
        }
        return address;
    }

    private String getRealEstateAddressFromSolr(Map<String, Object> solrOks) {
        String address = (String) solrOks.get("ugd_ssr_real_estate_address");
        String unom = (String) solrOks.get("ugd_ssr_real_estate_unom");
        String district = (String) solrOks.get("ugd_ssr_real_estate_mun_okrug");
        String area = (String) solrOks.get("ugd_ssr_real_estate_adm_area");
        if (nonNull(unom)) {
            address += " (УНОМ: " + unom;
        } else if (nonNull(district) && nonNull(area)) {
            address += " (";
        }
        if (nonNull(district) && nonNull(area)) {
            if (nonNull(unom)) {
                address += ", ";
            }
            address += area + ", " + district + ")";
        } else if (nonNull(unom)) {
            address += ")";
        }
        return address;
    }

    private void fillResStatsByFlats(StatsHouseResponse statsHouseResponse, List<FlatType> reFlats) {
        List<FlatType> flats = reFlats
            .stream()
            .filter(flat -> nonNull(flat.getResettlementStatus()))
            .collect(Collectors.toList());
        statsHouseResponse.setInProgress(
            String.valueOf(
                flats
                    .stream()
                    .filter(flat -> Integer.parseInt(flat.getResettlementStatus()) < 9)
                    .count()
            )
        );
        statsHouseResponse.setDone(
            String.valueOf(
                flats
                    .stream()
                    .filter(flat -> flat.getResettlementStatus().equals("9"))
                    .count()
            )
        );
    }

    private Map<String, Set<String>> getCooWithFlatsByRePersons(List<PersonType> persons) {
        List<PersonType.NewFlatInfo.NewFlat> newFlatsList = persons
            .stream()
            .filter(
                personType -> nonNull(personType.getNewFlatInfo())
                    && nonNull(personType.getNewFlatInfo().getNewFlat())
                    && !personType.getNewFlatInfo().getNewFlat().isEmpty()
            )
            .map(
                personType -> personType.getNewFlatInfo().getNewFlat()
                    .get(personType.getNewFlatInfo().getNewFlat().size() - 1)
            )
            .collect(Collectors.toList());

        Map<String, Set<String>> ccoWithFlats = new HashMap<>();
        if (!newFlatsList.isEmpty()) {
            for (PersonType.NewFlatInfo.NewFlat newFlat : newFlatsList) {
                if (isNull(newFlat.getCcoUnom())) {
                    continue;
                }
                String ccoUnom = newFlat.getCcoUnom().toString();
                if (ccoWithFlats.containsKey(ccoUnom)) {
                    if (nonNull(newFlat.getCcoFlatNum())) {
                        ccoWithFlats.get(ccoUnom).add(newFlat.getCcoFlatNum());
                    } else {
                        ccoWithFlats.get(ccoUnom).add("0");
                    }
                } else {
                    ccoWithFlats.put(ccoUnom, new HashSet<>());
                    ccoWithFlats.get(ccoUnom).add(newFlat.getCcoFlatNum());
                    if (nonNull(newFlat.getCcoFlatNum())) {
                        ccoWithFlats.get(ccoUnom).add(newFlat.getCcoFlatNum());
                    } else {
                        ccoWithFlats.get(ccoUnom).add("0");
                    }
                }
            }
        }

        return ccoWithFlats;
    }

    private int getResFlatRemovalType(Optional<TradeAdditionType> optionalTradeAddition, List<PersonType> flatPersons) {
        if (optionalTradeAddition.isPresent()) {
            TradeType tradeType = optionalTradeAddition.get().getTradeType();
            if (nonNull(tradeType)) {
                if (
                    tradeType.equals(TradeType.SIMPLE_TRADE)
                        || tradeType.equals(TradeType.TRADE_WITH_COMPENSATION)
                        || tradeType.equals(TradeType.TRADE_IN_TWO_YEARS)
                ) {
                    return 2;
                } else if (tradeType.equals(TradeType.COMPENSATION)) {
                    return 3;
                } else {
                    return 1;
                }
            } else {
                return 1;
            }
        } else {
            Optional<PersonType> optionalPersonType = flatPersons
                .stream()
                .filter(
                    personDocument -> nonNull(personDocument.getOfferLetters())
                        && !personDocument.getOfferLetters().getOfferLetter().isEmpty()
                )
                .findFirst();
            if (optionalPersonType.isPresent()) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    private FamilyResponse getFamilyResponseByAffairId(
        String affairId, List<PersonType> personDocuments, Map<String, RealEstateDataType> realEstatesMap
    ) {
        FamilyResponse familyResponse = new FamilyResponse();
        familyResponse.setAffairId(affairId);

        personDocuments
            .stream()
            .filter(
                personDocument ->
                    personDocument.getAffairId().equals(affairId)
            )
            .forEach(personType -> {
                if (isNull(familyResponse.getAddress())) {
                    Optional<FlatType> optionalFlatType = realEstatesMap.get(personType.getUNOM().toString())
                        .getFlats().getFlat()
                        .stream()
                        .filter(reFlatType -> reFlatType.getFlatID().equals(personType.getFlatID()))
                        .findFirst();
                    if (optionalFlatType.isPresent()) {
                        FlatType flatType = optionalFlatType.get();
                        familyResponse.setAddress(flatType.getAddress());
                        familyResponse.setLink(
                            "/ugd/ssr/#/app/flat/" + flatType.getFlatID() + "/affairId/" + affairId
                        );
                    }
                }

                PeopleResponse peopleResponse = new PeopleResponse();
                peopleResponse.setFio(personType.getFIO());
                if (isNull(personType.getStatusLiving())) {
                    peopleResponse.setLiveStatus("0");
                } else {
                    peopleResponse.setLiveStatus(personType.getStatusLiving());
                }
                peopleResponse.setLink("ugd/ssr/#/app/person/" + personType.getCadNum());
                familyResponse.getPeoples().add(peopleResponse);
            });

        return familyResponse;
    }

    /**
     * Получение номера последней новой квартиры по данным жителя.
     * @param personDocument житель
     * @return номер квартиры.
     */
    private String getPersonNewFlatNumber(PersonDocument personDocument) {
        PersonType personData = personDocument.getDocument().getPersonData();
        if (isNull(personData) || isNull(personData.getNewFlatInfo())
            || isNull(personData.getNewFlatInfo().getNewFlat()) || personData.getNewFlatInfo().getNewFlat().isEmpty()) {
            return null;
        }
        return personData.getNewFlatInfo().getNewFlat().get(personData.getNewFlatInfo().getNewFlat().size() - 1)
            .getCcoFlatNum();
    }

}
