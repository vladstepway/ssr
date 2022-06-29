package ru.croc.ugd.ssr.db.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import ru.reinform.cdp.db.model.DocumentData;

import java.util.List;
import javax.annotation.Nonnull;

/**
 * DAO по работе с дашбордРеквест.
 */
public interface DashboardRequestDocumentDao extends Repository<DocumentData, String> {

    /**
     * Получить дашборд по id.
     *
     * @param dashboardId id
     * @return дашборд.
     */
    @Query(
        value = "SELECT * FROM documents "
            + "WHERE doc_type='DASHBOARD-REQUEST' "
            + "AND json_data -> 'DashboardRequest' -> 'main' ->> 'dashboardId' = :dashboardId",
        nativeQuery = true
    )
    List<DocumentData> fetchByDashboardId(@Nonnull @Param("dashboardId") String dashboardId);

}
