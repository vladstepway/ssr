package ru.croc.ugd.ssr.db.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import ru.reinform.cdp.db.model.DocumentData;

import java.util.List;

/**
 * DAO для заявлений на предоставление документов.
 */
public interface PersonalDocumentApplicationDao extends Repository<DocumentData, String> {

    @Query(value = "select exists (select 1 "
        + "  from documents "
        + " where doc_type = 'PERSONAL-DOCUMENT-APPLICATION'"
        + "   and json_data -> 'personalDocumentApplication' -> 'personalDocumentApplicationData' "
        + "      ->> 'personDocumentId' = :personDocumentId)",
        nativeQuery = true)
    boolean existsByPersonDocumentId(@Param("personDocumentId") final String personDocumentId);

    @Query(value = "select exists (select 1 "
        + "  from documents "
        + " where doc_type = 'PERSONAL-DOCUMENT-APPLICATION'"
        + "   and json_data -> 'personalDocumentApplication' -> 'personalDocumentApplicationData' ->> 'eno' = :eno)",
        nativeQuery = true)
    boolean existsByEno(@Param("eno") final String eno);

    @Query(value = "select * from documents"
        + " where doc_type = 'PERSONAL-DOCUMENT-APPLICATION'"
        + "   and json_data -> 'personalDocumentApplication' -> 'personalDocumentApplicationData' "
        + "       ->> 'personDocumentId' = :personDocumentId"
        + "   and (:includeRequestedApplications = 'true' "
        + "        or json_data -> 'personalDocumentApplication' -> 'personalDocumentApplicationData' "
        + "           ->> 'requestDocumentId' is null)",
        nativeQuery = true)
    List<DocumentData> findAll(
        @Param("personDocumentId") final String personDocumentId,
        @Param("includeRequestedApplications") final boolean includeRequestedApplications
    );
}
