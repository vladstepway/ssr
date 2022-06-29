package ru.croc.ugd.ssr.db.dao;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import ru.croc.ugd.ssr.db.projection.FlatTenantProjection;
import ru.croc.ugd.ssr.db.projection.PersonBirthDateProjection;
import ru.croc.ugd.ssr.db.projection.PersonProjection;
import ru.croc.ugd.ssr.db.projection.TenantProjection;
import ru.reinform.cdp.db.model.DocumentData;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nonnull;

/**
 * DAO по работе с жителями.
 */
public interface PersonDocumentDao extends Repository<DocumentData, String> {

    /**
     * Получить жителей с похожими атрибутами ЕВД. Сравнивает ФИО + ДР, СНИЛС или personID.
     *
     * @param id жителя, с кем сравнивают
     * @return список DocumentData
     */
    @Query(value = "WITH current_person AS ("
        + "    SELECT id                                                    AS id,"
        + "           json_data -> 'Person' -> 'PersonData' ->> 'FIO'       AS person_full_name,"
        + "           json_data -> 'Person' -> 'PersonData' ->> 'birthDate' AS birth_date,"
        + "           json_data -> 'Person' -> 'PersonData' ->> 'SNILS'     AS snils,"
        + "           json_data -> 'Person' -> 'PersonData' ->> 'personID'  AS person_id"
        + "    FROM documents"
        + "    WHERE doc_type = 'PERSON'"
        + "      AND id = :id"
        + " )"
        + " SELECT doc.*"
        + " FROM documents doc,"
        + "     current_person"
        + " WHERE doc_type = 'PERSON'"
        + "  AND doc.id != current_person.id"
        + "  AND (doc.json_data -> 'Person' -> 'PersonData' ->> 'isArchive' = 'false'"
        + "    OR doc.json_data -> 'Person' -> 'PersonData' ->> 'isArchive' is null)"
        + "  AND (doc.json_data -> 'Person' -> 'PersonData' ->> 'FIO' = current_person.person_full_name"
        + "           AND doc.json_data -> 'Person' -> 'PersonData' ->> 'birthDate' = current_person.birth_date"
        + "    OR doc.json_data -> 'Person' -> 'PersonData' ->> 'SNILS' = current_person.snils"
        + "    OR doc.json_data -> 'Person' -> 'PersonData' ->> 'personID' = current_person.person_id)",
        nativeQuery = true)
    List<DocumentData> fetchSimilarPersons(@Nonnull @Param("id") String id);

    /**
     * Получить PersonDocument со статусом ПФР "Превышен суточный лимит запросов на документ".
     *
     * @return список DocumentData
     */
    @Query(value = "select id from documents "
        + "where json_data -> 'Person' -> 'PersonData' ->> 'UpdatedFromPFRstatus' "
        + "= 'Превышен суточный лимит запросов на документ' "
        + "and doc_type = 'PERSON'", nativeQuery = true)
    List<String> fetchIdsWithLimitedRequests();

    /**
     * Получить Person по ЕНО (IntegrationNumber).
     *
     * @param eno IntegrationNumber
     * @return список ids
     */
    @Query(value = "select id from documents "
        + "where (json_data -> 'Person' -> 'PersonData' ->> 'IntegrationNumberMale' = :eno "
        + "or json_data -> 'Person' -> 'PersonData' ->> 'IntegrationNumberFemale' = :eno "
        + "or json_data -> 'Person' -> 'PersonData' ->> 'IntegrationNumberElk' = :eno) and doc_type = 'PERSON'",
        nativeQuery = true)
    List<String> fetchPersonsByEno(@Nonnull @Param("eno") String eno);

    /**
     * Получить PersonDocument по flatId.
     *
     * @param flatId идентификатор квартиры
     * @return список DocumentData
     */
    @Query(value = "select * from documents "
        + "where json_data -> 'Person' -> 'PersonData' ->> 'flatID' = :flatId and doc_type = 'PERSON'",
        nativeQuery = true)
    List<DocumentData> fetchPersonsByFlatId(@Nonnull @Param("flatId") String flatId);

    /**
     * Получить PersonDocument по UNOM.
     *
     * @param unom UNOM ОН
     * @return список DocumentData
     */
    @Query(value = "select * from documents where json_data -> 'Person' -> 'PersonData' ->> 'UNOM' = :unom "
        + "and doc_type = 'PERSON'", nativeQuery = true)
    List<DocumentData> fetchPersonsByUnom(@Nonnull @Param("unom") String unom);

