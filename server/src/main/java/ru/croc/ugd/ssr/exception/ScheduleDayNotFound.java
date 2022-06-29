package ru.croc.ugd.ssr.exception;

/**
 * Validation error when schedule not found.
 */
public class ScheduleDayNotFound extends SsrException {

    /**
     * Creates ScheduleDayAlreadyExisted.
     */
    public ScheduleDayNotFound() {
        super("На данную дату или период расписание не существует");
    }

}
