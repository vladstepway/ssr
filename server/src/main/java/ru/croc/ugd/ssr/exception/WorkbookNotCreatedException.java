package ru.croc.ugd.ssr.exception;

/**
 * Validation error when an error occurred while creating the workbook.
 */
public class WorkbookNotCreatedException extends SsrException {

    /**
     * Creates WorkbookNotCreated.
     */
    public WorkbookNotCreatedException() {
        super("Ошибка при формировании excel файла");
    }

}