    /**
     * Получить пустые PersonDocument по id задачи на разбор ошибок.
     *
     * @param id ID документа-задачи на разбор
     * @return список id жителей-пустышек
     */
    @Query(value = "select id from documents where json_data -> 'Person' -> 'PersonData' "
        + "->> 'firstFlowErrorAnalyticsId' = :id "
        + "and json_data -> 'Person' -> 'PersonData' ->> 'UNOM' is null "
        + "and doc_type = 'PERSON'", nativeQuery = true)
    List<String> fetchByFirstFlowErrorAnalyticsId(@Nonnull @Param("id") String id);

    /**
     * Получить PersonDocument по UNOMам.
     *
     * @param unoms UNOMы ОН
     * @return список DocumentData
     */
    @Query(value = "select * from documents where json_data -> 'Person' -> 'PersonData' ->> 'UNOM' in :unoms "
        + "and doc_type = 'PERSON'", nativeQuery = true)
    List<DocumentData> fetchPersonsByUnoms(@Nonnull @Param("unoms") List<String> unoms);

    /**
     * Получить PersonDocument по personId и affairId.
     *
     * @param personId ид жителя из дги
     * @param affairId ид жил площади
     * @return список DocumentData
     */
    @Query(value = "select * from documents "
        + "where json_data -> 'Person' -> 'PersonData' ->> 'personID' = cast(:personId as text) "
        + "and json_data -> 'Person' -> 'PersonData' ->> 'affairId' = cast(:affairId as text) "
        + "and (json_data -> 'Person' -> 'PersonData' ->> 'isArchive' = 'false' "
        + "or json_data -> 'Person' -> 'PersonData' ->> 'isArchive' is null) "
        + "and doc_type = 'PERSON'",
        nativeQuery = true)
    List<DocumentData> fetchByPersonIdAndAffairId(@Nonnull @Param("personId") String personId,
                                                  @Nonnull @Param("affairId") String affairId);

    /**
     * Получить PersonDocument по personId.
     *
     * @param personId ид жителя из дги
     * @return список DocumentData
     */
    @Query(value = "select * from documents "
        + "where json_data -> 'Person' -> 'PersonData' ->> 'personID' = cast(:personId as text) "
        + "and (json_data -> 'Person' -> 'PersonData' ->> 'isArchive' = 'false' "
        + "or json_data -> 'Person' -> 'PersonData' ->> 'isArchive' is null) "
        + "and doc_type = 'PERSON'",
        nativeQuery = true)
    List<DocumentData> fetchByPersonId(@Nonnull @Param("personId") String personId);

    /**
     * Получить список PersonDocument по personId, unom и flatId.
     *
     * @param personId идентификатор жителя
     * @param unom     уном расселяемого дома
     * @param flatId   id расселяемой квартиры жителя
     * @return список DocumentData
     */
    @Query(value = "select * from documents "
        + "where json_data -> 'Person' -> 'PersonData' ->> 'personID' = cast(:personId as text) "
        + "and json_data -> 'Person' -> 'PersonData' ->> 'UNOM' = cast(:unom as text) "
        + "and json_data -> 'Person' -> 'PersonData' ->> 'flatID' = cast(:flatId as text) "
        + "and doc_type = 'PERSON'",
        nativeQuery = true)
    List<DocumentData> fetchPersonByPersonIdAndUnomAndFlatId(@Param("personId") String personId,
                                                             @Param("unom") String unom,
                                                             @Param("flatId") String flatId);

    /**
     * Получить письмо с предложением по letterId.
     *
     * @param letterId ид письма
     * @return список файлов (json)
     */
    @Query(value = "select cast(json_data as character varying) from "
        + "(select jsonb_array_elements(json_data -> 'Person' -> 'PersonData' -> 'offerLetters' -> 'offerLetter') "
        + "as json_data from documents where doc_type = 'PERSON') as offerLetter "
        + "where json_data ->> 'letterId' = cast(:letterId as character varying)", nativeQuery = true)
    List<String> fetchPersonOfferLettersByLetterId(@Nonnull @Param("letterId") String letterId);

