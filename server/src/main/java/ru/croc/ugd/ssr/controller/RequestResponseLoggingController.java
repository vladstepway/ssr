package ru.croc.ugd.ssr.controller;

import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.service.RequestResponseLoggingService;

import java.util.List;

/**
 * Контроллер для настройки логирования запросов и ответов.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/request-response-logging")
@ConditionalOnProperty(name = "ugd.ssr.request-response-logging.uri-filter.enabled")
public class RequestResponseLoggingController {

    private final RequestResponseLoggingService requestResponseLoggingService;

    @ApiOperation(value = "Обновить список URI, для которых включено логирование")
    @PutMapping(value = "/uri-filter")
    public void updateUriFilter(@RequestBody final List<String> uriFilterValues) {
        requestResponseLoggingService.updateUriFilter(uriFilterValues);
    }

    @ApiOperation(value = "Получить список URI, для которых включено логирование")
    @GetMapping(value = "/uri-filter")
    public List<String> fetchUriFilter() {
        return requestResponseLoggingService.fetchUriFilter();
    }
}
