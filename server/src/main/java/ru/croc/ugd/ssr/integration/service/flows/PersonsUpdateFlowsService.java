package ru.croc.ugd.ssr.integration.service.flows;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static ru.croc.ugd.ssr.utils.RealEstateUtils.getFlatByFlatNumber;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.FirstFlowError;
import ru.croc.ugd.ssr.FirstFlowErrorAnalytics;
import ru.croc.ugd.ssr.FirstFlowErrorAnalyticsData;
import ru.croc.ugd.ssr.FlatType;
import ru.croc.ugd.ssr.Person;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.RealEstate;
import ru.croc.ugd.ssr.RealEstateDataType;
import ru.croc.ugd.ssr.dto.DummyPersonDto;
import ru.croc.ugd.ssr.dto.PersonSearchDto;
import ru.croc.ugd.ssr.integration.config.IntegrationProperties;
import ru.croc.ugd.ssr.integration.config.IntegrationPropertyConfig;
import ru.croc.ugd.ssr.integration.eno.EnoSequenceCode;
import ru.croc.ugd.ssr.integration.service.flows.mfr.dgptomfr.PersonInfoMfrFlowService;
import ru.croc.ugd.ssr.integration.util.MessageUtils;
import ru.croc.ugd.ssr.integration.util.XmlUtils;
import ru.croc.ugd.ssr.model.FirstFlowErrorAnalyticsDocument;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.RealEstateDocument;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPPersonsMessage;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPPersonsMessageType;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPPersonsMessageType.LinkedFlats;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPPersonsRequest;
import ru.croc.ugd.ssr.model.integration.dto.FlowReceivedMessageDto;
import ru.croc.ugd.ssr.mq.interop.MqSender;
import ru.croc.ugd.ssr.mq.interop.QueueProperties;
import ru.croc.ugd.ssr.service.FirstFlowErrorAnalyticsService;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.RealEstateDocumentService;
import ru.croc.ugd.ssr.utils.PersonUtils;
import ru.croc.ugd.ssr.utils.RealEstateUtils;
import ru.reinform.cdp.security.utils.RiAuthenticationUtils;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Сервис для обновления данных по жильцам полученных из очереди. Первый поток.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PersonsUpdateFlowsService {

    private final PersonDocumentService personDocumentService;

    private final RealEstateDocumentService realEstateDocumentService;

    private final FlatResettlementStatusUpdateService flatResettlementStatusUpdateService;

    private final RiAuthenticationUtils riAuthenticationUtils;

    private final QueueProperties queueProperties;

    private final MqSender mqSender;

    private final MessageUtils messageUtils;

    private final XmlUtils xmlUtils;

    private final IntegrationProperties integrationProperties;

    private final FirstFlowErrorAnalyticsService firstFlowErrorAnalyticsService;

    private final IntegrationPropertyConfig propertyConfig;

    private final PersonInfoMfrFlowService personInfoMfrFlowService;

    /**
     * Запрос сведений о жителях расселяемого дома.
     *
     * @param unom    дома
     * @param cadastr кадастровый номер
     */
    public void sendResettledHouseRequest(Integer unom, String cadastr) {
        final SuperServiceDGPPersonsRequest personsRequest = new SuperServiceDGPPersonsRequest();
        personsRequest.setPCadastr(cadastr);
        personsRequest.setPUnom(unom);
        final String message =
            messageUtils.createCoordinateTaskMessage(EnoSequenceCode.UGD_SSR_ENO_DGI_SUBSCRIBE_PERSON_UPDATE_SEQ,
                propertyConfig.getRessetelmentBegining(),
                personsRequest);
        xmlUtils.writeXmlFile(message, integrationProperties.getXmlExportFlowFirst());
        mqSender.send(queueProperties.getPersonRequest(), message);

        flatResettlementStatusUpdateService.updateFlatResettlementStatusStartRenovation(unom.toString());
    }

    /**
     * Обновления данных о жителях переселяемых домов.
     *
     * @param personsMessage объект сообщения из очереди
     */
    public void updatePersonsInfo(FlowReceivedMessageDto<SuperServiceDGPPersonsMessage> personsMessage) {
        final AtomicInteger countUpdatedRows = new AtomicInteger();
        final AtomicInteger countErrorRows = new AtomicInteger();

        final List<SuperServiceDGPPersonsMessageType> personsResult =
            personsMessage.getParsedMessage().getSuperServiceDGPPersonsResult();

        FirstFlowErrorAnalyticsDocument document = new FirstFlowErrorAnalyticsDocument();
        FirstFlowErrorAnalytics firstFlowErrorAnalytics = new FirstFlowErrorAnalytics();
        document.setDocument(firstFlowErrorAnalytics);
        FirstFlowErrorAnalyticsData data = new FirstFlowErrorAnalyticsData();
        firstFlowErrorAnalytics.setData(data);
        List<FirstFlowError> errors = data.getErrors();
        document = firstFlowErrorAnalyticsService.createDocument(document, false, "");

        RealEstateDocument realEstateDocument = null;

        // список жителей: для предотвращения потери жителя-владельца двух коммуналок в рамках одной квартиры
        List<PersonDocument> personList = new ArrayList<>();

        for (SuperServiceDGPPersonsMessageType personsMessageType : personsResult) {
            for (LinkedFlats.Flat flat : personsMessageType.getLinkedFlats().getFlat()) {
                if (StringUtils.isBlank(data.getUnom()) && StringUtils.isNotBlank(flat.getSnosUnom())) {
                    data.setUnom(flat.getSnosUnom().trim());
                    realEstateDocument = realEstateDocumentService.fetchDocumentByUnom(data.getUnom());
                }
                if (StringUtils.isBlank(flat.getStatusLiving()) || !flat.getStatusLiving().trim().equals("5")) {
                    riAuthenticationUtils.setSecurityContextByServiceuser();
                    findAndUpdatePersonDocument(
                        document.getId(),
                        realEstateDocument,
                        countUpdatedRows,
                        countErrorRows,
                        personsMessageType,
                        flat,
                        errors,
                        personList
                    );
                }
            }
        }

        // добавим жителей, которые не были найдены в ДГИ
        final List<PersonDocument> personDocuments = personDocumentService.fetchByUnom(data.getUnom())
            .stream()
            .filter(p -> !p.getDocument().getPersonData().isIsArchive())
            .collect(Collectors.toList());
        for (PersonDocument personDocument : personDocuments) {
            PersonType personData = personDocument.getDocument().getPersonData();
            if (personData.getUpdatedFromDgiStatus() == null) {
                if (StringUtils.isNotBlank(personData.getFIO())
                    && StringUtils.isBlank(personData.getLastName())
                    && StringUtils.isBlank(personData.getFirstName())
                    && StringUtils.isBlank(personData.getMiddleName())) {
                    String fio = personData.getFIO().trim();
                    while (fio.contains("  ")) {
                        fio = fio.replace("  ", " ");
                    }
                    List<String> strings = Arrays.asList(fio.split(" "));
                    if (strings.size() > 0) {
                        personData.setLastName(strings.get(0));
                    }
                    if (strings.size() > 1) {
                        personData.setFirstName(strings.get(1));
                    }
                    if (strings.size() > 2) {
                        personData.setMiddleName(String.join(" ", strings.subList(2, strings.size())));
                    }
                }

                String gender = null;
                if (StringUtils.isNotBlank(personData.getGender())) {
                    if (personData.getGender().trim().equalsIgnoreCase("Мужской")) {
                        gender = "Мужской";
                    } else if (personData.getGender().trim().equalsIgnoreCase("Женский")) {
                        gender = "Женский";
                    }
                }
                personData.setGender(gender);

                if (StringUtils.isBlank(personData.getFlatNum()) && StringUtils.isNotBlank(personData.getFlatID())) {
                    String flatId = personData.getFlatID();

                    ofNullable(realEstateDocument)
                        .map(RealEstateDocument::getDocument)
                        .map(RealEstate::getRealEstateData)
                        .map(RealEstateDataType::getFlats)
                        .map(RealEstateDataType.Flats::getFlat)
                        .map(List::stream)
                        .orElse(Stream.empty())
                        .filter(f -> StringUtils.isNotBlank(f.getFlatID()))
                        .filter(f -> f.getFlatID().equals(flatId))
                        .map(FlatType::getApartmentL4VALUE)
                        .findAny()
                        .ifPresent(personData::setFlatNum);
                }

                personData.setUpdatedFromDgiStatus(1);
                personData.setUpdatedFromDgiDate(LocalDate.now());

                personDocumentService.updateDocument(personDocument.getId(), personDocument, true, true, "");
                createAndAddError(errors, personDocument, realEstateDocument, null, null);
            }
        }

        if (errors.size() > 0) {
            final FirstFlowErrorAnalyticsData firstFlowErrorAnalyticsData = document.getDocument().getData();
            firstFlowErrorAnalyticsService.startDocumentProcess(document.getId())
                .ifPresent(firstFlowErrorAnalyticsData::setProcessInstanceId);
        } else {
            personInfoMfrFlowService.sendPersonInfo(personDocuments);
        }
        firstFlowErrorAnalyticsService.updateDocument(document.getId(), document, true, true, "updatePersonsInfo");

        if (ofNullable(realEstateDocument).map(RealEstateDocument::getDocument)
            .map(RealEstate::getRealEstateData).isPresent()) {
            if (
                personDocuments.stream().allMatch(p -> p.getDocument().getPersonData().getUpdatedFromDgiStatus() == 2)
            ) {
                realEstateDocument.getDocument().getRealEstateData().setUpdatedFromDgiDate(LocalDate.now());
                realEstateDocument.getDocument().getRealEstateData().setUpdatedFromDgiStatus("Обогащено");
            } else {
                realEstateDocument.getDocument().getRealEstateData().setUpdatedFromDgiStatus("Не обогащено");
            }
            realEstateDocumentService.updateDocument(realEstateDocument.getId(), realEstateDocument, true, true, "");
        }

        log.info("All rows:{}. Updated rows {}. Errored rows: {}",
            personsResult.size(),
            countUpdatedRows.get(),
            countErrorRows.get()
        );
    }

    public List<DummyPersonDto> searchForDummyPersons(final String firstFlowErrorAnalyticsDocumentId) {
        final FirstFlowErrorAnalyticsDocument firstFlowErrorAnalyticsDocument =
            firstFlowErrorAnalyticsService.fetchDocument(firstFlowErrorAnalyticsDocumentId);
        final List<FirstFlowError> firstFlowErrors =
            firstFlowErrorAnalyticsDocument.getDocument().getData().getErrors();

        final Set<String> unoms = firstFlowErrors.stream()
            .map(FirstFlowError::getResult)
            .map(FirstFlowError.Result::getUnom)
            .filter(StringUtils::isNotBlank)
            .map(StringUtils::trim)
            .collect(Collectors.toSet());
        final Map<String, List<PersonDocument>> personDocumentsMap = retrievePersonDocumentsByUnom(unoms);
        final Map<String, RealEstateDocument> realEstateDocumentMap = retrieveRealEstateDocumentByUnom(unoms);

        return firstFlowErrors.stream()
            .filter(Objects::nonNull)
            .map(error -> retrieveDummyPersonDto(error, personDocumentsMap, realEstateDocumentMap))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    private List<PersonDocument> searchForDummyPersons(final PersonSearchDto personSearchDto) {
        final List<PersonDocument> personDocuments = personDocumentService
            .fetchByUnom(trimIfNotNull(personSearchDto.getUnom()));
        final RealEstateDocument realEstateDocument = ofNullable(trimIfNotNull(personSearchDto.getUnom()))
            .map(realEstateDocumentService::fetchDocumentByUnom)
            .orElse(null);

        return searchForDummyPersons(personSearchDto, personDocuments, realEstateDocument);
    }

    private List<PersonDocument> searchForDummyPersons(
        final PersonSearchDto personSearchDto,
        final List<PersonDocument> personDocuments,
        final RealEstateDocument realEstateDocument
    ) {
        if (isNull(personDocuments)) {
            return Collections.emptyList();
        }

        final Optional<FlatType> oldFlatOptional = ofNullable(realEstateDocument)
            .map(RealEstateDocument::getDocument)
            .map(RealEstate::getRealEstateData)
            .map(RealEstateDataType::getFlats)
            .map(RealEstateDataType.Flats::getFlat)
            .map(List::stream)
            .orElse(Stream.empty())
            .filter(flatType -> String.valueOf(flatType.getFlatNumber()).equalsIgnoreCase(personSearchDto.getFlatNum())
                || StringUtils.equalsIgnoreCase(personSearchDto.getFlatNum(), flatType.getApartmentL4VALUE()))
            .findAny();

        List<PersonDocument> resultDocumentList;

        resultDocumentList = personDocuments.stream().filter(personDocument -> {
            PersonType personData = personDocument.getDocument().getPersonData();
            boolean result = personData.getPersonID() != null
                && personSearchDto.getPersonId() != null
                && personData.getPersonID().equals(personSearchDto.getPersonId().trim());
            result = result && personData.getAffairId() != null
                && personSearchDto.getAffairId() != null
                && personData.getAffairId().equals(personSearchDto.getAffairId().trim());
            return result;
        }).collect(Collectors.toList());

        if (resultDocumentList.size() > 1) {
            log.info(
                "Есть жители с одинаковыми personId = {} и affairId = {} - ошибка",
                personSearchDto.getPersonId(),
                personSearchDto.getAffairId()
            );
        }
        if (resultDocumentList.size() == 1) {
            return resultDocumentList;
        }

        if (oldFlatOptional.isPresent()) {
            final FlatType oldFlat = oldFlatOptional.get();
            resultDocumentList = personDocuments.stream().filter(personDocument -> {
                PersonType personData = personDocument.getDocument().getPersonData();
                boolean result = personData.getSNILS() != null
                    && personSearchDto.getSnils() != null
                    && personData.getSNILS().equals(personSearchDto.getSnils().trim());
                result = result && oldFlat.getFlatID().equals(personData.getFlatID());
                return result;
            }).collect(Collectors.toList());

            if (resultDocumentList.isEmpty()) {
                resultDocumentList = personDocuments.stream().filter(personDocument -> {
                    PersonType personData = personDocument.getDocument().getPersonData();
                    String fio = personData.getFIO() != null ? personData.getFIO().trim() : null;
                    while (fio != null && fio.contains("  ")) {
                        fio = fio.replace("  ", " ");
                    }
                    boolean result = (personData.getFirstName() != null
                        && personData.getMiddleName() != null
                        && personData.getLastName() != null
                        && personData.getFirstName().trim()
                        .equalsIgnoreCase(trimIfNotNull(personSearchDto.getFirstname()))
                        && personData.getMiddleName().trim()
                        .equalsIgnoreCase(trimIfNotNull(personSearchDto.getMiddlename()))
                        && personData.getLastName().trim()
                        .equalsIgnoreCase(trimIfNotNull(personSearchDto.getLastname())))
                        || getFio(trimIfNotNull(personSearchDto.getFirstname()),
                        trimIfNotNull(personSearchDto.getLastname()),
                        trimIfNotNull(personSearchDto.getMiddlename()))
                        .equalsIgnoreCase(fio);
                    result = result
                        && personData.getBirthDate() != null
                        && personData.getBirthDate().equals(personSearchDto.getBirthdate());
                    result = result && oldFlat.getFlatID().equals(personData.getFlatID());
                    return result;
                }).collect(Collectors.toList());
            } else if (resultDocumentList.size() > 1) {
                // пытаемся сузить список
                List<PersonDocument> narrowedList = resultDocumentList.stream().filter(personDocument -> {
                    PersonType personData = personDocument.getDocument().getPersonData();
                    boolean result = (personData.getFirstName() != null
                        && personData.getMiddleName() != null
                        && personData.getLastName() != null
                        && personData.getFirstName().equals(trimIfNotNull(personSearchDto.getFirstname()))
                        && personData.getMiddleName().equals(trimIfNotNull(personSearchDto.getMiddlename()))
                        && personData.getLastName().equals(trimIfNotNull(personSearchDto.getLastname())))
                        || getFio(trimIfNotNull(personSearchDto.getFirstname()),
                        trimIfNotNull(personSearchDto.getLastname()),
                        trimIfNotNull(personSearchDto.getMiddlename())).equals(personData.getFIO());
                    result = result
                        && personData.getBirthDate() != null
                        && personData.getBirthDate().equals(personSearchDto.getBirthdate());
                    result = result && oldFlat.getFlatID().equals(personData.getFlatID());
                    return result;
                }).collect(Collectors.toList());

                if (narrowedList.size() > 0) {
                    resultDocumentList = narrowedList;
                }
            }
        } else {
            log.info(
                "Не найдена квартира: unom: {}, flatNum: {}", personSearchDto.getUnom(), personSearchDto.getFlatNum()
            );
        }

        return resultDocumentList;
    }

    private DummyPersonDto retrieveDummyPersonDto(
        final FirstFlowError error,
        final Map<String, List<PersonDocument>> personDocumentsMap,
        final Map<String, RealEstateDocument> realEstateDocumentMap
    ) {
        final FirstFlowError.Flat flat = error.getFlat();
        final FirstFlowError.Message message = error.getMessage();
        final FirstFlowError.Result result = error.getResult();
        final String unom = nonNull(result) ? result.getUnom() : null;

        final PersonSearchDto personSearchDto = PersonSearchDto.builder()
            .affairId(nonNull(flat) ? flat.getAffairId() : null)
            .flatNum(nonNull(flat) ? flat.getSnosFlatNum() : null)
            .unom(unom)
            .birthdate(nonNull(message) ? message.getBirthDate() : null)
            .firstname(nonNull(message) ? message.getFirstName() : null)
            .lastname(nonNull(message) ? message.getLastName() : null)
            .middlename(nonNull(message) ? message.getMiddleName() : null)
            .personId(nonNull(message) ? message.getPersonId() : null)
            .snils(nonNull(message) ? message.getSnils() : null)
            .build();

        final List<PersonDocument> potentialDummyPersons = searchForDummyPersons(
            personSearchDto, personDocumentsMap.get(unom), realEstateDocumentMap.get(unom)
        );

        final String personId = error.getPersonId();

        final boolean existsPerson = potentialDummyPersons.stream()
            .anyMatch(personDocument -> nonNull(personId)
                && personId.equals(personDocument.getId()));

        if (existsPerson) {
            return null;
        }

        return potentialDummyPersons.stream()
            .findFirst()
            .map(personDocument -> DummyPersonDto.builder()
                .personId(personId)
                .dummyPerson(personDocument)
                .build()
            )
            .orElse(null);
    }

    private Map<String, List<PersonDocument>> retrievePersonDocumentsByUnom(final Set<String> unoms) {
        return unoms.stream()
            .collect(Collectors.toMap(
                Function.identity(),
                personDocumentService::fetchByUnom,
                (p1, p2) -> p1
            ));
    }

    private Map<String, RealEstateDocument> retrieveRealEstateDocumentByUnom(final Set<String> unoms) {
        return unoms.stream()
            .collect(Collectors.toMap(
                Function.identity(),
                realEstateDocumentService::fetchDocumentByUnom,
                (r1, r2) -> r1
            ));
    }

    private void findAndUpdatePersonDocument(
        String firstFlowErrorAnalyticsDocumentId,
        RealEstateDocument realEstateDocument,
        AtomicInteger countUpdatedRows,
        AtomicInteger countErrorRows,
        SuperServiceDGPPersonsMessageType personsMessageType,
        LinkedFlats.Flat flat,
        List<FirstFlowError> errors,
        List<PersonDocument> personList
    ) {
        final List<PersonDocument> personDocuments = searchPerson(personsMessageType, flat).stream()
            .filter(p -> !p.getDocument().getPersonData().isIsArchive())
            .collect(Collectors.toList());
        PersonDocument personDocument;
        Person person;
        PersonType personData;

        if (personDocuments.isEmpty()) {
            countErrorRows.getAndIncrement();
            log.info(
                "Person from message not found. PersonId:{}, Snils:{}, Fio:{}, Birthday:{}",
                personsMessageType.getPersonId(),
                personsMessageType.getSnils(),
                getFio(
                    personsMessageType.getFirstname(),
                    personsMessageType.getLastname(),
                    personsMessageType.getMiddlename()
                ),
                personsMessageType.getBirthdate()
            );

            personDocument = new PersonDocument();
            person = new Person();
            personDocument.setDocument(person);
            personData = new PersonType();
            person.setPersonData(personData);

            personData.setUpdatedFromDgiStatus(1);
            personData.setUpdatedFromDgiDate(LocalDate.now());
            personData.setFirstFlowErrorAnalyticsId(firstFlowErrorAnalyticsDocumentId);

            fillPersonWithFlowData(flat, personsMessageType, personDocument, realEstateDocument);

            personDocumentService.createDocument(personDocument, true, "findAndUpdatePersonDocument");

            createAndAddError(errors, personDocument, realEstateDocument, flat, personsMessageType);
        }

        if (personDocuments.size() == 1) {
            personDocument = personDocuments.get(0);
            person = personDocument.getDocument();
            personData = person.getPersonData();

            Optional<PersonDocument> anyPerson = personList.stream().filter(p -> {
                PersonType pd = p.getDocument().getPersonData();
                boolean result = StringUtils.isNotBlank(pd.getPersonID())
                    && StringUtils.isNotBlank(personsMessageType.getPersonId())
                    && pd.getPersonID().equals(personsMessageType.getPersonId().trim());
                result = result && StringUtils.isNotBlank(pd.getFlatNum())
                    && StringUtils.isNotBlank(flat.getSnosFlatNum())
                    && pd.getFlatNum().equalsIgnoreCase(flat.getSnosFlatNum().trim());
                result = result && StringUtils.isNotBlank(pd.getAffairId())
                    && StringUtils.isNotBlank(flat.getAffairId())
                    && !pd.getAffairId().equals(flat.getAffairId().trim());
                return result;
            }).findAny();
            if (anyPerson.isPresent()) {
                personDocument = new PersonDocument();
                person = new Person();
                personDocument.setDocument(person);
                person.setPersonData(anyPerson.get().getDocument().getPersonData());

                personDocument = personDocumentService.createDocument(personDocument, true, "createDoublePerson");
                personData = personDocument.getDocument().getPersonData();
            }
            personList.add(personDocument);

            handleOneFoundPerson(personDocument, personData, personsMessageType, flat, errors, realEstateDocument);
            countUpdatedRows.getAndIncrement();
            log.info("Record updated. Person id {}", personsMessageType.getPersonId());
        }

        if (personDocuments.size() > 1) {
            countErrorRows.getAndIncrement();
            log.info(
                "Found several person for message. PersonId:{}, Snils:{}, Fio:{}, Birthday:{}",
                personsMessageType.getPersonId(),
                personsMessageType.getSnils(),
                getFio(
                    personsMessageType.getFirstname(),
                    personsMessageType.getLastname(),
                    personsMessageType.getMiddlename()
                ),
                personsMessageType.getBirthdate()
            );

            Optional<PersonDocument> any = personDocuments.stream()
                .filter(pd ->
                    compareDocument(personsMessageType, flat, pd.getDocument().getPersonData(), realEstateDocument)
                )
                .findAny();
            personDocument = any.orElseGet(() -> personDocuments.get(0));
            person = personDocument.getDocument();
            personData = person.getPersonData();

            Optional<PersonDocument> anyPerson = personList.stream().filter(p -> {
                PersonType pd = p.getDocument().getPersonData();
                boolean result = StringUtils.isNotBlank(pd.getPersonID())
                    && StringUtils.isNotBlank(personsMessageType.getPersonId())
                    && pd.getPersonID().equals(personsMessageType.getPersonId().trim());
                result = result && StringUtils.isNotBlank(pd.getFlatNum())
                    && StringUtils.isNotBlank(flat.getSnosFlatNum())
                    && pd.getFlatNum().equalsIgnoreCase(flat.getSnosFlatNum().trim());
                result = result && StringUtils.isNotBlank(pd.getAffairId())
                    && StringUtils.isNotBlank(flat.getAffairId())
                    && !pd.getAffairId().equals(flat.getAffairId().trim());
                return result;
            }).findAny();
            if (anyPerson.isPresent()) {
                personDocument = new PersonDocument();
                person = new Person();
                personDocument.setDocument(person);
                person.setPersonData(anyPerson.get().getDocument().getPersonData());

                personDocument = personDocumentService.createDocument(personDocument, true, "createDoublePerson");
                personData = personDocument.getDocument().getPersonData();
            }
            personList.add(personDocument);

            handleOneFoundPerson(personDocument, personData, personsMessageType, flat, errors, realEstateDocument);
            countUpdatedRows.getAndIncrement();
        }
    }

    private void fillPersonWithFlowData(
        LinkedFlats.Flat flat,
        SuperServiceDGPPersonsMessageType personsMessageType,
        PersonDocument personDocument,
        RealEstateDocument realEstateDocument
    ) {
        PersonType personData = personDocument.getDocument().getPersonData();

        personData.setPersonID(trimIfNotNull(personsMessageType.getPersonId()));
        personData.setSNILS(trimIfNotNull(personsMessageType.getSnils()));
        personData.setAffairId(trimIfNotNull(flat.getAffairId()));
        personData.setLastName(trimIfNotNull(personsMessageType.getLastname()));
        personData.setFirstName(trimIfNotNull(personsMessageType.getFirstname()));
        personData.setMiddleName(trimIfNotNull(personsMessageType.getMiddlename()));
        personData.setFIO(PersonUtils.getFullName(personData));
        personData.setBirthDate(personsMessageType.getBirthdate());
        personData.setGender(resolveGender(trimIfNotNull(personsMessageType.getSex())));
        personData.setEncumbrances(trimIfNotNull(flat.getEncumbrances()));
        personData.setStatusLiving(trimIfNotNull(flat.getStatusLiving()));
        personData.setWaiter(trimIfNotNull(personsMessageType.getIsQueue()));
        personData.setUNOM(ofNullable(trimIfNotNull(flat.getSnosUnom())).map(BigInteger::new).orElse(null));
        personData.setCadNum(trimIfNotNull(flat.getSnosCadnum()));

        String flatNumber = trimIfNotNull(flat.getSnosFlatNum());
        personData.setFlatNum(flatNumber);

        personData.setFlatID(
            getFlatByFlatNumber(realEstateDocument, flatNumber).map(FlatType::getFlatID).orElse(null)
        );

        List<String> roomsNum = flat.getSnosRoomsNum()
            .stream()
            .filter(StringUtils::isNotBlank)
            .flatMap(rn -> Arrays.stream(rn.replace(" ", "").split(",")))
            .distinct()
            .sorted()
            .collect(Collectors.toList());

        personData.getRoomNum().addAll(roomsNum);

        PersonType.AddFlatInfo addFlatInfo = new PersonType.AddFlatInfo();
        personData.setAddFlatInfo(addFlatInfo);

        addFlatInfo.setNoFlat(trimIfNotNull(flat.getNoFlat()));
        addFlatInfo.setOwnFederal(trimIfNotNull(flat.getIsFederal()));
        addFlatInfo.setInCourt(trimIfNotNull(flat.getInCourt()));

        PersonType.AddInfo addInfo = new PersonType.AddInfo();
        personData.setAddInfo(addInfo);

        addInfo.setIsDead(trimIfNotNull(personsMessageType.getIsDead()));
        addInfo.setDelReason(trimIfNotNull(personsMessageType.getDelReason()));
    }

    private void handleOneFoundPerson(PersonDocument personDocument,
                                      PersonType personData,
                                      SuperServiceDGPPersonsMessageType message,
                                      LinkedFlats.Flat flat,
                                      List<FirstFlowError> errors,
                                      RealEstateDocument realEstateDocument) {
        if (StringUtils.isNotBlank(personData.getFIO())
            && StringUtils.isBlank(personData.getLastName())
            && StringUtils.isBlank(personData.getFirstName())
            && StringUtils.isBlank(personData.getMiddleName())) {
            String fio = personData.getFIO().trim();
            while (fio.contains("  ")) {
                fio = fio.replace("  ", " ");
            }
            List<String> strings = Arrays.asList(fio.split(" "));
            if (strings.size() > 0) {
                personData.setLastName(strings.get(0));
            }
            if (strings.size() > 1) {
                personData.setFirstName(strings.get(1));
            }
            if (strings.size() > 2) {
                personData.setMiddleName(String.join(" ", strings.subList(2, strings.size())));
            }
        }

        String gender = null;
        if (StringUtils.isNotBlank(personData.getGender())) {
            if (personData.getGender().trim().equalsIgnoreCase("Мужской")) {
                gender = "Мужской";
            } else if (personData.getGender().trim().equalsIgnoreCase("Женский")) {
                gender = "Женский";
            }
        }
        personData.setGender(gender);

        if (StringUtils.isBlank(personData.getFlatNum()) && StringUtils.isNotBlank(personData.getFlatID())) {
            String flatId = personData.getFlatID();
            ofNullable(realEstateDocument)
                .map(RealEstateDocument::getDocument)
                .map(RealEstate::getRealEstateData)
                .map(RealEstateDataType::getFlats)
                .map(RealEstateDataType.Flats::getFlat)
                .ifPresent(e -> {
                    Optional<FlatType> optFlatType =
                        e.stream()
                            .filter(f -> StringUtils.isNotBlank(f.getFlatID()))
                            .filter(f -> f.getFlatID().equals(flatId))
                            .findAny();
                    if (optFlatType.isPresent()) {
                        FlatType flatType = optFlatType.get();
                        personData.setFlatNum(flatType.getApartmentL4VALUE());
                    }
                });
        }

        personData.setWaiter(trimIfNotNull(message.getIsQueue()));
        ofNullable(trimIfNotNull(message.getPersonId()))
            .ifPresent(personData::setPersonID);
        ofNullable(trimIfNotNull(flat.getAffairId()))
            .ifPresent(personData::setAffairId);
        ofNullable(trimIfNotNull(flat.getStatusLiving()))
            .ifPresent(personData::setStatusLiving);
        personData.setEncumbrances(trimIfNotNull(flat.getEncumbrances()));
        ofNullable(trimIfNotNull(flat.getSnosCadnum()))
            .ifPresent(personData::setCadNum);

        List<String> roomNum = personData.getRoomNum();
        List<String> snosRoomsNum = flat.getSnosRoomsNum()
            .stream()
            .filter(StringUtils::isNotBlank)
            .flatMap(rn -> Arrays.stream(rn.replace(" ", "").split(",")))
            .distinct()
            .collect(Collectors.toList());
        roomNum.clear();
        roomNum.addAll(snosRoomsNum);

        PersonType.AddFlatInfo addFlatInfo =
            personData.getAddFlatInfo() == null ? new PersonType.AddFlatInfo() : personData.getAddFlatInfo();
        personData.setAddFlatInfo(addFlatInfo);
        addFlatInfo.setNoFlat(trimIfNotNull(flat.getNoFlat()));
        addFlatInfo.setOwnFederal(trimIfNotNull(flat.getIsFederal()));
        addFlatInfo.setInCourt(trimIfNotNull(flat.getInCourt()));

        PersonType.AddInfo addInfo =
            personData.getAddInfo() == null ? new PersonType.AddInfo() : personData.getAddInfo();
        personData.setAddInfo(addInfo);
        addInfo.setIsDead(trimIfNotNull(message.getIsDead()));

        personData.setUpdatedFromDgiStatus(1);
        personData.setUpdatedFromDgiDate(LocalDate.now());

        if (compareDocument(message, flat, personData, realEstateDocument)) {
            if (StringUtils.isNotBlank(message.getChangeStatus())
                && "Удален".equals(message.getChangeStatus().trim())) {
                createAndAddError(errors, personDocument, realEstateDocument, flat, message);
                personData.setUpdatedFromDgiStatus(null);
                personData.setUpdatedFromDgiDate(null);
            } else {
                personData.setUpdatedFromDgiStatus(2);
                personData.setUpdatedFromDgiDate(LocalDate.now());

                if (StringUtils.isNotBlank(message.getFirstname())) {
                    personData.setFirstName(message.getFirstname());
                }
                if (StringUtils.isNotBlank(message.getLastname())) {
                    personData.setLastName(message.getLastname());
                }
                if (StringUtils.isNotBlank(message.getMiddlename())) {
                    personData.setMiddleName(message.getMiddlename());
                }
                personData.setFIO(
                    getFio(personData.getFirstName(), personData.getLastName(), personData.getMiddleName()));
            }
        } else {
            createAndAddError(errors, personDocument, realEstateDocument, flat, message);
        }

        personDocumentService.updateDocument(personDocument.getId(), personDocument, true, true, "");
    }

    private void createAndAddError(List<FirstFlowError> errors,
                                   PersonDocument personDocument,
                                   RealEstateDocument realEstateDocument,
                                   LinkedFlats.Flat flat,
                                   SuperServiceDGPPersonsMessageType message) {
        FirstFlowError error = new FirstFlowError();
        errors.add(error);

        error.setPersonId(personDocument.getId());
        if (flat != null) {
            FirstFlowError.Flat errorFlat = new FirstFlowError.Flat();
            error.setFlat(errorFlat);
            copyFlatToErrorFlat(flat, errorFlat);
        }
        if (message != null) {
            FirstFlowError.Message errorMessage = new FirstFlowError.Message();
            error.setMessage(errorMessage);
            copyMessageToErrorMessage(message, errorMessage);
        }
        FirstFlowError.Result result = new FirstFlowError.Result();
        fillResultByFlatAndMessage(result, flat, message, personDocument, realEstateDocument);
        error.setResult(result);
    }

    private void fillResultByFlatAndMessage(FirstFlowError.Result result,
                                            LinkedFlats.Flat flat,
                                            SuperServiceDGPPersonsMessageType message,
                                            PersonDocument personDocument,
                                            RealEstateDocument realEstateDocument) {
        PersonType personData = personDocument.getDocument().getPersonData();
        String flatId = personData.getFlatID();
        FlatType flatType = null;
        Optional<List<FlatType>> flats = ofNullable(realEstateDocument)
            .map(RealEstateDocument::getDocument)
            .map(RealEstate::getRealEstateData)
            .map(RealEstateDataType::getFlats)
            .map(RealEstateDataType.Flats::getFlat);
        if (flats.isPresent()) {
            Optional<FlatType> optFlatType = flats.get()
                .stream()
                .filter(f -> StringUtils.isNotBlank(f.getFlatID()))
                .filter(f -> f.getFlatID().equals(flatId))
                .findAny();
            if (optFlatType.isPresent()) {
                flatType = optFlatType.get();
            }
        }
        String flatNum = flatType != null ? flatType.getApartmentL4VALUE() : null;
        if (StringUtils.isBlank(flatNum)) {
            flatNum = personData.getFlatNum();
        }
        presetValue(flatNum, flat != null ? trimIfNotNull(flat.getSnosFlatNum()) : null, result::setFlat);
        presetValue(personData.getUNOM() == null ? null : personData.getUNOM().toString(),
            flat != null ? trimIfNotNull(flat.getSnosUnom()) : null,
            result::setUnom);
        presetValue(personData.getCadNum(),
            flat != null ? trimIfNotNull(flat.getSnosCadnum()) : null,
            result::setCadastralNumber);
        presetValue(personData.getAffairId(),
            flat != null ? trimIfNotNull(flat.getAffairId()) : null,
            result::setAffairId);
        presetValue(personData.getEncumbrances(),
            flat != null ? trimIfNotNull(flat.getEncumbrances()) : null,
            result::setEncumbrances);

        List<String> roomsNum;
        if (flat != null) {
            roomsNum = flat.getSnosRoomsNum()
                .stream()
                .filter(StringUtils::isNotBlank)
                .flatMap(rn -> Arrays.stream(rn.replace(" ", "").split(",")))
                .distinct()
                .collect(Collectors.toList());
        } else {
            roomsNum = new ArrayList<>();
        }
        Collections.sort(personData.getRoomNum());
        Collections.sort(roomsNum);
        if (personData.getRoomNum().equals(roomsNum)) {
            result.getRoomsNum().addAll(personData.getRoomNum());
        } else {
            if (personData.getRoomNum().size() > 0) {
                result.getRoomsNum().addAll(personData.getRoomNum());
            } else if (roomsNum.size() > 0) {
                result.getRoomsNum().addAll(roomsNum);
            }
        }

        boolean isCommunDgpValue = flatType != null && "Коммунальная".equals(flatType.getFlatType());
        boolean isCommunDgiValue = !roomsNum.isEmpty();
        result.setCommun(isCommunDgpValue || isCommunDgiValue);

        presetValue(personData.getAddFlatInfo() == null ? null : personData.getAddFlatInfo().getNoFlat(),
            flat != null ? trimIfNotNull(flat.getNoFlat()) : null,
            result::setNoFlat);
        presetValue(personData.getAddFlatInfo() == null ? null : personData.getAddFlatInfo().getOwnFederal(),
            flat != null ? trimIfNotNull(flat.getIsFederal()) : null,
            result::setIsFederal);
        presetValue(personData.getAddFlatInfo() == null ? null : personData.getAddFlatInfo().getInCourt(),
            flat != null ? trimIfNotNull(flat.getInCourt()) : null,
            result::setInCourt);
        presetValue(personData.getPersonID(),
            message != null ? trimIfNotNull(message.getPersonId()) : null,
            result::setPersonId);
        presetValue(personData.getSNILS(),
            message != null ? trimIfNotNull(message.getSnils()) : null,
            result::setSnils);
        presetValue(personData.getLastName(),
            message != null ? trimIfNotNull(message.getLastname()) : null,
            result::setLastName);
        presetValue(personData.getFirstName(),
            message != null ? trimIfNotNull(message.getFirstname()) : null,
            result::setFirstName);
        presetValue(personData.getMiddleName(),
            message != null ? trimIfNotNull(message.getMiddlename()) : null,
            result::setMiddleName);
        presetValueDate(personData.getBirthDate(),
            message != null ? message.getBirthdate() : null,
            result::setBirthDate);

        String gender = null;
        if (StringUtils.isNotBlank(personData.getGender())) {
            if (personData.getGender().trim().equalsIgnoreCase("Мужской")) {
                gender = "Мужской";
            } else if (personData.getGender().trim().equalsIgnoreCase("Женский")) {
                gender = "Женский";
            }
        }
        presetValue(gender, message != null ? resolveGender(trimIfNotNull(message.getSex())) : null, result::setSex);
        presetValue(personData.getStatusLiving(),
            flat != null ? trimIfNotNull(flat.getStatusLiving()) : null,
            result::setStatusLiving);
        presetValue(personData.getWaiter(),
            message != null ? trimIfNotNull(message.getIsQueue()) : null,
            result::setIsQueue);
        presetValue(personData.getAddInfo() == null ? null : personData.getAddInfo().getIsDead(),
            message != null ? trimIfNotNull(message.getIsDead()) : null,
            result::setIsDead);
        presetValue(personData.getAddInfo() == null ? null : personData.getAddInfo().getDelReason(),
            message != null ? trimIfNotNull(message.getDelReason()) : null,
            result::setDelReason);
    }

    /**
     * Предустаовка значения.
     *
     * @param dgpValue значение ДГП
     * @param dgiValue значение ДГИ
     * @param consumer установщик
     */
    public void presetValue(String dgpValue, String dgiValue, Consumer<String> consumer) {
        if (StringUtils.isNotBlank(dgpValue) && StringUtils.isNotBlank(dgiValue)) {
            if (dgpValue.equalsIgnoreCase(dgiValue)) {
                consumer.accept(dgiValue);
            }
        } else {
            if (StringUtils.isNotBlank(dgpValue)) {
                consumer.accept(dgpValue);
            } else if (StringUtils.isNotBlank(dgiValue)) {
                consumer.accept(dgiValue);
            }
        }
    }

    private void presetValueDate(LocalDate dgpValue, LocalDate dgiValue, Consumer<LocalDate> consumer) {
        if (Objects.nonNull(dgpValue) && Objects.nonNull(dgiValue)) {
            if (dgpValue.equals(dgiValue)) {
                consumer.accept(dgpValue);
            }
        } else {
            if (Objects.nonNull(dgpValue)) {
                consumer.accept(dgpValue);
            } else if (Objects.nonNull(dgiValue)) {
                consumer.accept(dgiValue);
            }
        }
    }

    private boolean compareDocument(
        SuperServiceDGPPersonsMessageType message, LinkedFlats.Flat flat,
        PersonType personData, RealEstateDocument realEstateDocument
    ) {
        boolean result = compareStringsWithNotNull(personData.getPersonID(), message.getPersonId());
        String unom = personData.getUNOM() == null ? null : personData.getUNOM().toString();
        result = result && (compareStringsWithNotNull(unom, flat.getSnosUnom()));
        result = result
            && (compareStringsWithNotNull(personData.getLastName(), message.getLastname())
            && compareStringsWithNotNull(personData.getFirstName(), message.getFirstname())
            && compareStringsWithNotNull(personData.getMiddleName(), message.getMiddlename())
            || compareStringsWithNotNull(personData.getFIO(),
            getFio(message.getFirstname(), message.getLastname(), message.getMiddlename())));
        result = result && compareDatesWithNotNull(personData.getBirthDate(), message.getBirthdate());
        result = result && compareStringsWithNotNull(personData.getGender(), resolveGender(message.getSex()));
        if (StringUtils.isNotBlank(message.getSnils()) && StringUtils.isNotBlank(personData.getSNILS())) {
            result = result && compareStringsWithNotNull(personData.getSNILS(), message.getSnils());
        }
        result = result && compareStringsWithNotNull(personData.getAffairId(), flat.getAffairId());
        if (StringUtils.isNotBlank(flat.getSnosCadnum()) && StringUtils.isNotBlank(personData.getCadNum())) {
            result = result && compareStringsWithNotNull(personData.getCadNum(), flat.getSnosCadnum());
        }
        result = result && compareStringsWithNotNull(personData.getWaiter(), message.getIsQueue());
        result = result && compareStringsWithNotNull(personData.getEncumbrances(), flat.getEncumbrances());
        result = result && compareStringsWithNotNull(personData.getFlatNum(), flat.getSnosFlatNum());
        result = result && compareStringsWithNotNull(personData.getStatusLiving(), flat.getStatusLiving());

        List<String> roomNum = personData.getRoomNum();
        List<String> snosRoomsNum = flat.getSnosRoomsNum();
        Collections.sort(roomNum);
        Collections.sort(snosRoomsNum);
        result = result && roomNum.equals(snosRoomsNum);

        FlatType flatType = RealEstateUtils.findFlat(personData.getFlatID(), realEstateDocument);
        boolean isCommunDgpValue = flatType != null && "Коммунальная".equals(flatType.getFlatType());
        boolean isCommunDgiValue = flat.getSnosRoomsNum() != null && flat.getSnosRoomsNum().size() > 0;
        result = result && isCommunDgpValue == isCommunDgiValue;

        PersonType.AddInfo addInfo = personData.getAddInfo();
        String delReason = addInfo == null ? null : addInfo.getDelReason();
        String isDead = addInfo == null ? null : addInfo.getIsDead();
        result = result && compareStringsWithNotNull(delReason, message.getDelReason());
        result = result && compareStringsWithNotNull(isDead, message.getIsDead());

        PersonType.AddFlatInfo flatInfo = personData.getAddFlatInfo();
        String noFlat = flatInfo == null ? null : flatInfo.getNoFlat();
        String ownFederal = flatInfo == null ? null : flatInfo.getOwnFederal();
        String inCourt = flatInfo == null ? null : flatInfo.getInCourt();
        result = result && compareStringsWithNotNull(noFlat, flat.getNoFlat());
        result = result && compareStringsWithNotNull(ownFederal, flat.getIsFederal());
        result = result && compareStringsWithNotNull(inCourt, flat.getInCourt());
        return result;
    }

    private void copyMessageToErrorMessage(SuperServiceDGPPersonsMessageType message,
                                           FirstFlowError.Message errorMessage) {
        if (message == null || errorMessage == null) {
            return;
        }
        errorMessage.setActualDate(message.getActualDate());
        errorMessage.setBirthDate(message.getBirthdate());
        errorMessage.setChangeStatus(trimIfNotNull(message.getChangeStatus()));
        errorMessage.setDelReason(trimIfNotNull(message.getDelReason()));
        errorMessage.setFirstName(trimIfNotNull(message.getFirstname()));
        errorMessage.setIsDead(trimIfNotNull(message.getIsDead()));
        errorMessage.setIsQueue(trimIfNotNull(message.getIsQueue()));
        errorMessage.setLastName(trimIfNotNull(message.getLastname()));
        errorMessage.setMiddleName(trimIfNotNull(message.getMiddlename()));
        errorMessage.setPersonId(trimIfNotNull(message.getPersonId()));
        errorMessage.setSex(resolveGender(trimIfNotNull(message.getSex())));
        errorMessage.setSnils(trimIfNotNull(message.getSnils()));
    }

    private void copyFlatToErrorFlat(LinkedFlats.Flat flat, FirstFlowError.Flat errorFlat) {
        if (flat == null || errorFlat == null) {
            return;
        }
        errorFlat.setAffairId(trimIfNotNull(flat.getAffairId()));
        errorFlat.setEncumbrances(trimIfNotNull(flat.getEncumbrances()));
        errorFlat.setInCourt(trimIfNotNull(flat.getInCourt()));
        errorFlat.setIsFederal(trimIfNotNull(flat.getIsFederal()));
        errorFlat.setNoFlat(trimIfNotNull(flat.getNoFlat()));
        errorFlat.setSnosCadnum(trimIfNotNull(flat.getSnosCadnum()));
        errorFlat.setSnosFlatNum(trimIfNotNull(flat.getSnosFlatNum()));
        errorFlat.setSnosUnom(trimIfNotNull(flat.getSnosUnom()));
        errorFlat.setStatusLiving(trimIfNotNull(flat.getStatusLiving()));
        errorFlat.getSnosRoomsNum().addAll(flat.getSnosRoomsNum()
            .stream()
            .filter(StringUtils::isNotBlank)
            .flatMap(rn -> Arrays.stream(rn.replace(" ", "").split(",")))
            .distinct()
            .collect(Collectors.toList())
        );
    }

    private List<PersonDocument> searchPerson(SuperServiceDGPPersonsMessageType personsMessage, LinkedFlats.Flat flat) {
        return searchForDummyPersons(
            PersonSearchDto
                .builder()
                .affairId(flat.getAffairId())
                .flatNum(flat.getSnosFlatNum())
                .unom(flat.getSnosUnom())
                .birthdate(personsMessage.getBirthdate())
                .firstname(personsMessage.getFirstname())
                .lastname(personsMessage.getLastname())
                .middlename(personsMessage.getMiddlename())
                .personId(personsMessage.getPersonId())
                .snils(personsMessage.getSnils())
                .build()
        );
    }

    /**
     * Получает ФИО по Ф, И и О.
     *
     * @param firstName имя
     * @param lastName фамилия
     * @param middleName отчество
     * @return ФИО
     */
    @NotNull
    public String getFio(String firstName, String lastName, String middleName) {
        String fio = lastName + " " + firstName;
        fio += StringUtils.isNotBlank(middleName) ? " " + middleName : "";
        return fio;
    }

    private String resolveGender(String sex) {
        if (StringUtils.isBlank(sex)) {
            return "";
        }
        switch (sex) {
            case "1":
                return "Мужской";
            case "2":
                return "Женский";
            default:
                return "";
        }
    }

    /**
     * Сравнивает строки без учета null.
     *
     * @param s1 строка 1
     * @param s2 строка 2
     * @return true/false
     */
    public boolean compareStringsWithNotNull(String s1, String s2) {
        return StringUtils.isBlank(s1) && StringUtils.isBlank(s2)
            || StringUtils.isNotBlank(s1) && StringUtils.isNotBlank(s2) && s1.trim().equalsIgnoreCase(s2.trim());
    }

    /**
     * Сравнивает даты без учета null.
     *
     * @param d1 дата 1
     * @param d2 дата 2
     * @return true/false
     */
    public boolean compareDatesWithNotNull(LocalDate d1, LocalDate d2) {
        return d1 == null && d2 == null || d1 != null && d1.equals(d2);
    }

    private static String trimIfNotNull(String str) {
        return StringUtils.isNotBlank(str) ? str.trim() : null;
    }
}
