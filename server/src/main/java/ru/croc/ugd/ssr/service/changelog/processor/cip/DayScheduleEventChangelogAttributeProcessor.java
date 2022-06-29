package ru.croc.ugd.ssr.service.changelog.processor.cip;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static ru.croc.ugd.ssr.utils.LocaleUtils.ru_RU;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.enums.DayScheduleEventType;
import ru.croc.ugd.ssr.exception.SsrException;
import ru.croc.ugd.ssr.service.DictionaryService;
import ru.croc.ugd.ssr.service.changelog.processor.ChangelogAttributeDescriptionProcessor;

import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;

/**
 * Обработчик изменения событий по рабочим дням.
 */
@Component("dayScheduleEventChangelogAttributeProcessor")
public class DayScheduleEventChangelogAttributeProcessor
    implements ChangelogAttributeDescriptionProcessor {

    private static final String CONTRACT_APPOINTMENT_DAY_SCHEDULE_EVENT_ATTRIBUTE_TYPE =
        "contractAppointmentDayScheduleEvent";
    private static final String FLAT_APPOINTMENT_DAY_SCHEDULE_EVENT_ATTRIBUTE_TYPE = "flatAppointmentDayScheduleEvent";

    private static final String CONTRACT_APPOINTMENT_DAY_SCHEDULE_NAME = "Расписание на заключение договоров. ";
    private static final String FLAT_APPOINTMENT_DAY_SCHEDULE_NAME = "Расписание осмотров квартир. ";

    private final DictionaryService dictionaryService;
    private final DayScheduleEventConverter dayScheduleEventConverter;

    private Set<DayScheduleEvent> dayScheduleEvents;

    public DayScheduleEventChangelogAttributeProcessor(
        DictionaryService dictionaryService, DayScheduleEventConverter dayScheduleEventConverter
    ) {
        this.dictionaryService = dictionaryService;
        this.dayScheduleEventConverter = dayScheduleEventConverter;
    }

    /**
     * Заполнение списка данными из справочника.
     */
    @PostConstruct
    public void afterInit() {
        dayScheduleEvents = dayScheduleEventConverter.convertElements(
            dictionaryService.getDictionaryElementsAsServiceUser("ugd_ssr_dayScheduleEvent")
        );
    }

    /**
     * Возвращает описание изменения по добавлению события по рабочим дням.
     *
     * @param patch   jsonPatch
     * @param oldJson старый json документа
     * @param type    тип атрибута
     * @return описание изменения по добавлению события по рабочим дням.
     */
    @Override
    public List<String> processDescription(JSONObject patch, JSONObject oldJson, String type) {
        final List<String> descriptionMessages = new ArrayList<>();
        final String op = patch.getString("op");
        if (!"add".equals(op) && !"copy".equals(op)) {
            return descriptionMessages;
        }

        final Object nestedJsonValue = getNestedJsonValue(patch, "/value/DayScheduleEvent");
        if (nestedJsonValue != null) {
            final JSONArray jsonArray = (JSONArray) nestedJsonValue;
            if (jsonArray.length() > 0) {
                patch = new JSONObject().put("value", jsonArray.getJSONObject(0));
            }
        } else if ("copy".equals(op)) {
            patch = new JSONObject().put("value", getNestedJsonValue(oldJson, patch.getString("from")));
        }

        final String code = getValue(patch, "code");
        if (nonNull(code)) {
            final DayScheduleEventType eventType = DayScheduleEventType.fromCode(code);
            final DayScheduleEvent dayScheduleEvent = dayScheduleEvents.stream()
                .filter(event -> event.getType().equals(eventType))
                .findFirst()
                .orElseThrow(SsrException::new);
            descriptionMessages.add("Наименование: " + getTypeName(type) + dayScheduleEvent.getName() + ".");
            descriptionMessages.addAll(getDescriptionMessages(patch, dayScheduleEvent));
        }

        return descriptionMessages;
    }

    private String getTypeName(final String type) {
        switch (type) {
            case CONTRACT_APPOINTMENT_DAY_SCHEDULE_EVENT_ATTRIBUTE_TYPE:
                return CONTRACT_APPOINTMENT_DAY_SCHEDULE_NAME;
            case FLAT_APPOINTMENT_DAY_SCHEDULE_EVENT_ATTRIBUTE_TYPE:
                return FLAT_APPOINTMENT_DAY_SCHEDULE_NAME;
            default:
                return "";
        }
    }

    private String getValue(JSONObject patch, String key) {
        try {
            return patch.getJSONObject("value").getString(key);
        } catch (Exception ex) {
            return null;
        }
    }

    private Integer getIntValue(JSONObject patch, String key) {
        try {
            return patch.getJSONObject("value").getInt(key);
        } catch (Exception ex) {
            return null;
        }
    }

    private List<String> getDescriptionMessages(JSONObject patch, DayScheduleEvent dayScheduleEvent) {
        final List<String> descriptionMessages = new ArrayList<>();
        try {
            switch (dayScheduleEvent.getType()) {
                case CREATE_DAY:
                case DELETE_DAY:
                case UPDATE_DAY:
                    if (dayScheduleEvent.getMessageTemplateList().size() > 0) {
                        descriptionMessages.add(
                            String.format(
                                dayScheduleEvent.getMessageTemplateList().get(0),
                                getDateValue(getValue(patch, "date"))
                            )
                        );
                    }
                    break;
                case COPY_PERIOD:
                case DELETE_PERIOD:
                    if (dayScheduleEvent.getMessageTemplateList().size() == 4) {
                        ofNullable(getDateMessage(patch, dayScheduleEvent))
                            .map(descriptionMessages::add);
                        ofNullable(getDayRepeatNumberMessage(patch, dayScheduleEvent))
                            .map(descriptionMessages::add);
                        ofNullable(getWeekRepeatNumberMessage(patch, dayScheduleEvent))
                            .map(descriptionMessages::add);
                        ofNullable(getWeekdaysMessage(patch, dayScheduleEvent))
                            .map(descriptionMessages::add);
                    }
                    break;
                default:
                    return Collections.emptyList();
            }
        } catch (Exception ex) {
            return Collections.emptyList();
        }
        return descriptionMessages;
    }

    private String getDateMessage(JSONObject patch, DayScheduleEvent dayScheduleEvent) {
        switch (dayScheduleEvent.getType()) {
            case COPY_PERIOD:
                return getCopyDateMessage(patch, dayScheduleEvent);
            case DELETE_PERIOD:
                return getDeleteDateMessage(patch, dayScheduleEvent);
            default:
                return null;
        }
    }

    private String getCopyDateMessage(JSONObject patch, DayScheduleEvent dayScheduleEvent) {
        final String date = getDateValue(getValue(patch, "date"));
        final String dateFrom = getDateValue(getValue(patch, "dateFrom"));
        final String dateTo = getDateValue(getValue(patch, "dateTo"));
        if (isNotEmpty(date)
            && isNotEmpty(dateFrom)
            && isNotEmpty(dateTo)
            && isNotEmpty(dayScheduleEvent.getMessageTemplateList().get(0))
        ) {
            return String.format(dayScheduleEvent.getMessageTemplateList().get(0), date, dateFrom, dateTo);
        }
        return null;
    }

    private String getDeleteDateMessage(JSONObject patch, DayScheduleEvent dayScheduleEvent) {
        final String dateFrom = getDateValue(getValue(patch, "dateFrom"));
        final String dateTo = getDateValue(getValue(patch, "dateTo"));
        if (isNotEmpty(dateFrom)
            && isNotEmpty(dateTo)
            && isNotEmpty(dayScheduleEvent.getMessageTemplateList().get(0))
        ) {
            return String.format(dayScheduleEvent.getMessageTemplateList().get(0), dateFrom, dateTo);
        }
        return null;
    }

    private String getDayRepeatNumberMessage(JSONObject patch, DayScheduleEvent dayScheduleEvent) {
        final Integer dayRepeatNumber = getIntValue(patch, "dayRepeatNumber");
        if (nonNull(dayRepeatNumber) && isNotEmpty(dayScheduleEvent.getMessageTemplateList().get(1))) {
            return String.format(dayScheduleEvent.getMessageTemplateList().get(1), dayRepeatNumber);
        }
        return null;
    }

    private String getWeekRepeatNumberMessage(JSONObject patch, DayScheduleEvent dayScheduleEvent) {
        final Integer weekRepeatNumber = getIntValue(patch, "weekRepeatNumber");
        if (nonNull(weekRepeatNumber) && isNotEmpty(dayScheduleEvent.getMessageTemplateList().get(2))) {
            return String.format(dayScheduleEvent.getMessageTemplateList().get(2), weekRepeatNumber);
        }
        return null;
    }

    private String getWeekdaysMessage(JSONObject patch, DayScheduleEvent dayScheduleEvent) {
        try {
            final List<String> weekdays = convertToList(
                patch.getJSONObject("value").getJSONObject("weekdays").getJSONArray("weekday")
            );
            if (!weekdays.isEmpty() && isNotEmpty(dayScheduleEvent.getMessageTemplateList().get(3))) {
                return String.format(dayScheduleEvent.getMessageTemplateList().get(3), String.join(", ", weekdays));
            }
        } catch (Exception ex) {
            return null;
        }
        return null;
    }

    private List<String> convertToList(final JSONArray jsonArray) {
        final List<String> list = new ArrayList<>();
        if (nonNull(jsonArray)) {
            for (int i = 0; i < jsonArray.length(); i++) {
                final DayOfWeek dayOfWeek = DayOfWeek.valueOf(jsonArray.getString(i));
                list.add(retrieveWeekdayName(dayOfWeek));
            }
        }
        return list;
    }

    private String retrieveWeekdayName(DayOfWeek dayOfWeek) {
        return DateTimeFormatter.ofPattern("EEEE")
            .withLocale(ru_RU)
            .format(dayOfWeek);
    }

}
