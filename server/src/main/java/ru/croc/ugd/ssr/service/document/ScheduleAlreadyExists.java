package ru.croc.ugd.ssr.service.document;

import ru.croc.ugd.ssr.exception.SsrException;

/**
 * Occurs when schedule already exists for specified area and date.
 */
public class ScheduleAlreadyExists extends SsrException {

    /**
     * Creates ScheduleAlreadyExists.
     */
    public ScheduleAlreadyExists() {
        super("Расписание для указанного дня и района уже существует.");
    }
}
