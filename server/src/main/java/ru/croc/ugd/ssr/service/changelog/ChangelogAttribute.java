package ru.croc.ugd.ssr.service.changelog;

import lombok.Data;
import ru.croc.ugd.ssr.service.changelog.processor.ChangelogAttributeDescriptionProcessor;

/**
 * Класс для хранения информации атрибута для журнала изменений.
 */
@Data
public class ChangelogAttribute {

    private String path;
    private String name;
    private String title;
    private String type;
    private boolean isComplexPath;
    private ChangelogAttributeDescriptionProcessor processor;

}
