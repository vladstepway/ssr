package ru.croc.ugd.ssr.builder;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static ru.croc.ugd.ssr.utils.PersonUtils.EVENT_RESETTLEMENT_ACCEPTED;
import static ru.croc.ugd.ssr.utils.PersonUtils.EVENT_RESETTLEMENT_DECLINED;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import ru.croc.ugd.ssr.Person;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.dto.realestate.FlatDto;
import ru.croc.ugd.ssr.integration.service.MdmIntegrationService;
import ru.croc.ugd.ssr.model.CipDocument;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.service.CapitalConstructionObjectService;
import ru.croc.ugd.ssr.service.RealEstateDocumentService;
import ru.croc.ugd.ssr.service.cip.CipService;
import ru.croc.ugd.ssr.utils.PersonUtils;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;


/**
 * Строитель документа житель.
 */
@Slf4j
@Component
@AllArgsConstructor
public class PersonBuilder {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    private static final LocalDate NULL_DATE = LocalDate.of(1900, 1, 1);

    private static final Map<String, String> CIP_IDS_CACHE = new HashMap<>();
    private static final Map<BigInteger, Map<String, String>> REAL_ESTATE_CACHE = new HashMap<>();

    private final CapitalConstructionObjectService capitalConstructionObjectService;
    private final CipService cipService;
    private final MdmIntegrationService mdmIntegrationService;
    private final RealEstateDocumentService realEstateDocumentService;
    private final CapitalConstructionObjectService ccoService;

    private final List<String> snilsToProcessList;

    /**
     * Создать документ жителя.
     *
     * @param element элемент документа
     * @return PersonDocument документ
     */
    public PersonDocument createPersonDocument(final Element element) {
        final PersonType personType = new PersonType();

        setGeneralPersonInfo(element, personType);
        setAddInfo(element, personType);
        setAddFlatInfo(element, personType);

        getNewFlat(element)
            .ifPresent(flat -> createNewFlatInfo(personType, flat));

        getOfferLetter(element)
            .ifPresent(letter -> createOfferLetter(personType, letter));

        getAgreement(element)
            .ifPresent(agreement -> createAgreement(personType, agreement));

        getContract(element)
            .ifPresent(contract -> createContract(personType, contract));

        setRelocationStatus(personType);
        personType.setIsDgiData(true);

        final Person person = new Person();
        person.setPersonData(personType);

        final PersonDocument personDocument = new PersonDocument();
        personDocument.setDocument(person);

        return personDocument;
    }

    /**
     * Изменить документ жителя.
     *
     * @param personDocument документ жителя
     * @param element элемент документа
     * @return PersonDocument документ
     */
    public PersonDocument updatePersonDocument(final PersonDocument personDocument, final Element element) {
        final PersonType personType = personDocument.getDocument().getPersonData();

        setGeneralPersonInfo(element, personType);
        setAddInfo(element, personType);
        setAddFlatInfo(element, personType);

        getNewFlat(element)
            .ifPresent(flat -> updateNewFlatInfo(personType, flat));

        getOfferLetter(element)
            .ifPresent(letter -> updateOfferLetters(personType, letter));

        getAgreement(element)
            .ifPresent(agreement -> updateAgreements(personType, agreement));

        getContract(element)
            .ifPresent(contract -> updateContracts(personType, contract));

        setRelocationStatus(personType);

        return personDocument;
    }

