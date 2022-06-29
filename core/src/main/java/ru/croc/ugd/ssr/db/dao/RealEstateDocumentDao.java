package ru.croc.ugd.ssr.db.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import ru.croc.ugd.ssr.db.projection.BuildingData;
import ru.croc.ugd.ssr.db.projection.FlatProjection;
import ru.croc.ugd.ssr.db.projection.ResettledHouseProjection;
import ru.reinform.cdp.db.model.DocumentData;

import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;

/**
 * DAO по работе с ОН.
 */
public interface RealEstateDocumentDao extends Repository<DocumentData, String> {

    /**
     * Удалить все документы типа REAL-ESTATE.
     */
    @Modifying
    @Query(value = "delete from documents where doc_type = 'REAL-ESTATE'", nativeQuery = true)
    void deleteAll();

    /**
     * Является ли квартира коммунальной по flatId.
     *
     * @param flatId
     *            идентификатор квартиры
     * @return ОН
     */
    @Query(value = "select count(*) > 0 from documents "
        + "where json_data -> 'RealEstate' -> 'RealEstateData' -> 'Flats' -> 'Flat' "
        + "@> cast('[{\"flat_type\": \"Коммунальная\", \"flatID\": \"' || :flatId || '\"}]' as jsonb) "
        + "and doc_type = 'REAL-ESTATE'", nativeQuery = true)
    boolean isCommunalFlat(@Nonnull @Param("flatId") String flatId);

    /**
     * Получить ОН по flatId.
     *
     * @param flatId
     *            идентификатор квартиры
     * @return ОН
     */
    @Query(value = "select * from documents "
        + "where json_data -> 'RealEstate' -> 'RealEstateData' -> 'Flats' -> 'Flat' "
        + "@> cast('[{\"flatID\": \"' || :flatId || '\"}]' as jsonb) "
        + "and doc_type = 'REAL-ESTATE' limit 1", nativeQuery = true)
    DocumentData fetchByFlatId(@Nonnull @Param("flatId") String flatId);

    /**
     * Получить список ОН, не целиком обогащенных из ЕЖД.
     *
     * @param pageable
     *            pageable
     * @return список ОН
     */
    @Query(value = "select * from documents "
        + "where (json_data -> 'RealEstate' -> 'RealEstateData' ->> 'UpdatedStatus' != 'обогащено') "
        + "and cast(json_data -> 'RealEstate' -> 'RealEstateData' ->> 'enrichingFlag' as boolean) "
        + "and doc_type = 'REAL-ESTATE'",
        countQuery = "select count(*) from documents "
            + "where (json_data -> 'RealEstate' -> 'RealEstateData' ->> 'UpdatedStatus' != 'обогащено') "
            + "and cast(json_data -> 'RealEstate' -> 'RealEstateData' ->> 'enrichingFlag' as boolean) "
            + "and doc_type = 'REAL-ESTATE'",
        nativeQuery = true)
    Page<DocumentData> fetchNotUpdated(Pageable pageable);

    /**
     * Получить список ОН, обогащенных и не целиком обогащенных из ЕЖД.
     *
     * @param pageable
     *            pageable
     * @return список ОН
     */
    @Query(value = "select * from documents "
        + "where (json_data -> 'RealEstate' -> 'RealEstateData' ->> 'UpdatedStatus' is not null) "
        + "and cast(json_data -> 'RealEstate' -> 'RealEstateData' ->> 'enrichingFlag' as boolean) "
        + "and doc_type = 'REAL-ESTATE'",
        countQuery = "select count(*) from documents "
            + "where (json_data -> 'RealEstate' -> 'RealEstateData' ->> 'UpdatedStatus' is not null) "
            + "and cast(json_data -> 'RealEstate' -> 'RealEstateData' ->> 'enrichingFlag' as boolean) "
            + "and doc_type = 'REAL-ESTATE'",
        nativeQuery = true)
    Page<DocumentData> fetchUpdatedAndProgress(Pageable pageable);

    /**
     * Получить ОН по UNOM.
     *
     * @param unom
     *            unom дома
     * @return адрес ОН
     */
    @Query(value = "select * from documents "
        + "where json_data -> 'RealEstate' -> 'RealEstateData' ->> 'UNOM' = :unom "
        + "and doc_type = 'REAL-ESTATE' limit 1", nativeQuery = true)
    DocumentData fetchByUnom(@Nonnull @Param("unom") String unom);

