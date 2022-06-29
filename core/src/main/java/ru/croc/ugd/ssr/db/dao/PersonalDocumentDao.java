package ru.croc.ugd.ssr.db.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import ru.reinform.cdp.db.model.DocumentData;

import java.util.List;
import java.util.Optional;

/**
 * DAO для сведений о документах.
 */
public interface PersonalDocumentDao extends Repository<DocumentData, String> {

    @Query(value = "select * from documents"
        + " where doc_type = 'PERSONAL-DOCUMENT'"
        + "   and json_data -> 'personalDocument' -> 'personalDocumentData' "
        + "       ->> 'applicationDocumentId' = :applicationDocumentId",
        nativeQuery = true)
    Optional<DocumentData> findByApplicationDocumentId(
        @Param("applicationDocumentId") final String applicationDocumentId
    );

    @Query(value = "select * from documents"
        + " where doc_type = 'PERSONAL-DOCUMENT'"
        + "   and json_data -> 'personalDocument' -> 'personalDocumentData' "
        + "       ->> 'affairId' = :affairId",
        nativeQuery = true)
    List<DocumentData> findAllByAffairId(@Param("affairId") final String affairId);

    @Query(value = "select * from documents"
        + " where doc_type = 'PERSONAL-DOCUMENT'"
        + "   and json_data -> 'personalDocument' -> 'personalDocumentData' "
        + "       ->> 'unionFileStoreId' = :unionFileStoreId",
        nativeQuery = true)
    Optional<DocumentData> findByUnionFileStoreId(@Param("unionFileStoreId") final String unionFileStoreId);

    @Query(value = "select * from documents"
        + " where doc_type = 'PERSONAL-DOCUMENT'"
        + "   and json_data -> 'personalDocument' -> 'personalDocumentData' "
        + "       ->> 'affairId' = :affairId"
        + " order by create_date desc"
        + " limit 1",
        nativeQuery = true)
    Optional<DocumentData> findLastByAffairId(@Param("affairId") final String affairId);
}