    private void setGeneralPersonInfo(final Element element, final PersonType personType) {
        setIfAbsent(personType::getPersonID, personType::setPersonID, element, "PERSON_ID");

        setIfAbsent(personType::getAffairId, personType::setAffairId, element, "AFFAIR_ID");

        setIfAbsent(personType::getSNILS, personType::setSNILS, element, "SNILS");

        setIfAbsent(personType::getLastName, personType::setLastName, element, "LASTNAME");

        setIfAbsent(personType::getFirstName, personType::setFirstName, element, "FIRSTNAME");

        setIfAbsent(personType::getMiddleName, personType::setMiddleName, element, "MIDDLENAME");

        setIfAbsent(personType::getStatusLiving, personType::setStatusLiving, element, "STATUSLIVING");

        setIfAbsent(personType::getEncumbrances, personType::setEncumbrances, element, "ENCUMBRANCES");

        setIfAbsent(personType::getCadNum, personType::setCadNum, element, "SNOS_CADNUM");

        setIfAbsent(personType::getFlatNum, personType::setFlatNum, element, "SNOS_FLAT_NUM");

        setIfAbsent(personType::getWaiter, personType::setWaiter, element, "ISQUEUE");

        if (isNull(personType.getSsoID())) {
            ofNullable(personType.getSNILS())
                .flatMap(this::retrieveSsoId)
                .ifPresent(personType::setSsoID);
        }

        if (isNull(personType.getFIO())) {
            final String lastname = ofNullable(personType.getLastName()).orElse("");
            final String firstname = ofNullable(personType.getFirstName()).orElse("");
            final String middlename = ofNullable(personType.getMiddleName()).orElse("");
            final String fio = lastname + " " + firstname + " " + middlename;

            if (!fio.trim().isEmpty()) {
                personType.setFIO(fio.trim());
            }
        }

        if (isNull(personType.getGender())) {
            ofNullable(element.getElementsByTagName("SEX"))
                .map(nodeList -> nodeList.item(0))
                .map(Node::getTextContent)
                .map(PersonUtils::getGender)
                .ifPresent(personType::setGender);
        }

        if (NULL_DATE.equals(personType.getBirthDate()) && personType.isIsDgiData()) {
            personType.setBirthDate(null);
        }

        if (isNull(personType.getBirthDate())) {
            retrieveBirthday(element)
                .ifPresent(personType::setBirthDate);
        }

        if (isNull(personType.getUNOM())) {
            ofNullable(element.getElementsByTagName("SNOS_UNOM"))
                .map(nodeList -> nodeList.item(0))
                .map(Node::getTextContent)
                .filter(StringUtils::isNotBlank)
                .map(BigInteger::new)
                .ifPresent(personType::setUNOM);
        }

        if (personType.getRoomNum().isEmpty()) {
            ofNullable(element.getElementsByTagName("SNOS_ROOMS_NUM"))
                .map(nodeList -> nodeList.item(0))
                .map(Node::getTextContent)
                .filter(StringUtils::isNotBlank)
                .map(roomNum -> Arrays.stream(roomNum.split(","))
                    .map(String::trim)
                    .toArray(String[]::new)
                )
                .ifPresent(roomNum -> Collections.addAll(personType.getRoomNum(), roomNum));
        }

        if (isNull(personType.getFlatID())) {
            final BigInteger unom = personType.getUNOM();
            final String flatNum = personType.getFlatNum();
            retrieveFlatIdByUnomAndFlatNum(unom, flatNum)
                .ifPresent(personType::setFlatID);
        }
    }

    private static void setIfAbsent(
        final Supplier<String> fieldAccessor,
        final Consumer<String> fieldMutator,
        final Element element,
        final String tagName
    ) {
        if (isNull(fieldAccessor.get())) {
            retrieveValueByTagName(element, tagName)
                .ifPresent(fieldMutator);
        }
    }

    /**
     * Retrieve value by element name.
     * @param element element
     * @param tagName tagName
     * @return optional value
     */
    public static Optional<String> retrieveValueByTagName(final Element element, final String tagName) {
        return ofNullable(element.getElementsByTagName(tagName))
            .map(nodeList -> nodeList.item(0))
            .map(Node::getTextContent)
            .filter(StringUtils::isNotBlank)
            .map(String::trim);
    }

    /**
     * Retrieve birthday.
     * @param element element
     * @return birthday
     */
    public static Optional<LocalDate> retrieveBirthday(final Element element) {
        return retrieveValueByTagName(element, "BIRTHDAY")
            .map(birthdayString -> LocalDate.parse(birthdayString, DATE_FORMATTER));
    }

