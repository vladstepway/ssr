package ru.croc.ugd.ssr.db.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import ru.reinform.cdp.db.model.DocumentData;

import java.time.LocalDate;
import java.util.List;

public interface FlowReportedErrorDao extends Repository<DocumentData, String> {

    @Query(value = "SELECT * FROM documents"
        + "       WHERE doc_type='FLOW-REPORTED-ERROR'"
        + " AND (json_data -> 'flowReportedError' -> 'flowReportedErrorData' ->> 'isPublished' != 'true'"
        + " OR json_data -> 'flowReportedError' -> 'flowReportedErrorData' ->> 'isPublished' IS NULL )"
        + "         AND (CAST(json_data -> 'flowReportedError' -> 'flowReportedErrorData'"
        + "                     ->> 'reportedDate' AS DATE)) = :reportDate",
        nativeQuery = true)
    List<DocumentData> findFlowReportedErrorsByReportDate(
        @Param("reportDate") final LocalDate reportDate
    );

    /**
     * Searches such flow error for which only single person exists in DB.
     */
    @Query(value = "with persons as ("
        + "    select json_data -> 'Person' -> 'PersonData' ->> 'affairId' as affaidId, "
        + "           json_data -> 'Person' -> 'PersonData' ->> 'personID' as personId, "
        + "           count(*) "
        + "    from documents "
        + "    where doc_type = 'PERSON' "
        + "    group by affaidId, personId "
        + "    having count(*) = 1 "
        + ") "
        + "select * "
        + "from documents "
        + "         join persons "
        + "              on persons.affaidId = json_data -> 'flowReportedError' -> 'flowReportedErrorData' "
        + "                                        -> 'personFirst' ->> 'affairId' "
        + "                  and persons.personId = json_data -> 'flowReportedError' -> 'flowReportedErrorData' "
        + "                                             -> 'personFirst' ->> 'personId' "
        + "where doc_type = 'FLOW-REPORTED-ERROR' "
        + "  and COALESCE(documents.json_data -> 'flowReportedError' "
        + "                   -> 'flowReportedErrorData' ->> 'fixed', 'false') = 'false' "
        + "  and documents.json_data -> 'flowReportedError' "
        + "          -> 'flowReportedErrorData' ->> 'originalFlowMessage' IS NOT NULL "
        + "  and documents.json_data -> 'flowReportedError' -> 'flowReportedErrorData' ->> 'errorType' "
        + "    IN ('Житель отсутствует', 'Найдены два одинаковых жителя')",
        nativeQuery = true)
    List<DocumentData> findAvailableForFix();

    @Query(value = "select * "
        + "from documents "
        + "where doc_type = 'FLOW-REPORTED-ERROR' "
        + "  and COALESCE(documents.json_data -> 'flowReportedError' "
        + "                   -> 'flowReportedErrorData' ->> 'fixed', 'false') = 'false' "
        + "  and documents.json_data -> 'flowReportedError' "
        + "          -> 'flowReportedErrorData' ->> 'originalFlowMessage' IS NOT NULL "
        + "  and documents.json_data -> 'flowReportedError' -> 'flowReportedErrorData' ->> 'errorType' "
        + "    IN ('Житель отсутствует', 'Найдены два одинаковых жителя')",
        nativeQuery = true)
    List<DocumentData> findUnfixed();
}
