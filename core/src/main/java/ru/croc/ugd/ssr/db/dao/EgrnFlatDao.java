package ru.croc.ugd.ssr.db.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import ru.reinform.cdp.db.model.DocumentData;

import java.util.List;

/**
 * DAO для работы с выписками из ЕГРН по квартирам.
 */
public interface EgrnFlatDao extends Repository<DocumentData, String> {

    @Query(value = "select * "
        + "  from documents "
        + "  where doc_type = 'EGRN-FLAT-REQUEST'"
        + "   and json_data -> 'egrnFlatRequest' -> 'egrnFlatRequestData' -> 'requestCriteria' "
        + "     ->> 'unom' = :unom "
        + "   and json_data -> 'egrnFlatRequest' -> 'egrnFlatRequestData' -> 'requestCriteria' "
        + "     ->> 'flatNumber' = :flatNumber "
        + "   and json_data -> 'egrnFlatRequest' -> 'egrnFlatRequestData' ->> 'statusCode' = :statusCode",
        nativeQuery = true)
    List<DocumentData> fetchAllByUnomAndFlatNumberAndStatusCode(
        @Param("unom") final String unom,
        @Param("flatNumber") final String flatNumber,
        @Param("statusCode") final String statusCode
    );

    @Query(value = "SELECT * FROM documents "
        + " WHERE doc_type='EGRN-FLAT-REQUEST' "
        + "   AND json_data -> 'egrnFlatRequest' -> 'egrnFlatRequestData' "
        + "     ->> 'serviceNumber' = cast(:serviceNumber as text)",
        nativeQuery = true)
    List<DocumentData> fetchRequestsByServiceNumber(@Param("serviceNumber") final String serviceNumber);

    @Query(value = "SELECT * FROM documents"
        + " WHERE doc_type = 'EGRN-FLAT-REQUEST' "
        + "       AND json_data -> 'egrnFlatRequest' -> 'egrnFlatRequestData' -> 'requestCriteria' "
        + "         ->> 'ccoDocumentId' IS NOT NULL"
        + "       AND json_data -> 'egrnFlatRequest' -> 'egrnFlatRequestData' -> 'requestCriteria' "
        + "         ->> 'cadastralNumber' = cast(:cadastralNumber as text)"
        + "       AND json_data -> 'egrnFlatRequest' -> 'egrnFlatRequestData'"
        + "         ->> 'statusCode' = cast(:statusCode as text)",
        nativeQuery = true)
    List<DocumentData> fetchByCadNumAndStatusAndCcoDocumentNotNull(
        @Param("cadastralNumber") final String cadastralNumber,
        @Param("statusCode") final String statusCode
    );

    @Query(value = "select distinct on (json_data -> 'egrnFlatRequest' -> 'egrnFlatRequestData'"
        + " -> 'egrnResponse' -> 'extractAboutPropertyRoom' -> 'room_record' -> 'object'"
        + " -> 'common_data' ->> 'cad_number') *"
        + "  from documents"
        + "  where doc_type = 'EGRN-FLAT-REQUEST'"
        + "   and json_data -> 'egrnFlatRequest' -> 'egrnFlatRequestData' -> 'requestCriteria'"
        + "     ->> 'unom' = :unom"
        + "   and lower(trim(json_data -> 'egrnFlatRequest' -> 'egrnFlatRequestData' -> 'egrnResponse' ->"
        + " 'extractAboutPropertyRoom' -> 'room_record' -> 'params' -> 'purpose' ->> 'code')) = '206001000000'"
        + "   and json_data -> 'egrnFlatRequest' -> 'egrnFlatRequestData' ->> 'statusCode' = '1004'"
        + " order by (json_data -> 'egrnFlatRequest' -> 'egrnFlatRequestData' -> 'egrnResponse' ->"
        + " 'extractAboutPropertyRoom' -> 'room_record' -> 'object' -> 'common_data' ->> 'cad_number'),"
        + " cast((json_data -> 'egrnFlatRequest' -> 'egrnFlatRequestData' ->> 'creationDateTime') as timestamp) desc",
        nativeQuery = true)
    List<DocumentData> fetchLastNonResidentialByUnom(@Param("unom") final String unom);

}
