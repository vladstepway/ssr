package ru.croc.ugd.ssr.utils;

import static java.time.temporal.ChronoUnit.DAYS;

import ru.croc.ugd.ssr.exception.InvalidDataInput;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * PeriodUtils.
 */
public class PeriodUtils {

    /**
     * Генерация интервала дней.
     *
     * @param startDate Дата начала интегвала.
     * @param endDate Дата окончания интервала.
     * @return Интервала дней.
     */
    public static List<LocalDate> getDatesPeriod(final LocalDate startDate, final LocalDate endDate) {
        if (endDate != null && startDate.isAfter(endDate)) {
            throw new InvalidDataInput();
        }

        final long duration = endDate != null ? DAYS.between(startDate, endDate) + 1 : 1;
        final List<LocalDate> periodDates = new ArrayList<>();

        LocalDate currentDate = startDate;

        for (int i = 1; i <= duration; i++) {
            periodDates.add(currentDate);
            currentDate = currentDate.plusDays(1);
        }

        return periodDates;
    }

}
