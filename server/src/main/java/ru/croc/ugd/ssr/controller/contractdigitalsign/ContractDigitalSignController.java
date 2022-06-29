package ru.croc.ugd.ssr.controller.contractdigitalsign;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.dto.contractdigitalsign.ContractDigitalSignMoveDateDto;
import ru.croc.ugd.ssr.dto.contractdigitalsign.RestEmployeeContractDigitalSignDto;
import ru.croc.ugd.ssr.dto.contractdigitalsign.RestSaveSignatureDto;
import ru.croc.ugd.ssr.service.contractdigitalsign.RestContractDigitalSignService;

import java.util.List;

/**
 * Контроллер для многостороннего подписания договора с использованием УКЭП.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/contract-digital-signs")
public class ContractDigitalSignController {

    private final RestContractDigitalSignService contractDigitalSignService;

    /**
     * Получить данные заявлений для электронного подписания сотрудником в рамках текущего дня.
     *
     * @return список данных о заявлениях для электронного подписания.
     */
    @GetMapping
    public List<RestEmployeeContractDigitalSignDto> fetchAll() {
        return contractDigitalSignService.fetchAll();
    }

    /**
     * Изменить дату запланированного подписания договора с использованием УКЭП.
     * @param dto данные для изменения даты
     */
    @PostMapping(value = "/move-date")
    public void moveAppointmentDate(@RequestBody final ContractDigitalSignMoveDateDto dto) {
        contractDigitalSignService.moveAppointmentDate(dto);
    }

    /**
     * Сохранение информации об актуальной подписи сотрудника.
     *
     * @param id ИД документа многостороннего подписания договора с использованием УКЭП
     * @param restSaveSignatureDto данные для сохранения подписи
     */
    @PostMapping(value = "/{id}/save-signature")
    public void saveSignatureData(
        @PathVariable("id") final String id, @RequestBody final RestSaveSignatureDto restSaveSignatureDto
    ) {
        contractDigitalSignService.saveSignatureData(id, restSaveSignatureDto);
    }
}
