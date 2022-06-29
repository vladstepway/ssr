package ru.croc.ugd.ssr.service.excel;

import ru.croc.ugd.ssr.service.excel.model.process.ProcessParameters;
import ru.croc.ugd.ssr.service.excel.model.process.ProcessResult;

import java.io.FileOutputStream;

/**
 * General processor.
 *
 * @param <T> generic
 */
public interface ExcelProcessor<T> {
    /**
     * Process provided input.
     *
     * @param input generic input
     * @param parameters parameters
     * @return ProcessResult
     */
    ProcessResult process(final T input, final ProcessParameters parameters);

    /**
     * Process provided input.
     *
     * @param input generic input
     * @param logFos FileOutputStream for logs
     * @return ProcessResult
     */
    ProcessResult process(final T input, final FileOutputStream logFos);
}
