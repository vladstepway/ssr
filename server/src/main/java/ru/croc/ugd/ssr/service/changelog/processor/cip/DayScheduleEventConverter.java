package ru.croc.ugd.ssr.service.changelog.processor.cip;

import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.enums.DayScheduleEventType;
import ru.croc.ugd.ssr.service.DictionaryConverter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Класс-конвертер для значений из справочника ugd_ssr_dayScheduleEvent.
 */
@Service
public class DayScheduleEventConverter implements DictionaryConverter<DayScheduleEvent> {

    /**
     * Конвертирует значения, полученные из справочника, в DayScheduleEvent.
     *
     * @param elements элементы справочника
     * @return множество DayScheduleEvent
     */
    @Override
    public Set<DayScheduleEvent> convertElements(final List<Map<String, Object>> elements) {
        return elements
            .stream()
            .map(this::convertElement)
            .collect(Collectors.toSet());
    }

    private DayScheduleEvent convertElement(final Map<String, Object> element) {
        return DayScheduleEvent.builder()
            .name((String) element.get("name"))
            .type(DayScheduleEventType.fromCode((String) element.get("code")))
            .messageTemplateList(Arrays.asList(((String) element.get("text")).split("\n")))
            .build();
    }
}
