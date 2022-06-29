package ru.croc.ugd.ssr.exception.personuploadlog;

import ru.croc.ugd.ssr.exception.SsrException;

/**
 * Validation error when person upload document is invalid.
 */
public class InvalidPersonUploadLog extends SsrException {

    /**
     * Creates InvalidPersonUploadLog.
     */
    public InvalidPersonUploadLog() {
        super("Запись о загружке жителей некорректна");
    }
}