    private Optional<String> retrieveSsoId(final String snils) {
        try {
            final CompletableFuture<String> ssoIdRetrieval = mdmIntegrationService.getSsoIdAsync(snils);
            return Optional.of(ssoIdRetrieval.get(1, TimeUnit.SECONDS));
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.warn("Недоступен сервис по получению SsoId. СНИЛС: {}", snils);
            snilsToProcessList.add(snils);
            return Optional.empty();
        } catch (Exception e) {
            log.warn("Невозможно получить SsoId по СНИЛС: {}", e.getMessage(), e);
            snilsToProcessList.add(snils);
            return Optional.empty();
        }
    }

    private void setAddInfo(final Element element, final PersonType personType) {
        final PersonType.AddInfo addInfo = ofNullable(personType.getAddInfo())
            .orElseGet(PersonType.AddInfo::new);

        setIfAbsent(addInfo::getIsDead, addInfo::setIsDead, element, "ISDEAD");

        setIfAbsent(addInfo::getDelReason, addInfo::setDelReason, element, "DELREASON");

        personType.setAddInfo(addInfo);
    }

    private void setAddFlatInfo(final Element element, final PersonType personType) {
        final PersonType.AddFlatInfo addFlatInfo = ofNullable(personType.getAddFlatInfo())
            .orElseGet(PersonType.AddFlatInfo::new);

        setIfAbsent(addFlatInfo::getNoFlat, addFlatInfo::setNoFlat, element, "NOFLAT");

        setIfAbsent(addFlatInfo::getOwnFederal, addFlatInfo::setOwnFederal, element, "ISFEDERAL");

        setIfAbsent(addFlatInfo::getInCourt, addFlatInfo::setInCourt, element, "INCOURT");

        personType.setAddFlatInfo(addFlatInfo);
    }

    private Optional<PersonType.NewFlatInfo.NewFlat> getNewFlat(final Element element) {
        PersonType.NewFlatInfo.NewFlat newFlat = new PersonType.NewFlatInfo.NewFlat();

        ofNullable(element.getElementsByTagName("UNOM_START"))
            .map(nodeList -> nodeList.item(0))
            .map(Node::getTextContent)
            .filter(StringUtils::isNotBlank)
            .ifPresent(unom -> {
                newFlat.setCcoUnom(new BigInteger(unom));
                newFlat.setCcoAddress(ccoService.getCcoAddressByUnom(unom));
            });

        if (isNull(newFlat.getCcoUnom())) {
            return Optional.empty();
        }

        ofNullable(element.getElementsByTagName("CADR_NUM_START"))
            .map(nodeList -> nodeList.item(0))
            .map(Node::getTextContent)
            .filter(StringUtils::isNotBlank)
            .ifPresent(newFlat::setCcoCadNum);

        ofNullable(element.getElementsByTagName("APART_NUM_START"))
            .map(nodeList -> nodeList.item(0))
            .map(Node::getTextContent)
            .filter(StringUtils::isNotBlank)
            .ifPresent(newFlat::setCcoFlatNum);

        ofNullable(element.getElementsByTagName("ORDER_ID"))
            .map(nodeList -> nodeList.item(0))
            .map(Node::getTextContent)
            .filter(StringUtils::isNotBlank)
            .ifPresent(newFlat::setOrderId);

        ofNullable(element.getElementsByTagName("DOG_EVANT"))
            .map(nodeList -> nodeList.item(0))
            .map(Node::getTextContent)
            .ifPresent(newFlat::setEvent);

        newFlat.setIsDgiData(true);

        return Optional.of(newFlat);
    }

    private Optional<PersonType.OfferLetters.OfferLetter> getOfferLetter(final Element element) {
        final String unom = ofNullable(element.getElementsByTagName("UNOM_START"))
            .map(nodeList -> nodeList.item(0))
            .map(Node::getTextContent)
            .filter(StringUtils::isNotBlank)
            .orElse(null);

        final String cipId = getCipIdByUnom(unom);

        return ofNullable(element.getElementsByTagName("LETTER_ID"))
            .map(nodeList -> nodeList.item(0))
            .map(Node::getTextContent)
            .filter(StringUtils::isNotBlank)
            .map(letterId -> {
                final PersonType.OfferLetters.OfferLetter offerLetter = new PersonType.OfferLetters.OfferLetter();
                offerLetter.setLetterId(letterId);
                offerLetter.setDate(LocalDate.now());
                offerLetter.setIdCIP(cipId);
                offerLetter.setLetterStatus("Прочитано");
                offerLetter.setIsDgiData(true);
                return offerLetter;
            });
    }

