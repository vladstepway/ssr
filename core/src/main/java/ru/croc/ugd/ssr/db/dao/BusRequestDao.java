package ru.croc.ugd.ssr.db.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import ru.reinform.cdp.db.model.DocumentData;

import java.util.List;

public interface BusRequestDao extends Repository<DocumentData, String> {

    @Query(value = "SELECT * FROM documents "
        + " WHERE doc_type='BUS-REQUEST' "
        + "   AND json_data -> 'busRequest' -> 'busRequestData' ->> 'serviceNumber' = cast(:serviceNumber as text)",
        nativeQuery = true)
    List<DocumentData> findRequestsByServiceNumber(@Param("serviceNumber") final String serviceNumber);
}
