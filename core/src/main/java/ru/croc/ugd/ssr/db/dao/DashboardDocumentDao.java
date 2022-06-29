package ru.croc.ugd.ssr.db.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.reinform.cdp.db.dao.DocumentDao;
import ru.reinform.cdp.db.model.DocumentData;

import java.time.LocalDate;
import java.util.Optional;

/**
 * DAO по работе с дашбордом.
 */
public interface DashboardDocumentDao extends DocumentDao {

    /**
     * Получить последний активный дашборд.
     *
     * @return последний активный дашборд.
     */
    @Query(
        value = "SELECT * FROM documents WHERE doc_type='DASHBOARD' "
            + "AND json_data->'dashboard'->>'archived' = 'false' AND json_data->'dashboard'->>'published' = 'true' "
            + "AND (json_data->'dashboard'->>'auto' = 'false' OR json_data->'dashboard'->>'auto' is null) "
            + "ORDER BY json_data->'dashboard'->'data'->>'informationDate' DESC LIMIT 1",
        nativeQuery = true
    )
    Optional<DocumentData> getLastActiveDocument();

    /**
     * Получить последний активный автоматически сформированный дашборд.
     *
     * @return последний автоматически сформированный активный дашборд.
     */
    @Query(
            value = "SELECT * FROM documents WHERE doc_type='DASHBOARD' "
                    + "AND json_data->'dashboard'->>'archived' = 'false' "
                    + "AND json_data->'dashboard'->>'published' = 'true' "
                    + "AND json_data->'dashboard'->>'auto' = 'true' "
                    + "ORDER BY json_data->'dashboard'->'data'->>'informationDate' DESC LIMIT 1",
            nativeQuery = true
    )
    Optional<DocumentData> getLastActiveAutoDocument();

    /**
     * Получить активный автоматически сформированный дашборд на переданную дату.
     *
     * @param date дата
     * @return активный автоматически сформированный дашборд на переданную дату.
     */
    @Query(
        value = "SELECT * FROM documents WHERE doc_type= 'DASHBOARD' "
            + "AND json_data -> 'dashboard' ->> 'archived' = 'false' "
            + "AND json_data -> 'dashboard' ->> 'published' = 'true' "
            + "AND CAST(json_data -> 'dashboard' ->> 'fillInDate' AS DATE) = :date "
            + "AND json_data -> 'dashboard' ->> 'auto' = 'true' "
            + "ORDER BY json_data -> 'dashboard' -> 'data' ->> 'informationDate' DESC LIMIT 1",
        nativeQuery = true
    )
    Optional<DocumentData> getActiveAutoDocumentByDate(@Param("date") final LocalDate date);

    /**
     * Получить активный дашборд на переданную дату.
     *
     * @param date дата
     * @return активный дашборд на переданную дату.
     */
    @Query(
        value = "SELECT * FROM documents WHERE doc_type= 'DASHBOARD' "
            + "AND json_data -> 'dashboard' ->> 'archived' = 'false' "
            + "AND CAST(json_data -> 'dashboard' ->> 'fillInDate' AS DATE) = :date "
            + "AND (json_data -> 'dashboard' ->> 'auto' = 'false' OR json_data -> 'dashboard' ->> 'auto' is null) "
            + "ORDER BY create_date DESC LIMIT 1",
        nativeQuery = true
    )
    Optional<DocumentData> getActiveDocumentByDate(@Param("date") final LocalDate date);

}