    /**
     * Получить код ЦИП по UNOM.
     *
     * @param unom
     *            unom дома
     * @return адрес ОН
     */
    @Query(value = "select json_data -> 'RealEstate' -> 'RealEstateData' ->> 'informationCenterCode' from documents "
        + "where json_data -> 'RealEstate' -> 'RealEstateData' ->> 'UNOM' = :unom and doc_type = 'REAL-ESTATE'",
        nativeQuery = true)
    String getInformationCenterCodeByUnom(@Nonnull @Param("unom") String unom);

    /**
     * Получить район по UNOM.
     *
     * @param unom
     *            unom дома
     * @return район
     */
    @Query(value = "select json_data -> 'RealEstate' -> 'RealEstateData' -> 'MunOkrug_P5' ->> 'name' from documents "
        + "where json_data -> 'RealEstate' -> 'RealEstateData' ->> 'UNOM' = :unom and doc_type = 'REAL-ESTATE'",
        nativeQuery = true)
    String getDistrictByUnom(@Nonnull @Param("unom") String unom);

    /**
     * Получить округ по UNOM.
     *
     * @param unom
     *            unom дома
     * @return округ
     */
    @Query(value = "select json_data -> 'RealEstate' -> 'RealEstateData' -> 'DISTRICT' ->> 'name' from documents "
        + "where json_data -> 'RealEstate' -> 'RealEstateData' ->> 'UNOM' = :unom and doc_type = 'REAL-ESTATE'",
        nativeQuery = true)
    String getPrefectByUnom(@Nonnull @Param("unom") String unom);

    /**
     * Получить RealEstate по ЕНО (IntegrationNumber).
     *
     * @param eno
     *            IntegrationNumber
     * @return список ids
     */
    @Query(value = "select id from documents "
        + "where json_data -> 'RealEstate' -> 'RealEstateData' -> 'Flats' -> 'Flat' @> cast(:eno as jsonb) "
        + "and doc_type = 'REAL-ESTATE'", nativeQuery = true)
    List<String> fetchRealEstatesByEno(@Nonnull @Param("eno") String eno);

    /**
     * Получить список UNOM и адресов всех ОН.
     *
     * @return список UNOM и адресов всех ОН
     */
    @Query(value = "select json_data -> 'RealEstate' -> 'RealEstateData' ->> 'UNOM' "
        + "as unom, json_data -> 'RealEstate' -> 'RealEstateData' ->> 'Address' "
        + "as address from documents where doc_type = 'REAL-ESTATE'", nativeQuery = true)
    List<Map<String, Object>> getUnomAndAddressRealEstates();

    /**
     * Получить проекцию для расселяемых домов.
     *
     * @return список проекций для расселяемых домов
     */
    @Query(
        value = "select"
            + " json_data -> 'RealEstate' -> 'RealEstateData' ->> 'UNOM' as unom,"
            + " json_data -> 'RealEstate' -> 'RealEstateData' ->> 'Address' as address,"
            + " json_data -> 'RealEstate' -> 'RealEstateData' ->> 'SubjectRF_P1' as subjectRfp1Json,"
            + " json_data -> 'RealEstate' -> 'RealEstateData' ->> 'Element_P7' as elementP7Json,"
            + " json_data -> 'RealEstate' -> 'RealEstateData' ->> 'House_L1_TYPE' as houseL1TypeJson,"
            + " json_data -> 'RealEstate' -> 'RealEstateData' ->> 'House_L1_VALUE' as houseL1Value,"
            + " json_data -> 'RealEstate' -> 'RealEstateData' ->> 'Town_P4' as townP4Json,"
            + " json_data -> 'RealEstate' -> 'RealEstateData' ->> 'Settlement_P3' as settlementP3Json,"
            + " json_data -> 'RealEstate' -> 'RealEstateData' ->> 'Locality_P6' as localityP6Json,"
            + " json_data -> 'RealEstate' -> 'RealEstateData' ->> 'Corp_L2_TYPE' as corpL2TypeJson,"
            + " json_data -> 'RealEstate' -> 'RealEstateData' ->> 'Corp_L2_VALUE' as corpL2Value,"
            + " json_data -> 'RealEstate' -> 'RealEstateData' ->> 'Building_L3_TYPE' as buildingL3TypeJson,"
            + " json_data -> 'RealEstate' -> 'RealEstateData' ->> 'Building_L3_VALUE' as buildingL3Value"
            + " from documents"
            + " where doc_type = 'REAL-ESTATE'",
        nativeQuery = true)
    List<BuildingData> getBuildingDataList();

