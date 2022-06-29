package ru.croc.ugd.ssr.db.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import ru.reinform.cdp.db.model.DocumentData;

import java.util.List;

/**
 * DAO по работе с данными жителя из внешних систем.
 */
public interface PfrSnilsRequestDao extends Repository<DocumentData, String> {

    @Query(value = "SELECT * FROM documents"
        + " WHERE doc_type='PFR-SNILS-REQUEST'"
        + "   AND json_data -> 'pfrSnilsRequest' -> 'pfrSnilsRequestData'"
        + "     ->> 'serviceNumber' = cast(:serviceNumber as text)",
        nativeQuery = true)
    List<DocumentData> fetchRequestsByServiceNumber(@Param("serviceNumber") final String serviceNumber);

    @Query(value = "select * from (select distinct on (json_data -> 'pfrSnilsRequest'"
        + " -> 'pfrSnilsRequestData' -> 'requestCriteria' ->> 'personDocumentId') *"
        + "  from documents"
        + " where doc_type = 'PFR-SNILS-REQUEST'"
        + " order by (json_data -> 'pfrSnilsRequest' -> 'pfrSnilsRequestData' ->"
        + " 'requestCriteria' ->> 'personDocumentId'),"
        + " cast((json_data -> 'pfrSnilsRequest' -> 'pfrSnilsRequestData' ->>"
        + " 'creationDateTime') as timestamp) desc) t"
        + " where json_data -> 'pfrSnilsRequest' -> 'pfrSnilsRequestData' ->> 'errorDescription'"
        + " = 'Превышен суточный лимит запросов на документ'",
        nativeQuery = true)
    List<DocumentData> fetchDocumentsWithLimitedRequests();
}
