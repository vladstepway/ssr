package ru.croc.ugd.ssr.exception;

/**
 * Can't remove apartment inspection act.
 */
public class NotPossibleToRemoveApartmentInspection extends SsrException {

    /**
     * Creates NotPossibleToRemoveApartmentInspection.
     */
    public NotPossibleToRemoveApartmentInspection() {
        super("Нельзя удалить акт т.к. уже дано согласие жителя.");
    }
}
