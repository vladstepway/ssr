package ru.croc.ugd.ssr.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.report.FirstFlowErrorAnalyticsReport;
import ru.croc.ugd.ssr.report.FlowsReport;
import ru.croc.ugd.ssr.report.ReportScheduler;
import ru.croc.ugd.ssr.report.SentMessageStatisticsReport;

import java.time.LocalDate;
import java.util.List;
import javax.annotation.Nonnull;

/**
 * Контроллер для дневных отчетов.
 */
@RestController
@RequiredArgsConstructor
public class ReportController {

    private final FlowsReport flowsReport;
    private final FirstFlowErrorAnalyticsReport firstFlowErrorAnalyticsReport;
    private final SentMessageStatisticsReport sentMessageStatisticsReport;
    private final ReportScheduler reportScheduler;

    /**
     * Формирование отчета по ошибкам ДГИ.
     *
     * @param id ID документа задачи на разбор ошибок ДГИ.
     * @return статус формирования.
     */
    @GetMapping(value = "/firstFlowErrorReport/{id}")
    public ResponseEntity<String> firstFlowErrorReport(
        @Nonnull @PathVariable("id") String id) {
        String fileId = firstFlowErrorAnalyticsReport.createReport(id);
        return new ResponseEntity<>(fileId, HttpStatus.OK);
    }

    /**
     * Формирование отчета со статистикой по уведомлениям.
     *
     * @return статус формирования.
     */
    @GetMapping(value = "/sentMessagesStatistics")
    public ResponseEntity<String> sentMessagesStatistics() {
        String fileId = sentMessageStatisticsReport.createReport();
        return new ResponseEntity<>(fileId, HttpStatus.OK);
    }

    /**
     * Формирование отчета.
     *
     * @param date
     *            дата на которую формируется отчет.
     * @return статус формирования.
     */
    @GetMapping(value = "/dailyReport")
    public ResponseEntity<String> getDailyReport(
        @Nonnull @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        String fileId = flowsReport.createReport(date);
        return new ResponseEntity<>(fileId, HttpStatus.OK);
    }

    @PostMapping(value = "/send-defects-report")
    public void sendDefectsReport(
        @RequestParam("logins") final List<String> logins,
        @RequestParam("fileId") final String fileId
    ) {
        reportScheduler.sendDefectReport(logins, fileId);
    }
}
