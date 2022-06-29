package ru.croc.ugd.ssr.service;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.ApartmentInspectionType;
import ru.croc.ugd.ssr.CipType;
import ru.croc.ugd.ssr.FlatType;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.RealEstateDataType;
import ru.croc.ugd.ssr.model.ApartmentInspectionDocument;
import ru.croc.ugd.ssr.model.CipDocument;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.RealEstateDocument;
import ru.croc.ugd.ssr.model.ShippingApplicationDocument;
import ru.croc.ugd.ssr.model.api.chess.CcoFlatInfoResponse;
import ru.croc.ugd.ssr.model.land.PersonResType;
import ru.croc.ugd.ssr.model.trade.TradeAdditionDocument;
import ru.croc.ugd.ssr.service.cip.CipService;
import ru.croc.ugd.ssr.service.document.ShippingApplicationDocumentService;
import ru.croc.ugd.ssr.service.document.TradeAdditionDocumentService;
import ru.croc.ugd.ssr.shipping.ShippingApplicationType;
import ru.croc.ugd.ssr.trade.EstateInfoType;
import ru.croc.ugd.ssr.trade.TradeAdditionType;
import ru.croc.ugd.ssr.trade.TradeType;
import ru.reinform.cdp.utils.mapper.JsonMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Сервис для лэндинга.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LandingService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final DefaultApartmentInspectionService inspectionService;
    private final CapitalConstructionObjectService ccoService;
    private final CipService cipService;
    private final ShippingApplicationDocumentService shippingApplicationService;
    private final JsonMapper jsonMapper;
    private final PersonDocumentService personService;
    private final RealEstateDocumentService realEstateService;
    private final TradeAdditionDocumentService tradeAdditionService;

    //Вспомогательные списки для кэшировани внутри 1 запроса
    private final Map<String, CcoFlatInfoResponse> ccoFlatInfoResponses = new HashMap<>();
    private final Map<String, List<ApartmentInspectionDocument>> apartmentInspectionDocuments = new HashMap<>();
    private final Map<String, ShippingApplicationDocument> shippingApplications = new HashMap<>();

    /**
     * Получить данные жителя для лэндоса по СНИЛС.
     *
     * @param snils СНИЛС
     * @return status
     */
    public ResponseEntity<String> getPersonLandingInfoBySnils(String snils) {
        try {
            List<PersonDocument> personDocuments = personService.fetchBySnils(snils);
            if (personDocuments.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            PersonResType personResType = new PersonResType();
            personResType.setSnils(snils);
            Optional<PersonDocument> optionalPersonDocument = personDocuments
                .stream()
                .filter(pers -> nonNull(pers.getDocument().getPersonData().getWaiter()))
                .findFirst();
            if (optionalPersonDocument.isPresent()
                && "1".equals(optionalPersonDocument.get().getDocument().getPersonData().getWaiter())) {
                personResType.setWaiter("1");
            } else {
                personResType.setWaiter("0");
            }
            personResType.setMarital("0");
            personResType.setContract("0");

            PersonResType.Families families = new PersonResType.Families();
            personResType.setFamilies(families);

            List<PersonResType.Families.Family> familyList = families.getFamily();
            for (PersonDocument personDocument : personDocuments) {
                PersonResType.Families.Family family = new PersonResType.Families.Family();
                familyList.add(family);
                PersonResType.Families.Family.Residents residents = new PersonResType.Families.Family.Residents();
                family.setResidents(residents);
                PersonResType.Families.Family.Additions additions = new PersonResType.Families.Family.Additions();
                family.setAdditions(additions);
                PersonResType.Families.Family.CipData cipData = new PersonResType.Families.Family.CipData();
                family.setCipData(cipData);
                PersonResType.Families.Family.Flat flat = new PersonResType.Families.Family.Flat();
                family.setFlat(flat);
                PersonResType.Families.Family.Offer offer = new PersonResType.Families.Family.Offer();
                family.setOffer(offer);

                fillFamily(personDocument, family);
            }

            return ResponseEntity.ok(jsonMapper.writeObject(personResType));
        } catch (Exception e) {
            log.error(e.toString(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } finally {
            ccoFlatInfoResponses.clear();
            apartmentInspectionDocuments.clear();
            shippingApplications.clear();
        }
    }

    private void fillFamily(PersonDocument personDocument, PersonResType.Families.Family family) {
        PersonType personData = personDocument.getDocument().getPersonData();

        if (nonNull(personData.getAffairId())) {
            family.setAffairId(personData.getAffairId());
            family.setRoomType(
                personData.getRoomNum()
                    .stream()
                    .filter(StringUtils::isNotBlank)
                    .anyMatch(rn -> !rn.equals("0")) ? "rooms" : "flat"
            );

            // обговорили с Тимуром, пока их нигде нет, а текущий алгоритм не годится
            family.setEncumbrances("0");

            if (personData.getAddInfo() != null && personData.getAddInfo().getIsDisable() != null) {
                family.setInvalid(personData.getAddInfo().getIsDisable().equals("1") ? "1" : "2");
            } else {
                family.setInvalid("0");
            }
            family.setRegDate("0");

            if (nonNull(personData.getStatusLiving())) {
                family.setStatusLiving(personData.getStatusLiving());
            } else {
                family.setStatusLiving("0");
            }
            family.setSpouse("0");

            List<PersonResType.Families.Family.Residents.Resident> residents
                = family.getResidents().getResident();
            List<PersonDocument> personDocuments = personService.fetchByAffairIdAndFlatId(
                personData.getAffairId(), personData.getFlatID(), personDocument.getId()
            );
            for (PersonDocument person : personDocuments) {
                PersonResType.Families.Family.Residents.Resident resident
                    = new PersonResType.Families.Family.Residents.Resident();
                residents.add(resident);
                PersonType personType = person.getDocument().getPersonData();
                if (nonNull(personType.getBirthDate())) {
                    resident.setBirthDate(personType.getBirthDate());
                }
                if (nonNull(personType.getStatusLiving())) {
                    resident.setStatusLiving(personType.getStatusLiving());
                } else {
                    resident.setStatusLiving("0");
                }
            }

            PersonResType.Families.Family.Flat flat = family.getFlat();
            flat.setSnosUNOM(personData.getUNOM().toString());
            RealEstateDocument realEstate = realEstateService.fetchDocumentByUnom(personData.getUNOM().toString());
            RealEstateDataType realEstateData = realEstate.getDocument().getRealEstateData();
            flat.setSnosAddress(realEstateData.getAddress());
            flat.setSnosRooms(
                personData.getRoomNum()
                    .stream()
                    .filter(StringUtils::isNotBlank)
                    .filter(rn -> !rn.equals("0"))
                    .collect(Collectors.joining(", "))
            );

            String flatNum = personData.getFlatNum();
            if (nonNull(flatNum)) {
                flat.setSnosFlatNum(flatNum);
                Optional<FlatType> optionalFlatType = realEstateData.getFlats().getFlat()
                    .stream()
                    .filter(
                        flatType -> nonNull(flatType.getApartmentL4VALUE())
                            && flatType.getApartmentL4VALUE().equals(flatNum)
                            || nonNull(flatType.getFlatNumber())
                            && flatType.getFlatNumber().equals(flatNum)
                    )
                    .findFirst();
                if (optionalFlatType.isPresent()) {
                    FlatType flatType = optionalFlatType.get();
                    if (nonNull(flatType.getSAll())) {
                        flat.setSnosTotSquare(flatType.getSAll().toString());
                    } else {
                        flat.setSnosTotSquare("0");
                    }
                    if (nonNull(flatType.getSGil())) {
                        flat.setSnosLivSquare(flatType.getSGil().toString());
                    } else {
                        flat.setSnosLivSquare("0");
                    }
                    if (nonNull(flatType.getFloor())) {
                        flat.setSnosFloor(flatType.getFloor().toString());
                    } else {
                        flat.setSnosFloor("0");
                    }
                    if (nonNull(flatType.getRoomsCount())) {
                        flat.setSnosRoomCount(flatType.getRoomsCount().toString());
                    } else {
                        flat.setSnosRoomCount("0");
                    }
                } else {
                    flat.setSnosTotSquare("0");
                    flat.setSnosLivSquare("0");
                    flat.setSnosFloor("0");
                    flat.setSnosRoomCount("0");
                }
            } else {
                flat.setSnosFlatNum("0");
                flat.setSnosTotSquare("0");
                flat.setSnosLivSquare("0");
                flat.setSnosFloor("0");
                flat.setSnosRoomCount("0");
            }

        } else {
            family.setAffairId("0");
        }

        PersonResType.Families.Family.Additions additions = family.getAdditions();
        PersonResType.Families.Family.Offer offer = family.getOffer();
        PersonResType.Families.Family.Offer.Decision decision
            = new PersonResType.Families.Family.Offer.Decision();
        offer.setDecision(decision);
        decision.setDecisionResult("0");
        decision.setFullPacket("0");

        offer.setLetterStatus("0");
        PersonResType.Families.Family.Offer.Decision.Contract contract
            = new PersonResType.Families.Family.Offer.Decision.Contract();
        decision.setContract(contract);
        PersonResType.Families.Family.Offer.Views views = new PersonResType.Families.Family.Offer.Views();
        offer.setViews(views);

        List<TradeAdditionDocument> tradeAdditionDocuments = tradeAdditionService.fetchByOldEstateUnomAndPersonId(
                personData.getUNOM().toString(),
                personData.getAffairId(),
                personDocument.getId()
            )
            .stream()
            .filter(
                trAd -> trAd.getDocument().getTradeAdditionTypeData().isConfirmed()
                    && trAd.getDocument().getTradeAdditionTypeData().isIndexed()
            )
            .collect(Collectors.toList());

        Optional<TradeAdditionDocument> tradeAdditionOpt = tradeAdditionDocuments
            .stream()
            .filter(tad -> tad.getDocument().getTradeAdditionTypeData().getTradeType() == TradeType.COMPENSATION
                || tad.getDocument().getTradeAdditionTypeData().getTradeType() == TradeType.OUT_OF_DISTRICT)
            .findFirst();

        if (nonNull(personData.getOfferLetters())) {
            Optional<PersonType.OfferLetters.OfferLetter> lastLetterOpt = personData
                .getOfferLetters().getOfferLetter()
                .stream()
                .max(Comparator.comparing(
                    PersonType.OfferLetters.OfferLetter::getDate,
                    Comparator.nullsFirst(LocalDate::compareTo)
                ));

            if (lastLetterOpt.isPresent()) {
                PersonType.OfferLetters.OfferLetter lastLetter = lastLetterOpt.get();
                if (nonNull(lastLetter.getDate())) {
                    offer.setLetterDate(lastLetter.getDate().format(DATE_FORMATTER));
                }
                offer.setLetterId(lastLetter.getLetterId());
                if (nonNull(personData.getSendedMessages())) {
                    if (personData.getSendedMessages().getMessage()
                        .stream()
                        .filter(m -> "Уведомление граждан о письме с предложением".equals(m.getBusinessType()))
                        .anyMatch(m -> m.getMessageText().contains("ручено адресату"))) {
                        offer.setLetterStatus("1075");
                    } else if (personData.getSendedMessages().getMessage()
                        .stream()
                        .filter(m -> "Уведомление граждан о письме с предложением".equals(m.getBusinessType()))
                        .anyMatch(m -> m.getMessageText().contains("оставлено адресату")
                            && !m.getMessageText().equals("Не доставлено адресату")
                            && !m.getMessageText().equals("Уведомление не доставлено адресату в ЕЛК (НЛК)"))) {
                        offer.setLetterStatus("1040");
                    }
                }
                if (tradeAdditionOpt.isPresent()) {
                    tradeAdditionOpt
                        .map(tradeAdditionDocument -> tradeAdditionDocument.getDocument()
                            .getTradeAdditionTypeData())
                        .ifPresent(data -> {
                            if (data.getTradeType() == TradeType.COMPENSATION) {
                                offer.setOption("1");
                            } else if (data.getTradeType() == TradeType.OUT_OF_DISTRICT) {
                                offer.setOption("3");
                            } else {
                                offer.setOption("4");
                            }
                        });
                } else {
                    offer.setOption("4");
                }
                views.setViewFlat(nonNull(personData.getFlatDemo())
                    && personData.getFlatDemo().getFlatDemoItem()
                    .stream()
                    .anyMatch(fd -> nonNull(fd.getLetterId()) && fd.getLetterId().equals(offer.getLetterId()))
                    ? "1" : "0"
                );

                if (nonNull(lastLetter.getIdCIP())) {
                    try {
                        CipDocument cipDocument = cipService.fetchDocument(lastLetter.getIdCIP());
                        CipType data = cipDocument.getDocument().getCipData();
                        PersonResType.Families.Family.CipData cipData = new PersonResType.Families.Family.CipData();
                        family.setCipData(cipData);

                        cipData.setCipAddress(data.getAddress());
                        cipData.setCipTime(data.getWorkTime());
                        cipData.setCipPhone(data.getPhone());
                    } catch (Exception ex) {
                        log.error(ex.getMessage());
                    }
                }

                List<PersonResType.Families.Family.Offer.Views.View> viewList = views.getView();
                if (nonNull(personData.getFlatDemo())) {
                    Optional<PersonType.FlatDemo.FlatDemoItem> minView = personData.getFlatDemo().getFlatDemoItem()
                        .stream()
                        .filter(fd -> nonNull(fd.getLetterId()) && fd.getLetterId().equals(offer.getLetterId()))
                        .min(Comparator.comparing(
                            PersonType.FlatDemo.FlatDemoItem::getDate,
                            Comparator.nullsLast(LocalDate::compareTo)
                        ));
                    if (minView.isPresent() && nonNull(minView.get().getDate())) {
                        PersonResType.Families.Family.Offer.Views.View view
                            = new PersonResType.Families.Family.Offer.Views.View();
                        view.setViewDate(minView.get().getDate());
                        viewList.add(view);
                    }
                }

                if (nonNull(personData.getAgreements())) {
                    PersonType.Agreements.Agreement maxAgreement = null;
                    for (PersonType.Agreements.Agreement agreement : personData.getAgreements().getAgreement()) {
                        if (isNull(maxAgreement)
                            || nonNull(agreement.getDate())
                            && agreement.getDate().compareTo(maxAgreement.getDate()) >= 0) {
                            maxAgreement = agreement;
                        }
                    }

                    if (nonNull(maxAgreement)) {
                        if (nonNull(maxAgreement.getEvent())) {
                            decision.setDecisionResult(maxAgreement.getEvent());
                        }

                        decision.setDecisionDate(maxAgreement.getDate());
                        if (nonNull(maxAgreement.getFullDocs())) {
                            decision.setFullPacket(maxAgreement.getFullDocs());
                        }
                    }
                } else {
                    processOfferAndDecisionFromTradeAddition(offer, decision, tradeAdditionOpt);
                }
            } else {
                processOfferAndDecisionFromTradeAddition(offer, decision, tradeAdditionOpt);
            }
        } else {
            processOfferAndDecisionFromTradeAddition(offer, decision, tradeAdditionOpt);
        }

        if (nonNull(personData.getContracts())) {
            Optional<PersonType.Contracts.Contract> lastContractOpt = personData
                .getContracts().getContract()
                .stream()
                .max(Comparator.comparing(
                    PersonType.Contracts.Contract::getMsgDateTime,
                    Comparator.nullsFirst(LocalDateTime::compareTo)
                ));
            if (lastContractOpt.isPresent()) {
                PersonType.Contracts.Contract lastContract = lastContractOpt.get();
                if (nonNull(lastContract.getContractType())) {
                    contract.setRemovalType(lastContract.getContractType());
                } else {
                    contract.setRemovalType("0");
                }

                if (nonNull(lastContract.getFiles())) {
                    if (nonNull(personData.getSendedMessages())) {
                        if (personData.getSendedMessages().getMessage()
                            .stream()
                            .filter(
                                m -> "Уведомление о готовности проекта договора для ознакомления"
                                    .equals(m.getBusinessType())
                            )
                            .anyMatch(m -> m.getMessageText().contains("ручено адресату"))) {
                            contract.setContractProj("1075");
                        } else if (personData.getSendedMessages().getMessage()
                            .stream()
                            .filter(
                                m -> "Уведомление о готовности проекта договора для ознакомления"
                                    .equals(m.getBusinessType())
                            )
                            .anyMatch(m -> m.getMessageText().contains("оставлено адресату")
                                && !m.getMessageText().equals("Не доставлено адресату")
                                && !m.getMessageText().equals("Уведомление не доставлено адресату в ЕЛК (НЛК)"))) {
                            contract.setContractProj("1040");
                        }
                    }
                    if (isNull(contract.getContractProj()) || contract.getContractProj().equals("0")) {
                        if (lastContract.getFiles().getFile().stream().anyMatch(f -> "1".equals(f.getFileType()))) {
                            contract.setContractProj("1");
                        } else if (
                            lastContract.getFiles().getFile().stream().anyMatch(f -> "2".equals(f.getFileType()))
                        ) {
                            contract.setContractProj("3");
                        } else {
                            contract.setContractProj("0");
                        }
                    }
                } else {
                    contract.setContractProj("0");
                }

                if (nonNull(lastContract.getContractSignDate())) {
                    contract.setContractStatus("2");
                } else if ("4".equals(lastContract.getContractStatus())) {
                    contract.setContractStatus("3");
                } else if (nonNull(lastContract.getFiles())
                    && lastContract.getFiles().getFile().stream().anyMatch(f -> "2".equals(f.getFileType()))) {
                    contract.setContractStatus("1");
                } else {
                    contract.setContractStatus("0");
                }

                contract.setContractSignDate(lastContract.getContractSignDate());

                if (nonNull(personData.getKeysIssue()) && nonNull(personData.getKeysIssue().getActDate())) {
                    contract.setKeyIssue("1");
                } else {
                    contract.setKeyIssue("0");
                    tradeAdditionOpt
                        .map(tradeAdditionDocument ->
                            tradeAdditionDocument.getDocument().getTradeAdditionTypeData()
                        )
                        .ifPresent(data -> {
                            contract.setKeyIssue(nonNull(data.getKeysIssueDate()) ? "1" : "0");
                        });
                }

                if (nonNull(personData.getReleaseFlat()) && nonNull(personData.getReleaseFlat().getActDate())) {
                    contract.setKeyPass("1");
                } else {
                    contract.setKeyPass("0");
                }
            } else {
                processContractFromTradeAddition(contract, tradeAdditionOpt);
            }
        } else {
            processContractFromTradeAddition(contract, tradeAdditionOpt);
        }

        List<PersonResType.Families.Family.Offer.Decision.Contract.NewFlat> newFlatList = contract.getNewFlat();
        if (nonNull(personData.getNewFlatInfo()) && !personData.getNewFlatInfo().getNewFlat().isEmpty()) {
            personData.getNewFlatInfo().getNewFlat().forEach(nf -> {
                if (nf.getCcoUnom() == null) {
                    return;
                }

                PersonResType.Families.Family.Offer.Decision.Contract.NewFlat newFlat
                    = new PersonResType.Families.Family.Offer.Decision.Contract.NewFlat();
                newFlatList.add(newFlat);

                newFlat.setNewUNOM(nf.getCcoUnom().toString());
                newFlat.setNewFlatNum(nf.getCcoFlatNum());

                CcoFlatInfoResponse flatInfo = getCcoFlatInfoResponse(nf.getCcoUnom().toString(), nf.getCcoFlatNum());
                if (nonNull(flatInfo)) {
                    newFlat.setNewAddress(flatInfo.getCcoAddress());
                    newFlat.setNewRoomCount(String.valueOf(flatInfo.getRoomAmount()));
                    newFlat.setNewLivSquare(String.valueOf(flatInfo.getLivingSquare()));
                    newFlat.setNewTotSquare(String.valueOf(flatInfo.getTotalSquareWithSummer()));
                    newFlat.setNewFloor(String.valueOf(flatInfo.getFloor()));
                }

                processNewFlatFromPersonData(personDocument.getId(), personData, newFlat);
            });
        } else {
            tradeAdditionOpt
                .map(tradeAdditionDocument -> tradeAdditionDocument.getDocument().getTradeAdditionTypeData())
                .ifPresent(data -> {
                    for (EstateInfoType newEstate : data.getNewEstates()) {
                        PersonResType.Families.Family.Offer.Decision.Contract.NewFlat newFlat
                            = new PersonResType.Families.Family.Offer.Decision.Contract.NewFlat();
                        newFlatList.add(newFlat);

                        newFlat.setNewUNOM(newEstate.getUnom());
                        newFlat.setNewFlatNum(newEstate.getFlatNumber());

                        CcoFlatInfoResponse flatInfo = getCcoFlatInfoResponse(
                            newEstate.getUnom(), newEstate.getFlatNumber()
                        );
                        if (nonNull(flatInfo)) {
                            newFlat.setNewAddress(flatInfo.getCcoAddress());
                            newFlat.setNewRoomCount(String.valueOf(flatInfo.getRoomAmount()));
                            newFlat.setNewLivSquare(String.valueOf(flatInfo.getLivingSquare()));
                            newFlat.setNewTotSquare(String.valueOf(flatInfo.getTotalSquareWithSummer()));
                            newFlat.setNewFloor(String.valueOf(flatInfo.getFloor()));
                        }

                        processNewFlatFromPersonData(personDocument.getId(), personData, newFlat);
                    }
                });
        }

        List<TradeAdditionDocument> tradeAdditions = tradeAdditionDocuments
            .stream()
            .filter(tad -> tad.getDocument().getTradeAdditionTypeData().getTradeType()
                == TradeType.SIMPLE_TRADE
                || tad.getDocument().getTradeAdditionTypeData().getTradeType()
                == TradeType.TRADE_WITH_COMPENSATION
                || tad.getDocument().getTradeAdditionTypeData().getTradeType()
                == TradeType.TRADE_IN_TWO_YEARS)
            .collect(Collectors.toList());

        for (TradeAdditionDocument tradeAddition : tradeAdditions) {
            TradeAdditionType data = tradeAddition.getDocument().getTradeAdditionTypeData();

            List<PersonResType.Families.Family.Additions.Addition> additionList = additions.getAddition();
            PersonResType.Families.Family.Additions.Addition addition
                = new PersonResType.Families.Family.Additions.Addition();
            additionList.add(addition);

            addition.setApplicationId(data.getUniqueRecordKey());
            addition.setApplicationDate(data.getApplicationDate());
            if (data.getTradeType() == TradeType.SIMPLE_TRADE
                || data.getTradeType() == TradeType.TRADE_WITH_COMPENSATION) {
                addition.setTradeType("1");
            } else if (data.getTradeType() == TradeType.TRADE_IN_TWO_YEARS) {
                addition.setTradeType("2");
            }
            PersonResType.Families.Family.Additions.Addition.Decision dec
                = new PersonResType.Families.Family.Additions.Addition.Decision();
            addition.setDecision(dec);

            if (nonNull(data.getCommissionDecisionResult())) {
                dec.setCommissionDecisionResult(data.getCommissionDecisionResult().value());
            }
            if (nonNull(data.getAuctionResult())) {
                dec.setAuction(data.getAuctionResult().value());
            }

            PersonResType.Families.Family.Additions.Addition.Decision.Contract con
                = new PersonResType.Families.Family.Additions.Addition.Decision.Contract();
            dec.setContract(con);

            if (isNull(data.getContractNumber())
                && isNull(data.getContractSignedDate())
                && isNull(data.getContractReadinessDate())) {
                con.setContractStatus("0");
            } else if (nonNull(data.getContractNumber()) && isNull(data.getContractSignedDate())) {
                con.setContractStatus("1");
            } else if (nonNull(data.getContractSignedDate())) {
                con.setContractStatus("2");
            }
            con.setContractSignDate(data.getContractSignedDate());

            List<PersonResType.Families.Family.Additions.Addition.Decision.Contract.NewFlat> newFlats
                = con.getNewFlat();
            for (EstateInfoType newEstate : data.getNewEstates()) {
                PersonResType.Families.Family.Additions.Addition.Decision.Contract.NewFlat newFlat
                    = new PersonResType.Families.Family.Additions.Addition.Decision.Contract.NewFlat();
                newFlats.add(newFlat);

                newFlat.setNewUNOM(newEstate.getUnom());
                newFlat.setNewFlatNum(newEstate.getFlatNumber());

                CcoFlatInfoResponse flatInfo = getCcoFlatInfoResponse(newEstate.getUnom(), newEstate.getFlatNumber());
                if (nonNull(flatInfo)) {
                    newFlat.setNewAddress(flatInfo.getCcoAddress());
                    newFlat.setNewRoomCount(String.valueOf(flatInfo.getRoomAmount()));
                    newFlat.setNewLivSquare(String.valueOf(flatInfo.getLivingSquare()));
                    newFlat.setNewTotSquare(String.valueOf(flatInfo.getTotalSquareWithSummer()));
                    newFlat.setNewFloor(String.valueOf(flatInfo.getFloor()));
                }

                PersonResType.Families.Family.Additions.Addition.Decision.Contract.NewFlat.Imperfections imperfections
                    = new PersonResType.Families.Family.Additions.Addition.Decision
                    .Contract.NewFlat.Imperfections();
                newFlat.setImperfections(imperfections);

                List<ApartmentInspectionDocument> inspections = getListApartmentInspectionDocument(
                    newFlat.getNewUNOM(),
                    newFlat.getNewFlatNum(),
                    personDocument.getId()
                );
                if (inspections.isEmpty()) {
                    imperfections.setDefect("0");
                } else if (inspections.stream().anyMatch(inspection ->
                    isNull(inspection.getDocument().getApartmentInspectionData()
                        .getDefectsEliminatedNotificationDate())
                        && isNull(inspection.getDocument().getApartmentInspectionData().getAcceptedDefectsDate())
                        && isNull(inspection.getDocument().getApartmentInspectionData()
                        .getAcceptedDefectsActFileId()))) {
                    imperfections.setDefect("1");
                } else if (inspections.stream().anyMatch(inspection ->
                    nonNull(inspection.getDocument().getApartmentInspectionData()
                        .getDefectsEliminatedNotificationDate())
                        && isNull(inspection.getDocument().getApartmentInspectionData().getAcceptedDefectsDate())
                        && isNull(inspection.getDocument().getApartmentInspectionData()
                        .getAcceptedDefectsActFileId()))) {
                    imperfections.setDefect("2");
                } else if (inspections.stream().anyMatch(inspection ->
                    nonNull(inspection.getDocument().getApartmentInspectionData().getAcceptedDefectsDate())
                        && nonNull(inspection.getDocument().getApartmentInspectionData()
                        .getAcceptedDefectsActFileId()))) {
                    imperfections.setDefect("3");
                }
                for (ApartmentInspectionDocument inspection : inspections) {
                    ApartmentInspectionType apartmentInspectionData
                        = inspection.getDocument().getApartmentInspectionData();
                    PersonResType.Families.Family.Additions.Addition.Decision.Contract
                        .NewFlat.Imperfections.Imperfection newImperfection
                        = new PersonResType.Families.Family.Additions.Addition.Decision.Contract
                        .NewFlat.Imperfections.Imperfection();
                    imperfections.getImperfection().add(newImperfection);

                    newImperfection.setActId(apartmentInspectionData.getSignedActFileId());
                    if (nonNull(apartmentInspectionData.getDefectsEliminatedNotificationDate())) {
                        newImperfection.setDefectsEliminatedNotificationDate(
                            apartmentInspectionData.getDefectsEliminatedNotificationDate().toLocalDate()
                        );
                    }
                    if (nonNull(apartmentInspectionData.getAcceptedDefectsDate())) {
                        newImperfection.setAcceptedDefectsDate(
                            apartmentInspectionData.getAcceptedDefectsDate().toLocalDate()
                        );
                    }
                    if (nonNull(apartmentInspectionData.getFilingDate())) {
                        newImperfection.setFilingDate(apartmentInspectionData.getFilingDate());
                    }
                    List<PersonResType.Families.Family.Additions.Addition.Decision.Contract
                        .NewFlat.Imperfections.Imperfection.DefectItem> defectItems
                        = newImperfection.getDefectItem();
                    for (ApartmentInspectionType.ApartmentDefects apartmentDefect
                        : apartmentInspectionData.getApartmentDefects()) {
                        if (isNull(apartmentDefect.getApartmentDefectData())) {
                            return;
                        }

                        PersonResType.Families.Family.Additions.Addition.Decision.Contract
                            .NewFlat.Imperfections.Imperfection.DefectItem defectItem
                            = new PersonResType.Families.Family.Additions.Addition.Decision.Contract
                            .NewFlat.Imperfections.Imperfection.DefectItem();
                        defectItems.add(defectItem);

                        defectItem.setDescription(apartmentDefect.getApartmentDefectData().getDescription());
                        defectItem.setFlatElement(apartmentDefect.getApartmentDefectData().getFlatElement());
                    }
                }

                PersonResType.Families.Family.Additions.Addition.Decision.Contract.NewFlat.Shipping shipping
                    = new PersonResType.Families.Family.Additions.Addition.Decision.Contract.NewFlat.Shipping();
                newFlat.setShipping(shipping);

                ShippingApplicationDocument shippingApplication = getShippingApplication(
                    personDocument.getId(),
                    newFlat.getNewUNOM(),
                    personData.getUNOM().toString()
                );
                if (nonNull(shippingApplication)) {
                    ShippingApplicationType shippingApplicationData
                        = shippingApplication.getDocument().getShippingApplicationData();
                    shipping.setEno(shippingApplicationData.getEno());
                    shipping.setStatus(shippingApplicationData.getStatus());
                    if (nonNull(shippingApplicationData.getShippingDateEnd())) {
                        shipping.setShippingDateEnd(shippingApplicationData.getShippingDateEnd().toLocalDate());
                    }
                    shipping.setProcessInstanceId(shippingApplicationData.getProcessInstanceId());
                    if (nonNull(shippingApplicationData.getShippingDateStart())) {
                        shipping.setShippingDateStart(shippingApplicationData.getShippingDateStart().toLocalDate());
                    }
                    shipping.setShippingDateTimeInfo(shippingApplicationData.getShippingDateTimeInfo());
                }
            }

        }
    }

    private void processNewFlatFromPersonData(
        String personId,
        PersonType personData,
        PersonResType.Families.Family.Offer.Decision.Contract.NewFlat newFlat
    ) {
        PersonResType.Families.Family.Offer.Decision.Contract.NewFlat.Imperfections imperfections
            = new PersonResType.Families.Family.Offer.Decision.Contract.NewFlat.Imperfections();
        newFlat.setImperfections(imperfections);

        List<ApartmentInspectionDocument> inspections = getListApartmentInspectionDocument(
            newFlat.getNewUNOM(),
            newFlat.getNewFlatNum(),
            personId
        );
        imperfections.setDefect("0");
        if (inspections.stream().anyMatch(inspection ->
            isNull(inspection.getDocument().getApartmentInspectionData().getDefectsEliminatedNotificationDate())
                && isNull(inspection.getDocument().getApartmentInspectionData().getAcceptedDefectsDate())
                && isNull(inspection.getDocument().getApartmentInspectionData().getAcceptedDefectsActFileId()))) {
            imperfections.setDefect("1");
        } else if (inspections.stream().anyMatch(inspection ->
            nonNull(inspection.getDocument().getApartmentInspectionData().getDefectsEliminatedNotificationDate())
                && isNull(inspection.getDocument().getApartmentInspectionData().getAcceptedDefectsDate())
                && isNull(inspection.getDocument().getApartmentInspectionData().getAcceptedDefectsActFileId()))) {
            imperfections.setDefect("2");
        } else if (inspections.stream().anyMatch(inspection ->
            nonNull(inspection.getDocument().getApartmentInspectionData().getAcceptedDefectsDate())
                && nonNull(inspection.getDocument().getApartmentInspectionData().getAcceptedDefectsActFileId()))) {
            imperfections.setDefect("3");
        }
        for (ApartmentInspectionDocument inspection : inspections) {
            ApartmentInspectionType data = inspection.getDocument().getApartmentInspectionData();
            PersonResType.Families.Family.Offer.Decision.Contract.NewFlat.Imperfections.Imperfection newImperfection
                = new PersonResType.Families.Family.Offer.Decision.Contract.NewFlat.Imperfections.Imperfection();
            imperfections.getImperfection().add(newImperfection);

            newImperfection.setActId(data.getSignedActFileId());
            if (nonNull(data.getDefectsEliminatedNotificationDate())) {
                newImperfection.setDefectsEliminatedNotificationDate(
                    data.getDefectsEliminatedNotificationDate().toLocalDate()
                );
            }
            if (nonNull(data.getAcceptedDefectsDate())) {
                newImperfection.setAcceptedDefectsDate(data.getAcceptedDefectsDate().toLocalDate());
            }
            if (nonNull(data.getFilingDate())) {
                newImperfection.setFilingDate(data.getFilingDate());
            }
            List<PersonResType.Families.Family.Offer.Decision.Contract
                .NewFlat.Imperfections.Imperfection.DefectItem> defectItems
                = newImperfection.getDefectItem();
            for (ApartmentInspectionType.ApartmentDefects apartmentDefect : data.getApartmentDefects()) {
                if (isNull(apartmentDefect.getApartmentDefectData())) {
                    return;
                }

                PersonResType.Families.Family.Offer.Decision.Contract
                    .NewFlat.Imperfections.Imperfection.DefectItem defectItem
                    = new PersonResType.Families.Family.Offer.Decision.Contract
                    .NewFlat.Imperfections.Imperfection.DefectItem();
                defectItems.add(defectItem);

                defectItem.setDescription(apartmentDefect.getApartmentDefectData().getDescription());
                defectItem.setFlatElement(apartmentDefect.getApartmentDefectData().getFlatElement());
            }
        }

        PersonResType.Families.Family.Offer.Decision.Contract.NewFlat.Shipping shipping
            = new PersonResType.Families.Family.Offer.Decision.Contract.NewFlat.Shipping();
        newFlat.setShipping(shipping);

        ShippingApplicationDocument shippingApplication = getShippingApplication(
            personId,
            newFlat.getNewUNOM(),
            personData.getUNOM().toString()
        );
        if (nonNull(shippingApplication)) {
            ShippingApplicationType data = shippingApplication.getDocument().getShippingApplicationData();
            shipping.setEno(data.getEno());
            shipping.setStatus(data.getStatus());
            if (nonNull(data.getShippingDateEnd())) {
                shipping.setShippingDateEnd(data.getShippingDateEnd().toLocalDate());
            }
            shipping.setProcessInstanceId(data.getProcessInstanceId());
            if (nonNull(data.getShippingDateStart())) {
                shipping.setShippingDateStart(data.getShippingDateStart().toLocalDate());
            }
            shipping.setShippingDateTimeInfo(data.getShippingDateTimeInfo());
        }
    }

    private void processContractFromTradeAddition(
        PersonResType.Families.Family.Offer.Decision.Contract contract,
        Optional<TradeAdditionDocument> tradeAdditionOpt
    ) {
        contract.setRemovalType("0");
        contract.setContractProj("0");
        contract.setContractStatus("0");
        contract.setKeyIssue("0");
        contract.setKeyPass("0");
        tradeAdditionOpt
            .map(tradeAdditionDocument -> tradeAdditionDocument.getDocument().getTradeAdditionTypeData())
            .ifPresent(data -> {
                contract.setContractStatus(nonNull(data.getContractSignedDate()) ? "2" : "0");
                contract.setContractSignDate(data.getContractSignedDate());
            });
    }

    private void processOfferAndDecisionFromTradeAddition(
        PersonResType.Families.Family.Offer offer,
        PersonResType.Families.Family.Offer.Decision decision,
        Optional<TradeAdditionDocument> tradeAdditionOpt
    ) {
        tradeAdditionOpt
            .map(tradeAdditionDocument -> tradeAdditionDocument.getDocument().getTradeAdditionTypeData())
            .ifPresent(data -> {
                if (nonNull(data.getOfferLetterDate())) {
                    offer.setLetterDate(
                        data.getOfferLetterDate().format(DATE_FORMATTER)
                    );
                }
                if (data.getTradeType() == TradeType.COMPENSATION) {
                    offer.setOption("1");
                } else if (data.getTradeType() == TradeType.OUT_OF_DISTRICT) {
                    offer.setOption("3");
                }

                if (nonNull(data.getTradeType())) {
                    if (data.getTradeType().value().equals("5")) {
                        decision.setDecisionResult("4");
                    } else if (data.getTradeType().value().equals("4")) {
                        decision.setDecisionResult("3");
                    } else {
                        decision.setDecisionResult(data.getTradeType().value());
                    }
                }
                decision.setDecisionDate(data.getApplicationDate());
                decision.setFullPacket("0");
            });
    }

    private CcoFlatInfoResponse getCcoFlatInfoResponse(String unom, String flatNumber) {
        if (ccoFlatInfoResponses.containsKey(unom + flatNumber)) {
            return ccoFlatInfoResponses.get(unom + flatNumber);
        }

        CcoFlatInfoResponse flatInfo = ccoService.getCcoChessFlatInfoByUnom(
            unom, flatNumber
        );
        ccoFlatInfoResponses.put(unom + flatNumber, flatInfo);

        return flatInfo;
    }

    private List<ApartmentInspectionDocument> getListApartmentInspectionDocument(
        String unom, String flatNumber, String personId
    ) {
        if (apartmentInspectionDocuments.containsKey(unom + flatNumber + personId)) {
            return apartmentInspectionDocuments.get(unom + flatNumber + personId);
        }

        List<ApartmentInspectionDocument> inspections = inspectionService.fetchByUnomAndFlatNumberAndPersonId(
            unom,
            flatNumber,
            personId
        );
        apartmentInspectionDocuments.put(unom + flatNumber + personId, inspections);

        return inspections;
    }

    private ShippingApplicationDocument getShippingApplication(
        String personId, String unomTo, String unomFrom
    ) {
        if (shippingApplications.containsKey(personId + unomTo + unomFrom)) {
            return shippingApplications.get(personId + unomTo + unomFrom);
        }

        ShippingApplicationDocument shippingApplication = shippingApplicationService
            .findShippingApplicationByPersonUidAndUnoms(
                personId,
                unomTo,
                unomFrom
            );
        shippingApplications.put(personId + unomTo + unomFrom, shippingApplication);

        return shippingApplication;
    }

}
