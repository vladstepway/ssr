package ru.croc.ugd.ssr.helper;

import org.springframework.data.domain.Sort;

/**
 * Класс, содержащий вспомогательные методы для сортировки.
 */
public class SortHelper {

    /**
     * По строке возвращает объект Sort.
     *
     * @param orderBy стркоа формата "unom asc", "address desc" и т.п.
     * @return Sort
     */
    public static Sort getSortByOrderString(String orderBy) {
        if (orderBy == null || orderBy.isEmpty()) {
            return null;
        }
        String[] strings = orderBy.split(" ");
        if (strings.length < 1) {
            return null;
        }

        String fieldName = strings[0];
        Sort.Direction direction;
        String directionString = strings.length > 1 ? strings[1] : "asc";

        if (directionString.equals("desc")) {
            direction = Sort.Direction.DESC;
        } else {
            direction = Sort.Direction.ASC;
        }

        return Sort.by(direction, fieldName);
    }

}
