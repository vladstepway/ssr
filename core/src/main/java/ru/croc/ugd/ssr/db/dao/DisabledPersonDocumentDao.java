package ru.croc.ugd.ssr.db.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import ru.croc.ugd.ssr.db.projection.DisabledPersonDetailsProjection;
import ru.reinform.cdp.db.model.DocumentData;

import java.time.LocalDate;
import java.util.List;

public interface DisabledPersonDocumentDao extends Repository<DocumentData, String> {

    @Query(
        value = "select "
            + "       json_data -> 'disabledPerson' -> 'disabledPersonData' ->> 'usingWheelchair' as usingWheelchair,"
            + "       json_data -> 'disabledPerson' -> 'disabledPersonData' ->> 'personDocumentId' as personDocumentId,"
            + "       json_data -> 'disabledPerson' -> 'disabledPersonData' ->> 'createdAt' as createdAt"
            + " from documents"
            + " where doc_type = 'DISABLED-PERSON'"
            + " and json_data -> 'disabledPerson' -> 'disabledPersonData' ->> 'deleted' = 'false'"
            + " and json_data -> 'disabledPerson' -> 'disabledPersonData' ->> 'unom' = cast(:unom as text)"
            + " and json_data -> 'disabledPerson' -> 'disabledPersonData' ->> 'flatNumber' = cast(:flatNumber as text)"
            + " order by createdAt desc",
        nativeQuery = true
    )
    List<DisabledPersonDetailsProjection> fetchDisabledPersonDetailsByUnomAndFlatNumber(
        @Param("unom") final String unom,
        @Param("flatNumber") final String flatNumber
    );

    @Query(
        value = "select *"
            + " from documents"
            + " where doc_type = 'DISABLED-PERSON'"
            + " and json_data -> 'disabledPerson' -> 'disabledPersonData' ->> 'deleted' = 'false'"
            + " and json_data -> 'disabledPerson' -> 'disabledPersonData' ->> 'unom' = cast(:unom as text)"
            + " and json_data -> 'disabledPerson' -> 'disabledPersonData' ->> 'flatNumber' = cast(:flatNumber as text)",
        nativeQuery = true
    )
    List<DocumentData> fetchByUnomAndFlatNumber(
        @Param("unom") final String unom, @Param("flatNumber") final String flatNumber
    );

    @Query(value = "select * from documents"
        + " where doc_type = 'DISABLED-PERSON'"
        + "   and json_data -> 'disabledPerson' -> 'disabledPersonData' "
        + "       ->> 'uniqueExcelRecordId' = :uniqueExcelRecordId"
        + " order by json_data -> 'disabledPerson' -> 'disabledPersonData' ->> 'createdAt' desc",
        nativeQuery = true)
    List<DocumentData> fetchAllByUniqueExcelRecordId(@Param("uniqueExcelRecordId") final String uniqueExcelRecordId);

    @Query(
        value = "select *"
            + " from documents"
            + " where doc_type = 'DISABLED-PERSON'"
            + " and json_data -> 'disabledPerson' -> 'disabledPersonData' ->> 'deleted' = 'false'"
            + " and json_data -> 'disabledPerson' -> 'disabledPersonData' ->> 'unom' = cast(:unom as text)"
            + " and json_data -> 'disabledPerson' -> 'disabledPersonData' ->> 'fullName' = cast(:fullName as text)"
            + " and json_data -> 'disabledPerson' -> 'disabledPersonData' ->> 'flatNumber' = cast(:flatNumber as text)"
            + " and cast(json_data -> 'disabledPerson' -> 'disabledPersonData'"
            + "             ->> 'birthDate' as date) = cast(cast(:birthDate as text) as date)",
        nativeQuery = true
    )
    List<DocumentData> fetchAllByUnomAndFlatNumberAndFullNameAndBirthDate(
        @Param("unom") final String unom,
        @Param("flatNumber") final String flatNumber,
        @Param("fullName") final String fullName,
        @Param("birthDate") final LocalDate birthDate
    );

    @Query(
        value = "select *"
            + " from documents"
            + " where doc_type = 'DISABLED-PERSON'"
            + " and json_data -> 'disabledPerson' -> 'disabledPersonData' ->> "
            + "'personDocumentId' in (:personsDocumentIds)",
        nativeQuery = true
    )
    List<DocumentData> fetchAllByPersonDocumentIds(
        @Param("personsDocumentIds") final List<String> personsDocumentIds
    );
}