    /**
     * Получить Жителя по ЕНО отправленных сообщений ЕЛК.
     *
     * @param jsonNode нода с ЕНО
     * @return житель
     */
    @Query(value = "select * from documents where doc_type = 'PERSON' "
        + "and json_data -> 'Person' -> 'PersonData' -> 'sendedMessages' -> 'Message' @> cast(:jsonNode as jsonb) ",
        nativeQuery = true)
    List<DocumentData> getPersonBySendMessageEno(@Nonnull @Param("jsonNode") String jsonNode);

    /**
     * Получить жителей по unom заселяемой квартиры.
     *
     * @param jsonNode нода с unom
     * @return список жителей
     */
    @Query(value = "select *"
        + "  from documents"
        + " where doc_type = 'PERSON'"
        + "   and coalesce(json_data -> 'Person' -> 'PersonData' ->> 'isArchive', 'false') = 'false'"
        + "   and json_data -> 'Person' -> 'PersonData' -> 'newFlatInfo' -> 'newFlat' @> cast(:jsonNode as jsonb)",
        nativeQuery = true)
    List<DocumentData> getPersonByCcoUnom(@Nonnull @Param("jsonNode") String jsonNode);

    @Query(value = "SELECT EXISTS(SELECT 1 from documents where doc_type = 'PERSON' "
        + "and json_data -> 'Person' -> 'PersonData' -> 'sendedMessages' -> 'Message' @> cast(:jsonNode as jsonb)) ",
        nativeQuery = true)
    boolean existsBySendMessage(@Nonnull @Param("jsonNode") String jsonNode);

    /**
     * Получить жителей по которым поступили письма с предложениями.
     *
     * @return жители
     */
    @Query(value = "select * from documents where doc_type = 'PERSON'"
        + " and (json_data -> 'Person' -> 'PersonData' -> 'offerLetters' -> 'offerLetter') IS NOT NULL "
        + " and (json_data -> 'Person' -> 'PersonData' ->> 'isArchive' = 'false' "
        + "   or json_data -> 'Person' -> 'PersonData' ->> 'isArchive' is null)",
        nativeQuery = true)
    List<DocumentData> fetchPersonsWithOfferLetters();

    /**
     * Получить PersonDocument по snils и ssoId.
     *
     * @param snils Снилс
     * @param ssoId ID на портале mos.ru
     * @return список DocumentData
     */
    @Query(
        value = "select * from documents "
            + "where json_data -> 'Person' -> 'PersonData' ->> 'SNILS' = :snils "
            + "and (:ssoId is null or json_data -> 'Person' -> 'PersonData' ->> 'SsoID' = CAST(:ssoId AS TEXT)) "
            + "and doc_type = 'PERSON'",
        nativeQuery = true
    )
    List<DocumentData> fetchDocumentsBySnilsAndSsoId(
        @Nonnull @Param("snils") String snils, @Param("ssoId") String ssoId
    );

    /**
     * Получить идентификаторы жителей для обогащения из ПФР.
     *
     * @return список идентификаторов
     */
    @Query(
        value = "SELECT id "
            + "FROM documents where doc_type = 'PERSON' "
            + "and json_data -> 'Person' -> 'PersonData' ->> 'UpdatedFromDgiStatus' is not null "
            + "and json_data -> 'Person' -> 'PersonData' ->> 'SNILS' is null "
            + "and json_data -> 'Person' -> 'PersonData' ->> 'FIO' is not null "
            + "and json_data -> 'Person' -> 'PersonData' ->> 'birthDate' is not null "
            + "and (json_data -> 'Person' -> 'PersonData' ->> 'relocationStatus' is null "
            + "or json_data -> 'Person' -> 'PersonData' ->> 'relocationStatus' != '15')", nativeQuery = true)
    List<String> fetchAllPersonIdsForPfrUpdate();

