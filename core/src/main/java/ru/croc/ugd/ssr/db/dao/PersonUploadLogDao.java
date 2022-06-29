package ru.croc.ugd.ssr.db.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import ru.reinform.cdp.db.model.DocumentData;

import java.util.List;

/**
 * DAO для документа с логами загрузки жителей.
 */
public interface PersonUploadLogDao extends Repository<DocumentData, String> {

    @Query(value = "select * from documents"
        + "    where doc_type = 'PERSON-UPLOAD-LOG'"
        + "    and json_data -> 'personUploadLog' -> 'personUploadLogData'"
        + "    ->> 'status' = 'В процессе'",
        nativeQuery = true)
    List<DocumentData> findAllInProgressDocuments();
}
