package ru.croc.ugd.ssr.db.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import ru.reinform.cdp.db.model.DocumentData;

import java.util.List;

/**
 * CommissionInspectionOrganisationDao.
 */
public interface CommissionInspectionOrganisationDao extends Repository<DocumentData, String> {

    /**
     * Fetches all organisations.
     *
     * @return organisations
     */
    @Query(value = "SELECT * FROM documents WHERE doc_type='COMMISSION-INSPECTION-ORGANISATION' ", nativeQuery = true)
    List<DocumentData> fetchAll();

    /**
     * Finds organisation by area and type.
     * @param area area
     * @param type type
     * @return organisation
     */
    @Query(value = "SELECT * "
        + "FROM documents "
        + "WHERE doc_type = 'COMMISSION-INSPECTION-ORGANISATION' "
        + " AND (:area IS NULL OR "
        + "     json_data -> 'commissionInspectionOrganisation' -> 'commissionInspectionOrganisationData' ->> 'area' = "
        + "     CAST(:area AS TEXT)) "
        + " AND json_data -> 'commissionInspectionOrganisation' -> 'commissionInspectionOrganisationData' ->> 'type' = "
        + "     CAST(:typeValue AS TEXT)", nativeQuery = true)
    List<DocumentData> findByAreaAndType(
        @Param("area") final String area,
        @Param("typeValue") final Integer type
    );
}
