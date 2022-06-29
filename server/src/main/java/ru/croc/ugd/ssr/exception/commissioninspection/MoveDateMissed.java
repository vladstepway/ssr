package ru.croc.ugd.ssr.exception.commissioninspection;

import ru.croc.ugd.ssr.exception.SsrException;

/**
 * Validation error when inspection move date was missed.
 */
public class MoveDateMissed extends SsrException {

    /**
     * MoveDateMissed.
     */
    public MoveDateMissed() {
        super("Дата и время переноса комиссионного осмотра должны быть указаны");
    }

}
