package ru.croc.ugd.ssr.integration.command;

import lombok.Builder;
import lombok.Data;
import ru.croc.ugd.ssr.contractappointment.ContractAppointmentData;
import ru.croc.ugd.ssr.dto.contractdigitalsign.ContractDigitalSignFlowStatus;

import java.time.LocalDateTime;

/**
 * Команда для отправки статусов в очередь для заявления на запись на подписание договора с помощью УКЭП.
 */
@Data
@Builder(toBuilder = true)
public class ContractDigitalSignPublishReasonCommand {

    private ContractAppointmentData contractAppointment;

    private ContractDigitalSignFlowStatus status;

    private String elkStatusText;

    private LocalDateTime responseReasonDate;
}
