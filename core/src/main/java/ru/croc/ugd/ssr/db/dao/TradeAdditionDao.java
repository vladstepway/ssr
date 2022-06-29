package ru.croc.ugd.ssr.db.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import ru.reinform.cdp.db.model.DocumentData;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;

/**
 * DAO по работе с TradeAdditionDao.
 */
public interface TradeAdditionDao extends Repository<DocumentData, String> {

    /**
     * Получить tradeAddition по recordNumber.
     *
     * @param uniqueRecordKey uniqueRecordKey
     * @return tradeAddition
     */
    @Query(value = "select * from documents "
        + "where doc_type='TRADE-ADDITION' "
        + "and json_data -> 'tradeAddition' -> 'tradeAdditionTypeData'"
        + " ->> 'uniqueRecordKey' = :uniqueRecordKey "
        + "and json_data -> 'tradeAddition' -> 'tradeAdditionTypeData' ->> 'confirmed' = 'true' "
        + "and json_data -> 'tradeAddition' -> 'tradeAdditionTypeData' ->> 'indexed' = 'true'"
        + "limit 1", nativeQuery = true)
    Optional<DocumentData> fetchIndexedByUniqueKey(
        @Nonnull @Param("uniqueRecordKey") String uniqueRecordKey);

    /**
     * Возвращает все подтвержденные документы для batchDocumentId.
     *
     * @param batchDocumentId batchDocumentId
     * @return list of documents
     */
    @Query(value = "select * from documents"
        + "  where doc_type = 'TRADE-ADDITION'"
        + "   and json_data -> 'tradeAddition' -> 'tradeAdditionTypeData' ->> 'batchDocumentId' = :batchDocumentId",
        nativeQuery = true)
    List<DocumentData> getAllDocumentsIdsForBatch(
        @Param("batchDocumentId") final String batchDocumentId
    );

    /**
     * Возвращает все документы где removalStatusUpdated != true.
     *
     * @return list of documents
     */
    @Query(value = "select * from documents"
        + "  where doc_type = 'TRADE-ADDITION'"
        + "   and json_data -> 'tradeAddition' -> 'tradeAdditionTypeData' ->> 'removalStatusUpdated' != 'true'",
        nativeQuery = true)
    List<DocumentData> getAllDocumentsForStatusUpdateProcessing();

    /**
     * Возвращает count всех документов для batchDocumentId.
     *
     * @param batchDocumentId batchDocumentId
     * @return cound
     */
    @Query(value = "select count(*) from documents"
        + "  where doc_type = 'TRADE-ADDITION'"
        + "   and json_data -> 'tradeAddition' -> 'tradeAdditionTypeData' ->> 'batchDocumentId' = :batchDocumentId",
        nativeQuery = true)
    long countAllDocumentForBatch(
        @Param("batchDocumentId") final String batchDocumentId
    );


    /**
     * Проверяет все ли документы всех партий загружены в реестр.
     *
     * @return ShippingDaySchedule
     */
    @Query(value = "select not exists( "
        + "select 1 from documents"
        + "  where doc_type = 'TRADE-DATA-BATCH-STATUS'"
        + "   and (json_data -> 'tradeDataBatchStatus' -> 'tradeDataBatchStatusTypeData' ->> 'deployed' != 'true'"
        + "    or json_data -> 'tradeDataBatchStatus' -> 'tradeDataBatchStatusTypeData' ->> 'deployed' is null) "
        + " and json_data -> 'tradeDataBatchStatus' -> 'tradeDataBatchStatusTypeData' ->> 'status' is not null"
        + " and json_data -> 'tradeDataBatchStatus' -> 'tradeDataBatchStatusTypeData' ->> 'startedProcessId'"
        + " is not null)",
        nativeQuery = true)
    Boolean areAllUploadedBatchesDeployed();

