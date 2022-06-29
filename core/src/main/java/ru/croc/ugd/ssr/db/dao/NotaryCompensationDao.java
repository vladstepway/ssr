package ru.croc.ugd.ssr.db.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import ru.reinform.cdp.db.model.DocumentData;

import java.util.List;

/**
 * DAO для заявлений на возмещение оплаты услуг нотариуса.
 */
public interface NotaryCompensationDao extends Repository<DocumentData, String> {

    @Query(value = "SELECT EXISTS(SELECT 1 "
        + "from documents "
        + "where doc_type = 'NOTARY-COMPENSATION' "
        + "  and json_data -> 'notaryCompensation' -> 'notaryCompensationData' ->> 'eno' = :eno)",
        nativeQuery = true)
    boolean existsByEno(@Param("eno") String eno);

    @Query(value = "select * "
        + " from documents "
        + "where doc_type = 'NOTARY-COMPENSATION' "
        + "  and json_data -> 'notaryCompensation' -> 'notaryCompensationData' ->> 'affairId' = cast(:affairId as text)"
        + "  and json_data -> 'notaryCompensation' -> 'notaryCompensationData' ->> 'statusId' "
        + "      not in ('1075', '1080', '8021.3')",
        nativeQuery = true)
    List<DocumentData> findOpenApplicationsByAffairId(@Param("affairId") final String affairId);

    @Query(value = "select * "
        + " from documents "
        + "where doc_type = 'NOTARY-COMPENSATION' "
        + "  and json_data -> 'notaryCompensation' -> 'notaryCompensationData' ->> 'affairId' = cast(:affairId as text)"
        + "  and json_data -> 'notaryCompensation' -> 'notaryCompensationData' ->> 'statusId' = '1075'",
        nativeQuery = true)
    List<DocumentData> findPerformedApplicationsByAffairId(@Param("affairId") final String affairId);

}
