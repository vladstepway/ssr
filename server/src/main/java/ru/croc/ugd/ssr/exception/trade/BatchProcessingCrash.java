package ru.croc.ugd.ssr.exception.trade;

import ru.croc.ugd.ssr.exception.SsrException;

/**
 * BatchProcessingCrash.
 */
public class BatchProcessingCrash extends SsrException {

    /**
     * Creates BatchProcessingCrash.
     */
    public BatchProcessingCrash() {
        super("Не удалось начать обработку файла.");
    }
}