    /**
     * Обезличить жителей для тестовых стендов.
     */
    @Modifying
    @Query(
        value = "UPDATE documents "
            + "SET json_data = jsonb_set( "
            + "json_data, ('{Person, PersonData, FIO}'), "
            + "cast(concat("
            + "'\"','testFIO','\"' ) as jsonb)) "
            + "where doc_type = 'PERSON' and json_data -> 'Person' -> 'PersonData' ->> 'FIO' like '%\\\"%'; "
            + "UPDATE documents "
            + "SET json_data = jsonb_set( "
            + "json_data, ('{Person, PersonData, FIO}'), "
            + "cast(concat("
            + "'\"',overlay(json_data->'Person'->'PersonData'->>'FIO' placing 'test' from 2 for 5),'\"' ) as jsonb)) "
            + "where doc_type = 'PERSON' and json_data -> 'Person' -> 'PersonData' ->> 'FIO' is not null;"
            + "UPDATE documents "
            + "SET json_data = jsonb_set( "
            + "json_data, ('{Person, PersonData, LastName}'), "
            + "cast(concat( "
            + "'\"',overlay("
            + "json_data->'Person'->'PersonData'->>'LastName' placing 'test' from 2 for 5),'\"') as jsonb)) "
            + "where doc_type = 'PERSON' and json_data -> 'Person' -> 'PersonData' ->> 'LastName' is not null;"
            + "UPDATE documents "
            + "SET json_data = jsonb_set( "
            + "json_data, ('{Person, PersonData, passport}'), "
            + "cast(concat("
            + "'\"','passport','\"' ) as jsonb)) "
            + "where doc_type = 'PERSON' and json_data -> 'Person' -> 'PersonData' ->> 'passport' is not null; ",
        nativeQuery = true
    )
    void depersonalizePersons();

    /**
     * Получить имена жителей по списку идентификаторов документов.
     *
     * @param ids список идентификаторов документов жителей
     * @return список жителей
     */
    @Query(
        value = "SELECT json_data -> 'Person' -> 'PersonData' ->> 'FIO' FROM documents "
            + "WHERE doc_type = 'PERSON' AND id IN :ids",
        nativeQuery = true)
    List<String> fetchFioByIds(@Param("ids") List<String> ids);

    /**
     * Получить PersonDocument по SNILS.
     *
     * @param snils СНИЛС
     * @return список DocumentData
     */
    @Query(
        value = "select * "
            + "from documents "
            + "where json_data @> cast('{\"Person\": {\"PersonData\": {\"SNILS\": \"' || :snils || '\"}}}' as jsonb) "
            + "and doc_type = 'PERSON'",
        nativeQuery = true
    )
    List<DocumentData> fetchPersonsBySnils(@Nonnull @Param("snils") String snils);

    /**
     * Получить ids всех uploaded PersonDocument.
     *
     * @return список ids
     */
    @Query(value = "SELECT id FROM documents "
        + "WHERE json_data -> 'Person' -> 'PersonData' ->> 'isDgiData' = 'true'"
        + "AND doc_type = 'PERSON'",
        nativeQuery = true)
    List<String> getUploadedPersonIds();

    /**
     * Удалить все uploaded PersonDocument.
     */
    @Modifying
    @Query(value = "DELETE FROM documents "
        + "WHERE json_data -> 'Person' -> 'PersonData' ->> 'isDgiData' = 'true'"
        + "AND doc_type = 'PERSON'",
        nativeQuery = true)
    void deleteAllByIsUploaded();

    /**
     * Получить PersonDocument по affairId и id квартиры с исключающим id.
     *
     * @param affairId ид жителя из дги
     * @param flatId   ид жителя
     * @param personId ид жителя исключенного из выборки
     * @return список DocumentData
     */
    @Query(
        value = "select * from documents "
            + "where json_data @> cast('{"
            + "    \"Person\": {"
            + "        \"PersonData\": {"
            + "            \"affairId\": \"' || :affairId || '\", "
            + "            \"flatID\": \"' || :flatId || '\" "
            + "        }"
            + "    }"
            + "}' as jsonb)"
            + "and id != :personId "
            + "and (json_data -> 'Person' -> 'PersonData' ->> 'isArchive' = 'false' "
            + "or json_data -> 'Person' -> 'PersonData' ->> 'isArchive' is null) "
            + "and doc_type = 'PERSON'",
        nativeQuery = true
    )
    List<DocumentData> fetchByAffairIdAndFlatId(
        @Nonnull @Param("affairId") String affairId,
        @Nonnull @Param("flatId") String flatId,
        @Nonnull @Param("personId") String personId
    );

    /**
     * Получить PersonDocument по affairId.
     *
     * @param affairId ID семьи
     * @return список DocumentData
     */
    @Query(value = "select * from documents "
        + "where json_data -> 'Person' -> 'PersonData' ->> 'affairId' = :affairId "
        + "and (json_data -> 'Person' -> 'PersonData' ->> 'isArchive' = 'false' "
        + "or json_data -> 'Person' -> 'PersonData' ->> 'isArchive' is null) "
        + "and doc_type = 'PERSON'",
        nativeQuery = true)
    List<DocumentData> fetchByAffairId(@Nonnull @Param("affairId") String affairId);