    /**
     * Получение отселяемых домов по documentId.
     * @param documentIds - id документов.
     * @return отселяемый дом.
     */
    @Query(
            value = "WITH realEstates AS ( "
                    + "SELECT "
                    + " id, "
                    + " json_data->'RealEstate'->'RealEstateData'->>'UNOM' AS unom, "
                    + " json_data->'RealEstate'->'RealEstateData'->'MunOkrug_P5'->>'name' AS area, "
                    + " json_data->'RealEstate'->'RealEstateData'->'DISTRICT'->>'name' AS districtName, "
                    + " json_data->'RealEstate'->'RealEstateData'->'DISTRICT'->>'code' AS districtCode, "
                    + " json_data->'RealEstate'->'RealEstateData'->>'Address' AS address, "
                    + " json_data->'RealEstate'->'RealEstateData'->>'resettlementCompletionDate' "
                    + " AS resettlementCompletionDate "
                    + "FROM "
                    + " ssr.documents "
                    + "WHERE "
                    + " doc_type='REAL-ESTATE' "
                    + " AND id in (:documentIds)), "
                    + "resettlement_requests AS ( "
                    + " SELECT "
                    + "     DISTINCT ON (houses_to_resettle->>'realEstateUnom') "
                    + "     houses_to_resettle->>'realEstateUnom' AS unom, "
                    + "     resettlement_requests.json_data->'ResettlementRequest'->'main'->>'startResettlementDate' "
                    + "     AS startResettlementDate "
                    + " FROM "
                    + "     ssr.documents resettlement_requests "
                    + " JOIN "
                    + "     jsonb_array_elements(resettlement_requests.json_data->'ResettlementRequest'->'main'"
                    + "     ->'housesToSettle') AS houses_to_settle "
                    + " ON "
                    + "     TRUE "
                    + " JOIN "
                    + "     jsonb_array_elements(houses_to_settle->'housesToResettle') houses_to_resettle "
                    + " ON "
                    + "     TRUE "
                    + " WHERE "
                    + "     resettlement_requests.doc_type='RESETTLEMENT-REQUEST' "
                    + " ORDER BY unom, startResettlementDate nulls last) "
                    + ""
                    + "SELECT "
                    + " id, realEstates.area, "
                    + " realEstates.unom, realEstates.districtName, realEstates.districtCode, "
                    + " realEstates.address, realEstates.resettlementCompletionDate, "
                    + " resettlement_requests.startResettlementDate "
                    + "FROM  "
                    + " realEstates "
                    + "LEFT JOIN "
                    + " resettlement_requests "
                    + "ON "
                    + " resettlement_requests.unom = realEstates.unom",
            nativeQuery = true)
    List<ResettledHouseProjection> getResettledHousesesByIds(@Param("documentIds") final List<String> documentIds);

    /**
     * Получить квартиры по уному.
     *
     * @param unom уном отселяемого дома
     * @return список квартир по уному
     */
    @Query(
        value = "SELECT flats ->> 'flatID' as flatId,"
            + "  coalesce (flats ->> 'apartment_L4_VALUE' ,flats ->> 'FlatNumber') as flatNum "
            + "FROM documents,"
            + "     jsonb_array_elements("
            + "             json_data -> 'RealEstate' -> 'RealEstateData' -> 'Flats' -> 'Flat') as flats "
            + "WHERE doc_type = 'REAL-ESTATE'"
            + "      AND json_data -> 'RealEstate' -> 'RealEstateData' ->> 'UNOM' = :unom",
        nativeQuery = true)
    List<FlatProjection> getFlatsByUnom(@Param("unom") final String unom);

    /**
     * Получить ИД квартиры по уному и номеру квартиры.
     *
     * @param unom уном отселяемого дома
     * @param flatNumber уном отселяемого дома
     * @return flatId ИД квартиры
     */
    @Query(
        value = "SELECT flats ->> 'flatID' as flatId "
            + "FROM documents,"
            + "     jsonb_array_elements("
            + "             json_data -> 'RealEstate' -> 'RealEstateData' -> 'Flats' -> 'Flat') as flats "
            + "WHERE doc_type = 'REAL-ESTATE'"
            + "      AND json_data -> 'RealEstate' -> 'RealEstateData' ->> 'UNOM' = cast(:unom as text)"
            + "      AND coalesce (flats ->> 'apartment_L4_VALUE' ,flats ->> 'FlatNumber') = cast(:flatNumber as text)",
        nativeQuery = true)
    String fetchFlatIdByUnomAndFlatNumber(
        @Param("unom") final String unom,
        @Param("flatNumber") final String flatNumber
    );

