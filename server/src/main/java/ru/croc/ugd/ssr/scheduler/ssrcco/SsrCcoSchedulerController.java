package ru.croc.ugd.ssr.scheduler.ssrcco;

import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@AllArgsConstructor
@RequestMapping("/scheduler/ssr-ccos")
public class SsrCcoSchedulerController {

    private final SsrCcoScheduler ssrCcoScheduler;

    @PostMapping(value = "/calculate-defect-act-totals-data")
    public void calculateDefectActTotalsData() {
        ssrCcoScheduler.calculateDefectActTotalsData();
    }

    @PostMapping
    public void dailySsrCcoSync(
        @RequestParam("lastUpdateDateTime")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime lastUpdateDateTime
    ) {
        ssrCcoScheduler.dailySsrCcoSync(lastUpdateDateTime);
    }

    @PostMapping(value = "/sync")
    public void dailySsrCcoSync() {
        ssrCcoScheduler.dailySsrCcoSync();
    }

    @DeleteMapping
    public void deleteAll() {
        ssrCcoScheduler.deleteAll();
    }
}
