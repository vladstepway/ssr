package ru.croc.ugd.ssr.exception;

/**
 * Validation error when try to delete schedule day with booked slots.
 */
public class DeleteScheduleWhenSlotWasBooked extends SsrException {

    /**
     * Creates DeleteScheduleWhenSlotWasBooked.
     */
    public DeleteScheduleWhenSlotWasBooked() {
        super("На указанную дату или период есть забронированные слот(ы)");
    }

}
