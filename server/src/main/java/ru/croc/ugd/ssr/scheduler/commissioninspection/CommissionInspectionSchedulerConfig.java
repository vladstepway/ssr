package ru.croc.ugd.ssr.scheduler.commissioninspection;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "schedulers.commission-inspection")
public class CommissionInspectionSchedulerConfig {

    private int daysToFirstVisitConfirmation;
    private int daysToSecondVisitConfirmation;
    private int daysBeforeProlongation;
    private int daysToRefuseNoCall;
    private int daysToFinishNoCall;
}
