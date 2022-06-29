package ru.croc.ugd.ssr.controller.notary;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.dto.RestDayScheduleDto;
import ru.croc.ugd.ssr.dto.RestDaySchedulePeriodDto;
import ru.croc.ugd.ssr.model.NotaryDayScheduleDocument;
import ru.croc.ugd.ssr.service.RestDayScheduleService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;

/**
 * Контроллер рабочих дней нотариуса.
 */
@RestController
@AllArgsConstructor
public class NotaryDayScheduleController {

    private final RestDayScheduleService<NotaryDayScheduleDocument> restDayScheduleService;

    /**
     * Получение рабочих дней.
     *
     * @param from Дата начала периода.
     * @param to Дата окончания периода.
     * @param notaryId ID нотариуса.
     * @param isFreeOnly Только свободные рабочие дни.
     * @return Рабочие дни.
     */
    @GetMapping(value = "/notary/schedule")
    public List<RestDayScheduleDto> findAll(
        @RequestParam(required = false) final String from,
        @RequestParam(required = false) final String to,
        @RequestParam final String notaryId,
        @RequestParam(required = false, defaultValue = "false") final Boolean isFreeOnly
    ) {
        return restDayScheduleService.findAll(
            Optional.ofNullable(from).map(LocalDate::parse).orElse(null),
            Optional.ofNullable(to).map(LocalDate::parse).orElse(null),
            notaryId,
            isFreeOnly
        );
    }

    /**
     * Создание рабочего дня.
     *
     * @param body Рабочий день.
     * @return Рабочий день.
     */
    @PostMapping(value = "/notary/schedule")
    public RestDayScheduleDto create(
        @RequestBody @Valid final RestDayScheduleDto body
    ) {
        return restDayScheduleService.create(body);
    }

    /**
     * Изменение рабочего дня.
     *
     * @param id Уникальный идентификатор.
     * @param body Рабочий день.
     * @return Рабочий день.
     */
    @PutMapping(value = "/notary/schedule/{id}")
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
    @DeleteMapping(value = "/notary/schedule/{id}")
    public void delete(
        @PathVariable("id") final String id
    ) {
        restDayScheduleService.delete(id);
    }

    /**
     * Копирование рабочего дня.
     *
     * @param id Уникальный идентификатор.
     * @param restDaySchedulePeriodDto Параметры копирования.
     * @return restDayScheduleDtoList
     */
    @PostMapping(value = "/notary/schedule/{id}/copy")
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
    @DeleteMapping(value = "/notary/schedule")
    public List<RestDayScheduleDto> deleteDaySchedules(
        @RequestBody final RestDaySchedulePeriodDto restDaySchedulePeriodDto
    ) {
        return restDayScheduleService.deleteDaySchedules(restDaySchedulePeriodDto);
    }
}