    private Optional<PersonType.Agreements.Agreement> getAgreement(final Element element) {
        final PersonType.Agreements.Agreement agreement = new PersonType.Agreements.Agreement();

        final String agreementEvent = ofNullable(element.getElementsByTagName("NF_ANSWER"))
            .map(nodeList -> nodeList.item(0))
            .map(Node::getTextContent)
            .filter(StringUtils::isNotBlank)
            .map(PersonUtils::getAgreementEvent)
            .orElse(null);

        if (agreementEvent == null) {
            return Optional.empty();
        }
        agreement.setEvent(agreementEvent);

        if (agreementEvent.equals("1")) {
            agreement.setFullDocs("1");
        }

        ofNullable(element.getElementsByTagName("LETTER_ID"))
            .map(nodeList -> nodeList.item(0))
            .map(Node::getTextContent)
            .filter(StringUtils::isNotBlank)
            .ifPresent(agreement::setLetterId);

        ofNullable(element.getElementsByTagName("NF_DATE_ANSWER"))
            .map(nodeList -> nodeList.item(0))
            .map(Node::getTextContent)
            .filter(StringUtils::isNotBlank)
            .ifPresent(eventDate -> agreement.setDate(LocalDate.parse(eventDate, DATE_TIME_FORMATTER)));

        agreement.setIsDgiData(true);

        return Optional.of(agreement);
    }

    private Optional<PersonType.Contracts.Contract> getContract(final Element element) {
        final PersonType.Contracts.Contract contract = new PersonType.Contracts.Contract();

        final Optional<String> optionalOrderId = ofNullable(element.getElementsByTagName("ORDER_ID"))
            .map(nodeList -> nodeList.item(0))
            .map(Node::getTextContent)
            .filter(StringUtils::isNotBlank);

        if (optionalOrderId.isPresent()) {
            contract.setOrderId(optionalOrderId.get());
        } else {
            return Optional.empty();
        }

        ofNullable(element.getElementsByTagName("DOG_NUMBER"))
            .map(nodeList -> nodeList.item(0))
            .map(Node::getTextContent)
            .filter(StringUtils::isNotBlank)
            .ifPresent(contract::setContractNum);

        ofNullable(element.getElementsByTagName("DOG_BEGIN_DATE"))
            .map(nodeList -> nodeList.item(0))
            .map(Node::getTextContent)
            .filter(StringUtils::isNotBlank)
            .ifPresent(contactSignDate ->
                contract.setContractSignDate(LocalDate.parse(contactSignDate, DATE_TIME_FORMATTER))
            );

        ofNullable(element.getElementsByTagName("DOG_END_DATE"))
            .map(nodeList -> nodeList.item(0))
            .map(Node::getTextContent)
            .filter(StringUtils::isNotBlank)
            .ifPresent(contactEndDate ->
                contract.setContractDateEnd(LocalDate.parse(contactEndDate, DATE_TIME_FORMATTER))
            );

        final String status = ofNullable(element.getElementsByTagName("DOG_STATUS"))
            .map(nodeList -> nodeList.item(0))
            .map(Node::getTextContent)
            .filter(StringUtils::isNotBlank)
            .orElse(null);

        setContractStatus(contract, status);

        ofNullable(element.getElementsByTagName("DOG_TYPE"))
            .map(nodeList -> nodeList.item(0))
            .map(Node::getTextContent)
            .filter(StringUtils::isNotBlank)
            .ifPresent(contract::setContractType);

        contract.setIsDgiData(true);

        return Optional.of(contract);
    }

    private void updateNewFlatInfo(
        final PersonType personType,
        final PersonType.NewFlatInfo.NewFlat newFlat
    ) {
        final PersonType.NewFlatInfo flats = personType.getNewFlatInfo();
        if (flats == null) {
            createNewFlatInfo(personType, newFlat);
            return;
        }
        final List<PersonType.NewFlatInfo.NewFlat> allFlats = flats.getNewFlat();
        final List<PersonType.NewFlatInfo.NewFlat> existedFlats = retrieveFilteredNewFlats(allFlats, newFlat);
        if (existedFlats.isEmpty()) {
            allFlats.add(newFlat);
        } else {
            existedFlats.forEach(existedFlat -> updateExistedFlat(existedFlat, newFlat));
        }
    }