    /**
     * Получить PersonDocument по списку affairId.
     *
     * @param affairIds коллекция ID семьи
     * @return список DocumentData
     */
    @Query(value = "select * from documents "
        + "where json_data -> 'Person' -> 'PersonData' ->> 'affairId' IN (:affairIds) "
        + "and (json_data -> 'Person' -> 'PersonData' ->> 'isArchive' = 'false' "
        + "or json_data -> 'Person' -> 'PersonData' ->> 'isArchive' is null) "
        + "and doc_type = 'PERSON'",
        nativeQuery = true)
    List<DocumentData> fetchByAffairIds(@Nonnull @Param("affairIds") Collection<String> affairIds);

    /**
     * Получить список PersonDocument по snils, unom и flatId.
     *
     * @param snils  snils
     * @param unom   уном расселяемого дома
     * @param flatId id расселяемой квартиры жителя
     * @return список DocumentData
     */
    @Query(value = "select * from documents "
        + "where json_data -> 'Person' -> 'PersonData' ->> 'SNILS' = cast(:snils as text) "
        + "and json_data -> 'Person' -> 'PersonData' ->> 'UNOM' = cast(:unom as text) "
        + "and json_data -> 'Person' -> 'PersonData' ->> 'flatID' = cast(:flatId as text) "
        + "and doc_type = 'PERSON'",
        nativeQuery = true)
    List<DocumentData> fetchPersonBySnilsAndUnomAndFlatId(@Param("snils") String snils,
                                                          @Param("unom") String unom,
                                                          @Param("flatId") String flatId);

    /**
     * Получить всех жителей.
     *
     * @return список жителей
     */
    @Query(
        value = "SELECT"
            + " json_data -> 'Person' -> 'PersonData' ->> 'personID' AS personId,"
            + " json_data -> 'Person' -> 'PersonData' ->> 'affairId' AS affairId,"
            + " json_data -> 'Person' -> 'PersonData' ->> 'SNILS' AS snils,"
            + " json_data -> 'Person' -> 'PersonData' ->> 'FIO' AS fio,"
            + " json_data -> 'Person' -> 'PersonData' ->> 'birthDate' AS birthDate,"
            + " json_data -> 'Person' -> 'PersonData' ->> 'gender' AS gender,"
            + " json_data -> 'Person' -> 'PersonData' ->> 'statusLiving' AS statusLiving,"
            + " json_data -> 'Person' -> 'PersonData' ->> 'encumbrances' AS encumbrances,"
            + " cast(json_data -> 'Person' -> 'PersonData' ->> 'UNOM' AS text) AS unom,"
            + " json_data -> 'Person' -> 'PersonData' ->> 'cadNum' AS cadNum,"
            + " json_data -> 'Person' -> 'PersonData' ->> 'flatNum' AS flatNum,"
            + " string_agg(coalesce(roomNumbers, ''), ',') AS roomNum,"
            + " json_data -> 'Person' -> 'PersonData' ->> 'waiter' AS waiter,"
            + " json_data -> 'Person' -> 'PersonData' -> 'addInfo' ->> 'delReason'AS delReason,"
            + " json_data -> 'Person' -> 'PersonData' -> 'addInfo' ->> 'isDead'AS isDead,"
            + " json_data -> 'Person' -> 'PersonData' -> 'addFlatInfo' ->> 'noFlat'AS noFlat,"
            + " json_data -> 'Person' -> 'PersonData' -> 'addFlatInfo' ->> 'ownFederal'AS ownFederal,"
            + " json_data -> 'Person' -> 'PersonData' -> 'addFlatInfo' ->> 'inCourt'AS inCourt "
            + "FROM documents "
            + " LEFT JOIN jsonb_array_elements_text(json_data -> 'Person' -> 'PersonData' -> 'roomNum') as roomNumbers"
            + "     ON TRUE "
            + " WHERE doc_type = 'PERSON'"
            + "     AND (json_data -> 'Person' -> 'PersonData' ->> 'isArchive' = 'false' "
            + "         OR json_data -> 'Person' -> 'PersonData' ->> 'isArchive' is null) "
            + " GROUP BY id",
        nativeQuery = true)
    Stream<PersonProjection> streamAll();

