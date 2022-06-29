package ru.croc.ugd.ssr.service.document;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static ru.croc.ugd.ssr.service.shipping.DefaultShippingService.WORKING_DAYS_BEFORE_SHIPPING;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.croc.ugd.ssr.db.dao.TradeAdditionDao;
import ru.croc.ugd.ssr.dto.apartmentinspection.RestPersonDto;
import ru.croc.ugd.ssr.dto.person.RestOfferLetterDto;
import ru.croc.ugd.ssr.dto.person.RestPersonBirthDateDto;
import ru.croc.ugd.ssr.dto.tradeaddition.TradeAdditionDto;
import ru.croc.ugd.ssr.mapper.TradeAdditionMapper;
import ru.croc.ugd.ssr.model.trade.TradeAdditionDocument;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.SsrBusinessCalendarService;
import ru.croc.ugd.ssr.trade.PersonInfoType;
import ru.croc.ugd.ssr.trade.TradeAddition;
import ru.croc.ugd.ssr.trade.TradeAdditionType;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.db.model.DocumentData;
import ru.reinform.cdp.document.model.DocumentType;
import ru.reinform.cdp.document.service.AbstractDocumentService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

/**
 * TradeAdditionDocumentService.
 */
@Service
@Slf4j
public class TradeAdditionDocumentService extends AbstractDocumentService<TradeAdditionDocument> {

    private static final String BUY_IN_RESETTLEMENT_TYPE = "Докупка";

    private final TradeAdditionDao tradeAdditionDao;
    private final SsrBusinessCalendarService ssrBusinessCalendarService;
    private final TradeAdditionMapper tradeAdditionMapper;
    private final PersonDocumentService personDocumentService;

    public TradeAdditionDocumentService(
        final TradeAdditionDao tradeAdditionDao,
        final SsrBusinessCalendarService ssrBusinessCalendarService,
        final TradeAdditionMapper tradeAdditionMapper,
        @Lazy final PersonDocumentService personDocumentService
    ) {
        this.tradeAdditionDao = tradeAdditionDao;
        this.ssrBusinessCalendarService = ssrBusinessCalendarService;
        this.tradeAdditionMapper = tradeAdditionMapper;
        this.personDocumentService = personDocumentService;
    }

    @Nonnull
    @Override
    public DocumentType<TradeAdditionDocument> getDocumentType() {
        return SsrDocumentTypes.TRADE_ADDITION;
    }

    /**
     * find trade addition by unique key.
     * @param uniqueRecordKey uniqueRecordKey
     * @return TradeAdditionDocument
     */
    public Optional<TradeAdditionDocument> fetchIndexedByUniqueKey(final String uniqueRecordKey) {
        return tradeAdditionDao.fetchIndexedByUniqueKey(uniqueRecordKey)
            .map(this::parseDocumentData);
    }

