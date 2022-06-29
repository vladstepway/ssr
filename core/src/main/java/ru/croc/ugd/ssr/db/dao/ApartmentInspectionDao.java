package ru.croc.ugd.ssr.db.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import ru.croc.ugd.ssr.db.projection.ApartmentDefectProjection;
import ru.reinform.cdp.db.model.DocumentData;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;

/**
 * DAO по работе с ApartmentInspection.
 */
public interface ApartmentInspectionDao extends Repository<DocumentData, String> {

    /**
     * Получить ApartmentInspection по personId.
     *
     * @param personId PersonId
     * @return ApartmentInspection
     */
    @Query(value = "SELECT * FROM documents "
        + "WHERE doc_type='APARTMENT-INSPECTION' "
        + "AND json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData'"
        + " ->> 'personID' = :personId "
        + "AND json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData'"
        + " ->> 'processInstanceId' IS NOT NULL "
        + "AND json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData'"
        + " ->> 'acceptedDefectsDate' IS NULL "
        + "AND (json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'isRemoved' != 'true'"
        + "  OR json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' -> 'isRemoved' IS NULL) "
        + "LIMIT 1", nativeQuery = true)
    Optional<DocumentData> fetchDocWithStartedProcessByPersonId(
        @Nonnull @Param("personId") String personId
    );

    /**
     * Получить все акты по дефектам.
     * @param personId personId
     * @param commissionInspectionId commissionInspectionId
     * @return ApartmentInspections
     */
    @Query(value = "SELECT * FROM documents "
        + "WHERE (json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'isRemoved' != 'true'"
        + "  OR json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' -> 'isRemoved' IS NULL) "
        + " AND json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'pending' != 'true' "
        + " AND (:personId IS NULL "
        + "   OR json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'personID' = "
        + "     CAST(:personId AS TEXT)) "
        + " AND (:commissionInspectionId IS NULL "
        + "   OR json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'commissionInspectionId' = "
        + "     CAST(:commissionInspectionId AS TEXT)) "
        + " AND doc_type='APARTMENT-INSPECTION' ", nativeQuery = true)
    List<DocumentData> findAll(
        @Param("personId") String personId,
        @Param("commissionInspectionId") String commissionInspectionId
    );

    /**
     * Получить все ApartmentInspection.
     * @return ApartmentInspection
     */
    @Query(value = "SELECT * FROM documents "
        + "WHERE doc_type='APARTMENT-INSPECTION' ", nativeQuery = true)
    List<DocumentData> findAll();

    /**
     * Получить ApartmentInspection по personId.
     *
     * @param personId PersonId
     * @return ApartmentInspection
     */
    @Query(value = "SELECT * FROM documents "
        + "WHERE doc_type = 'APARTMENT-INSPECTION' "
        + "AND json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData'"
        + " ->> 'personID' = :personId ", nativeQuery = true)
    List<DocumentData> fetchByPersonId(
        @Nonnull @Param("personId") String personId
    );

    /**
     * Получить ApartmentInspection по unom и flatNumber.
     *
     * @param unom       уном
     * @param flatNumber номер квартиры
     * @return список ApartmentInspection
     */
    @Query(
        value = "SELECT * FROM documents WHERE doc_type = 'APARTMENT-INSPECTION' "
            + "AND json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'unom' = :unom "
            + "AND json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'flat' = :flatNumber ",
        nativeQuery = true
    )
    List<DocumentData> fetchByUnomAndFlatNumber(
        @Nonnull @Param("unom") String unom, @Nonnull @Param("flatNumber") String flatNumber
    );

    @Query(
        value = "SELECT id FROM documents WHERE doc_type = 'APARTMENT-INSPECTION' "
            + "AND json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'pending' = 'true' "
            + "AND json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' -> 'commissionInspectionId' IS NULL",
        nativeQuery = true
    )
    List<String> fetchPendingActIdsForRemoval();

    /**
     * Получить ApartmentInspection по unom и flatNumber и personId.
     *
     * @param unom       уном
     * @param flatNumber номер квартиры
     * @param personId   personId
     * @return список ApartmentInspection
     */
    @Query(
        value = "select "
            + "    * "
            + "from "
            + "    ssr.documents "
            + "where "
            + "    doc_type = 'APARTMENT-INSPECTION' "
            + "    and json_data @> cast('{"
            + "        \"ApartmentInspection\": {"
            + "            \"ApartmentInspectionData\": {"
            + "                \"unom\": \"' || :unom || '\", "
            + "                \"flat\": \"' || :flatNumber || '\", "
            + "                \"personID\": \"' || :personId || '\""
            + "            }"
            + "        }"
            + "    }' as jsonb)",
        nativeQuery = true
    )
    List<DocumentData> fetchByUnomAndFlatNumberAndPersonId(
        @Nonnull @Param("unom") String unom,
        @Nonnull @Param("flatNumber") String flatNumber,
        @Nonnull @Param("personId") String personId
    );

