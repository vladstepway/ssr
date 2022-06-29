package ru.croc.ugd.ssr.integration.command;

import lombok.Builder;
import lombok.Data;
import ru.croc.ugd.ssr.commission.CommissionInspectionData;
import ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus;

import java.time.LocalDateTime;

/**
 * Команда для отправки статусов по КО в очередь.
 */
@Data
@Builder(toBuilder = true)
public class CommissionInspectionPublishReasonCommand {

    private CommissionInspectionData commissionInspectionData;

    private CommissionInspectionFlowStatus status;

    private String elkStatusText;

    private LocalDateTime responseReasonDate;
}
