package ru.croc.ugd.ssr.controller.contractappointment;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.dto.RestDayScheduleDto;
import ru.croc.ugd.ssr.dto.RestDaySchedulePeriodDto;
import ru.croc.ugd.ssr.model.contractappointment.ContractAppointmentDayScheduleDocument;
import ru.croc.ugd.ssr.service.RestDayScheduleService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;

/**
 * Контроллер рабочих дней ЦИП.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/internal/contract-appointments/schedule")
public class ContractAppointmentDayScheduleController {

    private final RestDayScheduleService<ContractAppointmentDayScheduleDocument> restDayScheduleService;

    /**
     * Получение рабочих дней.
     *
     * @param from  Дата начала периода.
     * @param to    Дата окончания периода.
     * @param cipId ID ЦИП.
     * @param isFreeOnly Только свободные рабочие дни.
     * @return Рабочие дни.
     */
    @GetMapping
    public List<RestDayScheduleDto> findAll(
        @RequestParam(required = false) final String from,
        @RequestParam(required = false) final String to,
        @RequestParam final String cipId,
        @RequestParam(required = false, defaultValue = "false") final Boolean isFreeOnly
    ) {
        return restDayScheduleService.findAll(
            Optional.ofNullable(from).map(LocalDate::parse).orElse(null),
            Optional.ofNullable(to).map(LocalDate::parse).orElse(null),
            cipId,
            isFreeOnly
        );
    }

    /**
     * Создание рабочего дня.
     *
     * @param body Рабочий день.
     * @return Рабочий день.
     */
    @PostMapping
    public RestDayScheduleDto create(
        @RequestBody @Valid final RestDayScheduleDto body
    ) {
        return restDayScheduleService.create(body);
    }

    /**
     * Изменение рабочего дня.
     *
     * @param id   Уникальный идентификатор.
     * @param body Рабочий день.
     * @return Рабочий день.
     */
    @PutMapping(value = "/{id}")
    public RestDayScheduleDto update(
        @PathVariable("id") final String id,
        @RequestBody @Valid final RestDayScheduleDto body
    ) {
        return restDayScheduleService.update(id, body);
    }

    /**
     * Удаление рабочего дня.
     *
     * @param id Уникальный идентификатор.
     */
    @DeleteMapping(value = "/{id}")
    public void delete(
        @PathVariable("id") final String id
    ) {
        restDayScheduleService.delete(id);
    }

    /**
     * Копирование рабочего дня.
     *
     * @param id                       Уникальный идентификатор.
     * @param restDaySchedulePeriodDto Параметры копирования.
     * @return restDayScheduleDtoList
     */
    @PostMapping(value = "/{id}/copy")
    public List<RestDayScheduleDto> copyDaySchedule(
        @PathVariable("id") final String id,
        @RequestBody final RestDaySchedulePeriodDto restDaySchedulePeriodDto
    ) {
        return restDayScheduleService.copyDaySchedules(id, restDaySchedulePeriodDto);
    }

    /**
     * Удаление рабочих дней за период.
     *
     * @param restDaySchedulePeriodDto Параметры удаления.
     * @return Заблокированные для удаления рабочие дни.
     */
    @DeleteMapping
    public List<RestDayScheduleDto> deleteDaySchedules(
        @RequestBody final RestDaySchedulePeriodDto restDaySchedulePeriodDto
    ) {
        return restDayScheduleService.deleteDaySchedules(restDaySchedulePeriodDto);
    }
}
