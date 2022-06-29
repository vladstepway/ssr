package ru.croc.ugd.ssr.db.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import ru.reinform.cdp.db.model.DocumentData;

import java.time.LocalDateTime;

/**
 * DAO для запросов на сверку жителей.
 */
public interface AffairCollationDao extends Repository<DocumentData, String> {

    @Query(value = "select exists"
        + " (select 1"
        + "     from documents "
        + "     where doc_type = 'AFFAIR-COLLATION' "
        + "       and json_data -> 'affairCollation' -> 'affairCollationData' ->> 'affairId' = cast(:affairId as text)"
        + "       and cast(json_data -> 'affairCollation' -> 'affairCollationData'"
        + "          ->> 'requestDateTime' as timestamp) > cast(cast(:requestDateTime as text) as timestamp))",
        nativeQuery = true)
    boolean existsByAffairIdAndRequestDateTimeAfter(
        @Param("affairId") final String affairId,
        @Param("requestDateTime") final LocalDateTime requestDateTime
    );
}
