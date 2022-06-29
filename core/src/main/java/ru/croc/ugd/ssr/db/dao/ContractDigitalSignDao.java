package ru.croc.ugd.ssr.db.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import ru.reinform.cdp.db.model.DocumentData;

import java.util.List;
import java.util.Optional;

/**
 * DAO для многосторонних подписаний договоров с использованием УКЭП.
 */
public interface ContractDigitalSignDao extends Repository<DocumentData, String> {

    @Query(value = "with contract_appointment as ("
        + "    select id"
        + "    from documents"
        + "    where doc_type = 'CONTRACT-APPOINTMENT'"
        + "      and json_data -> 'contractAppointment' -> 'contractAppointmentData'"
        + "              ->> 'contractOrderId' = :contractOrderId"
        + "      and json_data -> 'contractAppointment' -> 'contractAppointmentData'"
        + "        ->> 'isContractEntered' is null"
        + "      and json_data -> 'contractAppointment' -> 'contractAppointmentData'"
        + "        ->> 'cancelDate' is null"
        + ")"
        + " select doc.*"
        + "  from documents doc,"
        + "      contract_appointment"
        + "  where doc.doc_type = 'CONTRACT-DIGITAL-SIGN'"
        + "    and doc.json_data -> 'contractDigitalSign' -> 'contractDigitalSignData'"
        + "          ->> 'contractAppointmentId' = contract_appointment.id",
        nativeQuery = true)
    Optional<DocumentData> findActiveByContractOrderId(@Param("contractOrderId") final String contractOrderId);

    @Query(value = "select * from documents"
        + " where doc_type = 'CONTRACT-DIGITAL-SIGN'"
        + "   and json_data -> 'contractDigitalSign' -> 'contractDigitalSignData' "
        + "       ->> 'contractAppointmentId' = :contractAppointmentId",
        nativeQuery = true)
    List<DocumentData> findByContractAppointmentId(
        @Param("contractAppointmentId") final String contractAppointmentId
    );

    @Query(value = "select distinct on (id) docs.* "
        + "  from documents docs,"
        + "       jsonb_array_elements(json_data -> 'contractDigitalSign' -> 'contractDigitalSignData' -> "
        + "           'owners' -> 'owner') as owners,"
        + "       jsonb_array_elements(owners -> 'elkUserNotifications' -> 'elkUserNotification') as notifications"
        + " where doc_type = 'CONTRACT-DIGITAL-SIGN'"
        + "   and notifications ->> 'eno' = :notificationEno",
        nativeQuery = true)
    List<DocumentData> findByNotificationEno(@Param("notificationEno") final String notificationEno);

    @Query(value = "select *"
        + "  from documents"
        + " where doc_type = 'CONTRACT-DIGITAL-SIGN'"
        + "   and cast(json_data -> 'contractDigitalSign' -> 'contractDigitalSignData'"
        + " ->> 'appointmentDate' as date) = current_date"
        + "   and json_data -> 'contractDigitalSign' -> 'contractDigitalSignData' -> 'employee' ->> 'login' = :login"
        + "   and json_data -> 'contractDigitalSign' -> 'contractDigitalSignData' ->> 'contractAppointmentId' in ("
        + "   select id"
        + "     from documents"
        + "    where doc_type = 'CONTRACT-APPOINTMENT'"
        + "      and cast(json_data -> 'contractAppointment' -> 'contractAppointmentData'"
        + " ->> 'appointmentDateTime' as date) = current_date"
        + "      and json_data -> 'contractAppointment' -> 'contractAppointmentData' ->> 'signType' = '2'"
        + "      and json_data -> 'contractAppointment' -> 'contractAppointmentData' ->> 'statusId' = '999001'"
        + "   )",
        nativeQuery = true)
    List<DocumentData> findActualByEmployeeLogin(@Param("login") final String login);

    @Query(value = "select *"
        + "  from documents"
        + " where doc_type = 'CONTRACT-DIGITAL-SIGN'"
        + "   and cast(json_data -> 'contractDigitalSign' -> 'contractDigitalSignData' "
        + "      ->> 'appointmentDate' as date) = current_date"
        + "   and ("
        + "     (json_data -> 'contractDigitalSign' -> 'contractDigitalSignData' -> 'employee' ->> 'login' = :login)"
        + "     or"
        + "     ((json_data -> 'contractDigitalSign' -> 'contractDigitalSignData' -> 'employee' ->> 'login') is null)"
        + "   )"
        + "   and json_data -> 'contractDigitalSign' -> 'contractDigitalSignData' ->> 'contractAppointmentId' in ("
        + "   select id"
        + "     from documents"
        + "    where doc_type = 'CONTRACT-APPOINTMENT'"
        + "      and cast(json_data -> 'contractAppointment' -> 'contractAppointmentData' "
        + "         ->> 'appointmentDateTime' as date) = current_date"
        + "      and json_data -> 'contractAppointment' -> 'contractAppointmentData' ->> 'signType' = '2'"
        + "      and json_data -> 'contractAppointment' -> 'contractAppointmentData' ->> 'statusId' = '999001'"
        + "   )",
        nativeQuery = true)
    List<DocumentData> findUnassignedOrActualByEmployeeLogin(@Param("login") final String login);

    @Query(value = "select * from documents"
        + " where doc_type = 'CONTRACT-DIGITAL-SIGN'"
        + "   and cast(json_data -> 'contractDigitalSign' -> 'contractDigitalSignData' "
        + "        ->> 'appointmentDate' as date) = current_date",
        nativeQuery = true)
    List<DocumentData> fetchAllForToday();
}
