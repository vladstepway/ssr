package ru.croc.ugd.ssr.integration.command;

import lombok.Builder;
import lombok.Data;
import ru.croc.ugd.ssr.dto.notarycompensation.NotaryCompensationFlowStatus;
import ru.croc.ugd.ssr.notarycompensation.NotaryCompensationData;

import java.time.LocalDateTime;

/**
 * Команда для отправки статусов в очередь для заявления на возмещение оплаты нотариуса.
 */
@Data
@Builder(toBuilder = true)
public class NotaryCompensationPublishReasonCommand {

    private NotaryCompensationData notaryCompensation;

    private NotaryCompensationFlowStatus status;

    private String elkStatusText;

    private LocalDateTime responseReasonDate;
}
