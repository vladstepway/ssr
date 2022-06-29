package ru.croc.ugd.ssr.db.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import ru.croc.ugd.ssr.db.projection.CipEmployeeProjection;
import ru.reinform.cdp.db.model.DocumentData;

import java.util.List;
import javax.annotation.Nonnull;

/**
 * DAO по работе с ЦИП.
 */
public interface CipDocumentDao extends Repository<DocumentData, String> {

    /**
     * Получить все ЦИПы по округу.
     *
     * @param area код округа
     * @return список ЦИПов
     */
    @Query(
        value = "select * from ssr.documents where doc_type = 'CIP' "
            + "and json_data -> 'Cip' -> 'cipData' -> 'Area' ->> 'code' = :area",
        nativeQuery = true
    )
    List<DocumentData> fetchByArea(@Nonnull @Param("area") String area);

    /**
     * Получить все ЦИПы, связанные с ОКСом.
     *
     * @param ccoId id ОКС (с которым связан ЦИП)
     * @return список ЦИПов
     */
    @Query(
        value = "select id, doc_type, create_date, json_data, md5_data from "
            + "(select *, jsonb_array_elements(json_data -> 'Cip' -> 'cipData' -> 'LinkedHouses' -> 'Link') ->> 'id' "
            + "as ccoid from documents) as ssrd where ccoid = :ccoId",
        nativeQuery = true
    )
    List<DocumentData> fetchByCcoId(@Nonnull @Param("ccoId") String ccoId);

    /**
     * Получить все UNOM, связанные с personId.
     *
     * @param personId personId
     * @return список UNOM
     */
    @Query(value = "select json_data ->> 'UNOM'"
        + "from (select jsonb_array_elements(json_data -> 'Cip' -> 'cipData' -> 'People' -> 'Person')"
        + "                 as json_data from documents where doc_type = 'CIP') as flats"
        + " where json_data ->> 'personID' = :personId", nativeQuery = true)
    List<String> fetchUnomsFromCipByPersonId(
        @Nonnull @Param("personId") String personId);

    /**
     * Получить адрес ЦИПа по ид.
     *
     * @param id ид ЦИПа
     * @return адрес
     */
    @Query(value = "select json_data -> 'Cip' -> 'cipData' ->> 'Address' from documents "
        + "where id = :id and doc_type = 'CIP'",
        nativeQuery = true)
    String getCipAddressById(@Nonnull @Param("id") String id);


    /**
     * Получить работников ЦИП по ИД ЦИП.
     *
     * @param cipId ИД ЦИП
     * @return список работников.
     */
    @Query(
        value = "SELECT employees ->> 'login' as login "
            + "FROM documents,"
            + "     jsonb_array_elements(json_data -> 'Cip' -> 'cipData' -> 'Employees' -> 'Employee') as employees "
            + "WHERE doc_type = 'CIP'"
            + "      AND id = :cipId",
        nativeQuery = true)
    List<CipEmployeeProjection> fetchCipEmployees(@Param("cipId") final String cipId);

    /**
     * Получить все ЦИПы.
     *
     * @return список ЦИПов
     */
    @Query(value = "select * from documents where doc_type = 'CIP'", nativeQuery = true)
    List<DocumentData> fetchAll();
}