    /**
     * Получить список PersonDocument по фио, дню рождения, unom и flatId.
     *
     * @param fullName fullName
     * @param birthday birthday
     * @param unom     уном расселяемого дома
     * @param flatId   id расселяемой квартиры жителя
     * @return список DocumentData
     */
    @Query(value = "select * from documents "
        + "where json_data -> 'Person' -> 'PersonData' ->> 'FIO' = cast(:fullName as text) "
        + "and cast(json_data -> 'Person' -> 'PersonData' ->> 'birthDate' as date) = cast(:birthday as date) "
        + "and json_data -> 'Person' -> 'PersonData' ->> 'UNOM' = cast(:unom as text) "
        + "and json_data -> 'Person' -> 'PersonData' ->> 'flatID' = cast(:flatId as text) "
        + "and doc_type = 'PERSON'",
        nativeQuery = true)
    List<DocumentData> fetchPersonByFullNameAndBirthdayAndUnomAndFlatId(@Param("fullName") String fullName,
                                                                        @Param("birthday") LocalDate birthday,
                                                                        @Param("unom") String unom,
                                                                        @Param("flatId") String flatId);

    /**
     * Получить список PersonDocument по фио и дате рождения.
     *
     * @param fullName fullName
     * @param birthDate birthDate
     * @return список DocumentData
     */
    @Query(value = "select * from documents "
        + "where json_data -> 'Person' -> 'PersonData' ->> 'FIO' is not null "
        + "and json_data -> 'Person' -> 'PersonData' ->> 'FIO' = cast(:fullName as text) "
        + "and json_data -> 'Person' -> 'PersonData' ->> 'birthDate' is not null "
        + "and cast(json_data -> 'Person' -> 'PersonData' ->> 'birthDate' as date) = cast(:birthDate as date) "
        + "and doc_type = 'PERSON'",
        nativeQuery = true)
    List<DocumentData> fetchPersonByFullNameAndBirthDate(
        @Param("fullName") String fullName, @Param("birthDate") LocalDate birthDate
    );

    /**
     * Получить список PersonDocument по фио, unom и flatId.
     *
     * @param fullName fullName
     * @param unom     уном расселяемого дома
     * @param flatId   id расселяемой квартиры жителя
     * @return список DocumentData
     */
    @Query(value = "select * from documents "
        + "where json_data -> 'Person' -> 'PersonData' ->> 'FIO' = cast(:fullName as text) "
        + "and json_data -> 'Person' -> 'PersonData' ->> 'UNOM' = cast(:unom as text) "
        + "and json_data -> 'Person' -> 'PersonData' ->> 'flatID' = cast(:flatId as text) "
        + "and doc_type = 'PERSON'",
        nativeQuery = true)
    List<DocumentData> fetchPersonByFullNameAndUnomAndFlatId(@Param("fullName") String fullName,
                                                             @Param("unom") String unom,
                                                             @Param("flatId") String flatId);

    /**
     * Получить Жителей по УНОМу заселяемого дома.
     *
     * @param jsonNode нода с unom
     * @return список жителей
     */
    @Query(value = "select * from documents where doc_type = 'PERSON' "
        + "and (json_data -> 'Person' -> 'PersonData' ->> 'isArchive' is null "
        + "or json_data -> 'Person' -> 'PersonData' ->> 'isArchive' = 'false') "
        + "and json_data -> 'Person' -> 'PersonData' -> 'newFlatInfo' -> 'newFlat' @> cast(:jsonNode as jsonb) ",
        nativeQuery = true)
    List<DocumentData> getPersonsByNewFlatCcoUnom(@Nonnull @Param("jsonNode") String jsonNode);

    /**
     * Получить Жителей по УНОМу и номеру квартиры заселяемого дома.
     *
     * @param jsonUnomNode нода с unom
     * @param jsonFlatNode нода с номером квартиры
     * @return список жителей
     */
    @Query(value = "select * from documents where doc_type = 'PERSON' "
        + "and json_data -> 'Person' -> 'PersonData' -> 'newFlatInfo' -> 'newFlat' @> cast(:jsonUnomNode as jsonb) "
        + "and json_data -> 'Person' -> 'PersonData' -> 'newFlatInfo' -> 'newFlat' @> cast(:jsonFlatNode as jsonb) ",
        nativeQuery = true)
    List<DocumentData> getPersonsByNewFlatCcoUnomAndFlatNum(
        @Nonnull @Param("jsonUnomNode") String jsonUnomNode,
        @Nonnull @Param("jsonFlatNode") String jsonFlatNode
    );