    /**
     * find confirmed trade additions.
     * @param batchId batchIdKey
     * @return list TradeAdditionDocument
     */
    public List<TradeAdditionDocument> getAllDocumentsByBatchId(final String batchId) {
        return tradeAdditionDao.getAllDocumentsIdsForBatch(batchId)
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    public List<TradeAdditionDocument> getAllDocumentsForStatusUpdateProcessing() {
        return tradeAdditionDao.getAllDocumentsForStatusUpdateProcessing()
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    /**
     * Reindex document by id.
     * @param document TradeAdditionDocument
     */
    public void indexDocument(final TradeAdditionDocument document) {
        reindexDocument(document, true);
    }

    /**
     * Reindex document by id.
     * @param document TradeAdditionDocument
     */
    public void unindexDocument(final TradeAdditionDocument document) {
        reindexDocument(document, false);
    }

    private void reindexDocument(final TradeAdditionDocument document, final boolean setIndexed) {
        Assert.notNull(document, "document is null");
        Assert.notNull(document.getId(), "documentId is null");

        document.getDocument().getTradeAdditionTypeData().setIndexed(setIndexed);
        this.updateDocument(document.getId(), document, false, true, null);
    }

    private TradeAdditionDocument parseDocumentData(@Nonnull DocumentData documentData) {
        return parseDocumentJson(documentData.getJsonData());
    }

    private TradeAdditionDocument parseDocumentJson(@Nonnull String json) {
        return jsonMapper.readObject(json, TradeAdditionDocument.class);
    }

    /**
     * getTradeAdditionDocumentFromType.
     * @param tradeAdditionType tradeAdditionType.
     * @return TradeAdditionDocument.
     */
    public TradeAdditionDocument getTradeAdditionDocumentFromType(final TradeAdditionType tradeAdditionType) {
        final TradeAdditionDocument tradeAdditionDocument = new TradeAdditionDocument();
        final TradeAddition tradeAddition = new TradeAddition();
        tradeAddition.setTradeAdditionTypeData(tradeAdditionType);
        tradeAdditionDocument.setDocument(tradeAddition);
        return tradeAdditionDocument;
    }

    /**
     * Все полученные заявления на докупку, зарегистрированное в Фонде реновации.
     *
     * @param personId ID жителя
     * @param affairId Идентификатор семьи
     * @return найденные документы
     */
    public List<TradeAdditionDocument> findReceivedApplications(final String personId, final String affairId) {
        if (isNull(personId) || isNull(affairId)) {
            return Collections.emptyList();
        }
        return tradeAdditionDao.findReceivedApplications(personId, affairId)
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());

    }

    /**
     * Есть заявления на докупку, зарегистрированное в Фонде реновации.
     *
     * @param personId ID жителя
     * @param affairId Идентификатор семьи
     * @return есть заявление
     */
    public boolean hasAnyReceivedApplication(final String personId, final String affairId) {
        return tradeAdditionDao.hasAnyReceivedApplication(personId, affairId);
    }

    /**
     * Есть заявления на компенсацию или "вне района", зарегистрированное в Фонде реновации.
     *
     * @param personId ID жителя
     * @param affairId Идентификатор семьи
     * @return есть заявление
     */
    public boolean hasCompensationsOrOutOfDistrict(final String personId, final String affairId) {
        return tradeAdditionDao.hasCompensationsOrOutOfDistrict(personId, affairId);
    }

    /**
     * Все полученные заявления на докупку с договорами, зарегистрированное в Фонде реновации,
     * за исключением докупки в течении 2 лет.
     *
     * @param personId ID жителя
     * @param affairId Идентификатор семьи
     * @return найденные документы
     */
    public List<TradeAdditionDocument> findReceivedApplicationsWithContract(String personId, String affairId) {
        return tradeAdditionDao.findReceivedApplicationsWithContract(personId, affairId)
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    public LocalDate getMoveDateFromTradeAddition(final TradeAdditionType tradeAdditionType) {
        return ofNullable(tradeAdditionType.getContractSignedDate())
            .map(contractSignDate -> ssrBusinessCalendarService.addWorkDays(
                contractSignDate, WORKING_DAYS_BEFORE_SHIPPING
            ))
            .orElse(null);
    }

    /**
     * Получить TRADE-ADDITION по unom расселяемого дома.
     *
     * @param unom unom расселяемого дома
     * @return список DocumentData
     */
    public List<TradeAdditionDocument> fetchByOldEstateUnom(String unom) {
        return tradeAdditionDao.fetchByOldEstateUnom(unom)
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    /**
     * Получить TRADE-ADDITION по unom расселяемого дома и ИД жителя.
     *
     * @param unom unom расселяемого дома
     * @param affairId affairId жителя
     * @param personDocumentId ID жителя
     * @return список TRADE-ADDITION
     */
    public List<TradeAdditionDocument> fetchByOldEstateUnomAndPersonId(
        String unom,
        String affairId,
        String personDocumentId
    ) {
        return tradeAdditionDao.fetchByOldEstateUnomAndPersonId(unom, affairId, personDocumentId)
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    public List<TradeAdditionDocument> fetchIndexedByPersonId(
        String personDocumentId
    ) {
        return tradeAdditionDao.fetchIndexedByPersonId(personDocumentId)
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    /**
     * Получить TRADE-ADDITION по unom заселяемого дома.
     *
     * @param unom unom заселяемого дома
     * @return список DocumentData
     */
    public List<TradeAdditionDocument> fetchByNewEstateUnom(String unom) {
        return tradeAdditionDao.fetchByNewEstateUnom(
                "[{\"unom\": \"" + unom + "\"}]"
            )
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    /**
     * Получить trade-addition по id.
     *
     * @param id trade addition document id
     * @return trade addition dto
     */
    public TradeAdditionDto fetchById(final String id) {
        final TradeAdditionDocument tradeAdditionDocument = fetchDocument(id);
        return tradeAdditionMapper.toTradeAdditionDto(tradeAdditionDocument);
    }

    public Optional<TradeAdditionDocument> fetchIndexedBySellId(final String sellId) {
        final List<DocumentData> tradeAdditionDocuments = tradeAdditionDao.fetchIndexedBySellId(sellId);

        if (tradeAdditionDocuments.size() > 1) {
            log.warn("More than one indexed trade addition document have been found: sellId = {}", sellId);
        }
        return tradeAdditionDocuments.stream()
            .map(this::parseDocumentData)
            .findFirst();
    }

    public List<TradeAdditionDocument> fetchByAffairId(final String affairId) {
        return tradeAdditionDao.fetchByAffairId(affairId)
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    public List<RestOfferLetterDto> fetchOfferLettersByUnomAndFlatNumber(
        final String unom, final String flatNumber
    ) {
        return tradeAdditionDao.fetchTradeAdditionsByLetterUnomAndLetterFlatNumber(unom, flatNumber)
            .stream()
            .map(this::parseDocumentData)
            .map(TradeAdditionDocument::getDocument)
            .map(TradeAddition::getTradeAdditionTypeData)
            .map(this::createOfferLetterDto)
            .collect(Collectors.toList());
    }

    private RestOfferLetterDto createOfferLetterDto(final TradeAdditionType tradeAddition) {
        final List<String> personDocumentIds = tradeAddition.getPersonsInfo().stream()
            .map(PersonInfoType::getPersonDocumentId)
            .collect(Collectors.toList());

        final List<RestPersonBirthDateDto> birthDateDtos =
            personDocumentService.fetchPersonsBirthDates(personDocumentIds);

        final List<RestPersonDto> personDtos = tradeAddition.getPersonsInfo().stream()
            .map(person -> createPersonDto(birthDateDtos, person))
            .collect(Collectors.toList());

        return RestOfferLetterDto.builder()
            .resettlementType(BUY_IN_RESETTLEMENT_TYPE)
            .letterId(null)
            .fileStoreId(tradeAddition.getApplicationFileId())
            .hasConsent(Objects.nonNull(tradeAddition.getAgreementDate()))
            .consentDate(tradeAddition.getAgreementDate())
            .contractNumber(tradeAddition.getContractNumber())
            .contractSingDate(tradeAddition.getContractSignedDate())
            .persons(personDtos)
            .build();
    }

    private RestPersonDto createPersonDto(
        final List<RestPersonBirthDateDto> birthDateDtos, final PersonInfoType person
    ) {
        final LocalDate birthDate = birthDateDtos.stream()
            .filter(dto -> dto.getPersonDocumentId().equals(person.getPersonDocumentId()))
            .map(RestPersonBirthDateDto::getBirthDate)
            .findFirst()
            .orElse(null);
        return RestPersonDto.builder()
            .fullName(person.getPersonFio())
            .birthDate(birthDate)
            .build();
    }
}
