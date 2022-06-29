package ru.croc.ugd.ssr.db.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import ru.croc.ugd.ssr.db.projection.DefectProjection;
import ru.reinform.cdp.db.model.DocumentData;

import java.util.List;

/**
 * DefectDao.
 */
public interface DefectDao extends Repository<DocumentData, String> {

    /**
     * Получение списка дефектов.
     * @return список дефектов
     */
    @Query(value = "select id,"
        + "       json_data -> 'ApartmentDefect' -> 'ApartmentDefectData' ->> 'flatElement' as flatElement,"
        + "       json_data -> 'ApartmentDefect' -> 'ApartmentDefectData' ->> 'description' as description"
        + " from documents"
        + " where doc_type = 'DEFECT'",
        nativeQuery = true)
    List<DefectProjection> fetchAll();
}
