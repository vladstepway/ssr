package ru.croc.ugd.ssr.db.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import ru.reinform.cdp.db.model.DocumentData;

import java.util.stream.Stream;

/**
 * DAO ответов из БР.
 */
public interface SsrSmevResponseDao extends Repository<DocumentData, String> {

    @Query(value = "select * "
        + "from documents "
        + "where doc_type = 'SSR-SMEV-RESPONSE' "
        + "  and json_data -> 'ssrSmevResponse' -> 'ssrSmevResponseData' ->> 'processEndDateTime' is null",
        nativeQuery = true)
    Stream<DocumentData> findByProcessEndDateTimeIsNullAndStream();
}
