package ru.croc.ugd.ssr.service.changelog.processor.apartmentinspection;

import static java.util.Objects.isNull;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.service.changelog.processor.ChangelogAttributeDescriptionProcessor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Обработчик изменений причин переноса в актах по дефектам.
 */
@Component("apartmentDelayReasonChangelogAttributeProcessor")
public class ApartmentDelayReasonChangelogAttributeProcessor implements ChangelogAttributeDescriptionProcessor {

    @Override
    public String getNewValue(final JSONObject oldJson, final JSONObject patch, final String type) {
        final List<String> strings = new ArrayList<>();

        JSONObject jsonWithValue = patch;
        boolean isNewDelay = false;
        final Object nestedJsonValue = getNestedJsonValue(jsonWithValue, "/value");
        if (nestedJsonValue instanceof JSONArray) {
            isNewDelay = true;
            final JSONArray jsonArray = (JSONArray) nestedJsonValue;
            if (jsonArray.length() > 0) {
                jsonWithValue = new JSONObject().put("value", jsonArray.getJSONObject(0));
            }
        }

        strings.add("Плановая дата: " + getDelayDate(jsonWithValue));
        if (!isNewDelay) {
            strings.add("Текст причины переноса: " + getDelayReasonText(jsonWithValue));
        }

        return String.join("; ", strings);
    }

    private String getDelayDate(final JSONObject patch) {
        try {
            return getDateValueFromDateTime(patch.getJSONObject("value").getString("delayDate"));
        } catch (Exception ex) {
            return "-";
        }
    }

    private String getDateValueFromDateTime(final Object value) {
        if (isNull(value)) {
            return null;
        }
        try {
            LocalDateTime dateTime = LocalDateTime.parse((String) value);
            return dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        } catch (Exception ex) {
            return null;
        }
    }

    private String getDelayReasonText(final JSONObject patch) {
        try {
            return patch.getJSONObject("value").getString("delayReasonText");
        } catch (Exception ex) {
            return "-";
        }
    }
}
