package ru.croc.ugd.ssr.exception;

/**
 * Validation error when schedule not found.
 */
public class ScheduleNotFound extends SsrException {

    /**
     * Creates SchedulesNotFound.
     */
    public ScheduleNotFound() {
        super("Расписание отсутствует");
    }
}