    /**
     * Получить список PersonDocument по фио, unom.
     *
     * @param fullName fullName
     * @param unom     уном расселяемого дома
     * @return список DocumentData
     */
    @Query(value = "select * from documents "
        + "where json_data -> 'Person' -> 'PersonData' ->> 'FIO' = cast(:fullName as text) "
        + "and json_data -> 'Person' -> 'PersonData' ->> 'UNOM' = cast(:unom as text) "
        + "and doc_type = 'PERSON'",
        nativeQuery = true)
    List<DocumentData> fetchPersonByFullNameAndUnom(@Param("fullName") String fullName,
                                                    @Param("unom") String unom);

    /**
     * Получает всех жителей с некорректным relocationStatus.
     *
     * @return список DocumentData
     */
    @Query(value = "select *"
        + "  from documents"
        + " where doc_type = 'PERSON'"
        + "   and (json_data -> 'Person' -> 'PersonData' -> 'releaseFlat' is not null"
        + "          and json_data -> 'Person' -> 'PersonData' ->> 'relocationStatus' <> '15'"
        + "   or json_data -> 'Person' -> 'PersonData' -> 'releaseFlat' is null"
        + "          and json_data -> 'Person' -> 'PersonData' -> 'keysIssue' is not null"
        + "          and json_data -> 'Person' -> 'PersonData' ->> 'relocationStatus' <> '14'"
        + "   )",
        nativeQuery = true)
    List<DocumentData> fetchByIncorrectRelocationStatus();

    /**
     * Получить проекцию жителя на момент осмотра по идентификатору документа.
     *
     * @param personDocumentId идентификатор документа жителя
     * @return проекция жильца
     */
    @Query(
        value = "select id,"
            + "     json_data -> 'Person' -> 'PersonData' ->> 'personID'     as personId,"
            + "     json_data -> 'Person' -> 'PersonData' ->> 'FIO'          as fullName,"
            + "     json_data -> 'Person' -> 'PersonData' ->> 'LastName'     as lastName,"
            + "     json_data -> 'Person' -> 'PersonData' ->> 'FirstName'    as firstName,"
            + "     json_data -> 'Person' -> 'PersonData' ->> 'MiddleName'   as middleName,"
            + "     json_data -> 'Person' -> 'PersonData' ->> 'birthDate'    as birthDate,"
            + "     json_data -> 'Person' -> 'PersonData' ->> 'statusLiving' as statusLiving"
            + " from documents "
            + "where doc_type = 'PERSON'"
            + "      and id = :personDocumentId",
        nativeQuery = true)
    Optional<TenantProjection> fetchTenantById(@Param("personDocumentId") final String personDocumentId);

    /**
     * Получает список айди жителей для который не извлегались данные по квартирам из писем с предложением.
     */
    @Query(value = "with offerLetter AS ("
        + "         SELECT id                                                    as person_id,"
        + "                offerLetter ->> 'flatData'                            as flat_data,"
        + "                offerLetter ->> 'letterId'                            as letter_id"
        + "         FROM documents,"
        + "              jsonb_array_elements("
        + "                json_data -> 'Person' -> 'PersonData' -> 'offerLetters' -> 'offerLetter') as offerLetter"
        + "         WHERE doc_type = 'PERSON'"
        + "               AND COALESCE(json_data -> 'Person' -> 'PersonData' ->> 'isArchive', 'false') = 'false'"
        + "     )"
        + " SELECT distinct person_id from documents person"
        + "    JOIN offerLetter ON person.id = offerLetter.person_id"
        + " where offerLetter.flat_data is NULL", nativeQuery = true)
    List<String> fetchPersonIdsForOfferLetterDataExtraction();

    @Query(
        value = "select id,"
            + "         json_data -> 'Person' -> 'PersonData' ->> 'flatID'    as flatId,"
            + "         json_data -> 'Person' -> 'PersonData' ->> 'flatNum'   as flatNum,"
            + "         json_data -> 'Person' -> 'PersonData' ->> 'affairId'  as affairId,"
            + "         json_data -> 'Person' -> 'PersonData' ->> 'FIO'       as fio,"
            + "         json_data -> 'Person' -> 'PersonData' ->> 'birthDate' as birthDate,"
            + "         string_agg(coalesce(roomNumbers, ''), ', ')           as roomNum"
            + " from documents"
            + " left join jsonb_array_elements_text(json_data -> 'Person' -> 'PersonData' -> 'roomNum') as roomNumbers"
            + "     on true"
            + " where doc_type = 'PERSON'"
            + "  and json_data -> 'Person' -> 'PersonData' ->> 'UNOM' = :unom"
            + " group by id",
        nativeQuery = true
    )
    List<FlatTenantProjection> fetchAllTenantsByUnom(@Param("unom") final String unom);