    /**
     * Получить ApartmentInspection по unom.
     *
     * @param unom       уном
     * @return список ApartmentInspection
     */
    @Query(
        value = "SELECT * FROM documents WHERE doc_type = 'APARTMENT-INSPECTION' "
            + "AND json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'unom' = :unom ",
        nativeQuery = true
    )
    List<DocumentData> fetchByUnom(
        @Nonnull @Param("unom") String unom
    );

    String DEFECTS_CONDITION = " from documents,"
        + "      jsonb_array_elements(json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->"
        + "                           'apartmentDefects') as defects"
        + " where doc_type = 'APARTMENT-INSPECTION'"
        + "  and json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'pending' != 'true'"
        + "  and (json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'isRemoved' != 'true'"
        + "   or json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' -> 'isRemoved' is null)"
        + "   and json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' -> 'delayReason' is not null"
        + "   and json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'acceptedDefectsDate' is null"
        + "   and json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'unom' = :unom"
        + "   and (:flat is null"
        + "    or json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'flat' = cast(:flat as text))"
        + "   and (:flatElement is null"
        + "    or defects -> 'ApartmentDefectData' ->> 'flatElement' = cast(:flatElement as text))"
        + "   and (:description is null"
        + "    or defects -> 'ApartmentDefectData' ->> 'description' = cast(:description as text))"
        + "   and (:isEliminated is null"
        + "    or defects -> 'ApartmentDefectData' ->> 'isEliminated' = cast(:isEliminated as text))"
        + "   and (cast(:isDeveloper as text) = 'true'"
        + "    or exists(select 1 from jsonb_array_elements(json_data -> 'ApartmentInspection' -> "
        + "              'ApartmentInspectionData' -> 'generalContractors') generalContractors "
        + "              where generalContractors ->> 'isAssigned' = 'true'))";

    @Query(value = "select id as apartmentInspectionId,"
        + "       json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'flat' as flat,"
        + "       defects -> 'ApartmentDefectData' ->> 'id' as id,"
        + "       defects -> 'ApartmentDefectData' ->> 'flatElement' as flatElement,"
        + "       defects -> 'ApartmentDefectData' ->> 'description' as description,"
        + "       defects -> 'ApartmentDefectData' ->> 'isBlocked' as isBlocked,"
        + "       defects -> 'ApartmentDefectData' ->> 'isEliminated' as isEliminated,"
        + "       coalesce(to_date(defects -> 'ApartmentDefectData' -> 'eliminationData' ->> "
        + "                        'eliminationDate', 'YYYY-MM-DD'), "
        + "                to_date(to_json(json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' -> "
        + "                        'delayReason' -> -1) ->> 'delayDate', 'YYYY-MM-DD')) as eliminationDate,"
        + "       defects -> 'ApartmentDefectData' -> 'eliminationData' ->> 'eliminationDateComment' "
        + "                  as eliminationDateComment,"
        + "       defects -> 'ApartmentDefectData' -> 'eliminationData' ->> 'itemRequired' as itemRequired,"
        + "       defects -> 'ApartmentDefectData' -> 'eliminationData' ->> 'itemRequiredComment' "
        + "                  as itemRequiredComment,"
        + "       defects -> 'ApartmentDefectData' -> 'eliminationData' ->> 'isNotDefect' as isNotDefect,"
        + "       defects -> 'ApartmentDefectData' -> 'eliminationData' ->> 'notDefectComment' as notDefectComment"
        + DEFECTS_CONDITION,
        countQuery = "select count(*)"
            + DEFECTS_CONDITION,
        nativeQuery = true)
    Page<ApartmentDefectProjection> fetchDefects(
        @Param("unom") final String unom,
        @Param("flat") final String flat,
        @Param("flatElement") final String flatElement,
        @Param("description") final String description,
        @Param("isEliminated") final Boolean isEliminated,
        @Param("isDeveloper") final Boolean isDeveloper,
        final Pageable pageable
    );

