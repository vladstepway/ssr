package ru.croc.ugd.ssr.controller;

import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.RepairDbService;

/**
 * Контроллер по исправлению данных в БД.
 */
@RestController
@AllArgsConstructor
public class RepairDbController {

    private static final Logger LOG = LoggerFactory.getLogger(RepairDbController.class);
    private final RepairDbService service;
    private final PersonDocumentService personDocumentService;

    /**
     * Удаление дублирующих квартир в домах.
     *
     * @return статус
     */
    @ApiOperation(value = "Удаление дублирующих квартир в домах")
    @GetMapping(value = "/deleteDoublesFlats")
    public ResponseEntity<String> deleteDoublesFlats() {
        LOG.info("Запуск процедуры удаления дублей квартир");
        service.deleteDoublesFlats();
        LOG.info("Процедура удаления дублей квартир завершена");
        return ResponseEntity.ok("ok");
    }

    /**
     * Удаление комнат.
     *
     * @return статус
     */
    @ApiOperation(value = "Удаление комнат")
    @GetMapping(value = "/deleteRooms")
    public ResponseEntity<String> deleteRooms() {
        service.deleteRooms();
        return ResponseEntity.ok("Процедура удаления запущена. Статус обработки смотрите в логах.");
    }

    /**
     * Сопоставление жителей ДГП и ДГИ в задачах на разбор.
     *
     * @return статус
     */
    @ApiOperation(value = "Сопоставление жителей ДГП и ДГИ в задачах на разбор")
    @GetMapping(value = "/firstFlowErrorFix")
    public ResponseEntity<String> firstFlowErrorFix() {
        service.firstFlowErrorFix();
        return ResponseEntity.ok("Процедура сопоставления запущена. Статус обработки смотрите в логах.");
    }

    /**
     * depersonalizePersons.
     */
    @ApiOperation(value = "Обезличить жителей. Выполнять только на тестовых стендах!")
    @GetMapping(value = "/depersonalizePersons")
    public void depersonalizePersons() {
        personDocumentService.depersonalizePersons();
    }

}
