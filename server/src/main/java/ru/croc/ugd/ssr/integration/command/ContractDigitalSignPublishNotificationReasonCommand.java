package ru.croc.ugd.ssr.integration.command;

import lombok.Builder;
import lombok.Data;
import ru.croc.ugd.ssr.dto.contractdigitalsign.ContractDigitalSignNotificationStatus;

import java.time.LocalDateTime;

/**
 * Команда для отправки в очередь статусов уведомлений по многостороннему подписанию договоров с использованием УКЭП.
 */
@Data
@Builder(toBuilder = true)
public class ContractDigitalSignPublishNotificationReasonCommand {

    private String eno;

    private ContractDigitalSignNotificationStatus status;

    private String elkStatusText;

    private LocalDateTime responseReasonDate;

    private Integer reasonCode;
}