    private void updateExistedFlat(
        final PersonType.NewFlatInfo.NewFlat existedFlat, final PersonType.NewFlatInfo.NewFlat newFlatData
    ) {
        ofNullable(newFlatData.getCcoAddress()).ifPresent(existedFlat::setCcoAddress);
        ofNullable(newFlatData.getCcoCadNum()).ifPresent(existedFlat::setCcoCadNum);
        ofNullable(newFlatData.getOrderId()).ifPresent(existedFlat::setOrderId);
        ofNullable(newFlatData.getEvent()).ifPresent(existedFlat::setEvent);

        existedFlat.setIsDgiData(true);
    }

    private void updateOfferLetters(
        final PersonType personType,
        final PersonType.OfferLetters.OfferLetter newOfferLetter
    ) {
        final PersonType.OfferLetters offerLetters = personType.getOfferLetters();
        if (offerLetters == null) {
            createOfferLetter(personType, newOfferLetter);
            return;
        }
        final List<PersonType.OfferLetters.OfferLetter> allOfferLetters = offerLetters.getOfferLetter();
        final List<PersonType.OfferLetters.OfferLetter> existedOfferLetters =
            retrieveFilteredOfferLetters(allOfferLetters, newOfferLetter);
        if (existedOfferLetters.isEmpty()) {
            allOfferLetters.add(newOfferLetter);
        } else {
            existedOfferLetters
                .forEach(existedOfferLetter -> updateExistedOfferLetter(existedOfferLetter, newOfferLetter));
        }
    }

    private void updateExistedOfferLetter(
        final PersonType.OfferLetters.OfferLetter existedOfferLetter,
        final PersonType.OfferLetters.OfferLetter newOfferLetterData
    ) {
        ofNullable(newOfferLetterData.getIdCIP()).ifPresent(existedOfferLetter::setIdCIP);

        existedOfferLetter.setIsDgiData(true);
    }

    private void updateAgreements(
        final PersonType personType,
        final PersonType.Agreements.Agreement newAgreement
    ) {
        final PersonType.Agreements agreements = personType.getAgreements();
        if (agreements == null) {
            createAgreement(personType, newAgreement);
            return;
        }
        final List<PersonType.Agreements.Agreement> allAgreements = agreements.getAgreement();
        final List<PersonType.Agreements.Agreement> existedAgreements =
            retrieveFilteredAgreements(allAgreements, newAgreement);
        if (existedAgreements.isEmpty()) {
            allAgreements.add(newAgreement);
        }
    }

    private void updateContracts(
        final PersonType personType,
        final PersonType.Contracts.Contract newContract
    ) {
        final PersonType.Contracts contracts = personType.getContracts();
        if (contracts == null) {
            createContract(personType, newContract);
            return;
        }
        final List<PersonType.Contracts.Contract> allContracts = contracts.getContract();
        final List<PersonType.Contracts.Contract> existedContracts =
            retrieveFilteredContracts(allContracts, newContract);
        if (existedContracts.isEmpty()) {
            allContracts.add(newContract);
        } else {
            existedContracts.forEach(existedContract -> updateExistedContract(existedContract, newContract));
        }
    }

    private void updateExistedContract(
        final PersonType.Contracts.Contract existedContract, final PersonType.Contracts.Contract newContractData
    ) {
        ofNullable(newContractData.getContractSignDate()).ifPresent(existedContract::setContractSignDate);
        ofNullable(newContractData.getContractDateEnd()).ifPresent(existedContract::setContractDateEnd);
        ofNullable(newContractData.getContractStatus()).ifPresent(existedContract::setContractStatus);
        ofNullable(newContractData.getContractType()).ifPresent(existedContract::setContractType);

        existedContract.setIsDgiData(true);
    }

