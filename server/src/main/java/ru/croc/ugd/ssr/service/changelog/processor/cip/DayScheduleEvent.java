package ru.croc.ugd.ssr.service.changelog.processor.cip;

import lombok.Builder;
import lombok.Data;
import ru.croc.ugd.ssr.enums.DayScheduleEventType;

import java.util.List;

/**
 * Класс событий по расписаниям.
 */
@Data
@Builder
public class DayScheduleEvent {

    private DayScheduleEventType type;
    private String name;
    private List<String> messageTemplateList;
}
