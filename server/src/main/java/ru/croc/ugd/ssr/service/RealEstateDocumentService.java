package ru.croc.ugd.ssr.service;

import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

import com.github.fge.jsonpatch.JsonPatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.croc.ugd.ssr.FlatType;
import ru.croc.ugd.ssr.NsiType;
import ru.croc.ugd.ssr.RealEstate;
import ru.croc.ugd.ssr.RealEstateDataType;
import ru.croc.ugd.ssr.RealEstateDataType.Flats;
import ru.croc.ugd.ssr.config.CacheConfig;
import ru.croc.ugd.ssr.db.dao.RealEstateDocumentDao;
import ru.croc.ugd.ssr.db.projection.BuildingData;
import ru.croc.ugd.ssr.db.projection.ResettledHouseProjection;
import ru.croc.ugd.ssr.dto.realestate.FlatDto;
import ru.croc.ugd.ssr.egrn.Dict;
import ru.croc.ugd.ssr.egrn.EgrnFlatRequest;
import ru.croc.ugd.ssr.egrn.EgrnFlatRequestData;
import ru.croc.ugd.ssr.egrn.ExtractAboutPropertyRoom;
import ru.croc.ugd.ssr.egrn.FlatEgrnResponse;
import ru.croc.ugd.ssr.egrn.FlatRequestCriteria;
import ru.croc.ugd.ssr.egrn.ParamsRoomBase;
import ru.croc.ugd.ssr.egrn.RestrictRecordsBaseParams;
import ru.croc.ugd.ssr.egrn.RightHoldersOut;
import ru.croc.ugd.ssr.egrn.RightRecordsAboutProperty;
import ru.croc.ugd.ssr.egrn.RoomRecordAboutProperty;
import ru.croc.ugd.ssr.enums.ResettlementStatus;
import ru.croc.ugd.ssr.helper.SortHelper;
import ru.croc.ugd.ssr.integration.service.mapper.FlatDetailsMapper;
import ru.croc.ugd.ssr.integration.service.wsdl.flat.FetchFlatDetailsSoapService;
import ru.croc.ugd.ssr.integration.service.wsdl.flat.model.FlatDetails;
import ru.croc.ugd.ssr.model.RealEstateDocument;
import ru.croc.ugd.ssr.model.egrn.EgrnFlatRequestDocument;
import ru.croc.ugd.ssr.service.document.EgrnFlatRequestDocumentService;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.croc.ugd.ssr.utils.RealEstateUtils;
import ru.croc.ugd.ssr.utils.RegularExpressionUtils;
import ru.croc.ugd.ssr.utils.StreamUtils;
import ru.reinform.cdp.db.model.DocumentData;
import ru.reinform.cdp.document.model.DocumentType;
import ru.reinform.cdp.utils.mapper.JsonMapper;
import ru.reinform.cdp.utils.rest.utils.SendRestUtils;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Сервис по работе с сущностью "расселяемый дом".
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RealEstateDocumentService extends DocumentWithFolder<RealEstateDocument> {
    private static final String NON_LIVING_PURPOSE_CODE = "206001000000";

    private final RealEstateDocumentDao realEstateDocumentDao;

    private final JsonMapper jsonMapper;

    private final SendRestUtils sendRestUtils;

    private final CachingService cachingService;

    private final FetchFlatDetailsSoapService fetchFlatDetailsSoapService;

    private final FlatDetailsMapper flatDetailsMapper;

    private final EgrnFlatRequestDocumentService egrnFlatRequestDocumentService;

    @Value("${mdm.url}")
    private String mdmUrl;

    // размер страницы для чтения документов
    public static final int PAGE_SIZE = 100;

    public NsiType parseNsiTypeJson(@Nonnull String json) {
        if (StringUtils.isBlank(json)) {
            return new NsiType();
        }
        return jsonMapper.readObject(json, NsiType.class);
    }

    @Nonnull
    @Override
    public RealEstateDocument updateDocument(@Nonnull String id,
                                             @Nonnull RealEstateDocument newDocument,
                                             boolean skipUnchanged,
                                             boolean flagReindex,
                                             @Nullable String notes) {
        cachingService.evictRealEstateCache();
        return super.updateDocument(id, newDocument, skipUnchanged, flagReindex, notes);
    }

    @Nonnull
    @Override
    public RealEstateDocument patchDocument(@Nonnull String id,
                                            @Nonnull JsonPatch jsonPatch,
                                            boolean skipUnchanged,
                                            boolean flagReindex,
                                            @Nullable String notes) {
        cachingService.evictRealEstateCache();
        return super.patchDocument(id, jsonPatch, skipUnchanged, flagReindex, notes);
    }

    @Nonnull
    @Override
    public RealEstateDocument createDocument(@Nonnull RealEstateDocument document,
                                             boolean flagReindex,
                                             @Nullable String notes) {
        cachingService.evictRealEstateCache();
        return super.createDocument(document, flagReindex, notes);
    }

    @Nonnull
    @Override
    public DocumentType<RealEstateDocument> getDocumentType() {
        return SsrDocumentTypes.REAL_ESTATE;
    }

    /**
     * Удаление всех документов REAL-ESTATE (ОСТОРОЖНО!).
     */
    public void deleteAll() {
        realEstateDocumentDao.deleteAll();
    }

    /**
     * Возвращает ОН по UNOM.
     *
     * @param unom - UNOM
     * @return Объект недвижимости (ОН)
     */
    public RealEstateDocument fetchDocumentByUnom(String unom) {
        return ofNullable(unom)
            .map(realEstateDocumentDao::fetchByUnom)
            .map(this::parseDocumentData)
            .orElse(null);
    }

    /**
     * Возвращает UNOM дома по flatID.
     *
     * @param flatId - идентификатор квартиры
     * @return UNOM ОН, к которому относится квартира
     */
    public String getUnomByFlatId(String flatId) {
        return realEstateDocumentDao.getUnomByFlatId("[{\"flatID\": \"" + flatId + "\"}]");
    }

    /**
     * Возвращает UNOM и адрес всех ОН.
     *
     * @return список JSON-объектов, содержащих UNOM и адрес ОН
     */
    public String getUnomAndAddressRealEstates() {
        return new JSONArray(realEstateDocumentDao.getUnomAndAddressRealEstates()).toString();
    }

    /**
     * Возвращает проекцию для расселяемых домов.
     *
     * @return список проекций для расселяемых домов
     */
    public List<BuildingData> getBuildingDataList() {
        final List<BuildingData> buildingData =
            realEstateDocumentDao.getBuildingDataList();
        return buildingData
            .stream()
            .filter(data -> StringUtils.isNoneBlank(data.getUnom()))
            .filter(StreamUtils.distinctByKey(BuildingData::getUnom))
            .sorted(Comparator.comparing(BuildingData::getAddress, nullsLast(naturalOrder())))
            .collect(Collectors.toList());
    }

    /**
     * Unom to address mapping.
     *
     * @return Map
     */
    @Cacheable(value = CacheConfig.REAL_ESTATE)
    public Map<String, String> getUnomToAddressMapping() {
        return getBuildingDataList()
            .stream()
            .collect(Collectors
                .toMap(
                    BuildingData::getUnom,
                    this::buildAddress,
                    (u1, u2) -> u1
                )
            );
    }

    private String buildAddress(final BuildingData buildingData) {
        return ofNullable(buildingData.getAddress())
            .orElseGet(() -> RealEstateUtils
                .buildAddressFromElements(
                    parseNsiTypeJson(buildingData.getTownP4Json()),
                    parseNsiTypeJson(buildingData.getSettlementP3Json()),
                    parseNsiTypeJson(buildingData.getLocalityP6Json()),
                    parseNsiTypeJson(buildingData.getElementP7Json()),
                    parseNsiTypeJson(buildingData.getHouseL1TypeJson()),
                    buildingData.getHouseL1Value(),
                    parseNsiTypeJson(buildingData.getCorpL2TypeJson()),
                    buildingData.getCorpL2Value(),
                    parseNsiTypeJson(buildingData.getBuildingL3TypeJson()),
                    buildingData.getBuildingL3Value()
                )
            );
    }

    /**
     * Получить постранично ОН для расселения (ОН целиком).
     *
     * @param pageNum       номер страницы
     * @param pageSize      размер страницы
     * @param filter        строка фильтра
     * @param orderByString строка сортировки
     * @return json строка со списком ОН слоя расселения
     */
    public String fetchFullRealEstatePage(int pageNum, int pageSize, final String filter, String orderByString) {
        final String preparedFilter = ofNullable(filter)
            .map(RegularExpressionUtils::escapePostgreRegexCharacters)
            .map(f -> "%" + f + "%")
            .orElse("%%");

        JSONObject result = new JSONObject();
        Pageable pageable = orderByString == null
            ? PageRequest.of(pageNum, pageSize)
            : PageRequest.of(pageNum, pageSize, SortHelper.getSortByOrderString(orderByString));
        Page<DocumentData> page = realEstateDocumentDao.findFullRealEstatesByUnomOrAddress(preparedFilter, pageable);
        List<RealEstateDocument> data =
            page.getContent().stream().map(this::parseDocumentData).collect(Collectors.toList());
        result.put("data", data);
        long count = page.getTotalElements();
        JSONObject hints = new JSONObject();
        hints.put("total", count);
        result.put("hints", hints);
        return result.toString();
    }

    /**
     * Получить постранично ОН для расселения.
     *
     * @param pageNum       номер страницы
     * @param pageSize      размер страницы
     * @param filter        строка фильтра
     * @param orderByString строка сортировки
     * @return json строка со списком ОН слоя расселения
     */
    public String fetchRealEstatePage(int pageNum, int pageSize, final String filter, String orderByString) {
        final String preparedFilter = ofNullable(filter)
            .map(RegularExpressionUtils::escapePostgreRegexCharacters)
            .map(f -> "%" + f + "%")
            .map(f -> f.replaceAll("[е|ё]", "[е|ё]"))
            .orElse("%%");

        JSONObject result = new JSONObject();
        Pageable pageable = orderByString == null
            ? PageRequest.of(pageNum, pageSize)
            : PageRequest.of(pageNum, pageSize, SortHelper.getSortByOrderString(orderByString));
        Page<Map<String, Object>> page = realEstateDocumentDao.findRealEstatesByUnomOrAddress(preparedFilter, pageable);
        List<Map<String, Object>> data = page.getContent();
        result.put("data", data);
        long count = page.getTotalElements();
        JSONObject hints = new JSONObject();
        hints.put("total", count);
        result.put("hints", hints);
        return result.toString();
    }

    /**
     * Возвращает RealEstate по eno.
     *
     * @param eno - идентификатор квартиры
     * @return ОН, к которому относится eno
     */
    public RealEstateDocument getRealEstatesByEno(String eno) {
        List<String> ids = realEstateDocumentDao.fetchRealEstatesByEno("[{\"IntegrationNumber\": \"" + eno + "\"}]");
        if (ids.size() > 0) {
            return this.fetchDocument(ids.get(0));
        }
        return null;
    }

    /**
     * Получает ОН, содержащий квартиру с flatId.
     *
     * @param flatId идентификатор квартиры
     * @return ОН
     */
    public RealEstateDocument fetchByFlatId(String flatId) {
        DocumentData documentData = realEstateDocumentDao.fetchByFlatId(flatId);
        if (documentData != null) {
            return parseDocumentData(documentData);
        }
        return null;
    }

    /**
     * Получает id и адрес ОН, содержащий квартиру с flatId.
     *
     * @param flatId идентификатор квартиры
     * @return id и адрес ОН
     */
    public String getIdAndAddressByFlatId(String flatId) {
        return new JSONObject(realEstateDocumentDao.getIdAndAddressByFlatId(flatId)).toString();
    }

    /**
     * Получает список не целиком обогащенных ОН.
     *
     * @param pageNum  номер страницы
     * @param pageSize размер страницы
     * @return список ОН
     */
    public List<RealEstateDocument> fetchNotUpdated(int pageNum, int pageSize) {
        return realEstateDocumentDao.fetchNotUpdated(PageRequest.of(pageNum, pageSize))
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    /**
     * Получает список ОН, обогащенных и не целиком обогащенных из ЕЖД.
     *
     * @param pageNum  номер страницы
     * @param pageSize размер страницы
     * @return список ОН
     */
    public List<RealEstateDocument> fetchUpdatedAndProgress(int pageNum, int pageSize) {
        return realEstateDocumentDao.fetchUpdatedAndProgress(PageRequest.of(pageNum, pageSize))
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    /**
     * Проставляет ОН код информационного центра по идентификаторам.
     *
     * @param realEstateIds          список идентификаторов ОН
     * @param informationServiceCode код информационного центра
     */
    public void updateInformationServiceCodeOfRealEstates(List<String> realEstateIds, String informationServiceCode) {
        if (informationServiceCode == null || informationServiceCode.isEmpty()) {
            return;
        }
        for (RealEstateDocument realEstateDocument : fetchDocuments(realEstateIds)) {
            RealEstateDataType realEstateData = realEstateDocument.getDocument().getRealEstateData();
            realEstateData.setInformationCenterCode(informationServiceCode);
            updateDocument(realEstateDocument.getId(), realEstateDocument, true, true, "set informationServiceCode");
        }
    }

    /**
     * Возвращает объект информаицонного центра по коду (из справочника).
     *
     * @param code код ЦИП
     * @return ЦИП json
     */
    public JSONObject getInformationCenterByCode(String code) {
        String json =
            "{\n" + "  \"nickAttr\": \"code\",\n" + "  \"values\": [\n" + "    \"" + code + "\"\n" + "  ]\n" + "}";
        String result = sendRestUtils.sendJsonRequest(mdmUrl + "/api/v2/dictionary/dto/ugd_ssr_informationCenters",
            HttpMethod.POST,
            json,
            String.class);
        JSONArray jsonArray = new JSONArray(result);
        if (jsonArray.length() > 0) {
            return jsonArray.getJSONObject(0);
        }
        return null;
    }

    /**
     * Получить расселяемые дома по заселяемому.
     *
     * @param ccoId ид окса.
     * @return список расселяемых домов.
     */
    public List<RealEstateDocument> fetchDocumentByCcoId(String ccoId) {
        return realEstateDocumentDao.fetchByByCcoId(ccoId)
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    /**
     * Возвращает UNOM всех ОН.
     *
     * @return список JSON-объектов, содержащих UNOM и адрес ОН
     */
    public List<String> getUnomRealEstates() {
        return realEstateDocumentDao.getUnomRealEstates();
    }

    /**
     * Получить УНОМ по ид дома.
     *
     * @param realEstateId идентификатор ОН
     * @return УНОМ
     */
    public String getUnomByRealEstateId(String realEstateId) {
        return realEstateDocumentDao.getUnomByRealEstateId(realEstateId);
    }

    /**
     * Получить список квартир по уном.
     *
     * @param unom уном
     * @return Список RealEstateDto
     */
    public List<FlatDto> getFlatsByUnom(final String unom) {
        return realEstateDocumentDao.getFlatsByUnom(unom)
            .stream()
            .map(flatProjection -> FlatDto
                .builder()
                .flatId(flatProjection.getFlatId())
                .flatNum(flatProjection.getFlatNum())
                .build())
            .collect(Collectors.toList());
    }

    @Async
    public void updateAllRealEstateMissingFlatsWithCatalogData() {
        final List<String> missingFlatsUnoms = realEstateDocumentDao.fetchRealEstateUnomsWithMissingFlats();
        final List<String> missingSquareOrEntranceFlatsUnoms = realEstateDocumentDao
            .fetchUnomsWhereFlatsThatMissSquareOrEntrance();
        final Set<String> unomsToProcess = new HashSet<>();
        unomsToProcess.addAll(missingFlatsUnoms);
        unomsToProcess.addAll(missingSquareOrEntranceFlatsUnoms);
        log.info("updateAllRealEstateMissingFlatsWithCatalogData: total UNOMs to process: " + unomsToProcess.size());
        final AtomicInteger currentAmountOfProcessedAtom = new AtomicInteger(0);
        unomsToProcess
            .forEach(unom -> {
                int currentAmountOfProcessed = currentAmountOfProcessedAtom.getAndIncrement();
                updateRealEstateFlatsWithCatalogData(unom);
                if (currentAmountOfProcessed % 10 == 0) {
                    log.info("updateAllRealEstateMissingFlatsWithCatalogData: UNOMs processed: "
                        + currentAmountOfProcessed);
                }
            });
    }

    @Async
    public void updateRealEstateFlatsWithCatalogData(final String unom) {
        final List<FlatDetails> flatDetails = fetchFlatDetailsSoapService.getFlatDetailsByUnom(unom);
        updateRealEstateByFlatDetails(unom, flatDetails);
    }

    public void updateRealEstateByFlatDetails(final String unom, final List<FlatDetails> flatDetails) {
        final RealEstateDocument realEstateDocument = fetchDocumentByUnom(unom);
        if (realEstateDocument == null) {
            log.info("updateRealEstateByFlatDetails: real estate not found for UNOM: " + unom);
            return;
        }
        final RealEstateDataType realEstateDataType = realEstateDocument.getDocument()
            .getRealEstateData();
        if (realEstateDataType.getFlats() == null) {
            realEstateDataType.setFlats(new Flats());
        }
        final List<FlatType> existingFlats = realEstateDataType.getFlats().getFlat();

        if (CollectionUtils.isEmpty(flatDetails)) {
            populateFlatCalculatableDataIfMissing(existingFlats, realEstateDataType);
            log.info("updateRealEstateByFlatDetails: flatDetails not found for UNOM: " + unom);
            return;
        }
        normalizeFlatDetailsData(flatDetails);
        updateRealEstateFlats(existingFlats, flatDetails);
        populateFlatCalculatableDataIfMissing(existingFlats, realEstateDataType);
        updateDocument(realEstateDocument.getId(),
            realEstateDocument,
            true,
            true,
            "Обновление из каталога 28609");
        log.info("updateRealEstateByFlatDetails: real estate updated for UNOM: " + unom);
    }

    private void populateFlatCalculatableDataIfMissing(
        final List<FlatType> flats,
        final RealEstateDataType realEstateDataType
    ) {
        flats.forEach(flatType -> {
            if (StringUtils.isEmpty(flatType.getAddress())) {
                flatType.setAddress(RealEstateUtils.getFlatAddress(realEstateDataType, flatType));
            }
            if (StringUtils.isEmpty(flatType.getResettlementStatus())) {
                flatType.setResettlementStatus("0");
            }
        });
    }

    private void updateRealEstateFlats(final List<FlatType> existingFlats, final List<FlatDetails> newFlatDetails) {
        final Set<String> flatNumberDuplicates = findDuplicateFlatNumber(newFlatDetails);
        newFlatDetails
            .stream()
            .filter(flatDetails -> !flatNumberDuplicates.contains(flatDetails.getFlatNumber()))
            .forEach(newFlatDetail -> mapNewFlatDetail(existingFlats, newFlatDetail));
    }

    private void normalizeFlatDetailsData(final List<FlatDetails> flatDetails) {
        flatDetails
            .forEach(flatDetail -> {
                trimValues(flatDetail);
                nullifyIfNil(flatDetail);
            });
    }

    private void trimValues(final FlatDetails flatDetail) {
        flatDetail.setAddressIdentification(StringUtils.trim(flatDetail.getAddressIdentification()));
        flatDetail.setFullSquare(StringUtils.trim(flatDetail.getFullSquare()));
        flatDetail.setCalculatedSquare(StringUtils.trim(flatDetail.getCalculatedSquare()));
        flatDetail.setFloor(StringUtils.trim(flatDetail.getFloor()));
        flatDetail.setLivingSquare(StringUtils.trim(flatDetail.getLivingSquare()));
        flatDetail.setUniqueFlatNumber(StringUtils.trim(flatDetail.getUniqueFlatNumber()));
        flatDetail.setUnom(StringUtils.trim(flatDetail.getUnom()));
        flatDetail.setSectionNumber(StringUtils.trim(flatDetail.getSectionNumber()));
        flatDetail.setAddressIdentification(StringUtils.trim(flatDetail.getAddressIdentification()));
        flatDetail.setFlatTypeExternalRefId(StringUtils.trim(flatDetail.getFlatTypeExternalRefId()));
    }

    private void nullifyIfNil(final FlatDetails flatDetail) {
        if (StringUtils.equals(flatDetail.getFullSquare(), BigInteger.ZERO.toString())) {
            flatDetail.setFullSquare(null);
        }
        if (StringUtils.equals(flatDetail.getAmountOfLivingRooms(), BigInteger.ZERO.toString())) {
            flatDetail.setAmountOfLivingRooms(null);
        }
        if (StringUtils.equals(flatDetail.getCalculatedSquare(), BigInteger.ZERO.toString())) {
            flatDetail.setCalculatedSquare(null);
        }
        if (StringUtils.equals(flatDetail.getFloor(), BigInteger.ZERO.toString())) {
            flatDetail.setFloor(null);
        }
        if (StringUtils.equals(flatDetail.getLivingSquare(), BigInteger.ZERO.toString())) {
            flatDetail.setLivingSquare(null);
        }
        if (StringUtils.equals(flatDetail.getUniqueFlatNumber(), BigInteger.ZERO.toString())) {
            flatDetail.setUniqueFlatNumber(null);
        }
        if (StringUtils.equals(flatDetail.getUnom(), BigInteger.ZERO.toString())) {
            flatDetail.setUnom(null);
        }
        if (StringUtils.equals(flatDetail.getSectionNumber(), BigInteger.ZERO.toString())) {
            flatDetail.setSectionNumber(null);
        }
        if (StringUtils.equals(flatDetail.getAddressIdentification(), BigInteger.ZERO.toString())) {
            flatDetail.setAddressIdentification(null);
        }
        if (StringUtils.equals(flatDetail.getFlatTypeExternalRefId(), BigInteger.ZERO.toString())) {
            flatDetail.setFlatTypeExternalRefId(null);
        }
    }

    private void mapNewFlatDetail(final List<FlatType> existingFlats, final FlatDetails newFlatDetail) {
        final List<FlatType> matchedExistingFlatTypes = getExistingFlats(newFlatDetail.getFlatNumber(), existingFlats);
        if (CollectionUtils.isEmpty(matchedExistingFlatTypes)) {
            existingFlats.add(flatDetailsMapper.toFlatTypeWithDefaultValue(newFlatDetail));
        } else {
            matchedExistingFlatTypes.forEach(flatType -> flatDetailsMapper
                .toFlatTypeWithDefaultValue(flatType, newFlatDetail));
        }
    }

    private Set<String> findDuplicateFlatNumber(final List<FlatDetails> list) {
        final Set<String> flatNumber = new HashSet<>();
        return list.stream()
            .map(FlatDetails::getFlatNumber)
            .filter(n -> !flatNumber.add(n)) // Set.add() returns false if the element was already in the set.
            .collect(Collectors.toSet());

    }

    private List<FlatType> getExistingFlats(final String newFlatNumber, final List<FlatType> existingFlats) {
        return existingFlats
            .stream()
            .filter(Objects::nonNull)
            .filter(flatType -> Objects.equals(flatType.getFlatNumber(), newFlatNumber)
                || Objects.equals(flatType.getApartmentL4VALUE(), newFlatNumber))
            .collect(Collectors.toList());
    }

    /**
     * Add flat to real estate.
     *
     * @param unom       unom
     * @param flatNumber flatNumber
     * @param note       note
     * @return result of addition
     */
    public Optional<FlatType> addFlatToRealEstateByUnom(final String unom, final String flatNumber, final String note) {
        final FlatType flat = createFlat(flatNumber, note);
        final RealEstateDocument realEstateDocument = fetchDocumentByUnom(unom);

        final Optional<RealEstateDataType> optionalRealEstateData = ofNullable(realEstateDocument)
            .map(RealEstateDocument::getDocument)
            .map(RealEstate::getRealEstateData);

        if (!optionalRealEstateData.isPresent()) {
            return Optional.empty();
        }

        final RealEstateDataType realEstateData = optionalRealEstateData.get();
        final Flats flats = ofNullable(realEstateData.getFlats())
            .orElseGet(Flats::new);
        flats.getFlat().add(flat);
        realEstateData.setFlats(flats);

        updateDocument(realEstateDocument.getId(), realEstateDocument, true, true, null);

        log.info(
            "Квартира номер: {}, flatId: {} добавлена для дома с уномом: {}",
            flat.getFlatNumber(),
            flat.getFlatID(),
            unom
        );

        return Optional.of(flat);
    }

    private FlatType createFlat(final String flatNumber, final String note) {
        final FlatType flat = new FlatType();

        flat.setFlatID(UUID.randomUUID().toString());
        flat.setFlatNumber(flatNumber);
        flat.setApartmentL4VALUE(flatNumber);
        flat.setNote(note);

        return flat;
    }

    /**
     * Получить ОН по адресу (подстрока).
     *
     * @param addrSubstr адрес (подстрока)
     * @param top        сколько селектить записей
     * @return список ОН
     */
    public List<RealEstateDocument> getRealEstatesBySubstrAddress(String addrSubstr, Integer top) {
        addrSubstr = "%" + addrSubstr + "%";
        addrSubstr = addrSubstr.replaceAll("[е|ё]", "[е|ё]");
        return realEstateDocumentDao.getRealEstatesBySubstrAddress(addrSubstr, top)
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    /**
     * Возвращает ОН по UNOMам.
     *
     * @param unoms уномы через запятую
     * @return список ОН
     */
    public List<RealEstateDocument> fetchDocumentsByUnoms(String unoms) {
        return realEstateDocumentDao.fetchDocumentsByUnoms(unoms)
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    /**
     * Получить список id ОН, не целиком обогащенных из ЕЖД.
     *
     * @return список id ОН
     */
    public List<String> fetchNotUpdatedIds() {
        return realEstateDocumentDao.fetchNotUpdatedIds();
    }

    public void fillResettlementStatus(final RealEstateDocument realEstateDocument) {
        final RealEstateDataType realEstateDataType = realEstateDocument.getDocument().getRealEstateData();
        final ResettlementStatus resettlementStatus = ResettlementStatus.of(realEstateDataType);

        realEstateDataType.setResettlementStatus(resettlementStatus.getCode());
    }

    public String fetchFlatIdByUnomAndFlatNumber(final String unom, final String flatNumber) {
        return realEstateDocumentDao.fetchFlatIdByUnomAndFlatNumber(unom, flatNumber);
    }

    /**
     * Получение отселяемых домов по documentId.
     * @param documentIds - id документов.
     * @return отселяемый дом.
     */
    public  List<ResettledHouseProjection> getResettledHousesByIds(final List<String> documentIds) {
        return realEstateDocumentDao.getResettledHousesesByIds(documentIds);
    }

    public void updateByEgrnResponse(final EgrnFlatRequestDocument egrnFlatRequestDocument) {
        ofNullable(egrnFlatRequestDocument.getDocument().getEgrnFlatRequestData())
            .map(EgrnFlatRequestData::getRequestCriteria)
            .map(FlatRequestCriteria::getRealEstateDocumentId)
            .flatMap(this::fetchById)
            .ifPresent(realEstateDocument ->
                updateByEgrnResponse(realEstateDocument, egrnFlatRequestDocument)
            );
    }

    private void updateByEgrnResponse(
        final RealEstateDocument realEstateDocument, final EgrnFlatRequestDocument egrnFlatRequestDocument
    ) {
        updateFlatWithCadastralNumber(realEstateDocument, egrnFlatRequestDocument);
        updateNonResidentialSpaceStatus(realEstateDocument, egrnFlatRequestDocument);
        updateDocument(realEstateDocument, "updateByEgrnResponse");
    }

    private void updateFlatWithCadastralNumber(
        final RealEstateDocument realEstateDocument, final EgrnFlatRequestDocument egrnFlatRequestDocument
    ) {
        final String flatCadastralNumber = retrieveFlatCadastralNumber(egrnFlatRequestDocument);
        final String flatNumber = retrieveFlatNumber(egrnFlatRequestDocument);

        final List<FlatType> flats = ofNullable(realEstateDocument.getDocument().getRealEstateData())
            .map(RealEstateDataType::getFlats)
            .map(Flats::getFlat)
            .orElseGet(Collections::emptyList);

        flats.stream()
            .filter(flatType -> nonNull(flatType.getFlatNumber()))
            .filter(flatType -> flatType.getFlatNumber().equals(flatNumber))
            .findFirst()
            .ifPresent(flat -> {
                flat.setCadastralNum(flatCadastralNumber);
                flat.setCadNum(flatCadastralNumber);
            });
    }

    private String retrieveFlatCadastralNumber(final EgrnFlatRequestDocument egrnFlatRequestDocument) {
        return ofNullable(egrnFlatRequestDocument.getDocument().getEgrnFlatRequestData())
            .map(EgrnFlatRequestData::getRequestCriteria)
            .map(FlatRequestCriteria::getCadastralNumber)
            .orElse(null);
    }

    private String retrieveFlatNumber(final EgrnFlatRequestDocument egrnFlatRequestDocument) {
        return ofNullable(egrnFlatRequestDocument.getDocument().getEgrnFlatRequestData())
            .map(EgrnFlatRequestData::getRequestCriteria)
            .map(FlatRequestCriteria::getFlatNumber)
            .orElse(null);
    }

    private void updateNonResidentialSpaceStatus(
        final RealEstateDocument realEstateDocument, final EgrnFlatRequestDocument egrnFlatRequestDocument
    ) {
        final ExtractAboutPropertyRoom extractAboutPropertyRoom = ofNullable(egrnFlatRequestDocument)
            .map(EgrnFlatRequestDocument::getDocument)
            .map(EgrnFlatRequest::getEgrnFlatRequestData)
            .map(EgrnFlatRequestData::getEgrnResponse)
            .map(FlatEgrnResponse::getExtractAboutPropertyRoom)
            .orElse(null);
        if (!hasNonLivingPurpose(extractAboutPropertyRoom)) {
            return;
        }
        final RealEstateDataType realEstateData = realEstateDocument.getDocument().getRealEstateData();
        realEstateData.setHasNonResidentialSpaces(true);

        realEstateData.setWithdrawnNonResidentialSpaces(
            isWithdrawn(extractAboutPropertyRoom)
                && !hasRestrict(extractAboutPropertyRoom)
                && isNonResidentialWithdrawn(realEstateData.getUNOM().toString())
        );

        fillResettlementStatus(realEstateDocument);
    }

    private boolean isNonResidentialWithdrawn(final String unom) {
        return egrnFlatRequestDocumentService.fetchLastNonResidentialByUnom(unom)
            .stream()
            .map(EgrnFlatRequestDocument::getDocument)
            .map(EgrnFlatRequest::getEgrnFlatRequestData)
            .map(EgrnFlatRequestData::getEgrnResponse)
            .filter(Objects::nonNull)
            .map(FlatEgrnResponse::getExtractAboutPropertyRoom)
            .filter(Objects::nonNull)
            .noneMatch(room -> !isWithdrawn(room) || hasRestrict(room));
    }

    private boolean isWithdrawn(final ExtractAboutPropertyRoom extractAboutPropertyRoom) {
        return ofNullable(extractAboutPropertyRoom)
            .map(ExtractAboutPropertyRoom::getRightRecords)
            .map(RightRecordsAboutProperty::getRightRecord)
            .map(Collection::stream)
            .orElse(Stream.empty())
            .map(RightRecordsAboutProperty.RightRecord::getRightHolders)
            .filter(Objects::nonNull)
            .map(RightHoldersOut::getRightHolder)
            .flatMap(Collection::stream)
            .noneMatch(holder -> nonNull(holder.getIndividual())
                || nonNull(holder.getLegalEntity())
                || nonNull(holder.getAnother()));
    }

    private boolean hasNonLivingPurpose(final ExtractAboutPropertyRoom extractAboutPropertyRoom) {
        return ofNullable(extractAboutPropertyRoom)
            .map(ExtractAboutPropertyRoom::getRoomRecord)
            .map(RoomRecordAboutProperty::getParams)
            .map(ParamsRoomBase::getPurpose)
            .map(Dict::getCode)
            .filter(NON_LIVING_PURPOSE_CODE::equals)
            .isPresent();
    }

    private boolean hasRestrict(final ExtractAboutPropertyRoom extractAboutPropertyRoom) {
        //TODO: Alexander: возможно нужно отличать коды документов (договор аренды), но примеров нет
        return ofNullable(extractAboutPropertyRoom)
            .map(ExtractAboutPropertyRoom::getRestrictRecords)
            .map(RestrictRecordsBaseParams::getRestrictRecord)
            .map(Collection::stream)
            .orElse(Stream.empty())
            .anyMatch(Objects::nonNull);
    }

}
