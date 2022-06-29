package ru.croc.ugd.ssr.integration.command;

import lombok.Builder;
import lombok.Data;
import ru.croc.ugd.ssr.dto.flatappointment.FlatAppointmentFlowStatus;
import ru.croc.ugd.ssr.flatappointment.FlatAppointmentData;

import java.time.LocalDateTime;

/**
 * Команда для отправки статусов в очередь для заявления на запись на осмотр квартиры.
 */
@Data
@Builder(toBuilder = true)
public class FlatAppointmentPublishReasonCommand {

    private FlatAppointmentData flatAppointment;

    private FlatAppointmentFlowStatus status;

    private String elkStatusText;

    private LocalDateTime responseReasonDate;
}