    private void createNewFlatInfo(final PersonType personType, final PersonType.NewFlatInfo.NewFlat flat) {
        final PersonType.NewFlatInfo newFlatInfo = new PersonType.NewFlatInfo();
        newFlatInfo.getNewFlat().add(flat);
        personType.setNewFlatInfo(newFlatInfo);
    }

    private void createOfferLetter(final PersonType personType, final PersonType.OfferLetters.OfferLetter offerLetter) {
        final PersonType.OfferLetters offerLetters = new PersonType.OfferLetters();
        offerLetters.getOfferLetter().add(offerLetter);
        personType.setOfferLetters(offerLetters);
    }

    private void createAgreement(final PersonType personType, final PersonType.Agreements.Agreement agreement) {
        final PersonType.Agreements agreements = new PersonType.Agreements();
        agreements.getAgreement().add(agreement);
        personType.setAgreements(agreements);
    }

    private void createContract(final PersonType personType, final PersonType.Contracts.Contract contract) {
        final PersonType.Contracts contracts = new PersonType.Contracts();
        contracts.getContract().add(contract);
        personType.setContracts(contracts);
    }

    private List<PersonType.NewFlatInfo.NewFlat> retrieveFilteredNewFlats(
        final List<PersonType.NewFlatInfo.NewFlat> flats, final PersonType.NewFlatInfo.NewFlat newFlat
    ) {
        return flats
            .stream()
            .filter(flat -> Objects.equals(flat.getCcoUnom(), newFlat.getCcoUnom()))
            .filter(flat -> Objects.equals(flat.getCcoFlatNum(), newFlat.getCcoFlatNum()))
            .collect(Collectors.toList());
    }

    private List<PersonType.OfferLetters.OfferLetter> retrieveFilteredOfferLetters(
        final List<PersonType.OfferLetters.OfferLetter> offerLetters,
        final PersonType.OfferLetters.OfferLetter newOfferLetter
    ) {
        return offerLetters
            .stream()
            .filter(offerLetter -> Objects.equals(offerLetter.getLetterId(), newOfferLetter.getLetterId()))
            .collect(Collectors.toList());
    }

    private List<PersonType.Agreements.Agreement> retrieveFilteredAgreements(
        final List<PersonType.Agreements.Agreement> agreements, final PersonType.Agreements.Agreement newAgreement
    ) {
        return agreements
            .stream()
            .filter(agreement -> Objects.equals(agreement.getLetterId(), newAgreement.getLetterId()))
            .filter(agreement -> Objects.equals(agreement.getEvent(), newAgreement.getEvent()))
            .filter(agreement -> Objects.equals(agreement.getDate(), newAgreement.getDate()))
            .collect(Collectors.toList());
    }

    private List<PersonType.Contracts.Contract> retrieveFilteredContracts(
        final List<PersonType.Contracts.Contract> contracts, final PersonType.Contracts.Contract newContract
    ) {
        return contracts
            .stream()
            .filter(contract -> Objects.equals(contract.getOrderId(), newContract.getOrderId()))
            .filter(contract -> isNull(contract.getContractNum()) || isNull(newContract.getContractNum())
                || Objects.equals(contract.getContractNum(), newContract.getContractNum()))
            .collect(Collectors.toList());
    }

    private String getCipIdByUnom(final String unom) {
        if (CIP_IDS_CACHE.containsKey(unom)) {
            return CIP_IDS_CACHE.get(unom);
        }
        final String cipId = ofNullable(capitalConstructionObjectService.getCcoIdByUnom(unom))
            .map(cipService::fetchByCcoId)
            .filter(cipDocuments -> !cipDocuments.isEmpty())
            .map(cipDocuments -> cipDocuments.get(0))
            .map(CipDocument::getId)
            .orElse(null);
        CIP_IDS_CACHE.put(unom, cipId);
        return cipId;
    }

