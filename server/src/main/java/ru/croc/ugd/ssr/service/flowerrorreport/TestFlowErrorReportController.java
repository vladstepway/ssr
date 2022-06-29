package ru.croc.ugd.ssr.service.flowerrorreport;

import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class TestFlowErrorReportController {

    private final FlowErrorReportScheduler flowErrorReportScheduler;

    @ApiOperation(value = "Отправить отчет ошибок данных из ДГИ за вчерашний день")
    @GetMapping("/sendFlowErrorReport")
    public void sendFlowErrorReport() {
        flowErrorReportScheduler.flowErrorReportsNotificationSentOut();
    }

    @ApiOperation(value = "Обработать ошибки доступные для исправления.")
    @GetMapping("/processAvailableForFixErrors")
    public void processAvailableForFixErrors() {
        flowErrorReportScheduler.processAvailableForFixErrors();
    }
}
