package ru.croc.ugd.ssr.db.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import ru.reinform.cdp.db.model.DocumentData;

import java.util.List;
import java.util.Optional;

public interface SsrCcoDao extends Repository<DocumentData, String> {

    @Query(value = "SELECT * FROM documents "
        + " WHERE doc_type='SSR-CCO' "
        + "   AND json_data -> 'ssrCco' -> 'ssrCcoData' ->> 'psDocumentId' = cast(:psDocumentId as text)",
        nativeQuery = true)
    List<DocumentData> fetchByPsDocumentId(@Param("psDocumentId") final String psDocumentId);

    @Query(value = "select max(json_data -> 'ssrCco' -> 'ssrCcoData' ->> 'updateDateTime')"
        + " from documents where doc_type='SSR-CCO'",
        nativeQuery = true)
    Optional<String> fetchLastUpdateDateTime();

    @Query(value = "SELECT * FROM documents "
        + "WHERE doc_type = 'SSR-CCO' "
        + "  AND json_data -> 'ssrCco' -> 'ssrCcoData' ->> 'unom' = cast(:unom as text)",
        nativeQuery = true)
    List<DocumentData> fetchByUnom(@Param("unom") final String unom);

    /**
     * Все документы.
     *
     * @return Список всех документов.
     */
    @Query(value = "SELECT * "
        + "FROM documents "
        + "WHERE doc_type = 'SSR-CCO'",
        nativeQuery = true)
    List<DocumentData> fetchAll();

}
