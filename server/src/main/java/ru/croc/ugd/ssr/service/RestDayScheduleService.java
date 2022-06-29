package ru.croc.ugd.ssr.service;

import ru.croc.ugd.ssr.dto.RestDayScheduleDto;
import ru.croc.ugd.ssr.dto.RestDaySchedulePeriodDto;
import ru.croc.ugd.ssr.model.DayScheduleDocumentAbstract;

import java.time.LocalDate;
import java.util.List;

/**
 * Сервис рабочих дней.
 */
public interface RestDayScheduleService<T extends DayScheduleDocumentAbstract> {

    /**
     * Получение рабочих дней.
     *
     * @param from Дата начала периода.
     * @param to Дата окончания периода.
     * @param parentDocumentId ID родительского документа.
     * @param isFreeOnly Только свободные рабочие дни.
     * @return Рабочие дни.
     */
    List<RestDayScheduleDto> findAll(
        final LocalDate from, final LocalDate to, final String parentDocumentId, final boolean isFreeOnly
    );

    /**
     * Создание рабочего дня.
     *
     * @param restDayScheduleDto Рабочий день.
     * @return Рабочий день.
     */
    RestDayScheduleDto create(final RestDayScheduleDto restDayScheduleDto);

    /**
     * Изменение рабочего дня.
     *
     * @param id Уникальный идентификатор.
     * @param restDayScheduleDto Рабочий день.
     * @return Рабочий день.
     */
    RestDayScheduleDto update(final String id, final RestDayScheduleDto restDayScheduleDto);

    /**
     * Удаление рабочего дня.
     *
     * @param id Уникальный идентификатор.
     */
    void delete(final String id);

    /**
     * Копирование рабочего дня.
     *
     * @param id Уникальный идентификатор.
     * @param restDaySchedulePeriodDto Параметры копирования.
     */
    List<RestDayScheduleDto> copyDaySchedules(final String id, final RestDaySchedulePeriodDto restDaySchedulePeriodDto);

    /**
     * Удаление рабочих дней за период.
     *
     * @param restDaySchedulePeriodDto Параметры удаления.
     * @return Заблокированные для удаления рабочие дни.
     */
    List<RestDayScheduleDto> deleteDaySchedules(final RestDaySchedulePeriodDto restDaySchedulePeriodDto);
}
