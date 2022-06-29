package ru.croc.ugd.ssr.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.dto.RestAddCipEmployeesDto;
import ru.croc.ugd.ssr.model.CipDocument;
import ru.croc.ugd.ssr.service.cip.CipService;

import java.util.List;

/**
 * Контроллер для работы с центрами переселения.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/cip")
public class CipController {

    private final CipService cipService;

    /**
     * Получение всех ЦИПов по округу.
     *
     * @param area код округа
     * @return список ЦИПов
     */
    @ApiOperation(value = "Получение всех ЦИПов по id ОКС")
    @GetMapping(value = "/fetchByArea/{area}")
    public List<CipDocument> fetchByArea(@ApiParam(value = "area") @PathVariable String area) {
        return cipService.fetchByArea(area);
    }

    /**
     * Получение всех ЦИПов по id ОКС.
     *
     * @param ccoId Id ОКС
     * @return список ЦИПов
     */
    @ApiOperation(value = "Получение всех ЦИПов по id ОКС")
    @GetMapping(value = "/fetchByCcoId/{ccoId}")
    public List<CipDocument> fetchByCcoId(@ApiParam(value = "Id ОКС") @PathVariable String ccoId) {
        return cipService.fetchByCcoId(ccoId);
    }

    /**
     * Получение UNOM по personId.
     *
     * @param personId personId
     * @return список ЦИПов
     */
    @ApiOperation(value = "Получение UNOM по personId")
    @GetMapping(value = "/fetchUNOMsFromCipByPersonId/{personId}")
    public List<String> fetchUnomsFromCipByPersonId(@ApiParam(value = "personId") @PathVariable String personId) {
        return cipService.fetchUnomsFromCipByPersonId(personId);
    }

    @PutMapping("/{cipId}/employees")
    public CipDocument updateCipEmployees(
        @PathVariable final String cipId,
        @RequestBody final List<String> employeeLogins
    ) {
        return cipService.updateCipEmployees(cipId, employeeLogins);
    }

    @PostMapping("/add-employees")
    public void addCipEmployees(
        @RequestBody RestAddCipEmployeesDto addCipEmployeesDto
    ) {
        cipService.addCipEmployees(addCipEmployeesDto);
    }
}