    /**
     * Есть заявления на докупку, зарегистрированное в Фонде реновации.
     *
     * @param personId ID жителя
     * @param affairId Идентификатор семьи
     * @return есть заявление
     */
    @Query(value = "select exists(select 1 from documents"
        + "    join jsonb_array_elements("
        + "       json_data -> 'tradeAddition' -> 'tradeAdditionTypeData' -> 'personsInfo') personinfo on true"
        + "    where doc_type = 'TRADE-ADDITION'"
        + "     and personinfo ->> 'personId' = cast(:personId as text)"
        + "     and json_data -> 'tradeAddition' -> 'tradeAdditionTypeData' ->> 'confirmed' = 'true' "
        + "     and json_data -> 'tradeAddition' -> 'tradeAdditionTypeData' ->> 'indexed' = 'true'"
        + "     and json_data -> 'tradeAddition' -> 'tradeAdditionTypeData' ->> 'affairId' = cast(:affairId as text)"
        + "     and (json_data -> 'tradeAddition' -> 'tradeAdditionTypeData' ->> 'buyInStatus' is not null and "
        + "           not json_data -> 'tradeAddition' -> 'tradeAdditionTypeData' ->> 'buyInStatus' in ('3','5', '6')"
        + "          or json_data -> 'tradeAddition' -> 'tradeAdditionTypeData' ->> 'compensationStatus' is not null "
        + "         and json_data -> 'tradeAddition' -> 'tradeAdditionTypeData' ->> 'compensationStatus' <>'4'))",
        nativeQuery = true)
    boolean hasAnyReceivedApplication(
        @Param("personId") final String personId,
        @Param("affairId") final String affairId
    );

    /**
     * Все полученные заявления на докупку, зарегистрированное в Фонде реновации.
     *
     * @param personId ID жителя
     * @param affairId Идентификатор семьи
     * @return найденные документы
     */
    @Query(value = "select * from documents"
        + "    join jsonb_array_elements("
        + "       json_data -> 'tradeAddition' -> 'tradeAdditionTypeData' -> 'personsInfo') personinfo on true"
        + "    where doc_type = 'TRADE-ADDITION'"
        + "     and personinfo ->> 'personId' = cast(:personId as text)"
        + "     and json_data -> 'tradeAddition' -> 'tradeAdditionTypeData' ->> 'confirmed' = 'true' "
        + "     and json_data -> 'tradeAddition' -> 'tradeAdditionTypeData' ->> 'indexed' = 'true'"
        + "     and json_data -> 'tradeAddition' -> 'tradeAdditionTypeData' ->> 'affairId' = cast(:affairId as text)"
        + "     and ( json_data -> 'tradeAddition' -> 'tradeAdditionTypeData' ->> 'buyInStatus' is not null and "
        + "           not json_data -> 'tradeAddition' -> 'tradeAdditionTypeData' ->> 'buyInStatus' in ('3','5', '6')"
        + "          or json_data -> 'tradeAddition' -> 'tradeAdditionTypeData' ->> 'compensationStatus' is not null "
        + "         and json_data -> 'tradeAddition' -> 'tradeAdditionTypeData' ->> 'compensationStatus' <>'4')",
        nativeQuery = true)
    List<DocumentData> findReceivedApplications(
        @Param("personId") final String personId,
        @Param("affairId") final String affairId
    );

    /**
     * Есть заявление на компенсацию или "вне района", зарегистрированное в Фонде реновации.
     *
     * @param personId ID жителя
     * @param affairId Идентификатор семьи
     * @return есть заявление
     */
    @Query(value = "select exists(select 1 from documents"
        + "    join jsonb_array_elements("
        + "       json_data -> 'tradeAddition' -> 'tradeAdditionTypeData' -> 'personsInfo') personinfo on true"
        + "    where doc_type = 'TRADE-ADDITION'"
        + "     and personinfo ->> 'personId' = cast(:personId as text)"
        + "     and json_data -> 'tradeAddition' -> 'tradeAdditionTypeData' ->> 'confirmed' = 'true' "
        + "     and json_data -> 'tradeAddition' -> 'tradeAdditionTypeData' ->> 'indexed' = 'true'"
        + "     and json_data -> 'tradeAddition' -> 'tradeAdditionTypeData' ->> 'affairId' = cast(:affairId as text)"
        + "     and json_data -> 'tradeAddition' -> 'tradeAdditionTypeData' ->> 'tradeType' in ('4', '5'))",
        nativeQuery = true)
    boolean hasCompensationsOrOutOfDistrict(
        @Param("personId") final String personId,
        @Param("affairId") final String affairId
    );