    private void setRelocationStatus(final PersonType personType) {

        ofNullable(personType.getOfferLetters())
            .map(PersonType.OfferLetters::getOfferLetter)
            .ifPresent(offerLetters -> {
                if (offerLetters.size() > 0) {
                    setNewRelocationStatus(personType, "2");
                }
            });

        ofNullable(personType.getAgreements())
            .map(PersonType.Agreements::getAgreement)
            .ifPresent(agreements -> {
                agreements.sort(Comparator
                    .comparing(PersonType.Agreements.Agreement::getDate,
                        Comparator.nullsFirst(LocalDate::compareTo)));
                final PersonType.Agreements.Agreement agreement = agreements.get(agreements.size() - 1);

                if (EVENT_RESETTLEMENT_ACCEPTED.equals(agreement.getEvent())) {
                    setNewRelocationStatus(personType, "4");
                }
                if (EVENT_RESETTLEMENT_DECLINED.equals(agreement.getEvent())) {
                    setNewRelocationStatus(personType, "5");
                }
            });

        ofNullable(personType.getContracts())
            .map(PersonType.Contracts::getContract)
            .ifPresent(contracts -> {
                contracts.sort(Comparator
                    .comparing(PersonType.Contracts.Contract::getContractSignDate,
                        Comparator.nullsFirst(LocalDate::compareTo)));

                final PersonType.Contracts.Contract contract = contracts.get(contracts.size() - 1);

                if ("2".equals(contract.getContractStatus())) {
                    setNewRelocationStatus(personType, "6");
                }
                if ("3".equals(contract.getContractStatus()) || "4".equals(contract.getContractStatus())) {
                    setNewRelocationStatus(personType, "10");
                }
            });

        if (personType.getRelocationStatus() == null) {
            setNewRelocationStatus(personType, "1");
        }

    }

    private void setNewRelocationStatus(final PersonType personType, final String newRelocationStatus) {
        final String existingRelocationStatus = personType.getRelocationStatus();
        if (StringUtils.isNumeric(existingRelocationStatus) && StringUtils.isNumeric(newRelocationStatus)) {
            final int oldStatusNum = Integer.valueOf(existingRelocationStatus);
            final int newStatusNum = Integer.valueOf(newRelocationStatus);
            if (newStatusNum > oldStatusNum) {
                personType.setRelocationStatus(newRelocationStatus);
            }
            return;
        }
        personType.setRelocationStatus(newRelocationStatus);
    }

    /**
     * Retrieve flatId by unom and flatNum.
     * @param unom unom
     * @param flatNum flatNum
     * @return flatId
     */
    public Optional<String> retrieveFlatIdByUnomAndFlatNum(final BigInteger unom, final String flatNum) {
        if (unom == null || flatNum == null) {
            return Optional.empty();
        }
        if (REAL_ESTATE_CACHE.containsKey(unom) && REAL_ESTATE_CACHE.get(unom).containsKey(flatNum)) {
            return ofNullable(REAL_ESTATE_CACHE.get(unom).get(flatNum));
        }

        final List<FlatDto> flatDtos = realEstateDocumentService.getFlatsByUnom(unom.toString());

        final boolean containsFlatNum = flatDtos
            .stream()
            .anyMatch(flatDto -> Objects.equals(flatNum, flatDto.getFlatNum()));
        if (!containsFlatNum) {
            realEstateDocumentService.addFlatToRealEstateByUnom(unom.toString(), flatNum, "DGI")
                .ifPresent(flat -> flatDtos.add(
                    FlatDto.builder()
                        .flatId(flat.getFlatID())
                        .flatNum(flatNum)
                        .build())
                );
        }

        if (flatDtos.isEmpty()) {
            return Optional.empty();
        }
        final Map<String, String> flats = flatDtos
            .stream()
            .collect(Collectors.toMap(
                flatDto -> ofNullable(flatDto.getFlatNum()).map(String::trim).orElse(null),
                FlatDto::getFlatId,
                (f1, f2) -> f1
            ));
        REAL_ESTATE_CACHE.put(unom, flats);

        final String flatId = flats.get(flatNum);
        return ofNullable(flatId);
    }

    private void setContractStatus(final PersonType.Contracts.Contract contract, final String status) {
        if (status == null) {
            return;
        }

        if (contract.getContractSignDate() == null) {
            contract.setContractStatus(status);
            return;
        }

        if (status.equals("2")) {
            contract.setContractStatus("4");
            return;
        }

        contract.setContractStatus(status);
    }

}
