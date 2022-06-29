package ru.croc.ugd.ssr.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.model.DashboardRequestDocument;
import ru.croc.ugd.ssr.service.dashboard.DashboardRequestDocumentService;

/**
 * Контроллер по работе с запросом на дашборд.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/dashboard-request")
public class DashboardRequestController {

    private final DashboardRequestDocumentService service;

    /**
     * Обновление документа.
     *
     * @param id            идентификатор документа
     * @param json          тело документа
     * @param skipUnchanged не фиксировать операцию в журнале, если документ не изменился
     * @param flagReindex   индексировать документ в поисковом движке
     * @param notes         комментарий к операции
     * @return DashboardRequestDocument
     */
    @ApiOperation(value = "Обновление документа")
    @RequestMapping(
            path = "/update/{id}",
            method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public DashboardRequestDocument createDocument(
            @ApiParam(value = "идентификатор документа", required = true)
            @PathVariable String id,
            @ApiParam(value = "тело документа", required = true)
            @RequestBody String json,
            @ApiParam(value = "не фиксировать операцию в журнале, если документ не изменился")
            @RequestParam(required = false, defaultValue = "false") boolean skipUnchanged,
            @ApiParam(value = "индексировать документ в поисковом движке")
            @RequestParam(required = false, defaultValue = "true") boolean flagReindex,
            @ApiParam(value = "комментарий к операции")
            @RequestParam(required = false) String notes) {
        DashboardRequestDocument document = service.parseDocumentJson(json);
        return service.updateDocument(id, document, skipUnchanged, flagReindex, notes);
    }
}