    /**
     * Все полученные заявления на докупку, зарегистрированное в Фонде реновации
     * у которых статус равняется или выше чем контракт подкисан,
     * за исключением докупки в течение 2 лет.
     *
     * @param personId ID жителя
     * @param affairId Идентификатор семьи
     * @return найденные документы
     */
    @Query(value = "select * from documents"
        + "    join jsonb_array_elements("
        + "       json_data -> 'tradeAddition' -> 'tradeAdditionTypeData' -> 'personsInfo') personinfo on true"
        + "    where doc_type = 'TRADE-ADDITION'"
        + "     and personinfo ->> 'personId' = cast(:personId as text)"
        + "     and json_data -> 'tradeAddition' -> 'tradeAdditionTypeData' ->> 'confirmed' = 'true' "
        + "     and json_data -> 'tradeAddition' -> 'tradeAdditionTypeData' ->> 'indexed' = 'true'"
        + "     and json_data -> 'tradeAddition' -> 'tradeAdditionTypeData' ->> 'affairId' = cast(:affairId as text)"
        + "     and  json_data -> 'tradeAddition' -> 'tradeAdditionTypeData' ->> 'tradeType' <> '3'"
        + "     and ((json_data -> 'tradeAddition' -> 'tradeAdditionTypeData' ->> 'buyInStatus' is not null "
        + "           and json_data -> 'tradeAddition' -> 'tradeAdditionTypeData' ->> 'buyInStatus' in ('9','10'))"
        + "          or (json_data -> 'tradeAddition' -> 'tradeAdditionTypeData' ->> 'compensationStatus' is not null "
        + "   and json_data -> 'tradeAddition' -> 'tradeAdditionTypeData' ->> 'compensationStatus' in ('6','7','8')))",
        nativeQuery = true)
    List<DocumentData> findReceivedApplicationsWithContract(
        @Param("personId") final String personId,
        @Param("affairId") final String affairId
    );

    /**
     * Получить TRADE-ADDITION по unom расселяемого дома.
     *
     * @param unom unom расселяемого дома
     * @return список DocumentData
     */
    @Query(value = "select * from documents where doc_type = 'TRADE-ADDITION' "
        + "and json_data -> 'tradeAddition' -> 'tradeAdditionTypeData' -> 'oldEstate' ->> 'unom' = :unom ",
        nativeQuery = true)
    List<DocumentData> fetchByOldEstateUnom(@Nonnull @Param("unom") String unom);

    /**
     * Получить TRADE-ADDITION по unom расселяемого дома и ИД жителя.
     *
     * @param unom             unom расселяемого дома
     * @param affairId         affairId жителя
     * @param personDocumentId ID жителя
     * @return список DocumentData
     */
    @Query(value = "select "
        + "    * "
        + "from "
        + "    documents "
        + "where "
        + "    doc_type = 'TRADE-ADDITION' "
        + "    and json_data @> cast("
        + "        '{\"tradeAddition\": { \"tradeAdditionTypeData\": {"
        + "            \"oldEstate\": {\"unom\": \"' || :unom || '\"}, "
        + "            \"affairId\": \"' || :affairId || '\", "
        + "            \"personsInfo\": [{\"personDocumentId\": \"' || :personDocumentId || '\"}]}}}' as jsonb"
        + "    )",
        nativeQuery = true)
    List<DocumentData> fetchByOldEstateUnomAndPersonId(
        @Nonnull @Param("unom") String unom,
        @Nonnull @Param("affairId") String affairId,
        @Nonnull @Param("personDocumentId") String personDocumentId
    );

