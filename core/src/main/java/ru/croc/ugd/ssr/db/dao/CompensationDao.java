package ru.croc.ugd.ssr.db.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import ru.reinform.cdp.db.model.DocumentData;

import java.util.List;
import java.util.Optional;

/**
 * DAO для квартир на компенсацию.
 */
public interface CompensationDao extends Repository<DocumentData, String> {

    @Query(value = "select * "
        + "from documents "
        + "where doc_type = 'COMPENSATION' "
        + "  and json_data -> 'compensation' -> 'compensationData' ->> 'unom' = :unom",
        nativeQuery = true)
    List<DocumentData> findAllByUnom(
        @Param("unom") final String unom
    );

    @Query(value = "select * "
        + "from documents "
        + "where doc_type = 'COMPENSATION' "
        + "  and json_data -> 'compensation' -> 'compensationData' ->> 'resettlementRequestId' = :resettlementRequestId"
        + "  and json_data -> 'compensation' -> 'compensationData' ->> 'realEstateId' = :realEstateId",
        nativeQuery = true)
    Optional<DocumentData> findByResettlementRequestIdAndRealEstateId(
        @Param("resettlementRequestId") final String resettlementRequestId,
        @Param("realEstateId") final String realEstateId
    );
}
