package ru.croc.ugd.ssr.service.excel.person;

import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class PersonLetterAndContractLogUtils {

    private static final String LOG_RECORD_TEMPLATE = "%s - %s - %s \n";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    public static void writeInfoLog(final FileOutputStream fos, final String fileMessage, final String logMessage) {
        log.info(logMessage);
        writeLog(fos, fileMessage, "INFO");
    }

    public static void writeWarnLog(final FileOutputStream fos, final String fileMessage, final String logMessage) {
        log.warn(logMessage);
        writeLog(fos, fileMessage, "WARN");
    }

    public static void writeErrorLog(
        final FileOutputStream fos, final String fileMessage, final String logMessage, final Exception exception
    ) {
        log.error(logMessage + exception.getMessage(), exception);
        writeLog(fos, fileMessage, "ERROR");
    }

    private static void writeLog(final FileOutputStream fos, final String message, final String status) {
        final String logRecord = createLogRecord(message, status);
        try {
            fos.write(logRecord.getBytes());
        } catch (Exception e) {
            log.warn("PersonLetterAndContract. Unable to write log: {}", e.getMessage(), e);
        }
    }

    private static String createLogRecord(final String message, final String level) {
        return String.format(
            LOG_RECORD_TEMPLATE,
            DATE_FORMATTER.format(LocalDateTime.now()),
            level,
            message
        );
    }
}
