package ru.croc.ugd.ssr.db.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import ru.reinform.cdp.db.model.DocumentData;

import java.util.List;
import java.util.Optional;

/**
 * DAO для заявлений на посещение нотариуса.
 */
public interface NotaryApplicationDao extends Repository<DocumentData, String> {

    /**
     * Получение списка заявлений на посещение нотариуса по ЕНО.
     * @param eno ЕНО.
     * @return список заявлений на посещение нотариуса.
     */
    @Query(value = "select * "
        + "from documents "
        + "where doc_type = 'NOTARY-APPLICATION' "
        + "  and json_data -> 'notaryApplication' -> 'notaryApplicationData' ->> 'eno' = :eno",
        nativeQuery = true)
    Optional<DocumentData> findByEno(@Param("eno") String eno);

    /**
     * Получение списка неотмененных заявлений по идентификатору жителя.
     *
     * @param personId personId.
     * @return список открытых заявлений на посещение нотариуса.
     */
    @Query(value = "SELECT * "
        + "FROM documents "
        + "WHERE doc_type = 'NOTARY-APPLICATION' "
        + " AND json_data -> 'notaryApplication' -> 'notaryApplicationData' -> 'applicant' ->> 'personId' = :personId "
        + " AND json_data -> 'notaryApplication' -> 'notaryApplicationData' ->> 'statusId' "
        + "    NOT IN ('1080.1', '1080.2', '1080.3', '1035')",
        nativeQuery = true)
    List<DocumentData> findNonCancelledApplicationsByPersonDocumentId(
        @Param("personId") final String personId
    );

    @Query(value = "SELECT EXISTS(SELECT 1 "
        + "from documents "
        + "where doc_type = 'NOTARY-APPLICATION' "
        + "  and json_data -> 'notaryApplication' -> 'notaryApplicationData' ->> 'eno' = :eno)",
        nativeQuery = true)
    boolean existsByEno(@Param("eno") String eno);

    @Query(value = "select * "
        + " from documents "
        + "where doc_type = 'NOTARY-APPLICATION' "
        + "  and json_data -> 'notaryApplication' -> 'notaryApplicationData' ->> 'affairId' = cast(:affairId as text)"
        + "  and json_data -> 'notaryApplication' -> 'notaryApplicationData' ->> 'statusId' = '1075'",
        nativeQuery = true)
    List<DocumentData> findApplicationsWithSignedContractByAffairId(@Param("affairId") final String affairId);
}
