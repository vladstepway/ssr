package ru.croc.ugd.ssr.db.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import ru.reinform.cdp.db.model.DocumentData;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * DAO для заявлений на осмотр квартиры.
 */
public interface FlatAppointmentDao extends Repository<DocumentData, String> {

    @Query(value = "SELECT EXISTS(SELECT 1 "
        + "from documents "
        + "where doc_type = 'FLAT-APPOINTMENT' "
        + "  and json_data -> 'flatAppointment' -> 'flatAppointmentData'"
        + " ->> 'eno' = :eno)",
        nativeQuery = true)
    boolean existsByEno(@Param("eno") String eno);

    /**
     * Получение заявления на осмотр квартиры по номеру заявления.
     *
     * @param eno номер заявления.
     * @return заявление на осмотр квартиры.
     */
    @Query(value = "select * "
        + "from documents "
        + "where doc_type = 'FLAT-APPOINTMENT' "
        + "  and json_data -> 'flatAppointment' -> 'flatAppointmentData'"
        + " ->> 'eno' = :eno",
        nativeQuery = true)
    Optional<DocumentData> findByEno(@Param("eno") String eno);

    @Query(value = "select * "
        + "from documents "
        + "where doc_type = 'FLAT-APPOINTMENT' "
        + "  and json_data -> 'flatAppointment' -> 'flatAppointmentData' -> 'applicant'"
        + " ->> 'personDocumentId' = :personDocumentId"
        + "  and (:includeInactive = 'true'"
        + "    or (json_data -> 'flatAppointment' -> 'flatAppointmentData' ->> 'cancelDate' is null"
        + "      and json_data -> 'flatAppointment' -> 'flatAppointmentData' ->> 'performed' is null))",
        nativeQuery = true)
    List<DocumentData> findAll(
        @Param("personDocumentId") final String personDocumentId,
        @Param("includeInactive") final boolean includeInactive
    );

    @Query(value = "select *"
        + "  from documents"
        + " where doc_type = 'FLAT-APPOINTMENT' ",
        nativeQuery = true)
    List<DocumentData> findAll();

    @Query(value = "SELECT EXISTS(select 1 "
        + "from documents "
        + "where doc_type = 'FLAT-APPOINTMENT' "
        + "  and cast(json_data -> 'flatAppointment' -> 'flatAppointmentData'"
        + " ->> 'appointmentDateTime' as timestamp) > now()"
        + "  and json_data -> 'flatAppointment' -> 'flatAppointmentData'"
        + " ->> 'bookingId' = :bookingId"
        + " and json_data -> 'flatAppointment' -> 'flatAppointmentData'"
        + "    ->> 'statusId' = :statusId)",
        nativeQuery = true)
    boolean existsByBookingIdAndStatusId(
        @Param("bookingId") String bookingId,
        @Param("statusId") String statusId
    );

    @Query(value = "select *"
        + " from documents "
        + " where doc_type = 'FLAT-APPOINTMENT' "
        + "  and json_data -> 'flatAppointment' -> 'flatAppointmentData' "
        + "      ->> 'statusId' = '1050'"
        + "  and (cast(json_data -> 'flatAppointment' -> 'flatAppointmentData'"
        + "      ->> 'appointmentDateTime' as date) < cast(cast(:appointmentDate as text) as date))",
        nativeQuery = true)
    List<DocumentData> findRegisteredAppointmentsByAppointmentDateTimeBefore(
        @Param("appointmentDate") final LocalDate appointmentDate
    );
}
