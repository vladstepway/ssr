package ru.croc.ugd.ssr.exception;

/**
 * Validation error when schedule already existed.
 */
public class ScheduleDayAlreadyExisted extends SsrException {

    /**
     * Creates ScheduleDayAlreadyExisted.
     */
    public ScheduleDayAlreadyExisted() {
        super("На данную дату или период расписание уже существует");
    }

}
