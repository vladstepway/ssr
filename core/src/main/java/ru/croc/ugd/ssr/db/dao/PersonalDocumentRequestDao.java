package ru.croc.ugd.ssr.db.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import ru.reinform.cdp.db.model.DocumentData;

import java.util.List;
import java.util.Optional;

/**
 * DAO для запросов документов.
 */
public interface PersonalDocumentRequestDao extends Repository<DocumentData, String> {

    @Query(value = "select * from documents"
        + " where doc_type = 'PERSONAL-DOCUMENT-REQUEST'"
        + "   and json_data -> 'personalDocumentRequest' -> 'personalDocumentRequestData' "
        + "       ->> 'personDocumentId' = :personDocumentId"
        + "   and json_data -> 'personalDocumentRequest' -> 'personalDocumentRequestData' "
        + "       ->> 'applicationDocumentId' is null"
        + " order by json_data -> 'personalDocumentRequest' -> 'personalDocumentRequestData' "
        + "       ->> 'requestDateTime' desc limit 1",
        nativeQuery = true)
    Optional<DocumentData> findLastActiveRequestByPersonDocumentId(
        @Param("personDocumentId") final String personDocumentId
    );

    @Query(value = "select * from documents"
        + " where doc_type = 'PERSONAL-DOCUMENT-REQUEST'"
        + "   and json_data -> 'personalDocumentRequest' -> 'personalDocumentRequestData' "
        + "       ->> 'personDocumentId' = :personDocumentId",
        nativeQuery = true)
    List<DocumentData> findByPersonDocumentId(@Param("personDocumentId") final String personDocumentId);
}