    @Query(value = "select distinct on (id) docs.*"
        + " from documents docs,"
        + "     jsonb_array_elements("
        + "             json_data -> 'Person' -> 'PersonData' -> 'offerLetters' -> 'offerLetter') as letters,"
        + "     jsonb_array_elements("
        + "             json_data -> 'Person' -> 'PersonData' -> 'newFlatInfo' -> 'newFlat') as newFlats,"
        + "     jsonb_array_elements("
        + "             letters -> 'files' -> 'file') as files"
        + " where doc_type = 'PERSON'"
        + "  and newFlats ->> 'letterId' = letters ->> 'letterId'"
        + "  and letters ->> 'flatNum' is null"
        + "  and letters ->> 'unom' is null"
        + "  and files ->> 'fileType' = '3'",
        nativeQuery = true)
    List<DocumentData> fetchAllPersonsWithIncorrectOfferLetters();

    @Query(value = "with contracts as ("
        + "    select id personDocumentId, contract ->> 'orderId' orderId"
        + "    from documents,"
        + "         jsonb_array_elements("
        + "                  json_data -> 'Person' -> 'PersonData' -> 'contracts' -> 'contract'"
        + "         ) as contract"
        + "    where doc_type = 'PERSON'"
        + "    group by id, orderId having count(*) > 1"
        + " )"
        + " select distinct on (id) docs.*"
        + "   from documents docs, contracts"
        + "  where docs.id = contracts.personDocumentId",
        nativeQuery = true)
    List<DocumentData> fetchAllPersonsWithContractDuplicates();

    /**
     * Получает всех жителей с незавершенным процессом переселения по УНОМ дома.
     *
     * @param unom УНОМ дома
     * @return список DocumentData
     */
    @Query(value = "select *"
        + "  from documents"
        + "  where doc_type = 'PERSON'"
        + "   and (json_data -> 'Person' -> 'PersonData' -> 'releaseFlat' is null"
        + "          and json_data -> 'Person' -> 'PersonData' ->> 'relocationStatus' <> '15'"
        + "          and json_data -> 'Person' -> 'PersonData' ->> 'UNOM' = :unom)",
        nativeQuery = true)
    List<DocumentData> fetchNotResettledPersonsByUnom(@Param("unom") final String unom);

    @Query(value = "select *"
        + "  from documents"
        + "  where doc_type = 'PERSON'"
        + "  and (json_data -> 'Person' -> 'PersonData' ->> 'isArchive' = 'false' "
        + "  or json_data -> 'Person' -> 'PersonData' ->> 'isArchive' is null) "
        + "          and json_data -> 'Person' -> 'PersonData' ->> 'flatNum' =  cast(:flatNumber as text)"
        + "          and json_data -> 'Person' -> 'PersonData' ->> 'UNOM' = cast(:unom as text)",
        nativeQuery = true)
    List<DocumentData> fetchLiversByUnomAndFlatNumber(
        @Param("unom") final String unom, @Param("flatNumber") final String flatNumber
    );


    @Query(value = "select "
        + "    doc.*"
        + "    from documents doc,"
        + "    jsonb_array_elements("
        + "        json_data -> 'Person' -> 'PersonData' -> 'offerLetters' -> 'offerLetter') as letters"
        + "    where doc_type = 'PERSON'"
        + "    and letters ->> 'flatData' is not null"
        + "    and letters -> 'flatData' ->> 'cadNumber' = cast(:cadastralNumber as text)",
        nativeQuery = true)
    List<DocumentData> fetchPersonsByOfferLetterCadastralNumber(
        @Param("cadastralNumber") final String cadastralNumber
    );

    @Query(
        value = "select doc.id as personDocumentId,"
            + "         json_data -> 'Person' -> 'PersonData' ->> 'birthDate' as birthDate"
            + " from documents doc"
            + " where doc_type = 'PERSON'"
            + " and doc.id in (:personDocumentIds)",
        nativeQuery = true
    )
    List<PersonBirthDateProjection> fetchPersonsBirthDates(
        @Param("personDocumentIds") final List<String> personDocumentIds
    );
}
