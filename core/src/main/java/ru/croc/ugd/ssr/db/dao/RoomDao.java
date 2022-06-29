package ru.croc.ugd.ssr.db.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import ru.reinform.cdp.db.model.DocumentData;

import javax.annotation.Nonnull;

/**
 * DAO по работе с комнатами.
 */
public interface RoomDao extends Repository<DocumentData, String> {

    /**
     * Получить комнату по ID.
     *
     * @param roomId ID комнаты
     * @return Строка-json комнаты
     */
    @Query(
        value = "select cast(json_data as character varying) "
            + "from (select jsonb_array_elements(json_data -> 'Rooms' -> 'Room') as json_data "
            + "from (select jsonb_array_elements(json_data -> 'RealEstate' -> 'RealEstateData' -> 'Flats' -> 'Flat') "
            + "as json_data from documents where doc_type = 'REAL-ESTATE') as flats) as rooms "
            + "where json_data ->> 'roomID' = cast(:roomId as character varying)",
        nativeQuery = true
    )
    String fetch(@Nonnull @Param("roomId") String roomId);

}