    @Query(value = "select id as apartmentInspectionId,"
        + "       json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'flat' as flat,"
        + "       defects -> 'ApartmentDefectData' ->> 'id' as id,"
        + "       defects -> 'ApartmentDefectData' ->> 'flatElement' as flatElement,"
        + "       defects -> 'ApartmentDefectData' ->> 'description' as description,"
        + "       defects -> 'ApartmentDefectData' ->> 'isBlocked' as isBlocked,"
        + "       defects -> 'ApartmentDefectData' ->> 'isEliminated' as isEliminated,"
        + "       coalesce(to_date(defects -> 'ApartmentDefectData' -> 'eliminationData' ->> "
        + "                        'eliminationDate', 'YYYY-MM-DD'), "
        + "                to_date(to_json(json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' -> "
        + "                        'delayReason' -> -1) ->> 'delayDate', 'YYYY-MM-DD')) as eliminationDate,"
        + "       defects -> 'ApartmentDefectData' -> 'eliminationData' ->> 'eliminationDateComment' "
        + "                  as eliminationDateComment,"
        + "       defects -> 'ApartmentDefectData' -> 'eliminationData' ->> 'itemRequired' as itemRequired,"
        + "       defects -> 'ApartmentDefectData' -> 'eliminationData' ->> 'itemRequiredComment' "
        + "                  as itemRequiredComment,"
        + "       defects -> 'ApartmentDefectData' -> 'eliminationData' ->> 'isNotDefect' as isNotDefect,"
        + "       defects -> 'ApartmentDefectData' -> 'eliminationData' ->> 'notDefectComment' as notDefectComment"
        + " from documents,"
        + "      jsonb_array_elements(json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->"
        + "                           'apartmentDefects') as defects"
        + " where doc_type = 'APARTMENT-INSPECTION'"
        + "  and json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'pending' != 'true'"
        + "  and (json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'isRemoved' != 'true'"
        + "   or json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' -> 'isRemoved' is null)"
        + "   and (cast(:skipNotPlannedElimination as text) = 'false' "
        + "    or json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' -> 'delayReason' is not null)"
        + "   and json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'acceptedDefectsDate' is null"
        + "   and json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'unom' = :unom"
        + "   and (:flat is null"
        + "    or json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'flat' = cast(:flat as text))"
        + "   and (:actNum is null"
        + "    or json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'actNum' = cast(:actNum as text))"
        + "   and (cast(cast(:filingDate as text) as date) is null"
        + "    or cast(json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' "
        + "       ->> 'filingDate' as date) = cast(cast(:filingDate as text) as date))"
        + "   and (:flatElement is null"
        + "    or defects -> 'ApartmentDefectData' ->> 'flatElement' = cast(:flatElement as text))"
        + "   and (:description is null"
        + "    or defects -> 'ApartmentDefectData' ->> 'description' = cast(:description as text))"
        + "   and (:isEliminated is null"
        + "    or defects -> 'ApartmentDefectData' ->> 'isEliminated' = cast(:isEliminated as text))",
        nativeQuery = true)
    List<ApartmentDefectProjection> fetchDefects(
        @Param("unom") final String unom,
        @Param("flat") final String flat,
        @Param("actNum") final String actNum,
        @Param("filingDate") final LocalDate filingDate,
        @Param("flatElement") final String flatElement,
        @Param("description") final String description,
        @Param("isEliminated") final Boolean isEliminated,
        @Param("skipNotPlannedElimination") final boolean skipNotPlannedElimination
    );

    @Query(value = "select * "
        + " from documents "
        + "where doc_type = 'APARTMENT-INSPECTION' "
        + "  and json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'pending' != 'true'"
        + "  and (json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'isRemoved' != 'true'"
        + "   or json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' -> 'isRemoved' is null)"
        + "  and json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'acceptedDefectsDate' is null"
        + "  and json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'unom' = :unom "
        + "  and (:flat is null"
        + "   or json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'flat' = cast(:flat as text))"
        + "  and (:actNum is null"
        + "   or json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'actNum' = cast(:actNum as text))"
        + "  and (cast(cast(:filingDate as text) as date) is null"
        + "   or cast(json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' "
        + "   ->> 'filingDate' as date) = cast(cast(:filingDate as text) as date))",
        nativeQuery = true)
    List<DocumentData> fetchActiveByUnomAndFlatAndActNumAndFillingDate(
        @Param("unom") final String unom,
        @Param("flat") final String flat,
        @Param("actNum") final String actNum,
        @Param("filingDate") final LocalDate filingDate
    );
}
