package ru.croc.ugd.ssr.db.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import ru.croc.ugd.ssr.db.projection.CommissionInspectionProjection;
import ru.reinform.cdp.db.model.DocumentData;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DAO для комиссионных осмотров.
 */
public interface CommissionInspectionDao extends Repository<DocumentData, String> {

    /**
     * Получение списка комиссионных осмотров по ЕНО.
     * @param eno ЕНО.
     * @return список комиссионных осмотров.
     */
    @Query(value = "select * "
        + "from documents "
        + "where doc_type = 'COMMISSION-INSPECTION' "
        + "  and json_data -> 'commissionInspection' -> 'commissionInspectionData' ->> 'eno' = :eno",
        nativeQuery = true)
    List<DocumentData> findByEno(@Param("eno") String eno);

    /**
     * Ищем заявления по статусу.
     * @return список заявлений.
     */
    @Query(value = "select d.* "
        + "from documents d "
        + "where d.doc_type = 'COMMISSION-INSPECTION' "
        + "  and d.json_data -> 'commissionInspection' -> 'commissionInspectionData' "
        + "         ->> 'applicationStatusId' in :statusIds",
        nativeQuery = true)
    List<DocumentData> findByStatusIdIn(@Param("statusIds") final List<String> statusIds);

    /**
     * Существуют активные заявки на КО по адресу.
     *
     * @param unom УНОМ заселяемого дома
     * @param flatNumber Номер заселяемой квартиры
     * @return существуют ли активные заявки
     */
    @Query(value = "SELECT EXISTS (SELECT 1 FROM documents "
        + "WHERE doc_type='COMMISSION-INSPECTION' "
        + "     AND json_data -> 'commissionInspection' -> 'commissionInspectionData'"
        + "         ->> 'ccoUnom' = :unom "
        + "     AND json_data -> 'commissionInspection' -> 'commissionInspectionData'"
        + "         ->> 'flatNum' = :flatNumber "
        + "     AND json_data -> 'commissionInspection' -> 'commissionInspectionData'-> 'completionDateTime' is null)",
        nativeQuery = true)
    boolean existsActiveByUnomAndFlatNumber(
        @Param("unom") final String unom,
        @Param("flatNumber") final String flatNumber
    );

    /**
     * Существуют активные заявки на КО по letterId.
     *
     * @param letterId letterId
     * @return существуют ли активные заявки
     */
    @Query(value = "SELECT EXISTS (SELECT 1 FROM documents "
        + "WHERE doc_type='COMMISSION-INSPECTION' "
        + "     AND json_data -> 'commissionInspection' -> 'commissionInspectionData'"
        + "         -> 'letter' ->> 'id' = :letterId "
        + "     AND json_data -> 'commissionInspection' -> 'commissionInspectionData'-> 'completionDateTime' is null)",
        nativeQuery = true)
    boolean existsActiveByLetterId(
        @Param("letterId") final String letterId
    );

    /**
     * Получение всех заявлений на КО.
     *
     * @param personId Фильтр по personId.
     * @return Список всех заявлений на КО.
     */
    @Query(value = "SELECT * "
        + "FROM documents "
        + "WHERE doc_type = 'COMMISSION-INSPECTION' "
        + "  AND (:personId IS NULL "
        + "    OR json_data -> 'commissionInspection' -> 'commissionInspectionData' -> 'applicant' "
        + "     ->> 'personId' = CAST(:personId AS text))",
        nativeQuery = true)
    List<DocumentData> findAll(@Param("personId") final String personId);

    /**
     * Получение всех заявлений на КО.
     *
     * @param personId Фильтр по personId.
     * @param statuses Фильтр по статусу.
     * @return Список всех заявлений на КО.
     */
    @Query(value = "SELECT * "
        + "FROM documents "
        + "WHERE doc_type = 'COMMISSION-INSPECTION' "
        + "  AND (:personId IS NULL "
        + "    OR json_data -> 'commissionInspection' -> 'commissionInspectionData' -> 'applicant' "
        + "     ->> 'personId' = CAST(:personId AS text)) "
        + "  AND json_data -> 'commissionInspection' -> 'commissionInspectionData' "
        + "     ->> 'applicationStatusId' IN :statuses",
        nativeQuery = true)
    List<DocumentData> findAll(
        @Param("personId") final String personId,
        @Param("statuses") final List<String> statuses
    );

    /**
     * Returns commission inspection projections filtered by ccoUnom and date between and time confirmed.
     * @param unom unom
     * @param startDate startDate
     * @param endDate endDate
     * @return commission inspection projections
     */
    @Query(value = "SELECT id,"
        + "       json_data -> 'commissionInspection' -> 'commissionInspectionData' ->> 'eno'     AS eno,"
        + "       json_data -> 'commissionInspection' -> 'commissionInspectionData' ->> 'flatNum' AS flatNum,"
        + "       json_data -> 'commissionInspection' -> 'commissionInspectionData' ->>"
        + "       'confirmedInspectionDateTime'                                                   AS inspectionDateTime"
        + " FROM documents"
        + "     WHERE doc_type = 'COMMISSION-INSPECTION'"
        + "         AND json_data -> 'commissionInspection' -> 'commissionInspectionData' ->> 'ccoUnom' = :unom"
        + "         AND json_data -> 'commissionInspection' -> 'commissionInspectionData' ->> 'applicationStatusId' IN"
        + "             ('1060.1', '1060.2')"
        + "         AND (json_data -> 'commissionInspection' -> 'commissionInspectionData'"
        + "             ->> 'confirmedInspectionDateTime' BETWEEN :startDate AND :endDate)",
        nativeQuery = true)
    List<CommissionInspectionProjection> findAllByCcoUnomAndDateBetweenAndTimeConfirmedStatus(
        @Param("unom") final String unom,
        @Param("startDate") final LocalDateTime startDate,
        @Param("endDate") final LocalDateTime endDate
    );

    /**
     * Returns commission inspections by letterId.
     *
     * @param letterId letterId
     * @return commission inspections by letterId
     */
    @Query(value = "SELECT * "
        + "FROM documents "
        + "WHERE doc_type = 'COMMISSION-INSPECTION' "
        + "  AND json_data -> 'commissionInspection' -> 'commissionInspectionData' -> 'letter' ->> 'id' = :letterId " 
        + "  AND json_data -> 'commissionInspection' -> 'commissionInspectionData' ->> 'applicationStatusId' "
        + " NOT IN ('1075.1', '1075.2', '1075.3', '1080.1', '1080.2', '1080.3', '1080.100')",
        nativeQuery = true)
    List<DocumentData> findRefuseableByLetterId(@Param("letterId") final String letterId);
}
