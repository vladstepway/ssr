package ru.croc.ugd.ssr.db.specification;

/**
 * Операция для фомрирования предикатов логов.
 */
public enum SearchOperation {

    /**
     * LocalDateTime >=.
     */
    DATE_TIME_GREATER_THAN_EQUAL,

    /**
     * LocalDateTime <=.
     */
    DATE_TIME_LESS_THAN_EQUAL,

    /**
     * Равенство (equals).
     */
    EQUAL,

    /**
     * Находится ли подстрока в jsonb.
     */
    JSONPATCH_HAS,

    /**
     * Находится ли одна из подстрок в jsonb.
     */
    JSONPATCH_HAS_ARRAY,

    /**
     * Предикат IN_STRINGS для строк.
     */
    IN_STRINGS
}
