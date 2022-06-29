package ru.croc.ugd.ssr.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Интерфейс конвертер для значений из справочников.
 */
public interface DictionaryConverter<T> {

    /**
     * Конвертирует значения, полученные из справочника, в тип T.
     *
     * @param elements элементы справочника
     * @return множество T
     */
    Set<T> convertElements(final List<Map<String, Object>> elements);
}
