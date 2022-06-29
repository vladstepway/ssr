package ru.croc.ugd.ssr.db.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import ru.reinform.cdp.db.model.DocumentData;

import java.util.List;

/**
 * DAO для сведения о судах.
 */
public interface CourtInfoDao extends Repository<DocumentData, String> {

    @Query(value = "select * from documents"
        + " where doc_type = 'COURT-INFO'"
        + "   and json_data -> 'courtInfo' -> 'courtInfoData' "
        + "       ->> 'affairId' = cast(:affairId as text) ",
        nativeQuery = true)
    List<DocumentData> fetchAllByAffairId(@Param("affairId") final String affairId);

    @Query(value = "select * from documents"
        + " where doc_type = 'COURT-INFO'"
        + "   and json_data -> 'courtInfo' -> 'courtInfoData' "
        + "       ->> 'caseId' = cast(:caseId as text) "
        + "   and json_data -> 'courtInfo' -> 'courtInfoData' "
        + "       ->> 'affairId' = cast(:affairId as text)",
        nativeQuery = true)
    List<DocumentData> fetchAllByCaseIdAndAffairId(
        @Param("caseId") final String caseId, @Param("affairId") final String affairId
    );
}
