package ru.croc.ugd.ssr.db.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import ru.croc.ugd.ssr.db.projection.ApartmentDefectProjection;
import ru.reinform.cdp.db.model.DocumentData;

import java.util.List;
import javax.annotation.Nonnull;

/**
 * DAO для подтверждений сведений об устранении дефектов.
 */
public interface ApartmentDefectConfirmationDao extends Repository<DocumentData, String> {

    String DEFECTS_CONDITION = " from documents,"
        + "      jsonb_array_elements(json_data -> 'apartmentDefectConfirmation' ->"
        + "                           'apartmentDefectConfirmationData' -> 'defects' -> 'defect') as defects"
        + " where doc_type = 'APARTMENT-DEFECT-CONFIRMATION'"
        + "   and id = :id"
        + "   and (:flat is null"
        + "    or defects -> 'flatData' ->> 'flat' = cast(:flat as text))"
        + "   and (:flatElement is null"
        + "    or defects ->> 'flatElement' = cast(:flatElement as text))"
        + "   and (:description is null"
        + "    or defects ->> 'description' = cast(:description as text))"
        + "   and (:isEliminated is null"
        + "    or defects ->> 'isEliminated' = cast(:isEliminated as text))"
        + "   and (cast(:skipApproved as text) = 'false'"
        + "    or coalesce(defects ->> 'isApproved', 'false') = 'false')"
        + "   and (cast(:skipExcluded as text) = 'false'"
        + "    or coalesce(defects ->> 'isExcluded', 'false') = 'false')";

    @Query(value = "select defects ->> 'id'                                 as id,"
        + "       defects ->> 'flatElement'                                 as flatElement,"
        + "       defects ->> 'description'                                 as description,"
        + "       defects ->> 'isEliminated'                                as isEliminated,"
        + "       defects ->> 'apartmentInspectionId'                       as apartmentInspectionId,"
        + "       defects ->> 'affairId'                                    as affairId,"
        + "       defects -> 'flatData' ->> 'flat'                          as flat,"
        + "       cast(defects -> 'flatData' ->> 'floor' as integer)        as floor,"
        + "       defects -> 'flatData' ->> 'entrance'                      as entrance,"
        + "       defects -> 'eliminationData' ->> 'oldEliminationDate'     as oldEliminationDate,"
        + "       defects -> 'eliminationData' ->> 'eliminationDate'        as eliminationDate,"
        + "       defects -> 'eliminationData' ->> 'eliminationDateComment' as eliminationDateComment,"
        + "       defects -> 'eliminationData' ->> 'itemRequired'           as itemRequired,"
        + "       defects -> 'eliminationData' ->> 'itemRequiredComment'    as itemRequiredComment,"
        + "       defects -> 'eliminationData' ->> 'isNotDefect'            as isNotDefect,"
        + "       defects -> 'eliminationData' ->> 'notDefectComment'       as notDefectComment"
        + DEFECTS_CONDITION,
        countQuery = "select count(*)"
            + DEFECTS_CONDITION,
        nativeQuery = true)
    Page<ApartmentDefectProjection> fetchDefects(
        @Param("id") final String id,
        @Param("flat") final String flat,
        @Param("flatElement") final String flatElement,
        @Param("description") final String description,
        @Param("isEliminated") final Boolean isEliminated,
        @Param("skipApproved") final boolean skipApproved,
        @Param("skipExcluded") final boolean skipExcluded,
        final Pageable pageable
    );

    @Query(value = "select id from documents where doc_type = 'APARTMENT-DEFECT-CONFIRMATION' "
        + "and json_data -> 'apartmentDefectConfirmation' -> 'apartmentDefectConfirmationData' ->> 'pending' = 'true'",
        nativeQuery = true)
    List<String> fetchPendingDocumentIdsForRemoving();

    @Query(
        value = "select * from documents where doc_type = 'APARTMENT-DEFECT-CONFIRMATION'"
            + " and json_data -> 'apartmentDefectConfirmation' -> 'apartmentDefectConfirmationData'"
            + " -> 'ccoData' ->> 'unom' = cast(:unom as text) ",
        nativeQuery = true
    )
    List<DocumentData> fetchByUnom(
        @Nonnull @Param("unom") String unom
    );

}