    @Query(value = "select * from documents where "
        + "    doc_type = 'TRADE-ADDITION' "
        + "    and json_data @> cast("
        + "        '{\"tradeAddition\": { \"tradeAdditionTypeData\": {"
        + "            \"indexed\": true, "
        + "            \"personsInfo\": [{\"personDocumentId\": \"' || :personDocumentId || '\"}]}}}' as jsonb"
        + "    )", nativeQuery = true)
    List<DocumentData> fetchIndexedByPersonId(
        @Nonnull @Param("personDocumentId") String personDocumentId
    );

    /**
     * Получить TRADE-ADDITION по unom заселяемого дома.
     *
     * @param jsonNode нода с unom
     * @return список DocumentData
     */
    @Query(value = "select * from documents where doc_type = 'TRADE-ADDITION' "
        + "and json_data -> 'tradeAddition' -> 'tradeAdditionTypeData' -> 'newEstates' @> cast(:jsonNode as jsonb) ",
        nativeQuery = true)
    List<DocumentData> fetchByNewEstateUnom(@Nonnull @Param("jsonNode") String jsonNode);

    /**
     * Получить TRADE-ADDITION-HISTORY для загруженных документов.
     *
     * @param uniqueKey uniqueKey
     * @return список DocumentData
     */
    @Query(value = "select * from documents history_doc "
        + "where history_doc.doc_type = 'TRADE-ADDITION-HISTORY' and "
        + " history_doc.json_data -> 'tradeHistory' -> 'tradeAdditionHistoryData' "
        + " ->> 'uniqueRecordKey' = :uniqueKey and "
        + " history_doc.json_data -> 'tradeHistory' -> 'tradeAdditionHistoryData' ->> 'isIndexed' = 'true'",
        nativeQuery = true)
    List<DocumentData> findActiveTradeAdditionHistoryByUniqueKey(
        @Nonnull @Param("uniqueKey") String uniqueKey
    );

    /**
     * Получить TRADE-ADDITION-HISTORY для загруженных документов.
     *
     * @param sellId sellId
     * @return список DocumentData
     */
    @Query(value = "select * from documents history_doc "
        + "where history_doc.doc_type = 'TRADE-ADDITION-HISTORY' and "
        + " history_doc.json_data -> 'tradeHistory' -> 'tradeAdditionHistoryData' "
        + " ->> 'sellId' = :sellId and "
        + " history_doc.json_data -> 'tradeHistory' -> 'tradeAdditionHistoryData' ->> 'isIndexed' = 'true'",
        nativeQuery = true)
    List<DocumentData> findActiveTradeAdditionHistoryBySellId(
        @Nonnull @Param("sellId") String sellId
    );

    /**
     * Получить TRADE-ADDITION-HISTORY для tradeAdditionId.
     *
     * @param tradeAdditionId tradeAdditionId
     * @return список DocumentData
     */
    @Query(value = "select * from documents history_doc "
        + "where history_doc.doc_type = 'TRADE-ADDITION-HISTORY' and "
        + " history_doc.json_data -> 'tradeHistory' -> 'tradeAdditionHistoryData' "
        + " ->> 'tradeAdditionDocumentId' = :tradeAdditionId",
        nativeQuery = true)
    List<DocumentData> findHistoryByTradeAdditionId(
        @Nonnull @Param("tradeAdditionId") String tradeAdditionId
    );

    /**
     * Получить ID TRADE-APPLICATION-FILE для которых нет соответствующего документа.
     *
     * @param batchDocumentId batchDocumentId
     * @return список ID TRADE-APPLICATION-FILE
     */
    @Query(value = "select id from documents where doc_type = 'TRADE-APPLICATION-FILE' and"
        + " json_data -> 'tradeApplicationFile' -> 'tradeApplicationFileTypeData' ->> 'fileName' not in"
        + " (select json_data -> 'tradeAddition' -> 'tradeAdditionTypeData' ->> 'attachedFileName'"
        + " from documents where doc_type = 'TRADE-ADDITION'"
        + " and json_data -> 'tradeAddition' -> 'tradeAdditionTypeData' ->> 'attachedFileName' is not null"
        + " and json_data -> "
        + "'tradeAddition' -> 'tradeAdditionTypeData' ->> 'batchDocumentId' = :batchDocumentId )"
        + " and  json_data -> 'tradeApplicationFile' -> 'tradeApplicationFileTypeData'"
        + " ->> 'batchDocumentId' = :batchDocumentId", nativeQuery = true)
    List<String> getNotLinkedTradeApplicationFilesForBatch(@Nonnull @Param("batchDocumentId") String batchDocumentId);

