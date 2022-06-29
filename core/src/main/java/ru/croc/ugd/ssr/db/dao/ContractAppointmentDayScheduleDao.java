package ru.croc.ugd.ssr.db.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import ru.reinform.cdp.db.model.DocumentData;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * DAO for ContractAppointmentDayScheduleDao.
 */
public interface ContractAppointmentDayScheduleDao extends Repository<DocumentData, String> {

    @Query(value = "select * from documents"
        + " where doc_type = 'CONTRACT-APPOINTMENT-DAY-SCHEDULE'"
        + " and (cast(cast(:from as text) as date) is null"
        + "   or cast(json_data -> 'contractAppointmentDaySchedule' -> 'contractAppointmentDayScheduleData'"
        + " ->> 'date' as date) >= cast(cast(:from as text) as date))"
        + " and (cast(cast(:to as text) as date) is null"
        + "   or cast(json_data -> 'contractAppointmentDaySchedule' -> 'contractAppointmentDayScheduleData'"
        + " ->> 'date' as date) <= cast(cast(:to as text) as date))"
        + " and (cast(:cipId as text) is null"
        + "   or json_data -> 'contractAppointmentDaySchedule' -> 'contractAppointmentDayScheduleData'"
        + " ->> 'cipId' = cast(:cipId as text))",
        nativeQuery = true)
    List<DocumentData> findAllByDatesAndCip(
        @Param("from") final LocalDate from,
        @Param("to") final LocalDate to,
        @Param("cipId") final String cipId
    );

    @Query(value = "SELECT DISTINCT ON (id) docs.* FROM documents docs, "
        + " jsonb_array_elements(docs.json_data -> 'contractAppointmentDaySchedule'"
        + " -> 'contractAppointmentDayScheduleData' -> 'slots' "
        + " -> 'slot') as slot, "
        + " jsonb_array_elements(slot -> 'windows' -> 'window') AS windows "
        + " WHERE doc_type = 'CONTRACT-APPOINTMENT-DAY-SCHEDULE' AND windows ->> 'preBookingId' = :preBookingId"
        + "    AND cast((cast(docs.json_data -> 'contractAppointmentDaySchedule'-> 'contractAppointmentDayScheduleData'"
        + "        ->> 'date' as text) || ' ' || cast(slot ->> 'timeFrom' as text)) as timestamp) > now() ",
        nativeQuery = true)
    List<DocumentData> findAllByPreBookingId(
        @Param("preBookingId") final String preBookingId
    );

    @Query(value = "SELECT distinct json_data, docs.id,"
        + " docs.doc_type, docs.create_date, docs.md5_data FROM documents docs, "
        + " jsonb_array_elements(docs.json_data -> 'contractAppointmentDaySchedule' ->"
        + " 'contractAppointmentDayScheduleData' -> 'slots'"
        + "  -> 'slot') as slot, "
        + " jsonb_array_elements(slot -> 'windows' -> 'window') as windows "
        + " WHERE doc_type = 'CONTRACT-APPOINTMENT-DAY-SCHEDULE'"
        + "  AND windows ->> 'preBookingId' = :preBookingId "
        + "  AND windows ->> 'preBookedUntil' is not null "
        + "  AND (windows ->> 'preBookedUntil')::::timestamptz > :currentServerTime"
        + "  AND cast((cast(docs.json_data -> 'contractAppointmentDaySchedule' -> 'contractAppointmentDayScheduleData' "
        + "     ->> 'date' as text) || ' ' || cast(slot ->> 'timeFrom' as text)) as timestamp) > now()",
        nativeQuery = true)
    Optional<DocumentData> findByPreBookingIdAndPreBookedUntilIsNotNull(
        @Param("preBookingId") final String preBookingId,
        @Param("currentServerTime") final LocalDateTime currentServerTime
    );

    @Query(value = "SELECT docs.* FROM documents docs, "
        + " jsonb_array_elements(docs.json_data -> 'contractAppointmentDaySchedule'"
        + " -> 'contractAppointmentDayScheduleData' -> 'slots' "
        + " -> 'slot') as slot, "
        + " jsonb_array_elements(slot -> 'windows' -> 'window') AS windows "
        + " WHERE doc_type = 'CONTRACT-APPOINTMENT-DAY-SCHEDULE' "
        + "     AND windows ->> 'preBookingId' = cast(:bookingId as text)"
        + "     AND windows ->> 'preBookedUntil' is null"
        + "     AND cast((cast(docs.json_data -> 'contractAppointmentDaySchedule' -> "
        + "         'contractAppointmentDayScheduleData' ->> 'date' as text)"
        + "         || ' ' || cast(slot ->> 'timeFrom' as text)) as timestamp) > now() ",
        nativeQuery = true)
    Optional<DocumentData> findByBookingIdAndPreBookedUntilIsNull(
        @Param("bookingId") final String bookingId
    );

    @Query(value = "select exists(select 1"
        + "  from documents docs"
        + "     , jsonb_array_elements(docs.json_data -> 'contractAppointmentDaySchedule'"
        + "       -> 'contractAppointmentDayScheduleData' -> 'slots' -> 'slot') as slot"
        + "     , jsonb_array_elements(slot -> 'windows' -> 'window') as windows"
        + " where doc_type = 'CONTRACT-APPOINTMENT-DAY-SCHEDULE'"
        + "   and windows ->> 'preBookingId' = :preBookingId"
        + "   and windows ->> 'preBookedUntil' is null"
        + "   and cast((cast(docs.json_data -> 'contractAppointmentDaySchedule' ->"
        + "       'contractAppointmentDayScheduleData' ->> 'date' as text) || ' ' ||"
        + "       cast(slot ->> 'timeFrom' as text)) as timestamp) > now())",
        nativeQuery = true)
    boolean isAlreadyBooked(
        @Param("preBookingId") final String preBookingId
    );

    @Query(value = "SELECT * FROM documents docs, "
        + " jsonb_array_elements(docs.json_data -> 'contractAppointmentDaySchedule'"
        + " -> 'contractAppointmentDayScheduleData' -> 'slots' "
        + "  -> 'slot') as slot "
        + "WHERE doc_type = 'CONTRACT-APPOINTMENT-DAY-SCHEDULE' "
        + " AND slot ->> 'slotId' = :slotId",
        nativeQuery = true)
    Optional<DocumentData> findBySlotId(
        @Param("slotId") final String slotId
    );
}
