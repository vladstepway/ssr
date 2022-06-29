package ru.croc.ugd.ssr.integration.command;

import lombok.Builder;
import lombok.Data;
import ru.croc.ugd.ssr.dto.notary.NotaryApplicationFlowStatus;
import ru.croc.ugd.ssr.notary.NotaryApplicationType;

import java.time.LocalDateTime;

/**
 * Команда для отправки статусов в очередь для заявления на приём к нотариусу.
 */
@Data
@Builder(toBuilder = true)
public class NotaryApplicationPublishReasonCommand {

    private NotaryApplicationType notaryApplication;

    private NotaryApplicationFlowStatus status;

    private String elkStatusText;

    private LocalDateTime responseReasonDate;
}
