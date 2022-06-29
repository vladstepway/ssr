package ru.croc.ugd.ssr.db.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import ru.reinform.cdp.db.model.DocumentData;

import java.util.List;

/**
 * DAO для работы с выписками из ЕГРН по ОН.
 */
public interface EgrnBuildingDao extends Repository<DocumentData, String> {

    @Query(value = "SELECT * FROM documents "
        + " WHERE doc_type='EGRN-BUILDING-REQUEST' "
        + "   AND json_data -> 'egrnBuildingRequest' -> 'egrnBuildingRequestData' "
        + "     ->> 'serviceNumber' = cast(:serviceNumber as text)",
        nativeQuery = true)
    List<DocumentData> fetchRequestsByServiceNumber(@Param("serviceNumber") final String serviceNumber);

    @Query(value = "SELECT * FROM documents "
        + " WHERE doc_type='EGRN-BUILDING-REQUEST' "
        + "   AND json_data -> 'egrnBuildingRequest' -> 'egrnBuildingRequestData' "
        + "     -> 'requestCriteria' ->> 'cadastralNumber' = cast(:cadastralNumber as text)"
        + "   AND json_data -> 'egrnBuildingRequest' -> 'egrnBuildingRequestData' "
        + "     ->> 'statusCode' = cast(:statusCode as text)",
        nativeQuery = true)
    List<DocumentData> fetchRequestsByCadastralNumberAndStatusCode(
        @Param("cadastralNumber") final String cadastralNumber,
        @Param("statusCode") final String statusCode
    );

    @Query(value = "SELECT * FROM documents "
        + " WHERE doc_type='EGRN-BUILDING-REQUEST' "
        + "   AND json_data -> 'egrnBuildingRequest' -> 'egrnBuildingRequestData' "
        + "     -> 'requestCriteria' ->> 'cadastralNumber' = cast(:cadastralNumber as text)",
        nativeQuery = true)
    List<DocumentData> fetchRequestsByCadastralNumber(
        @Param("cadastralNumber") final String cadastralNumber
    );
}