    @Query(value = "select * from documents where doc_type = 'TRADE-APPLICATION-FILE' "
        + " and  json_data -> 'tradeApplicationFile' -> 'tradeApplicationFileTypeData'"
        + " ->> 'batchDocumentId' = :batchDocumentId", nativeQuery = true)
    List<DocumentData> getAllBatchApplicationFiles(@Nonnull @Param("batchDocumentId") String batchDocumentId);

    @Query(value = "select * "
        + "from documents "
        + "where doc_type = 'TRADE-APPLICATION-FILE' "
        + "  and coalesce(json_data -> 'tradeApplicationFile' -> 'tradeApplicationFileTypeData' ->> 'fileName',"
        + " 'null') = coalesce(cast(:fileName as text), 'null') "
        + "  and json_data -> 'tradeApplicationFile' -> 'tradeApplicationFileTypeData' "
        + "          ->> 'batchDocumentId' = :batchDocumentId", nativeQuery = true)
    List<DocumentData> findByBatchDocumentIdAndFileName(
        @Param("batchDocumentId") final String batchDocumentId,
        @Param("fileName") final String fileName
    );

    @Query(value = "select * from documents"
        + " where doc_type = 'TRADE-ADDITION'"
        + "   and json_data -> 'tradeAddition' -> 'tradeAdditionTypeData' ->> 'sellId' = cast(:sellId as text)"
        + "   and json_data -> 'tradeAddition' -> 'tradeAdditionTypeData' ->> 'confirmed' = 'true' "
        + "   and json_data -> 'tradeAddition' -> 'tradeAdditionTypeData' ->> 'indexed' = 'true'",
        nativeQuery = true)
    List<DocumentData> fetchIndexedBySellId(@Param("sellId") final String sellId);

    @Query(value = "select * from documents"
        + " where doc_type = 'TRADE-ADDITION'"
        + "   and json_data -> 'tradeAddition' -> 'tradeAdditionTypeData' ->> 'affairId' = cast(:affairId as text)"
        + "   and json_data -> 'tradeAddition' -> 'tradeAdditionTypeData' ->> 'confirmed' = 'true' "
        + "   and json_data -> 'tradeAddition' -> 'tradeAdditionTypeData' ->> 'indexed' = 'true'",
        nativeQuery = true)
    List<DocumentData> fetchByAffairId(@Param("affairId") final String affairId);

    @Query(value = "select doc.*"
        + "    from documents doc,"
        + "    jsonb_array_elements("
        + "        json_data -> 'tradeAddition' -> 'tradeAdditionTypeData' -> 'newEstates') as newEstates"
        + "   where doc_type = 'TRADE-ADDITION'"
        + "   and newEstates ->> 'unom' = cast(:unom as text)"
        + "   and newEstates ->> 'flatNumber' = cast(:flatNumber as text)"
        + "   and json_data -> 'tradeAddition' -> 'tradeAdditionTypeData' ->> 'offerLetterDate' is not null"
        + "   and json_data -> 'tradeAddition' -> 'tradeAdditionTypeData' ->> 'confirmed' = 'true' "
        + "   and json_data -> 'tradeAddition' -> 'tradeAdditionTypeData' ->> 'indexed' = 'true'",
        nativeQuery = true)
    List<DocumentData> fetchTradeAdditionsByLetterUnomAndLetterFlatNumber(
        @Param("unom") final String unom, @Param("flatNumber") final String flatNumber
    );
}
