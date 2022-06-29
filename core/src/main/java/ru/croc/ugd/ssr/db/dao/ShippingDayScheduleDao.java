package ru.croc.ugd.ssr.db.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import ru.reinform.cdp.db.model.DocumentData;

import java.time.LocalDate;
import java.util.List;
import javax.annotation.Nonnull;

/**
 * DAO for ShippingDaySchedule.
 */
public interface ShippingDayScheduleDao extends Repository<DocumentData, String> {

    /**
     * Retrieves all with SHIPPING-DAY-SCHEDULE document type.
     *
     * @return result
     */
    @Query(value = "SELECT * FROM documents"
        + "       WHERE doc_type='SHIPPING-DAY-SCHEDULE'", nativeQuery = true)
    List<DocumentData> findAll();

    /**
     * Retrieves ShippingDaySchedule by dateFrom.
     *
     * @param moveDate           moveDate
     * @param districtName       districtName
     * @param startTimetableDate startTimetableDate
     * @return ShippingDaySchedule
     */
    @Query(value = "SELECT * FROM documents"
        + "       WHERE doc_type='SHIPPING-DAY-SCHEDULE'"
        + "         AND (CAST(json_data -> 'shippingDaySchedule' -> 'shippingDayScheduleData'"
        + "                     ->> 'shippingDate' AS DATE)"
        + "             BETWEEN :startDate AND :moveDate)"
        + "         AND ((:districtName IS NULL"
        + "             AND json_data -> 'shippingDaySchedule' -> 'shippingDayScheduleData' ->> 'area' IS NULL) "
        + "          OR json_data -> 'shippingDaySchedule' -> 'shippingDayScheduleData' "
        + "             ->> 'area' = CAST(:districtName AS TEXT))",
        nativeQuery = true)
    List<DocumentData> findByMoveDateAndDistrictName(
        @Param("startDate") final LocalDate startTimetableDate,
        @Param("moveDate") final LocalDate moveDate,
        @Param("districtName") final String districtName
    );

    /**
     * Retrieves ShippingDayScheduleJSON by slotId inside any of intervals.
     *
     * @param slotId slotId
     * @return ShippingDayScheduleJSON
     */
    @Query(value = "select Cast(day as varchar)"
        + "   from (select jsonb_array_elements(json_data -> 'bookingSlots') as json_data, day as day"
        + "   from (select jsonb_array_elements(json_data -> 'shippingDaySchedule'"
        + " -> 'shippingDayScheduleData' -> 'brigades')"
        + "                       as json_data, documents.json_data as day"
        + "   from documents where doc_type = 'SHIPPING-DAY-SCHEDULE') as brigades) as slots"
        + "   where json_data ->> 'slotId' = :slotId",
        nativeQuery = true
    )
    List<String> findShippingDayJsonBySlotIdInside(@Nonnull @Param("slotId") String slotId);

    /**
     * Retrieves ShippingDaySchedule by area and shippingDate.
     *
     * @param area         area
     * @param shippingDate shippingDate
     * @return ShippingDaySchedule
     */
    @Query(value = "SELECT EXISTS( "
        + "SELECT 1 FROM documents"
        + "  WHERE doc_type = 'SHIPPING-DAY-SCHEDULE'"
        + "   AND json_data -> 'shippingDaySchedule' -> 'shippingDayScheduleData' ->> 'area' = :area"
        + "   AND CAST (json_data -> 'shippingDaySchedule' -> 'shippingDayScheduleData' ->>"
        + "     'shippingDate' as DATE) = :shippingDate)",
        nativeQuery = true)
    Boolean existsByAreaAndShippingDate(
        @Param("area") final String area,
        @Param("shippingDate") final LocalDate shippingDate
    );

    /**
     * Retrieves ShippingDaySchedule by shippingDate.
     *
     * @param shippingDates shippingDates
     * @param area          area
     * @return ShippingDaySchedule
     */
    @Query(value = "SELECT * FROM documents"
        + "  WHERE doc_type = 'SHIPPING-DAY-SCHEDULE'"
        + "   AND CAST (json_data -> 'shippingDaySchedule' -> 'shippingDayScheduleData' ->>"
        + "     'shippingDate' as DATE) IN :shippingDates"
        + " AND json_data -> 'shippingDaySchedule' -> 'shippingDayScheduleData' ->> 'area' = :area",
        nativeQuery = true)
    List<DocumentData> findAllByShippingDatesAndArea(
        @Param("shippingDates") final List<LocalDate> shippingDates,
        @Param("area") final String area
    );

