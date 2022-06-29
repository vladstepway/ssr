package ru.croc.ugd.ssr.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.model.DashboardDocument;
import ru.croc.ugd.ssr.service.dashboard.DashboardDocumentService;

/**
 * Контроллер по работе с дашбордом.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardDocumentService service;

    /**
     * Получить актуальную информацию по дашборду.
     *
     * @return объект дашборда
     */
    @CrossOrigin(origins = "*", allowCredentials = "true", allowedHeaders = "*")
    @GetMapping(value = "/data")
    @ApiOperation("Получить актуальную информацию")
    public DashboardDocument getLastActiveDocument(
        @ApiParam(value = "Признак автоматической записи", required = true)
        @RequestParam boolean auto
    ) {
        return service.getLastActiveDocument(auto);
    }


    /**
     * Создание документа.
     *
     * @param json        тело документа
     * @param flagReindex индексировать документ в поисковом движке
     * @param notes       комментарий к операции
     * @return DashboardDocument
     */
    @ApiOperation(value = "Создание документа")
    @RequestMapping(
            path = "/create",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public DashboardDocument createDocument(
            @ApiParam(value = "тело документа", required = true)
            @RequestBody String json,
            @ApiParam(value = "индексировать документ в поисковом движке")
            @RequestParam(required = false, defaultValue = "true") boolean flagReindex,
            @ApiParam(value = "комментарий к операции")
            @RequestParam(required = false) String notes) {
        DashboardDocument document = service.parseDocumentJson(json);
        return service.createDocument(document, flagReindex, notes);
    }

    /**
     * Обновление документа.
     *
     * @param id            идентификатор документа
     * @param json          тело документа
     * @param skipUnchanged не фиксировать операцию в журнале, если документ не изменился
     * @param flagReindex   индексировать документ в поисковом движке
     * @param notes         комментарий к операции
     * @return DashboardDocument
     */
    @ApiOperation(value = "Обновление документа")
    @RequestMapping(
            path = "/update/{id}",
            method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public DashboardDocument createDocument(
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
        DashboardDocument document = service.parseDocumentJson(json);
        return service.updateDocument(id, document, skipUnchanged, flagReindex, notes);
    }

    /**
     * Сформировать автоматическую запись по дашборду.
     *
     * @return id записи
     */
    @ApiOperation(value = "Сформировать автоматическую запись по дашборду")
    @PostMapping("/createAutoDashboardDocument")
    public String createAutoDashboardDocument() {
        return service.createAutoDashboardDocument();
    }

    @ApiOperation(value = "Сформировать и отправить автоматическую запись по дашборду")
    @GetMapping("/createAndSendAutoDashboardDocument")
    public DashboardDocument createAndSendAutoDashboardDocument() {
        return service.getLastActiveDocument(true);
    }

    @ApiOperation(value = "Сформировать автоматическую запись по дашборду с показателями по заселяемым домам")
    @GetMapping("/createSettlementObjectsIndicatorsGroupDashboardDocument")
    public DashboardDocument createSettlementObjectsIndicatorsGroupDashboardDocument() {
        return service.createSettlementObjectsIndicatorsGroupDashboardDocument();
    }

    @ApiOperation(value = "Сформировать автоматическую запись по дашборду с показателями по отселяемым домам")
    @GetMapping("/createResettlementObjectsIndicatorsGroupDashboardDocument")
    public DashboardDocument createResettlementObjectsIndicatorsGroupDashboardDocument() {
        return service.createResettlementObjectsIndicatorsGroupDashboardDocument();
    }

    @ApiOperation(value = "Сформировать автоматическую запись по дашборду с показателями"
        + " по отселяемым домам в процессе переселения")
    @GetMapping("/createResettlementInProcessObjectsIndicatorsGroupDashboardDocument")
    public DashboardDocument createResettlementInProcessObjectsIndicatorsGroupDashboardDocument() {
        return service.createResettlementInProcessObjectsIndicatorsGroupDashboardDocument();
    }

    @ApiOperation(value = "Сформировать автоматическую запись по дашборду с показателями по жителям и семьям")
    @GetMapping("/createPersonsAndFamiliesIndicatorsGroupDashboardDocument")
    public DashboardDocument createPersonsAndFamiliesIndicatorsGroupDashboardDocument() {
        return service.createPersonsAndFamiliesIndicatorsGroupDashboardDocument();
    }

    @ApiOperation(value = "Сформировать автоматическую запись по дашборду с показателями по переселению")
    @GetMapping("/createResettlementIndicatorsGroupDashboardDocument")
    public DashboardDocument createResettlementIndicatorsGroupDashboardDocument() {
        return service.createResettlementIndicatorsGroupDashboardDocument();
    }
}
