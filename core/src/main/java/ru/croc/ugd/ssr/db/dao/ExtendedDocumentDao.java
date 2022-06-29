package ru.croc.ugd.ssr.db.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.reinform.cdp.db.model.DocumentData;

import java.util.List;
import javax.annotation.Nonnull;

/**
 * DAO по работе с DocumentData.
 */
public interface ExtendedDocumentDao extends JpaRepository<DocumentData, String> {

    /**
     * Постранчный поиск по документам с критерией поиска.
     *
     * @param jsonNode jsonNode
     * @param docType docType
     * @param pageable pageable
     * @return list DocumentData
     */
    @Query(
            value = "select * from documents where json_data @> cast(:jsonNode as jsonb) and doc_type = :docType",
            countQuery = "select count(*) from documents where json_data @> cast(:jsonNode as jsonb)"
                    + " and doc_type = :docType",
            nativeQuery = true
    )
    List<DocumentData> findByJsonNodeAndDocType(@Nonnull @Param("jsonNode") String jsonNode,
                                                @Nonnull @Param("docType") String docType,
                                                Pageable pageable);

    /**
     * countAllJsonNodeAndDocType.
     *
     * @param jsonNode jsonNode
     * @param docType docType
     * @return count
     */
    @Query(
            value = "select count(*) from documents where json_data @> cast(:jsonNode as jsonb)"
                    + " and doc_type = :docType",
            nativeQuery = true
    )
    long countAllJsonNodeAndDocType(@Nonnull @Param("jsonNode") String jsonNode,
                                    @Nonnull @Param("docType") String docType);

}
