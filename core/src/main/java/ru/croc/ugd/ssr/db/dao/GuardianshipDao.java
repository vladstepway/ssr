package ru.croc.ugd.ssr.db.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import ru.reinform.cdp.db.model.DocumentData;

import java.util.List;
import javax.annotation.Nonnull;

/**
 * DAO для опеки.
 */
public interface GuardianshipDao extends Repository<DocumentData, String> {

    /**
     * Существуют заявки для семьи.
     *
     * @param affairId ИД семьи
     * @return существуют ли активные заявки
     */
    @Query(value = "SELECT EXISTS (SELECT 1 FROM documents "
        + "WHERE doc_type='GUARDIANSHIP' "
        + "     AND json_data -> 'guardianshipRequest' -> 'guardianshipRequestData'"
        + "         ->> 'affairId' = cast(:affairId as text) "
        + "       AND json_data -> 'guardianshipRequest' -> 'guardianshipRequestData'"
        + "                 ->> 'completionDateTime' IS NULL)",
        nativeQuery = true)
    boolean existsNonCompleteRequestByAffairId(@Param("affairId") final String affairId);

    /**
     * Получить список guardianshipRequest по affairId и skipInactive.
     *
     * @param affairId affairId
     * @param skipInactive skipInactive
     * @return guardianshipRequest list
     */
    @Query(value = "SELECT * FROM documents "
        + "WHERE doc_type='GUARDIANSHIP' "
        + "     AND json_data -> 'guardianshipRequest' -> 'guardianshipRequestData'"
        + "         ->> 'affairId' = cast(:affairId as text) "
        + "     AND (cast(:skipInactive as text) = 'false' "
        + "         OR json_data -> 'guardianshipRequest' -> 'guardianshipRequestData'"
        + "             -> 'completionDateTime' IS NULL)",
        nativeQuery = true)
    List<DocumentData> findRequestByAffairIdAndSkipInactive(
        @Nonnull @Param("affairId") String affairId,
        @Param("skipInactive") boolean skipInactive
    );

    /**
     * Получить список guardianshipRequest, по которым не был запущен процесс, по affairId.
     *
     * @param affairId affairId
     * @return guardianshipRequest list
     */
    @Query(value = "SELECT * FROM documents "
        + "WHERE doc_type='GUARDIANSHIP' "
        + "     AND json_data -> 'guardianshipRequest' -> 'guardianshipRequestData'"
        + "         ->> 'affairId' = cast(:affairId as text) "
        + "     AND (json_data -> 'guardianshipRequest' -> 'guardianshipRequestData'"
        + "             -> 'processInstanceId' IS NULL)",
        nativeQuery = true)
    List<DocumentData> findRequestByAffairIdAndProcessInstanceIdIsNull(@Nonnull @Param("affairId") String affairId);
}
