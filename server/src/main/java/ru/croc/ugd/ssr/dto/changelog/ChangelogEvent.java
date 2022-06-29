package ru.croc.ugd.ssr.dto.changelog;

import lombok.Data;

import java.util.List;

/**
 * DTO для записи события изменения единичного поля.
 */
@Data
public class ChangelogEvent {
    private String attrName;
    private String attrTitle;
    private String attrType;
    private Object oldValue;
    private Object newValue;
    private List<String> description; // кастомно формируемые строки (для вывода в списке)
}

