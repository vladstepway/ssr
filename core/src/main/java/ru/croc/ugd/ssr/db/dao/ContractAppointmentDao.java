package ru.croc.ugd.ssr.db.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import ru.reinform.cdp.db.model.DocumentData;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * DAO для заявлений на запись на подписание договора.
 */
public interface ContractAppointmentDao extends Repository<DocumentData, String> {

    @Query(value = "SELECT EXISTS(SELECT 1 "
        + "from documents "
        + "where doc_type = 'CONTRACT-APPOINTMENT' "
        + "  and json_data -> 'contractAppointment' -> 'contractAppointmentData'"
        + " ->> 'eno' = :eno)",
        nativeQuery = true)
    boolean existsByEno(@Param("eno") String eno);

    /**
     * Получение заявления на подписание договора по номеру заявления.
     *
     * @param eno номер заявления.
     * @return заявление на подписание договора.
     */
    @Query(value = "select * "
        + "from documents "
        + "where doc_type = 'CONTRACT-APPOINTMENT' "
        + "  and json_data -> 'contractAppointment' -> 'contractAppointmentData'"
        + " ->> 'eno' = :eno",
        nativeQuery = true)
    Optional<DocumentData> findByEno(@Param("eno") String eno);

    @Query(value = "SELECT EXISTS(select 1 "
        + "from documents "
        + "where doc_type = 'CONTRACT-APPOINTMENT' "
        + "  and cast(json_data -> 'contractAppointment' -> 'contractAppointmentData'"
        + " ->> 'appointmentDateTime' as timestamp) > now()"
        + "  and json_data -> 'contractAppointment' -> 'contractAppointmentData'"
        + " ->> 'bookingId' = :bookingId"
        + " and json_data -> 'contractAppointment' -> 'contractAppointmentData'"
        + "    ->> 'statusId' = :statusId)",
        nativeQuery = true)
    boolean existsByBookingIdAndStatusId(
        @Param("bookingId") String bookingId,
        @Param("statusId") String statusId
    );

    @Query(value = "SELECT *"
        + " from documents"
        + " where doc_type = 'CONTRACT-APPOINTMENT'"
        + "  and json_data -> 'contractAppointment' -> 'contractAppointmentData'"
        + "          ->> 'contractOrderId' = :contractId"
        + "  and (json_data -> 'contractAppointment' -> 'contractAppointmentData'"
        + "           ->> 'isContractEntered' is null"
        + "    or json_data -> 'contractAppointment' -> 'contractAppointmentData'"
        + "           ->> 'isContractEntered' = 'true')"
        + "  and json_data -> 'contractAppointment' -> 'contractAppointmentData'"
        + "    ->> 'cancelDate' is null",
        nativeQuery = true)
    Optional<DocumentData> fetchActiveOrEnteredContractAppointment(
        @Param("contractId") final String contractId
    );

    @Query(value = "SELECT *"
        + " FROM documents"
        + " WHERE doc_type = 'CONTRACT-APPOINTMENT'"
        + "  AND json_data -> 'contractAppointment' -> 'contractAppointmentData'"
        + "          -> 'applicant' ->> 'personDocumentId' = :personDocumentId"
        + "  AND (:includeInactive = 'true'"
        + "    OR (json_data -> 'contractAppointment' -> 'contractAppointmentData' ->> 'cancelDate' IS NULL"
        + "      AND json_data -> 'contractAppointment' -> 'contractAppointmentData' ->> 'isContractEntered' IS NULL))",
        nativeQuery = true)
    List<DocumentData> fetchAll(
        @Param("personDocumentId") final String personDocumentId,
        @Param("includeInactive") final boolean includeInactive
    );

    @Query(value = "select *"
        + " from documents "
        + " where doc_type = 'CONTRACT-APPOINTMENT' "
        + "  and json_data -> 'contractAppointment' -> 'contractAppointmentData' "
        + "      ->> 'statusId' = '1050'"
        + "  and json_data -> 'contractAppointment' -> 'contractAppointmentData' "
        + "      ->> 'signType' = '1'"
        + "  and (cast(json_data -> 'contractAppointment' -> 'contractAppointmentData'"
        + "      ->> 'appointmentDateTime' as date) < cast(cast(:appointmentDate as text) as date))",
        nativeQuery = true)
    List<DocumentData> findRegisteredAppointmentsByAppointmentDateTimeBefore(
        @Param("appointmentDate") final LocalDate appointmentDate
    );

    @Query(value = "select * "
        + "from documents "
        + "where doc_type = 'CONTRACT-APPOINTMENT' "
        + "and json_data -> 'contractAppointment' -> 'contractAppointmentData' ->> 'statusId' = '999001' "
        + "and (cast(json_data -> 'contractAppointment' -> 'contractAppointmentData' "
        + "->> 'appointmentDateTime' as date)) = (current_date - 1)",
        nativeQuery = true)
    List<DocumentData> findUnsignedAppointmentsForYesterday();
}