    /**
     * Retrieves ShippingDaySchedule by shippingDate.
     *
     * @param shippingDates shippingDate
     * @param area          area
     * @return ShippingDaySchedule
     */
    @Query(value = "SELECT EXISTS (SELECT 1 FROM documents docs"
        + "           , jsonb_array_elements(docs.json_data -> 'shippingDaySchedule' -> 'shippingDayScheduleData'"
        + "         -> 'brigades') as brigades"
        + "           , jsonb_array_elements(brigades -> 'bookingSlots') as bookingSlots"
        + "         WHERE doc_type = 'SHIPPING-DAY-SCHEDULE' "
        + "          AND CAST (json_data -> 'shippingDaySchedule' -> 'shippingDayScheduleData' ->>"
        + "               'shippingDate' as DATE) IN :shippingDates"
        + "          AND bookingSlots ->> 'preBookingId' IS NOT NULL"
        + "          AND json_data -> 'shippingDaySchedule' -> 'shippingDayScheduleData' ->> 'area' = :area )",
        nativeQuery = true)
    boolean existsByShippingDatesAndNotNullPreBookingId(
        @Param("shippingDates") final List<LocalDate> shippingDates, @Param("area") final String area
    );

    /**
     * Retrieves all by preBookingId.
     *
     * @param preBookingId preBookingId
     * @return ShippingDaySchedule
     */
    @Query(value = "SELECT DISTINCT ON (id) docs.* FROM documents docs"
        + "           , jsonb_array_elements(docs.json_data -> 'shippingDaySchedule' -> 'shippingDayScheduleData'"
        + "         -> 'brigades') as brigades"
        + "           , jsonb_array_elements(brigades -> 'bookingSlots') as bookingSlots"
        + "         WHERE doc_type = 'SHIPPING-DAY-SCHEDULE'"
        + "          AND bookingSlots ->> 'preBookingId' = :preBookingId",
        nativeQuery = true)
    List<DocumentData> findAllByPreBookingId(
        @Param("preBookingId") final String preBookingId
    );

    /**
     * Finds district by preBookingId.
     *
     * @param preBookingId preBookingId
     * @return district
     */
    @Query(value = "SELECT DISTINCT docs.json_data -> 'shippingDaySchedule' -> 'shippingDayScheduleData'"
        + "         ->> 'area' as district"
        + " FROM documents docs"
        + "           , jsonb_array_elements(docs.json_data -> 'shippingDaySchedule' -> 'shippingDayScheduleData'"
        + "         -> 'brigades') as brigades"
        + "           , jsonb_array_elements(brigades -> 'bookingSlots') as bookingSlots"
        + "         WHERE doc_type = 'SHIPPING-DAY-SCHEDULE'"
        + "          AND bookingSlots ->> 'preBookingId' = :preBookingId"
        + "          AND bookingSlots ->> 'preBookedUntil' IS NULL",
        nativeQuery = true)
    List<String> findDistrictByPreBookingId(
        @Param("preBookingId") final String preBookingId
    );

    /**
     * Finds brigade name by preBookingId.
     *
     * @param preBookingId preBookingId
     * @return brigade name
     */
    @Query(value = "SELECT DISTINCT brigades ->> 'name' as brigadeName "
        + " FROM documents docs"
        + "           , jsonb_array_elements(docs.json_data -> 'shippingDaySchedule' -> 'shippingDayScheduleData'"
        + "         -> 'brigades') as brigades"
        + "           , jsonb_array_elements(brigades -> 'bookingSlots') as bookingSlots"
        + "         WHERE doc_type = 'SHIPPING-DAY-SCHEDULE'"
        + "          AND bookingSlots ->> 'preBookingId' = :preBookingId"
        + "          AND bookingSlots ->> 'preBookedUntil' IS NULL",
        nativeQuery = true)
    List<String> findBrigadeByPreBookingId(
        @Param("preBookingId") final String preBookingId
    );
}
