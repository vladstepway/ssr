package ru.croc.ugd.ssr.service.utils;

import java.time.Instant;

/**
 * Класс для работы с событиями в календаре.
 */
public class IcsUtils {

    private static final String ICS_FILE_NAME_TEMPLATE = "%s_%s.ics";

    public static String generateIcsFileName(final String documentId) {
        return String.format(ICS_FILE_NAME_TEMPLATE, documentId, Instant.now().toEpochMilli());
    }
}
