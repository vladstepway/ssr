package ru.croc.ugd.ssr.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.dto.ssrcco.CreateSsrCcoEmployeesDto;
import ru.croc.ugd.ssr.dto.ssrcco.SsrCcoDto;
import ru.croc.ugd.ssr.dto.ssrcco.SsrCcoEmployeePeriod;
import ru.croc.ugd.ssr.service.ssrcco.RestSsrCcoService;

/**
 * Контроллер для работы с ОКС в системе ССР.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/ssr-ccos")
public class SsrCcoController {

    private final RestSsrCcoService restSsrCcoService;

    /**
     * Получить сср окс.
     *
     * @param id id сср окса
     * @return карточка
     */
    @GetMapping(value = "/{id}")
    public SsrCcoDto fetchById(@PathVariable("id") final String id) {
        return restSsrCcoService.fetchById(id);
    }

    /**
     * Получить карточку по идентификатору ОКСа в системе PS.
     *
     * @param psDocumentId идентификатор ОКСа в системе PS
     * @return карточка
     */
    @GetMapping(value = "/ps-document-id/{psDocumentId}")
    public SsrCcoDto fetchByPsDocumentId(@PathVariable("psDocumentId") final String psDocumentId) {
        return restSsrCcoService.fetchByPsDocumentId(psDocumentId);
    }

    /**
     * Привязать содтрудников к оксам.
     * @param createEmployeesDto информация о привязке сотрудников к оксам
     */
    @PostMapping(value = "/-/employees")
    public void createEmployees(
        @RequestBody final CreateSsrCcoEmployeesDto createEmployeesDto
    ) {
        restSsrCcoService.createEmployees(createEmployeesDto);
    }

    /**
     * Удалить сотрудника окса.
     * @param id id сср окса
     * @param employeeId id сотрудника
     */
    @DeleteMapping(value = "/{id}/employees/{employeeId}")
    public void deleteEmployee(
        @PathVariable("id") final String id,
        @PathVariable("employeeId") final String employeeId
    ) {
        restSsrCcoService.deleteEmployee(id, employeeId);
    }

    /**
     * Изменить период назначения сотрудника окса.
     * @param id id сср окса
     * @param employeeId id сотрудника
     * @param employeePeriod период назначения сотрудника
     */
    @PatchMapping(value = "/{id}/employees/{employeeId}")
    public void changeEmployeePeriod(
        @PathVariable("id") final String id,
        @PathVariable("employeeId") final String employeeId,
        @RequestBody final SsrCcoEmployeePeriod employeePeriod
    ) {
        restSsrCcoService.changeEmployeePeriod(id, employeeId, employeePeriod);
    }
}
