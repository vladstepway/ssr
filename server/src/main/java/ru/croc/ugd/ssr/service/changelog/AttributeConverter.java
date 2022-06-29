package ru.croc.ugd.ssr.service.changelog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.service.changelog.processor.ChangelogAttributeDescriptionProcessor;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Класс-конвертер для атрибутов из справочника mdm.
 */
@Service
public class AttributeConverter {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Конвертирует мапу значений, полученных из справочника, в наш ChangelogAttribute.
     *
     * @param elements элементы справочника
     * @return мапа (path - атрибут)
     */
    public Set<ChangelogAttribute> convertNewDictElements(List<Map<String, Object>> elements) {
        return elements
            .stream()
            .map(this::convertElement)
            .collect(Collectors.toSet());
    }

    private ChangelogAttribute convertElement(Map<String, Object> element) {
        ChangelogAttributeDescriptionProcessor bean
            = (ChangelogAttributeDescriptionProcessor) applicationContext.getBean((String) element.get("beanName"));
        ChangelogAttribute changelogAttribute = new ChangelogAttribute();
        changelogAttribute.setPath((String) element.get("code"));
        changelogAttribute.setName((String) element.get("name"));
        changelogAttribute.setProcessor(bean);
        if (element.containsKey("title")) {
            changelogAttribute.setTitle((String) element.get("title"));
        }
        if (element.containsKey("type")) {
            changelogAttribute.setType((String) element.get("type"));
        }
        if (element.containsKey("isComplexPath") && element.get("isComplexPath") != null) {
            changelogAttribute.setComplexPath((Boolean) element.get("isComplexPath"));
        }

        return changelogAttribute;
    }

}
