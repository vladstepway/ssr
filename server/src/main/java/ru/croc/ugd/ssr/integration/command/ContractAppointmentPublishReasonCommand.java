package ru.croc.ugd.ssr.integration.command;

import lombok.Builder;
import lombok.Data;
import ru.croc.ugd.ssr.contractappointment.ContractAppointmentData;
import ru.croc.ugd.ssr.dto.contractappointment.ContractAppointmentFlowStatus;

import java.time.LocalDateTime;

/**
 * Команда для отправки статусов в очередь для заявления на запись на подписание договора.
 */
@Data
@Builder(toBuilder = true)
public class ContractAppointmentPublishReasonCommand {

    private ContractAppointmentData contractAppointment;

    private ContractAppointmentFlowStatus status;

    private String elkStatusText;

    private LocalDateTime responseReasonDate;
}
