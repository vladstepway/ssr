package ru.croc.ugd.ssr.exception.trade;

import ru.croc.ugd.ssr.exception.SsrException;

/**
 * BatchAlreadyProcessed.
 */
public class BatchAlreadyProcessed extends SsrException {

    /**
     * Creates BatchProcessingCrash.
     */
    public BatchAlreadyProcessed() {
        super("Файл уже обработан или обработка в процессе.");
    }
}

