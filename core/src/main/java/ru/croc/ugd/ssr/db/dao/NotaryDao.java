package ru.croc.ugd.ssr.db.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import ru.reinform.cdp.db.model.DocumentData;

import java.util.List;
import java.util.Optional;

/**
 * DAO для карточек нотариусов.
 */
public interface NotaryDao extends Repository<DocumentData, String> {

    /**
     * найти все активные карточки.
     * @return список найденных карточек нотариусов.
     */
    @Query(value = "select * "
        + "from documents "
        + "where doc_type = 'NOTARY' "
        + "  and json_data -> 'notary' -> 'notaryData' ->> 'status' = 'Активный'",
        nativeQuery = true)
    List<DocumentData> findAllActive();

    /**
     * Получение всех нотариусов.
     *
     * @param fullName Фильтр по фио нотариуса.
     * @param login Фильтр по логину
     * @param includeUnassignedEmployee Фильтр по закрепленным за нотариусом сотрудникам.
     * @return Список найденных карточек нотариусов.
     */
    @Query(value = "SELECT * "
        + "FROM documents "
        + "WHERE doc_type = 'NOTARY' "
        + "  AND json_data -> 'notary' -> 'notaryData' ->> 'status' = 'Активный'"
        + "  and (:fullName IS NULL "
        + "    OR LOWER(json_data -> 'notary' -> 'notaryData' ->> 'fullName') "
        + "       LIKE concat('%', LOWER (CAST(:fullName AS text)), '%'))"
        + "  AND (:includeUnassignedEmployee = 'true' "
        + "     OR json_data -> 'notary' -> 'notaryData' -> 'employeeLogin' IS NOT NULL) "
        + "  AND (:login IS NULL "
        + "    OR json_data -> 'notary' -> 'notaryData' ->> 'employeeLogin' = CAST(:login AS text)) "
        + " ORDER BY json_data -> 'notary' -> 'notaryData' ->> 'fullName'",
        nativeQuery = true)
    List<DocumentData> findByFullNameAndLoginAndEmployeeAssigned(
        @Param("fullName") final String fullName,
        @Param("login") final String login,
        @Param("includeUnassignedEmployee") final boolean includeUnassignedEmployee
    );

    /**
     * Получить логин сотрудника относящегося к нотарису.
     *
     * @param notaryId ИД notaryId
     * @return логин сотрудника.
     */
    @Query(value = "SELECT json_data -> 'notary' -> 'notaryData' ->> 'employeeLogin' as login "
        + "FROM documents "
        + " WHERE doc_type = 'NOTARY'"
        + "     AND id = :notaryId",
        nativeQuery = true)
    Optional<String> fetchNotaryLoginById(@Param("notaryId") final String notaryId);
}
