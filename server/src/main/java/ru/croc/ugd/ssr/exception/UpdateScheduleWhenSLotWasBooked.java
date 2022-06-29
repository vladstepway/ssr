package ru.croc.ugd.ssr.exception;

/**
 * Validation error when try to update schedule day with booked slots.
 */
public class UpdateScheduleWhenSLotWasBooked extends SsrException {

    /**
     * Creates ScheduleDayAlreadyExisted.
     */
    public UpdateScheduleWhenSLotWasBooked() {
        super("На указанную дату или период есть забронированные слот(ы)");
    }

}
