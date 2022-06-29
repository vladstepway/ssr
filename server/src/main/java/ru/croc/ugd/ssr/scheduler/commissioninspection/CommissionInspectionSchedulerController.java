package ru.croc.ugd.ssr.scheduler.commissioninspection;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class CommissionInspectionSchedulerController {

    private final CommissionInspectionScheduler commissionInspectionScheduler;

    @PostMapping(value = "/scheduler/commission-inspection/first-visit-confirmation")
    public void sendFirstVisitConfirmationRequired(@RequestParam("days") Integer daysToFirstVisitConfirmation) {
        commissionInspectionScheduler.sendFirstVisitConfirmationRequired(daysToFirstVisitConfirmation);
    }

    @PostMapping(value = "/scheduler/commission-inspection/second-visit-confirmation")
    public void sendSecondVisitConfirmationRequired(@RequestParam("days") Integer daysToSecondVisitConfirmation) {
        commissionInspectionScheduler.sendSecondVisitConfirmationRequired(daysToSecondVisitConfirmation);
    }

    @PostMapping(value = "/scheduler/commission-inspection/prolongation")
    public void sendProlongation(@RequestParam("days") Integer daysBeforeProlongation) {
        commissionInspectionScheduler.sendProlongation(daysBeforeProlongation);
    }

    @PostMapping(value = "/scheduler/commission-inspection/refuse-no-call")
    public void sendRefuseNoCall(@RequestParam("days") Integer daysToRefuseNoCall) {
        commissionInspectionScheduler.sendRefuseNoCall(daysToRefuseNoCall);
    }

    @PostMapping(value = "/scheduler/commission-inspection/finish-no-call")
    public void sendFinishNoCall(@RequestParam("days") Integer daysToFinishNoCall) {
        commissionInspectionScheduler.sendFinishNoCall(daysToFinishNoCall);
    }
}
