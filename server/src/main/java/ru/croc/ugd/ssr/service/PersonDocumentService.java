package ru.croc.ugd.ssr.service;

import static java.util.Comparator.comparing;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static ru.croc.ugd.ssr.integration.service.flows.AdministrativeDocumentFlowService.ADMINISTRATIVE_DOC_FILE_TYPE;
import static ru.croc.ugd.ssr.service.shipping.DefaultShippingService.WORKING_DAYS_BEFORE_SHIPPING;
import static ru.croc.ugd.ssr.utils.PersonUtils.getOfferLetter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.computel.common.filenet.client.FilenetFileBean;
import ru.croc.ugd.ssr.FlatType;
import ru.croc.ugd.ssr.HouseToResettle;
import ru.croc.ugd.ssr.HouseToSettle;
import ru.croc.ugd.ssr.OfferLetterParsedFlatData;
import ru.croc.ugd.ssr.Person;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.PersonType.OfferLetters.OfferLetter;
import ru.croc.ugd.ssr.PersonType.OfferLetters.OfferLetter.Files.File;
import ru.croc.ugd.ssr.RealEstateDataType;
import ru.croc.ugd.ssr.ResettlementRequest;
import ru.croc.ugd.ssr.ResettlementRequestType;
import ru.croc.ugd.ssr.db.dao.PersonDocumentDao;
import ru.croc.ugd.ssr.db.dao.RealEstateDocumentDao;
import ru.croc.ugd.ssr.db.projection.FlatTenantProjection;
import ru.croc.ugd.ssr.db.projection.TenantProjection;
import ru.croc.ugd.ssr.dto.NewFlatDto;
import ru.croc.ugd.ssr.dto.RestNotResettledPersonDto;
import ru.croc.ugd.ssr.dto.apartmentinspection.RestPersonDto;
import ru.croc.ugd.ssr.dto.flat.RestFlatDto;
import ru.croc.ugd.ssr.dto.flat.RestFlatLiverDto;
import ru.croc.ugd.ssr.dto.person.RestOfferLetterDto;
import ru.croc.ugd.ssr.dto.person.RestPersonBirthDateDto;
import ru.croc.ugd.ssr.integration.service.MdmIntegrationService;
import ru.croc.ugd.ssr.mapper.PersonMapper;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.RealEstateDocument;
import ru.croc.ugd.ssr.model.ResettlementRequestDocument;
import ru.croc.ugd.ssr.model.offerletterparsing.OfferLetterParsingDocument;
import ru.croc.ugd.ssr.service.document.OfferLetterParsingDocumentService;
import ru.croc.ugd.ssr.service.document.TradeAdditionDocumentService;
import ru.croc.ugd.ssr.service.flowerrorreport.FlowErrorReportService;
import ru.croc.ugd.ssr.service.flowerrorreport.FlowType;
import ru.croc.ugd.ssr.service.offerletterparsing.OfferLetterParsingService;
import ru.croc.ugd.ssr.service.realestate.RealEstateService;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.croc.ugd.ssr.utils.PersonDocumentUtils;
import ru.croc.ugd.ssr.utils.PersonUtils;
import ru.croc.ugd.ssr.utils.StreamUtils;
import ru.reinform.cdp.db.model.DocumentData;
import ru.reinform.cdp.document.model.DocumentType;
import ru.reinform.cdp.utils.mapper.JsonMapper;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Сервис по работе с жителем.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class PersonDocumentService extends DocumentWithFolder<PersonDocument> {

    private static final String UGD_SSR_RELOCATION_STATUS_DICTIONARY = "ugd_ssr_relocationStatus";
    private static final String EQUIVALENT_RESETTLEMENT_TYPE = "Равнозначное переселение";

    private final PersonDocumentUtils personDocumentUtils;
    private final PersonDocumentDao personDocumentDao;
    private final RealEstateDocumentDao realEstateDocumentDao;
    private final SsrBusinessCalendarService ssrBusinessCalendarService;
    private final JsonMapper jsonMapper;
    private final MdmIntegrationService mdmIntegrationService;
    private final CapitalConstructionObjectService ccoService;
    private final PersonMapper personMapper;
    private final FlowErrorReportService flowErrorReportService;
    private final OfferLetterParsingDocumentService offerLetterParsingDocumentService;
    private final OfferLetterParsingService offerLetterParsingService;
    private final RealEstateDocumentService realEstateDocumentService;
    private final RealEstateService realEstateService;
    private final SsrFilestoreService ssrFilestoreService;
    private final ChedFileService chedFileService;
    private final DictionaryService dictionaryService;
    private final TradeAdditionDocumentService tradeAdditionDocumentService;
    private final ResettlementRequestDocumentService resettlementRequestDocumentService;

    @Nonnull
    @Override
    @Transactional
    public PersonDocument updateDocument(
        @Nonnull String id,
        @Nonnull PersonDocument newDocument,
        boolean skipUnchanged,
        boolean flagReindex,
        @Nullable String notes
    ) {
        ssrFilestoreService.createFolderIfNeeded(SsrDocumentTypes.PERSON, newDocument);
        return super.updateDocument(id, newDocument, skipUnchanged, flagReindex, notes);
    }

    @Nonnull
    @Override
    public DocumentType<PersonDocument> getDocumentType() {
        return SsrDocumentTypes.PERSON;
    }

    @Override
    public void afterUpdate(
        @Nonnull PersonDocument oldDocument, @Nonnull PersonDocument newDocument, boolean hasChanges
    ) {
        if (hasChanges) {
            checkChangeRealEstateStatus(oldDocument, newDocument);
        }
    }

    public boolean hasEmptyReleaseFlats(final List<PersonDocument> personDocuments) {
        return personDocuments.stream()
            .anyMatch(personDocument -> isNull(personDocument.getDocument().getPersonData().getReleaseFlat())
                || isNull(personDocument.getDocument().getPersonData().getReleaseFlat().getActNum()));
    }

    private void checkChangeRealEstateStatus(
        @Nonnull PersonDocument oldDocument, @Nonnull PersonDocument newDocument
    ) {
        final PersonType oldPersonType = oldDocument.getDocument().getPersonData();
        final PersonType newPersonType = newDocument.getDocument().getPersonData();
        if (nonNull(newPersonType.getUNOM())
            && ((isNull(oldPersonType.getReleaseFlat()) || isNull(oldPersonType.getReleaseFlat().getActNum()))
            && nonNull(newPersonType.getReleaseFlat())
            && nonNull(newPersonType.getReleaseFlat().getActNum()))) {
            checkChangeRealEstateStatus(newPersonType.getUNOM());
        }
    }

    private void checkChangeRealEstateStatus(final BigInteger unom) {
        if (!hasEmptyReleaseFlats(fetchByUnom(unom))) {
            realEstateService.createCompleteResettlementProcess(unom);
        }
    }

    /**
     * Получить жителей с похожими атрибутами ЕВД.
     * Сравнивает ФИО + ДР, СНИЛС или personID.
     *
     * @param id жителя, с кем сравнивают
     * @return список похожих PersonDocument
     */
    public List<PersonDocument> fetchSimilarPersons(String id) {
        return personDocumentDao.fetchSimilarPersons(id)
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    public List<String> fetchPersonIdsForOfferLetterDataExtraction() {
        return personDocumentDao.fetchPersonIdsForOfferLetterDataExtraction();
    }

    /**
     * Получает жителей по unom.
     *
     * @param unom идентификатор
     * @return список PersonDocument, проживающих в доме
     */
    public List<PersonDocument> fetchByUnom(String unom) {
        return ofNullable(unom)
            .map(personDocumentDao::fetchPersonsByUnom)
            .map(List::stream)
            .orElseGet(Stream::empty)
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    /**
     * Получает жителей по unom.
     *
     * @param unom идентификатор
     * @return список PersonDocument, проживающих в доме
     */
    public List<PersonDocument> fetchByUnom(BigInteger unom) {
        return ofNullable(unom)
            .map(Objects::toString)
            .map(personDocumentDao::fetchPersonsByUnom)
            .map(List::stream)
            .orElse(Stream.empty())
            .map(this::parseDocumentData)
            .filter(personDocument -> !personDocument.getDocument().getPersonData().isIsArchive())
            .collect(Collectors.toList());
    }

    /**
     * Получить id пустых PersonDocument по id задачи на разбор ошибок.
     *
     * @param id ID документа-задачи на разбор
     * @return список id жителей-пустышек
     */
    public List<String> fetchByFirstFlowErrorAnalyticsId(String id) {
        return personDocumentDao.fetchByFirstFlowErrorAnalyticsId(id);
    }

    /**
     * Получает жителей по unom-ам.
     *
     * @param unoms UNOMы домов
     * @return список PersonDocument, проживающих в домах
     */
    public List<PersonDocument> fetchByUnoms(List<String> unoms) {
        return personDocumentDao.fetchPersonsByUnoms(unoms)
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    /**
     * Получает жителей по realEstateId.
     *
     * @param realEstateId идентификатор ОН
     * @return список PersonDocument, проживающих в доме
     */
    public List<PersonDocument> fetchByRealEstateId(String realEstateId) {
        String unom = realEstateDocumentDao.getUnomByRealEstateId(realEstateId);
        return personDocumentDao.fetchPersonsByUnom(unom)
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    public Pair<Boolean, Boolean> isValidCommunalFlatLiver(final PersonDocument personDocument) {
        final Optional<PersonType> personTypeOpt = ofNullable(personDocument)
            .map(PersonDocument::getDocument)
            .map(Person::getPersonData);
        final boolean isCommunalFlat = realEstateDocumentDao.isCommunalFlat(
            personTypeOpt
                .map(PersonType::getFlatID)
                .orElse(StringUtils.EMPTY)
        );
        final boolean isLiverHasAnyRoom = personTypeOpt
            .map(PersonType::getRoomNum)
            .orElse(new ArrayList<>())
            .stream()
            .anyMatch(StringUtils::isNotBlank);

        return Pair.of(isCommunalFlat == isLiverHasAnyRoom, isCommunalFlat);
    }

    /**
     * Получает жителей по flatId.
     *
     * @param flatId идентификатор квартиры
     * @return список PersonDocument, проживающих в квартире
     */
    public List<PersonDocument> fetchByFlatId(final String flatId) {
        return personDocumentDao.fetchPersonsByFlatId(flatId)
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    public List<RestFlatLiverDto> fetchOtherFlatOwnersByPersonId(final String personId, final boolean includePerson) {
        return fetchById(personId)
            .map(personDocument -> getOtherFlatOwners(personDocument, includePerson))
            .map(List::stream)
            .orElse(Stream.empty())
            .map(personMapper::toRestFlatLiverDto)
            .collect(Collectors.toList());
    }

    public List<PersonDocument> getOtherFlatOwners(final PersonDocument personDocument) {
        return getOtherFlatOwners(personDocument, false);
    }

    public List<PersonDocument> getOtherFlatOwners(final PersonDocument personDocument, final boolean includePerson) {
        if (isNull(personDocument)) {
            return Collections.emptyList();
        }
        final List<PersonDocument> otherFlatOwners = getOtherFlatOwners(
            personDocument.getId(), personDocument.getDocument().getPersonData()
        );
        if (includePerson) {
            otherFlatOwners.add(personDocument);
        }
        return otherFlatOwners;
    }

    public List<PersonDocument> getOtherFlatOwners(final String personDocumentId, final PersonType person) {
        final List<PersonDocument> allFamilyMembers = fetchByAffairId(person.getAffairId());
        return allFamilyMembers
            .stream()
            .filter(otherMembers -> PersonUtils.isOwnersOrTenant(otherMembers
                .getDocument()
                .getPersonData()
                .getStatusLiving()))
            .filter(otherMembers -> !personDocumentId.equals(otherMembers.getId()))
            .collect(Collectors.toList());
    }

    public List<PersonDocument> getOtherFlatOwners(
        final String personDocumentId, final PersonType person, final boolean includePerson
    ) {
        final List<PersonDocument> otherFlatOwners = getOtherFlatOwners(personDocumentId, person);

        if (includePerson) {
            final PersonDocument personDocument = fetchDocument(personDocumentId);
            otherFlatOwners.add(personDocument);
        }

        return otherFlatOwners;
    }

    /**
     * Получает жителей по personId, affairId.
     *
     * @param personId ид жителя из дги
     * @param affairId ид жил площади
     * @return список PersonDocument
     */
    public List<PersonDocument> fetchByPersonIdAndAffairId(String personId, String affairId) {
        return personDocumentDao.fetchByPersonIdAndAffairId(personId, affairId)
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    /**
     * Получает жителеля по personId.
     *
     * @param personId ид жителя из дги
     * @return список PersonDocument
     */
    public List<PersonDocument> fetchByPersonId(String personId) {
        return personDocumentDao.fetchByPersonId(personId)
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    /**
     * Получает жителей с письмами с прадложениями.
     *
     * @return список PersonDocument
     */
    public List<PersonDocument> fetchPersonsWithOfferLetters() {
        return personDocumentDao.fetchPersonsWithOfferLetters()
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    /**
     * Получает одного жителя по personId, affairId.
     *
     * @param personId ид жителя из дги
     * @param affairId ид жил площади
     * @return список PersonDocument
     */
    public Optional<PersonDocument> fetchOneByPersonIdAndAffairId(final String personId, final String affairId) {
        return fetchOneByPersonIdAndAffairIdAndReportFlowErrorIfRequired(personId, affairId, null, null);
    }

    public Optional<PersonDocument> fetchOneByPersonIdAndAffairIdAndReportFlowErrorIfRequired(
        String personId, String affairId, FlowType flowType, String flowMessage
    ) {
        List<PersonDocument> personDocumentList = personDocumentDao.fetchByPersonIdAndAffairId(personId, affairId)
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
        if (personDocumentList.size() == 0) {
            log.info("Нет в реестре жителя с personId={} и affairId={}", personId, affairId);
            if (flowType != null) {
                flowErrorReportService.reportPersonNotFoundError(flowType, personId, affairId, flowMessage);
            }
            return Optional.empty();
        }
        if (personDocumentList.size() > 1) {
            log.info("В реестре несколько жителей с personId={} и affairId={}", personId, affairId);
            if (flowType != null) {
                flowErrorReportService.reportPersonDuplicateFoundError(
                    flowType,
                    personDocumentList.get(0),
                    personDocumentList.get(1),
                    flowMessage
                );
            }
            return Optional.empty();
        }
        return of(personDocumentList.get(0));
    }

    /**
     * Возвращает Person по eno.
     *
     * @param eno - идентификатор квартиры
     * @return ОН, к которому относится eno
     */
    public PersonDocument getPersonByEno(String eno) {
        List<String> ids = personDocumentDao.fetchPersonsByEno(eno);
        if (ids.size() > 0) {
            return this.fetchDocument(ids.get(0));
        }
        return null;
    }

    /**
     * Получить PersonDocument со статусом ПФР "Превышен суточный лимит запросов на документ".
     *
     * @return список PersonDocument
     */
    public List<String> fetchIdsWithLimitedRequests() {
        return personDocumentDao.fetchIdsWithLimitedRequests();
    }

    /**
     * Получить письмо с предложением по letterId.
     *
     * @param letterId ид письма
     * @return список файлов (json)
     */
    public List<OfferLetter> fetchPersonOfferLettersByLetterId(String letterId) {
        return personDocumentDao.fetchPersonOfferLettersByLetterId(letterId)
            .stream()
            .map(i -> jsonMapper.readObject(i, OfferLetter.class))
            .collect(Collectors.toList());
    }

    /**
     * Получить Жителя по ЕНО отправленных сообщений ЕЛК.
     *
     * @param eno ЕНО
     * @return житель
     */
    public PersonDocument getPersonBySendMessageEno(String eno) {
        List<PersonDocument> personDocuments = personDocumentDao.getPersonBySendMessageEno(
                "[{\"eno\": \"" + eno + "\"}]"
            )
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
        if (!personDocuments.isEmpty()) {
            return personDocuments.get(0);
        }
        return null;
    }

    /**
     * Получить жителей по unom заселяемой квартиры.
     *
     * @param ccoUnom unom заселяемой квартиры
     * @return список жителей
     */
    public List<PersonDocument> getPersonByCcoUnom(String ccoUnom) {
        return personDocumentDao.getPersonByCcoUnom(
                "[{\"ccoUnom\": " + ccoUnom + "}]"
            )
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    /**
     * Существует ли житель по ЕНО отправленных сообщений ЕЛК.
     *
     * @param eno ЕНО
     * @param event Event
     * @return Отправлено ли сообщение.
     */
    public boolean isNotificationAlreadyHandled(final String eno, final String event) {
        return personDocumentDao.existsBySendMessage(
            "[{\"eno\": \"" + eno + "\", \"event\": \"" + event + "\"}]"
        );
    }

    /**
     * Returns contract sign date.
     * @param personType personType
     * @return contract sign date
     */
    public Optional<LocalDate> getContractSignDate(final PersonType personType) {
        return Stream.concat(getAgrDateStream(personType), getContractSignDateStream(personType))
            .filter(Objects::nonNull)
            .max(LocalDate::compareTo);
    }

    /**
     * Returns contract sign date.
     * @param personType personType
     * @param newFlat new flat info
     * @return contract sign date
     */
    public Optional<LocalDate> getContractSignDate(final PersonType personType,
                                                   final PersonType.NewFlatInfo.NewFlat newFlat) {
        Optional<PersonType.Contracts.Contract> contract = Optional.ofNullable(personType.getContracts())
            .map(PersonType.Contracts::getContract)
            .map(List::stream)
            .orElse(Stream.empty())
            .filter(e -> e.getOrderId().equals(newFlat.getOrderId()))
            .findFirst();
        if (contract.isPresent() && contract.get().getContractSignDate() != null) {
            return of(contract.get().getContractSignDate());
        }
        return Optional.empty();
    }

    public Stream<PersonType.Contracts.Contract> getContractsStream(final PersonType personType) {
        return ofNullable(personType)
            .map(PersonType::getContracts)
            .map(PersonType.Contracts::getContract)
            .map(List::stream)
            .orElse(Stream.empty());
    }

    private Stream<LocalDate> getContractSignDateStream(final PersonType personType) {
        return getContractsStream(personType)
            .map(PersonType.Contracts.Contract::getContractSignDate);
    }

    private Stream<LocalDate> getAgrDateStream(final PersonType personType) {
        return getNewFlatStream(personType)
            .map(PersonType.NewFlatInfo.NewFlat::getAgrDate);
    }

    /**
     * Returns contract sign date and its number.
     * @param personType personType
     * @return contract sign date and its number.
     */
    public Optional<Pair<String, LocalDate>> getContractSignDateAndNumber(final PersonType personType) {
        return Stream.concat(getAgrDateAndNumberStream(personType), getContractSignDateAndNumberStream(personType))
            .filter(pair -> Objects.nonNull(pair.getRight()))
            .max(comparing(Pair::getRight));
    }

    private Stream<Pair<String, LocalDate>> getContractSignDateAndNumberStream(final PersonType personType) {
        return getContractsStream(personType)
            .map(contract -> Pair.of(
                ofNullable(contract.getContractNum()).orElse(""),
                contract.getContractSignDate()
            ));
    }

    private Stream<Pair<String, LocalDate>> getAgrDateAndNumberStream(final PersonType personType) {
        return getNewFlatStream(personType)
            .map(newFlat -> Pair.of(
                ofNullable(newFlat.getAgrNum()).orElse(""),
                newFlat.getAgrDate()
            ));
    }

    private Stream<PersonType.NewFlatInfo.NewFlat> getNewFlatStream(final PersonType personType) {
        return ofNullable(personType)
            .map(PersonType::getNewFlatInfo)
            .map(PersonType.NewFlatInfo::getNewFlat)
            .map(List::stream)
            .orElse(Stream.empty());
    }

    /**
     * Returns person movement date, should happen in 15 days since last signed contract.
     *
     * @param personType personType
     * @return personMovementDate
     */
    public LocalDate getPersonMovementDate(final PersonType personType) {
        return getContractSignDate(personType)
            .map(contractSignDate -> ssrBusinessCalendarService.addWorkDays(
                contractSignDate, WORKING_DAYS_BEFORE_SHIPPING
            ))
            .orElse(null);
    }

    /**
     * Returns person movement date.
     *
     * @param fromDate fromDate
     * @return personMovementDate
     */
    public LocalDate getPersonMovementDate(final LocalDate fromDate) {
        return ssrBusinessCalendarService.addWorkDays(fromDate, WORKING_DAYS_BEFORE_SHIPPING);
    }

    /**
     * Получить идентификаторы жителей для обогащения из ПФР.
     *
     * @return список идентификаторов
     */
    public List<String> fetchAllPersonIdsForPfrUpdate() {
        return personDocumentDao.fetchAllPersonIdsForPfrUpdate();
    }

    /**
     * Обезличить жителей для тестового стенда.
     */
    @Transactional
    public void depersonalizePersons() {
        personDocumentDao.depersonalizePersons();
    }

    /**
     * Получает данные по жителю по snils, ssoID.
     *
     * @param snils Снилс
     * @param ssoId ID на портале mos.ru
     * @return PersonDocument
     */
    public List<PersonDocument> findPersonDocumentsWithUniqueId(final String snils, final String ssoId) {
        return this
            .fetchDocumentsBySnilsAndSsoId(snils, ssoId)
            .stream()
            .filter(StreamUtils.distinctByKey(PersonDocument::getId))
            .collect(Collectors.toList());
    }

    /**
     * Получает данные по жителю по snils, ssoID.
     *
     * @param snils Снилс
     * @param ssoId ID на портале mos.ru
     * @return PersonDocument
     */
    public List<PersonDocument> fetchDocumentsBySnilsAndSsoId(final String snils, final String ssoId) {
        List<PersonDocument> personDocumentList = fetchBySnilsAndSsoId(snils, ssoId);

        if (personDocumentList.size() == 0 && ssoId != null) {
            personDocumentList = fetchBySnilsAndSsoId(snils, null);
            personDocumentList
                .stream()
                .filter(PersonDocumentService::needToUpdateSsoId)
                .forEach(personDocument -> {
                    try {
                        final PersonType person = personDocument.getDocument().getPersonData();
                        retrieveSsoId(person.getSNILS()).ifPresent(person::setSsoID);

                        updateDocument(
                            personDocument.getId(), personDocument, true, true, null
                        );
                    } catch (Exception e) {
                        log.warn("Unable to update ssoId for person {}", personDocument.getId());
                    }
                });
        }

        return personDocumentList;
    }

    private static boolean needToUpdateSsoId(final PersonDocument personDocument) {
        final PersonType person = personDocument
            .getDocument()
            .getPersonData();

        return nonNull(person.getSNILS()) && (isNull(person.getSsoID()) || person.getSsoID().isEmpty());
    }

    private Optional<String> retrieveSsoId(final String snils) {
        try {
            final CompletableFuture<String> ssoIdRetrieval = mdmIntegrationService.getSsoIdAsync(snils);
            return of(ssoIdRetrieval.get(1, TimeUnit.SECONDS));
        } catch (Exception e) {
            log.warn("Невозможно получить SsoId по СНИЛС: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    private List<PersonDocument> fetchBySnilsAndSsoId(final String snils, final String ssoId) {
        return personDocumentDao.fetchDocumentsBySnilsAndSsoId(snils, ssoId)
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    /**
     * Получает жителей по СНИЛС.
     *
     * @param snils СНИЛС
     * @return список PersonDocument
     */
    public List<PersonDocument> fetchBySnils(String snils) {
        return personDocumentDao.fetchPersonsBySnils(snils)
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    /**
     * Получить PersonDocument по affairId и id квартиры с исключающим id.
     *
     * @param affairId ид жителя из дги
     * @param flatId   ид жителя
     * @param personId ид жителя исключенного из выборки
     * @return список DocumentData
     */
    public List<PersonDocument> fetchByAffairIdAndFlatId(String affairId, String flatId, String personId) {
        return personDocumentDao.fetchByAffairIdAndFlatId(affairId, flatId, personId)
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    /**
     * Получить PersonDocument по affairId.
     *
     * @param affairId ид жителя из дги
     * @return список DocumentData
     */
    public List<PersonDocument> fetchByAffairId(final String affairId) {
        return ofNullable(affairId)
            .map(personDocumentDao::fetchByAffairId)
            .map(List::stream)
            .orElse(Stream.empty())
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    /**
     * Получить PersonDocument по списку affairId.
     *
     * @param affairIds список ID семей
     * @return список DocumentData
     */
    public List<PersonDocument> fetchByAffairIds(final Collection<String> affairIds) {
        return personDocumentDao.fetchByAffairIds(affairIds)
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    /**
     * Получить жителя по personId, уному расселяемого дома и id расселяемой квартиры.
     * @param personId personId
     * @param unom unom
     * @param flatId flatId
     * @return жителя
     */
    public Optional<PersonDocument> fetchPersonByPersonIdAndUnomAndFlatId(
        final String personId,
        final String unom,
        final String flatId
    ) {
        return personDocumentDao.fetchPersonByPersonIdAndUnomAndFlatId(personId, unom, flatId)
            .stream()
            .map(this::parseDocumentData)
            .findFirst();
    }

    /**
     * Получить список идентификаторов загруженных жителей.
     * @return список идентификаторов загруженных жителей
     */
    public List<String> getUploadedPersonIds() {
        return personDocumentDao.getUploadedPersonIds();
    }

    /**
     * Удалить загруженных жителей.
     */
    public void deleteAllByIsUploaded() {
        personDocumentDao.deleteAllByIsUploaded();
    }

    /**
     * Получить жителя по snils, уному расселяемого дома и id расселяемой квартиры.
     * @param snils snils
     * @param unom unom
     * @param flatId flatId
     * @return жителя
     */
    public Optional<PersonDocument> fetchPersonBySnilsAndUnomAndFlatId(
        final String snils, final String unom, final String flatId
    ) {
        return personDocumentDao.fetchPersonBySnilsAndUnomAndFlatId(snils, unom, flatId)
            .stream()
            .map(this::parseDocumentData)
            .findFirst();
    }

    /**
     * Получить жителя по фио, дню рождения, уному расселяемого дома и id расселяемой квартиры.
     * @param fullName fullName
     * @param birthday birthday
     * @param unom unom
     * @param flatId flatId
     * @return жителя
     */
    public Optional<PersonDocument> fetchPersonByFullNameAndBirthdayAndUnomAndFlatId(
        final String fullName, final LocalDate birthday, final String unom, final String flatId
    ) {
        return personDocumentDao.fetchPersonByFullNameAndBirthdayAndUnomAndFlatId(fullName, birthday, unom, flatId)
            .stream()
            .map(this::parseDocumentData)
            .findFirst();
    }

    /**
     * Получить жителя по фио и дате рождения.
     * @param fullName fullName
     * @param birthDate birthDate
     * @return жителя
     */
    public Optional<PersonDocument> fetchPersonByFullNameAndBirthDate(
        final String fullName, final LocalDate birthDate
    ) {
        final List<DocumentData> personDocuments = personDocumentDao.fetchPersonByFullNameAndBirthDate(
            fullName, birthDate
        );

        if (personDocuments.size() > 1) {
            log.warn("More than one persons have been found: fullName = {}, birthDate = {}", fullName, birthDate);
        }
        return personDocuments.stream()
            .map(this::parseDocumentData)
            .findFirst();
    }

    /**
     * Получить жителя по фио, уному расселяемого дома и id расселяемой квартиры.
     * @param fullName fullName
     * @param unom unom
     * @param flatId flatId
     * @return жителя
     */
    public Optional<PersonDocument> fetchPersonByFullNameAndUnomAndFlatId(
        final String fullName, final String unom, final String flatId
    ) {
        return personDocumentDao.fetchPersonByFullNameAndUnomAndFlatId(fullName, unom, flatId)
            .stream()
            .map(this::parseDocumentData)
            .findFirst();
    }

    /**
     * Получить жителя по фио и уному расселяемого дома.
     * @param fullName fullName
     * @param unom unom
     * @return жителя
     */
    public Optional<PersonDocument> fetchPersonByFullNameAndUnom(
        final String fullName, final String unom
    ) {
        return personDocumentDao.fetchPersonByFullNameAndUnom(fullName, unom)
            .stream()
            .map(this::parseDocumentData)
            .findFirst();
    }

    /**
     * Получить список всех жильцов.
     * @param personType сведения о жителе
     * @return список сожителей
     */
    public Map<String, PersonType> getAllSameFlatLivers(final PersonType personType) {
        final List<PersonDocument> allFlatLiversDocuments = fetchByFlatId(personType.getFlatID());
        return personDocumentUtils.mapPersonIdToPerson(allFlatLiversDocuments);
    }

    /**
     * Получить ИД ЦИП.
     * @param personId personId
     * @return ИД ЦИП
     */
    public Optional<String> getCipId(final String personId) {
        final PersonDocument personDocument = fetchDocument(personId);
        return PersonUtils.getCipId(personDocument);
    }

    /**
     * Получить Жителей по УНОМу заселяемого дома.
     *
     * @param unom unom
     * @return список жителей
     */
    public List<PersonDocument> getPersonsByNewFlatCcoUnom(String unom) {
        return personDocumentDao.getPersonsByNewFlatCcoUnom(
                "[{\"ccoUnom\": " + unom + "}]"
            )
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    /**
     * Получить Жителей по УНОМу и номеру квартиры заселяемого дома.
     *
     * @param unom       unom
     * @param flatNumber номер квартиры
     * @return список жителей
     */
    public List<PersonDocument> getPersonsByNewFlatCcoUnomAndFlatNum(String unom, String flatNumber) {
        return personDocumentDao.getPersonsByNewFlatCcoUnomAndFlatNum(
                "[{\"ccoUnom\": " + unom + "}]",
                "[{\"ccoFlatNum\": \"" + flatNumber + "\"}]"
            )
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    /**
     * Получить ФИО жителя по идентификатору.
     * @param personId personId
     * @return ФИО жителя
     */
    public Optional<String> getPersonFullNameById(final String personId) {
        return ofNullable(personId)
            .map(this::fetchDocument)
            .map(PersonDocument::getDocument)
            .map(Person::getPersonData)
            .map(PersonType::getFIO);
    }

    public String getPersonNewFlatAddress(final PersonType personType) {
        if (personType.getNewFlatInfo() == null || personType.getNewFlatInfo().getNewFlat().isEmpty()) {
            return StringUtils.EMPTY;
        }
        return personType.getNewFlatInfo().getNewFlat()
            .stream()
            .min(Comparator.comparing(
                PersonType.NewFlatInfo.NewFlat::getMsgDateTime,
                Comparator.nullsLast(Comparator.reverseOrder())
            ))
            .map(flat -> {
                if (flat.getCcoUnom() == null) {
                    return StringUtils.EMPTY;
                }
                String address = ccoService
                    .getCcoAddressByUnom(flat.getCcoUnom().toString());
                if (isNotBlank(address) && isNotBlank(flat.getCcoFlatNum())) {
                    address += ", квартира " + flat.getCcoFlatNum();
                }
                return address;
            })
            .orElse(StringUtils.EMPTY);
    }

    private boolean existsPersonWithSsoId(final String affairId) {
        final List<PersonDocument> allFamilyMembers = fetchByAffairId(affairId);
        return allFamilyMembers
            .stream()
            .map(PersonDocument::getDocument)
            .map(Person::getPersonData)
            .map(PersonType::getSsoID)
            .anyMatch(StringUtils::isNotBlank);
    }

    public boolean existsFamilyMemberWithSsoId(final String personDocumentId) {
        PersonType personType = fetchById(personDocumentId)
            .map(PersonDocument::getDocument)
            .map(Person::getPersonData)
            .orElse(null);
        if (nonNull(personType) && isNotBlank(personType.getSsoID())) {
            return true;
        } else {
            return ofNullable(personType)
                .map(PersonType::getAffairId)
                .map(this::existsPersonWithSsoId)
                .orElse(false);
        }
    }

    /**
     * Получает всех жителей с некорректным relocationStatus.
     *
     * @return список PersonDocument
     */
    public List<PersonDocument> fetchByIncorrectRelocationStatus() {
        return personDocumentDao.fetchByIncorrectRelocationStatus()
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    /**
     * Получить проекцию жильца по идентификатору документа.
     *
     * @param personDocumentId идентификатор документа жителя
     * @return проекция жильца
     */
    public Optional<TenantProjection> fetchTenantById(final String personDocumentId) {
        return personDocumentDao.fetchTenantById(personDocumentId);
    }

    public List<RestFlatLiverDto> fetchFlatLiversByAffairId(final String affairId, final boolean isOwner) {
        final List<PersonDocument> allFamilyMembers = fetchByAffairId(affairId);
        return allFamilyMembers.stream()
            .filter(otherMembers -> !isOwner
                || PersonUtils.isOwnersOrTenant(
                otherMembers.getDocument().getPersonData().getStatusLiving()
            ))
            .map(personMapper::toRestFlatLiverDto)
            .collect(Collectors.toList());
    }

    public void createNewFlatAndUpdateOfferLetter(final String affairId, final NewFlatDto newFlatDto) {
        final List<PersonDocument> personDocuments = fetchByAffairId(affairId);
        personDocuments.forEach(personDocument -> createNewFlatAndUpdateOfferLetter(personDocument, newFlatDto));

        if (StringUtils.isNotEmpty(newFlatDto.getOfferLetterParsingDocumentId())) {
            final OfferLetterParsingDocument offerLetterParsingDocument =
                offerLetterParsingDocumentService.fetchDocument(newFlatDto.getOfferLetterParsingDocumentId());
            offerLetterParsingService.finishBpmProcessIfNeeded(offerLetterParsingDocument);
        }
    }

    private void createNewFlatAndUpdateOfferLetter(final PersonDocument personDocument, final NewFlatDto newFlatDto) {
        createNewFlat(personDocument, newFlatDto);
        updateOfferLetterFlatData(personDocument, newFlatDto);
        updateDocument(personDocument);
    }

    private void createNewFlat(final PersonDocument personDocument, final NewFlatDto newFlatDto) {
        final PersonType person = personDocument.getDocument().getPersonData();

        final PersonType.NewFlatInfo newFlatInfo = ofNullable(person.getNewFlatInfo())
            .orElseGet(PersonType.NewFlatInfo::new);
        person.setNewFlatInfo(newFlatInfo);

        final List<PersonType.NewFlatInfo.NewFlat> newFlatList = newFlatInfo.getNewFlat();

        final PersonType.NewFlatInfo.NewFlat newFlatByCcoFlatNumAndCcoUnom =
            getNewFlatByCcoFlatNumAndCcoUnom(newFlatList, newFlatDto.getCcoFlatNum(), newFlatDto.getCcoUnom());

        if (nonNull(newFlatByCcoFlatNumAndCcoUnom) && nonNull(newFlatDto.getLetterId())) {
            newFlatByCcoFlatNumAndCcoUnom.setLetterId(newFlatDto.getLetterId());
        } else {
            final PersonType.NewFlatInfo.NewFlat newFlat = personMapper.toNewFlat(newFlatDto);
            newFlatList.add(newFlat);
        }
    }

    private PersonType.NewFlatInfo.NewFlat getNewFlatByCcoFlatNumAndCcoUnom(
        final List<PersonType.NewFlatInfo.NewFlat> newFlatList, final String ccoFlatNum, final BigInteger ccoUnom
    ) {
        return ofNullable(newFlatList)
            .map(List::stream)
            .orElse(Stream.empty())
            .filter(newFlat -> Objects.equals(newFlat.getCcoFlatNum(), ccoFlatNum)
                && Objects.equals(newFlat.getCcoUnom(), ccoUnom))
            .findAny()
            .orElse(null);
    }

    public List<RestFlatDto> fetchFlatsWithLivers(final String unom, final boolean includeFlatsWithoutLivers) {
        final List<FlatTenantProjection> tenants = personDocumentDao.fetchAllTenantsByUnom(unom);
        final Map<String, List<FlatTenantProjection>> liversMap = tenants.stream()
            .filter(tenant -> nonNull(tenant.getAffairId()))
            .collect(
                Collectors.groupingBy(
                    FlatTenantProjection::getAffairId,
                    Collectors.mapping(
                        tenantProjection -> tenantProjection,
                        Collectors.toList()
                    )
                )
            );

        final RealEstateDocument realEstateDocument = realEstateDocumentService.fetchDocumentByUnom(unom);
        final List<FlatType> flats = of(realEstateDocument.getDocument().getRealEstateData())
            .map(RealEstateDataType::getFlats)
            .map(RealEstateDataType.Flats::getFlat)
            .orElse(Collections.emptyList());

        final List<RestFlatDto> flatsWithLivers = liversMap.entrySet()
            .stream()
            .map(entry -> retrieveRestFlatDto(entry.getKey(), entry.getValue(), flats))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        if (includeFlatsWithoutLivers) {
            final List<RestFlatDto> flatsWithoutLivers = flats.stream()
                .filter(flat -> nonNull(flat.getFlatID())
                    && flatsWithLivers.stream()
                    .noneMatch(flatWithLivers -> flatWithLivers.getFlatId().equals(flat.getFlatID()))
                )
                .map(flat -> RestFlatDto.builder()
                    .flatNum(nonNull(flat.getApartmentL4VALUE()) ? flat.getApartmentL4VALUE() : flat.getFlatNumber())
                    .flatId(flat.getFlatID())
                    .build()
                )
                .collect(Collectors.toList());

            return Stream.concat(flatsWithLivers.stream(), flatsWithoutLivers.stream())
                .collect(Collectors.toList());
        } else {
            return flatsWithLivers;
        }
    }

    private RestFlatDto retrieveRestFlatDto(
        final String affairId, List<FlatTenantProjection> flatTenants, final List<FlatType> flats
    ) {
        final FlatType flat = retrieveFlat(flats, flatTenants);

        if (nonNull(flat)) {
            return RestFlatDto.builder()
                .flatNum(nonNull(flat.getApartmentL4VALUE()) ? flat.getApartmentL4VALUE() : flat.getFlatNumber())
                .flatId(flat.getFlatID())
                .roomNum(retrieveRoomNumber(flatTenants))
                .affairId(affairId)
                .livers(
                    flatTenants.stream()
                        .map(flatTenant -> RestFlatLiverDto.builder()
                            .personDocumentId(flatTenant.getId())
                            .fullName(flatTenant.getFio())
                            .birthDate(flatTenant.getBirthdate())
                            .build()
                        )
                        .collect(Collectors.toList())
                )
                .build();
        } else {
            return null;
        }
    }

    private FlatType retrieveFlat(final List<FlatType> flats, final List<FlatTenantProjection> flatTenants) {
        final String flatId = retrieveFlatId(flatTenants);

        return flats.stream()
            .filter(flat -> nonNull(flatId) && flatId.equals(flat.getFlatID()))
            .findFirst()
            .orElse(null);
    }

    private String retrieveFlatId(final List<FlatTenantProjection> flatTenants) {
        return flatTenants.stream()
            .map(FlatTenantProjection::getFlatId)
            .filter(StringUtils::isNotEmpty)
            .findFirst()
            .orElse(null);
    }

    private String retrieveRoomNumber(final List<FlatTenantProjection> flatTenants) {
        return flatTenants.stream()
            .map(FlatTenantProjection::getRoomNum)
            .filter(StringUtils::isNotEmpty)
            .findFirst()
            .orElse(null);
    }

    private void updateOfferLetterFlatData(final PersonDocument personDocument, final NewFlatDto newFlatDto) {
        final PersonType person = personDocument.getDocument().getPersonData();

        ofNullable(person.getOfferLetters())
            .map(PersonType.OfferLetters::getOfferLetter)
            .orElse(Collections.emptyList())
            .stream()
            .filter(letter -> letter.getLetterId().equals(newFlatDto.getLetterId()))
            .findFirst()
            .ifPresent(letter -> {
                letter.getFlatData().setUnom(newFlatDto.getCcoUnom().toString());
                letter.getFlatData().setFlatNumber(newFlatDto.getCcoFlatNum());
            });
    }

    public void fillOfferLetterFlatData() {
        final List<PersonDocument> personDocumentList = personDocumentDao.fetchAllPersonsWithIncorrectOfferLetters()
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());

        personDocumentList.forEach(personDocument -> {
            final PersonType personType = personDocument.getDocument().getPersonData();
            final List<OfferLetter> offerLetterList = personType.getOfferLetters().getOfferLetter();
            final List<PersonType.NewFlatInfo.NewFlat> newFlatList = personType.getNewFlatInfo().getNewFlat();
            newFlatList.forEach(newFlat -> setFlatDataFromNewFlat(newFlat, offerLetterList));
            updateDocument(personDocument);
        });
    }

    private void setFlatDataFromNewFlat(
        final PersonType.NewFlatInfo.NewFlat newFlat, final List<OfferLetter> offerLetterList
    ) {
        offerLetterList.forEach(letter -> {
            if (Objects.equals(newFlat.getLetterId(), letter.getLetterId())
                && existsAdministrativeDocumentFileType(letter)) {
                final OfferLetterParsedFlatData flatData = ofNullable(letter.getFlatData())
                    .orElseGet(OfferLetterParsedFlatData::new);
                flatData.setUnom(newFlat.getCcoUnom().toString());
                flatData.setFlatNumber(newFlat.getCcoFlatNum());
                letter.setFlatData(flatData);
            }
        });
    }

    private boolean existsAdministrativeDocumentFileType(final OfferLetter letter) {
        return ofNullable(letter.getFiles())
            .map(OfferLetter.Files::getFile)
            .orElse(Collections.emptyList())
            .stream()
            .map(File::getFileType)
            .anyMatch(ADMINISTRATIVE_DOC_FILE_TYPE::equals);
    }

    /**
     * Получает жителей с дублями в списке договоров.
     * @return список PersonDocument
     */
    public List<PersonDocument> fetchAllPersonsWithContractDuplicates() {
        return personDocumentDao.fetchAllPersonsWithContractDuplicates()
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    public List<PersonDocument> deletePersonDocumentsByIds(final List<String> ids) {
        return ids.stream()
            .map(this::archivePersonDocument)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
    }

    private Optional<PersonDocument> archivePersonDocument(final String id) {
        return fetchById(id)
            .map(personDocument -> {
                final PersonType personData = personDocument.getDocument().getPersonData();
                personData.setIsArchive(true);

                return updateDocument(personDocument.getId(), personDocument, true, true, "archivePersonDocument");
            });
    }

    /**
     * Получение файла письма с предложением.
     * @param personDocumentId ИД документа жителя
     * @param letterId ИД письма с предложением
     * @return файл письма
     */
    public Pair<String, byte[]> fetchOfferLetterPdfFile(final String personDocumentId, final String letterId) {
        final PersonDocument personDocument = fetchDocument(personDocumentId);
        final File file = of(personDocument)
            .flatMap(person -> getOfferLetter(person, letterId))
            .flatMap(PersonUtils::getOfferLetterFile)
            .orElse(null);

        if (nonNull(file)) {
            return retrieveFile(file, personDocument);
        } else {
            return null;
        }
    }

    private Pair<String, byte[]> retrieveFile(final File file, final PersonDocument personDocument) {
        if (isNull(file.getFileLink()) && nonNull(file.getChedFileId())) {
            final String fileLink = chedFileService.extractFileFromChedAndGetFileLink(
                file.getChedFileId(), personDocument.getFolderId()
            );
            file.setFileLink(fileLink);
            updateDocument(personDocument, "fetchOfferLetterPdfFile");
        }

        return ofNullable(file.getFileLink())
            .map(this::retrieveFile)
            .orElse(null);
    }

    private Pair<String, byte[]> retrieveFile(final String fileId) {
        final byte[] fileContent = ssrFilestoreService.getFile(fileId);
        final FilenetFileBean fileInfo = ssrFilestoreService.getFileInfo(fileId);
        final String fileName = ofNullable(fileInfo)
            .map(FilenetFileBean::getFileName)
            .orElse(null);
        return Pair.of(fileName, fileContent);
    }

    public Optional<String> fetchFolderIdByPersonDocumentId(String personDocumentId) {
        return fetchById(personDocumentId).map(PersonDocument::getFolderId);
    }

    /**
     * Получить жителей с незавершенным процессом переселения по УНОМ дома.
     * @param unom УНОМ дома
     * @return данные жителей
     */
    public List<RestNotResettledPersonDto> fetchNotResettledPersonsByUnom(final String unom) {
        return personDocumentDao.fetchNotResettledPersonsByUnom(unom)
            .stream()
            .map(this::parseDocumentData)
            .map(person -> personMapper.toRestNotResettledPersonDto(person, this::retrieveRelocationStatusName))
            .collect(Collectors.toList());
    }

    private String retrieveRelocationStatusName(final String relocationStatusCode) {
        return dictionaryService.getNameByCode(UGD_SSR_RELOCATION_STATUS_DICTIONARY, relocationStatusCode);
    }

    public List<PersonDocument> fetchLiversByUnomAndFlatNumber(final String unom, final String flatNumber) {
        return personDocumentDao.fetchLiversByUnomAndFlatNumber(unom, flatNumber)
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    /**
     * Изменить доступность подписания договора с помощью УКЭП.
     * @param isEnabled доступно подписание договора с помощью УКЭП
     * @param affairIds список семей
     */
    public void setElectronicSignEnabled(final boolean isEnabled, final List<String> affairIds) {
        if (!CollectionUtils.isEmpty(affairIds)) {
            affairIds.forEach(affairId -> fetchByAffairId(affairId)
                .forEach(personDocument -> {
                    personDocument.getDocument().getPersonData().setElectronicSignEnabled(isEnabled);
                    updateDocument(personDocument, "setElectronicSignEnabled");
                })
            );
        }
    }

    public List<RestOfferLetterDto> fetchOfferLettersByUnomAndFlatNumber(
        final String unom, final String flatNumber, final String cadastralNumber
    ) {
        final List<RestOfferLetterDto> offerLetters = fetchOfferLettersByCadastralNumber(cadastralNumber);

        final List<RestOfferLetterDto> tradeAdditionOfferLetters =
            tradeAdditionDocumentService.fetchOfferLettersByUnomAndFlatNumber(unom, flatNumber);

        return Stream.concat(offerLetters.stream(), tradeAdditionOfferLetters.stream())
            .collect(Collectors.toList());
    }

    private List<RestOfferLetterDto> fetchOfferLettersByCadastralNumber(
        final String cadastralNumber
    ) {
        final List<Person> persons = personDocumentDao.fetchPersonsByOfferLetterCadastralNumber(cadastralNumber)
            .stream()
            .map(this::parseDocumentData)
            .map(PersonDocument::getDocument)
            .collect(Collectors.toList());

        return persons.stream()
            .map(this::combinePersonsForLetters)
            .flatMap(List::stream)
            .collect(Collectors.groupingBy(
                Pair::getKey,
                Collectors.mapping(Pair::getValue, Collectors.toList())
            ))
            .entrySet()
            .stream()
            .map(entry -> createOfferLetterDto(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
    }

    private RestOfferLetterDto createOfferLetterDto(final String letterId, final List<Person> persons) {
        final List<RestPersonDto> personDtos = persons.stream()
            .map(person -> RestPersonDto.builder()
                .personDocumentId(person.getDocumentID())
                .birthDate(person.getPersonData().getBirthDate())
                .fullName(person.getPersonData().getFIO())
                .build()
            )
            .collect(Collectors.toList());

        final PersonType.Agreements.Agreement agreement = persons.stream()
            .map(person -> PersonUtils.getLastAgreementByLetterId(person.getPersonData(), letterId))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findFirst()
            .orElse(null);

        final PersonType.Contracts.Contract contract = persons.stream()
            .map(person -> PersonUtils.getContractByLetterId(person.getPersonData(), letterId))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findFirst()
            .orElse(null);

        final OfferLetter offerLetter = persons.stream()
            .map(person -> PersonUtils.getOfferLetter(person.getPersonData(), letterId))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findFirst()
            .orElse(null);

        final String fileStoreId = PersonUtils.extractOfferLetterFileByType(offerLetter, PersonUtils.OFFER_FILE_TYPE)
            .map(File::getFileLink)
            .orElse(null);

        return RestOfferLetterDto.builder()
            .fileStoreId(fileStoreId)
            .letterId(letterId)
            .resettlementType(EQUIVALENT_RESETTLEMENT_TYPE)
            .persons(personDtos)
            .hasConsent(nonNull(agreement))
            .consentDate(nonNull(agreement) ? agreement.getDate() : null)
            .contractNumber(nonNull(contract) ? contract.getContractNum() : null)
            .contractSingDate(nonNull(contract) ? contract.getContractSignDate() : null)
            .build();
    }

    private List<Pair<String, Person>> combinePersonsForLetters(final Person person) {
        return person.getPersonData()
            .getOfferLetters()
            .getOfferLetter()
            .stream()
            .map(letter -> Pair.of(letter.getLetterId(), person))
            .collect(Collectors.toList());
    }

    public List<RestPersonBirthDateDto> fetchPersonsBirthDates(final List<String> personDocumentIds) {
        return personDocumentDao.fetchPersonsBirthDates(personDocumentIds)
            .stream()
            .map(personMapper::toRestPersonBirthDate)
            .collect(Collectors.toList());
    }

    @Async
    public void actualizeKeysIssueAndReleaseFlat(final LocalDate historicalResettlementDate) {
        log.info(
            "Start actualize keys issue and release flat (historicalResettlementDate = {})", historicalResettlementDate
        );
        final List<ResettlementRequestType> resettlementRequests = resettlementRequestDocumentService
            .findDocuments("{}")
            .stream()
            .filter(document -> nonNull(document.getDocument().getMain())
                && nonNull(document.getDocument().getMain().getStartResettlementDate()))
            .map(ResettlementRequestDocument::getDocument)
            .map(ResettlementRequest::getMain)
            .collect(Collectors.toList());

        try {
            log.info("Start actualize keys issue and release flat for old houses.");
            resettlementRequests.stream()
                .filter(main -> main.getStartResettlementDate().isBefore(historicalResettlementDate))
                .map(ResettlementRequestType::getHousesToSettle)
                .flatMap(List::stream)
                .map(HouseToSettle::getHousesToResettle)
                .flatMap(List::stream)
                .forEach(houseToResettle -> actualizeKeysIssueAndReleaseFlat(
                    houseToResettle, this::updateOldKeysIssueAndReleaseFlat
                ));
            log.info("Finish actualize keys issue and release flat for old houses.");
        } catch (Exception e) {
            log.error("Unable to actualize keys issue and release flat for old houses: {}", e.getMessage(), e);
        }

        try {
            log.info("Start actualize keys issue and release flat for new houses.");
            resettlementRequests.stream()
                .filter(main -> !main.getStartResettlementDate().isBefore(historicalResettlementDate))
                .map(ResettlementRequestType::getHousesToSettle)
                .flatMap(List::stream)
                .map(HouseToSettle::getHousesToResettle)
                .flatMap(List::stream)
                .forEach(houseToResettle -> actualizeKeysIssueAndReleaseFlat(
                    houseToResettle, this::updateNewKeysIssue
                ));
            log.info("Finish actualize keys issue and release flat for new houses.");
        } catch (Exception e) {
            log.error("Unable to actualize keys issue and release flat for new houses: {}", e.getMessage(), e);
        }
        log.info(
            "Finish actualize keys issue and release flat (historicalResettlementDate = {})", historicalResettlementDate
        );
    }

    private void actualizeKeysIssueAndReleaseFlat(
        final HouseToResettle houseToResettle, final Consumer<PersonDocument> updateKeysIssue
    ) {
        final List<PersonDocument> personDocuments = fetchByUnom(houseToResettle.getRealEstateUnom());

        if ("full".equals(houseToResettle.getResettlementBy())) {
            log.info("Start update persons (full resettlement), unom = {}", houseToResettle.getRealEstateUnom());
            personDocuments.forEach(personDocument -> {
                try {
                    updateKeysIssue.accept(personDocument);
                } catch (Exception e) {
                    log.error(
                        "Unable to update persons (full resettlement), unom = {}: {}",
                        houseToResettle.getRealEstateUnom(),
                        e.getMessage(),
                        e
                    );
                }
            });
            log.info("Finish update persons (full resettlement), unom = {}", houseToResettle.getRealEstateUnom());
        } else {
            houseToResettle.getFlatNumbers()
                .stream()
                .filter(Objects::nonNull)
                .forEach(flatNumber -> actualizeKeyIssueAndReleaseFlat(
                    personDocuments, flatNumber, updateKeysIssue, houseToResettle.getRealEstateUnom()
                ));
        }
    }

    private void actualizeKeyIssueAndReleaseFlat(
        final List<PersonDocument> personDocuments,
        final String flatNumber,
        final Consumer<PersonDocument> updateKeysIssue,
        final String realEstateUnom
    ) {
        personDocuments.forEach(personDocument -> {
            if (flatNumber.equals(PersonUtils.getFlatNumber(personDocument))) {
                log.info(
                    "Start update persons (part resettlement), unom = {}, flatNumber = {}",
                    realEstateUnom,
                    flatNumber
                );
                try {
                    updateKeysIssue.accept(personDocument);
                } catch (Exception e) {
                    log.error(
                        "Unable to update persons (part resettlement), unom = {}, flatNumber = {}: {}",
                        realEstateUnom,
                        flatNumber,
                        e.getMessage(),
                        e
                    );
                }
            }
        });
    }

    public void updatePersonFioInformation(final PersonDocument personDocument) {
        final PersonType person = personDocument.getDocument().getPersonData();

        if (StringUtils.isBlank(person.getLastName())) {
            person.setLastName(null);
        } else {
            person.setLastName(person.getLastName().trim());
        }
        if (StringUtils.isBlank(person.getFirstName())) {
            person.setFirstName(null);
        } else {
            person.setFirstName(person.getFirstName().trim());
        }
        if (StringUtils.isBlank(person.getMiddleName())) {
            person.setMiddleName(null);
        } else {
            person.setMiddleName(person.getMiddleName().trim());
        }
        if (StringUtils.isNotBlank(person.getFIO())
            && isNull(person.getLastName())
            && isNull(person.getFirstName())
            && isNull(person.getMiddleName())) {

            String fio = person.getFIO().trim();

            while (fio.contains("  ")) {
                fio = fio.replace("  ", " ");
            }

            person.setFIO(fio);

            final List<String> strings = Arrays.asList(fio.split(" "));

            if (strings.size() > 0) {
                person.setLastName(strings.get(0));
            }
            if (strings.size() > 1) {
                person.setFirstName(strings.get(1));
            }
            if (strings.size() > 2) {
                person.setMiddleName(String.join(" ", strings.subList(2, strings.size())));
            }
        }

        updateDocument(personDocument, "updatePersonFioInformation");
    }

    private void updateNewKeysIssue(final PersonDocument personDocument) {
        final PersonType personData = personDocument.getDocument().getPersonData();
        PersonUtils.getContractsStream(personData)
            .map(PersonType.Contracts.Contract::getContractSignDate)
            .filter(Objects::nonNull)
            .findFirst()
            .ifPresent(contractSignDate -> {
                if (Objects.nonNull(personData.getReleaseFlat())) {
                    log.debug("Update keys issue: personDocumentId = {}", personDocument.getId());
                    if (Objects.nonNull(personData.getKeysIssue())) {
                        personData.getKeysIssue().setActDate(contractSignDate);
                    } else {
                        final PersonType.KeysIssue keysIssue = new PersonType.KeysIssue();
                        keysIssue.setActDate(contractSignDate);
                        personData.setKeysIssue(keysIssue);
                    }
                    updateDocument(personDocument, "updateNewKeysIssue");
                }
            });
    }

    private void updateOldKeysIssueAndReleaseFlat(final PersonDocument personDocument) {
        final PersonType personData = personDocument.getDocument().getPersonData();
        PersonUtils.getContractsStream(personData)
            .map(PersonType.Contracts.Contract::getContractSignDate)
            .filter(Objects::nonNull)
            .findFirst()
            .ifPresent(contractSignDate -> {
                log.debug("Update keys issue and release flat: personDocumentId = {}", personDocument.getId());
                if (Objects.nonNull(personData.getReleaseFlat())) {
                    personData.getReleaseFlat().setActDate(contractSignDate);
                } else {
                    final PersonType.ReleaseFlat releaseFlat = new PersonType.ReleaseFlat();
                    releaseFlat.setActDate(contractSignDate);
                    personData.setReleaseFlat(releaseFlat);
                }

                if (Objects.nonNull(personData.getKeysIssue())) {
                    personData.getKeysIssue().setActDate(contractSignDate);
                } else {
                    final PersonType.KeysIssue keysIssue = new PersonType.KeysIssue();
                    keysIssue.setActDate(contractSignDate);
                    personData.setKeysIssue(keysIssue);
                }
                updateDocument(personDocument, "updateOldKeysIssueAndReleaseFlat");
            });
    }

    public boolean existsOnlyOnePerson(final String personId, final String affairId) {
        return fetchByPersonIdAndAffairId(personId, affairId).size() == 1;
    }
}