    /**
     * Получить UNOM по realEstateId.
     *
     * @param realEstateId
     *            идентификатор ОН
     * @return UNOM ОН
     */
    @Query(value = "select json_data -> 'RealEstate' -> 'RealEstateData' ->> 'UNOM' from documents "
        + "where json_data ->> 'id' = :realEstateId and doc_type = 'REAL-ESTATE'", nativeQuery = true)
    String getUnomByRealEstateId(@Nonnull @Param("realEstateId") String realEstateId);

    /**
     * Получить UNOM по flatId.
     *
     * @param jsonNode
     *            нода с квартирой
     * @return UNOM ОН
     */
    @Query(value = "select json_data -> 'RealEstate' -> 'RealEstateData' ->> 'UNOM' from documents "
        + "where json_data -> 'RealEstate' -> 'RealEstateData' -> 'Flats' -> 'Flat' @> cast(:jsonNode as jsonb) "
        + "and doc_type = 'REAL-ESTATE'", nativeQuery = true)
    String getUnomByFlatId(@Nonnull @Param("jsonNode") String jsonNode);

    /**
     * Получить список идентификаторов RealEstateDocument по наличию квартиры.
     *
     * @param jsonNode
     *            нода с квартирой
     * @return список идентификаторов ОН
     */
    @Query(value = "select id from documents "
        + "where json_data -> 'RealEstate' -> 'RealEstateData' -> 'Flats' -> 'Flat' @> cast(:jsonNode as jsonb) "
        + "and doc_type = 'REAL-ESTATE'", nativeQuery = true)
    List<String> findRealEstateIdsByFlatId(@Nonnull @Param("jsonNode") String jsonNode);

    /**
     * Получить постранично RealEstateDocument по UNOM или адресу.
     *
     * @param unomOrAddress
     *            UNOM или адрес
     * @param pageable
     *            Pageable
     * @return страница DocumentData
     */
    @Query(value = "select id, cast(json_data -> 'RealEstate' -> 'RealEstateData' ->> 'UNOM' as integer) "
        + "as unom, json_data -> 'RealEstate' -> 'RealEstateData' ->> 'Address' "
        + "as address, json_data -> 'RealEstate' -> 'RealEstateData' ->> 'UpdatedStatus' as updatedStatus "
        + "from documents where (json_data -> 'RealEstate' -> 'RealEstateData' ->> 'UNOM' SIMILAR TO :unomOrAddress"
        + " or LOWER(json_data -> 'RealEstate' -> 'RealEstateData' ->> 'Address') SIMILAR TO LOWER(:unomOrAddress))"
        + " and doc_type = 'REAL-ESTATE'",
        countQuery = "select count(*) from documents "
            + "where (json_data -> 'RealEstate' -> 'RealEstateData' ->> 'UNOM' SIMILAR TO :unomOrAddress "
            + "or LOWER(json_data -> 'RealEstate' -> 'RealEstateData' ->> 'Address') "
            + "SIMILAR TO LOWER(:unomOrAddress)) "
            + "and doc_type = 'REAL-ESTATE'",
        nativeQuery = true)
    Page<Map<String, Object>> findRealEstatesByUnomOrAddress(@Nonnull @Param("unomOrAddress") String unomOrAddress,
                                                             Pageable pageable);

    /**
     * Получить постранично RealEstateDocument по UNOM или адресу.
     *
     * @param unomOrAddress
     *            UNOM или адрес
     * @param pageable
     *            Pageable
     * @return страница DocumentData
     */
    @Query(value = "select * from documents "
        + "where (json_data -> 'RealEstate' -> 'RealEstateData' ->> 'UNOM' like :unomOrAddress "
        + "or json_data -> 'RealEstate' -> 'RealEstateData' ->> 'Address' like :unomOrAddress) "
        + "and doc_type = 'REAL-ESTATE'",
        countQuery = "select count(*) from documents "
            + "where (json_data -> 'RealEstate' -> 'RealEstateData' ->> 'UNOM' like :unomOrAddress "
            + "or json_data -> 'RealEstate' -> 'RealEstateData' ->> 'Address' like :unomOrAddress) "
            + "and doc_type = 'REAL-ESTATE'",
        nativeQuery = true)
    Page<DocumentData> findFullRealEstatesByUnomOrAddress(@Nonnull @Param("unomOrAddress") String unomOrAddress,
                                                          Pageable pageable);

