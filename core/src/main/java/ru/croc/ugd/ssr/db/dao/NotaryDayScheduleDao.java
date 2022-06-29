package ru.croc.ugd.ssr.db.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import ru.reinform.cdp.db.model.DocumentData;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * DAO for NotaryDayScheduleDao.
 */
public interface NotaryDayScheduleDao extends Repository<DocumentData, String> {

    @Query(value = "select * from documents"
        + " where doc_type = 'NOTARY-DAY-SCHEDULE'"
        + " and (cast(cast(:from as text) as date) is null"
        + "   or cast(json_data -> 'notaryDaySchedule' -> 'notaryDayScheduleData'"
        + " ->> 'date' as date) >= cast(cast(:from as text) as date))"
        + " and (cast(cast(:to as text) as date) is null"
        + "   or cast(json_data -> 'notaryDaySchedule' -> 'notaryDayScheduleData'"
        + " ->> 'date' as date) <= cast(cast(:to as text) as date))"
        + " and (cast(:notaryId as text) is null"
        + "   or json_data -> 'notaryDaySchedule' -> 'notaryDayScheduleData'"
        + " ->> 'notaryId' = cast(:notaryId as text))",
        nativeQuery = true)
    List<DocumentData> findAllByDatesAndNotary(
        @Param("from") final LocalDate from,
        @Param("to") final LocalDate to,
        @Param("notaryId") final String notaryId
    );

    @Query(value = "SELECT DISTINCT ON (id) docs.* FROM documents docs, "
        + " jsonb_array_elements(docs.json_data -> 'notaryDaySchedule' -> 'notaryDayScheduleData' -> 'slots' "
        + " -> 'slot') as slot, "
        + " jsonb_array_elements(slot -> 'windows' -> 'window') AS windows "
        + " WHERE doc_type = 'NOTARY-DAY-SCHEDULE' AND windows ->> 'preBookingId' = :preBookingId"
        + "     AND cast((cast(docs.json_data -> 'notaryDaySchedule' -> 'notaryDayScheduleData' "
        + "         ->> 'date' as text) || ' ' || cast(slot ->> 'timeFrom' as text)) as timestamp) > now() ",
        nativeQuery = true)
    List<DocumentData> findAllByPreBookingId(
        @Param("preBookingId") final String preBookingId
    );

    @Query(value = "SELECT docs.* FROM documents docs, "
        + " jsonb_array_elements(docs.json_data -> 'notaryDaySchedule' -> 'notaryDayScheduleData' -> 'slots' "
        + " -> 'slot') as slot, "
        + " jsonb_array_elements(slot -> 'windows' -> 'window') AS windows "
        + " WHERE doc_type = 'NOTARY-DAY-SCHEDULE' "
        + "     AND windows ->> 'preBookingId' = cast(:preBookingId as text)"
        + "     AND windows ->> 'preBookedUntil' is null"
        + "     AND cast((cast(docs.json_data -> 'notaryDaySchedule' -> 'notaryDayScheduleData' "
        + "         ->> 'date' as text) || ' ' || cast(slot ->> 'timeFrom' as text)) as timestamp) > now() ",
        nativeQuery = true)
    Optional<DocumentData> findBookedByPreBookingId(
        @Param("preBookingId") final String preBookingId
    );

    @Query(value = "select exists (SELECT 1 FROM documents docs, "
        + " jsonb_array_elements(docs.json_data -> 'notaryDaySchedule' -> 'notaryDayScheduleData' -> 'slots' "
        + " -> 'slot') as slot, "
        + " jsonb_array_elements(slot -> 'windows' -> 'window') AS windows "
        + " WHERE doc_type = 'NOTARY-DAY-SCHEDULE'"
        + " AND windows ->> 'preBookingId' = :preBookingId "
        + " AND windows ->> 'preBookedUntil' is null"
        + " AND cast((cast(docs.json_data -> 'notaryDaySchedule' -> 'notaryDayScheduleData' "
        + "     ->> 'date' as text) || ' ' || cast(slot ->> 'timeFrom' as text)) as timestamp) > now())",
        nativeQuery = true)
    boolean isAlreadyBooked(
        @Param("preBookingId") final String preBookingId
    );

    @Query(value = "SELECT * FROM documents docs, "
        + " jsonb_array_elements(docs.json_data -> 'notaryDaySchedule' -> 'notaryDayScheduleData' -> 'slots' "
        + "  -> 'slot') as slot "
        + "WHERE doc_type = 'NOTARY-DAY-SCHEDULE' "
        + " AND slot ->> 'slotId' = :slotId",
        nativeQuery = true)
    Optional<DocumentData> findBySlotId(
        @Param("slotId") final String slotId
    );

    @Query(value = "SELECT * FROM documents docs, "
        + " jsonb_array_elements(docs.json_data -> 'notaryDaySchedule' -> 'notaryDayScheduleData' -> 'slots'"
        + "  -> 'slot') as slot, "
        + " jsonb_array_elements(slot -> 'windows' -> 'window') as windows "
        + " WHERE doc_type = 'NOTARY-DAY-SCHEDULE'"
        + "  AND windows ->> 'preBookingId' = :preBookingId "
        + "  AND windows ->> 'eno' = :eno "
        + "  AND cast((cast(docs.json_data -> 'notaryDaySchedule' -> 'notaryDayScheduleData' "
        + "     ->> 'date' as text) || ' ' || cast(slot ->> 'timeFrom' as text)) as timestamp) > now()",
        nativeQuery = true)
    Optional<DocumentData> findByPreBookingIdAndEno(
        @Param("preBookingId") final String preBookingId,
        @Param("eno") final String eno
    );

}