    /**
     * Получить id и адрес ОН по flatId.
     *
     * @param flatId
     *            id квартиры
     * @return id и адрес ОН
     */
    @Query(value = "select id, json_data -> 'RealEstate' -> 'RealEstateData' ->> 'Address' as address from documents "
        + "where json_data -> 'RealEstate' -> 'RealEstateData' -> 'Flats' -> 'Flat' @> "
        + "cast('[{\"flatID\": \"' || :flatId || '\"}]' as jsonb) and doc_type = 'REAL-ESTATE' limit 1",
        nativeQuery = true)
    Map<String, Object> getIdAndAddressByFlatId(@Nonnull @Param("flatId") String flatId);

    /**
     * Получить список расселяемых домов по заселяемым.
     *
     * @param ccoId
     *            ид окса.
     * @return список документов.
     */
    @Query(value = "select * from documents where doc_type='REAL-ESTATE' and "
        + "json_data -> 'RealEstate' -> 'RealEstateData' -> 'Flats' -> 'Flat'  @> "
        + "cast('[{\"ccoId\":\"' || :ccoId || '\"}]' as jsonb)", nativeQuery = true)
    List<DocumentData> fetchByByCcoId(@Nonnull @Param("ccoId") String ccoId);

    /**
     * Получить список UNOM всех ОН.
     *
     * @return список UNOM всех ОН
     */
    @Query(value = "select json_data -> 'RealEstate' -> 'RealEstateData' ->> 'UNOM' as unom "
        + "from documents where doc_type = 'REAL-ESTATE' "
        + "and json_data -> 'RealEstate' -> 'RealEstateData' ->> 'UNOM' is not null", nativeQuery = true)
    List<String> getUnomRealEstates();

    /**
     * Получить ОН по адресу (подстрока).
     *
     * @param addrSubstr адрес (подстрока)
     * @param top        сколько селектить записей
     * @return список ОН
     */
    @Query(value = "select * from documents where doc_type = 'REAL-ESTATE' "
        + "and LOWER(json_data -> 'RealEstate' -> 'RealEstateData' ->> 'Address') SIMILAR TO LOWER(:addrSubstr) "
        + "limit :top",
        nativeQuery = true)
    List<DocumentData> getRealEstatesBySubstrAddress(
        @Nonnull @Param("addrSubstr") String addrSubstr, @Nonnull @Param("top") Integer top
    );

    /**
     * Возвращает ОН по UNOMам.
     *
     * @param unoms уномы через запятую
     * @return список ОН
     */
    @Query(
        value = "select * from documents where doc_type = 'REAL-ESTATE' "
            + "and json_data -> 'RealEstate' -> 'RealEstateData' ->> 'UNOM' in (:unoms)",
        nativeQuery = true
    )
    List<DocumentData> fetchDocumentsByUnoms(@Nonnull @Param("unoms") String unoms);

    /**
     * Получить список id ОН, не целиком обогащенных из ЕЖД.
     *
     * @return список id ОН
     */
    @Query(value = "select id from documents "
        + "where (json_data -> 'RealEstate' -> 'RealEstateData' ->> 'UpdatedStatus' != 'обогащено') "
        + "and cast(json_data -> 'RealEstate' -> 'RealEstateData' ->> 'enrichingFlag' as boolean) "
        + "and doc_type = 'REAL-ESTATE'",
        nativeQuery = true)
    List<String> fetchNotUpdatedIds();

    @Query(value = "select json_data -> 'RealEstate' -> 'RealEstateData' ->> 'UNOM' as unom from documents "
        + "where doc_type = 'REAL-ESTATE' and"
        + "    (json_data -> 'RealEstate' -> 'RealEstateData' -> 'Flats' IS NULL"
        + "           OR json_data -> 'RealEstate' -> 'RealEstateData' -> 'Flats' -> 'Flat' IS NULL"
        + "                      OR json_data -> 'RealEstate' -> 'RealEstateData' -> 'Flats' -> 'Flat'  = '[]')",
        nativeQuery = true)
    List<String> fetchRealEstateUnomsWithMissingFlats();

    @Query(value = "select distinct json_data -> 'RealEstate' -> 'RealEstateData' ->> 'UNOM' as unom "
        + "from documents, "
        + "     jsonb_array_elements(json_data -> 'RealEstate' -> 'RealEstateData' -> 'Flats' -> 'Flat') as flats "
        + "where doc_type = 'REAL-ESTATE' "
        + "  and (flats -> 'totalSquare' is null or flats -> 'entrance' is null)",
        nativeQuery = true)
    List<String> fetchUnomsWhereFlatsThatMissSquareOrEntrance();

}
