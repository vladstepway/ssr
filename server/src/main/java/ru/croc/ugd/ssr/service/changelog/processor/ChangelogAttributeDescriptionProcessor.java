package ru.croc.ugd.ssr.service.changelog.processor;

import static java.util.Objects.isNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Интерфейс для обработчиков атрибутов.
 */
public interface ChangelogAttributeDescriptionProcessor {

    /**
     * Получить описание изменения.
     *
     * @param oldJson  старый json
     * @param newJson  новый json
     * @return описание изменения
     */
    default List<String> processDescription(final JSONObject oldJson, final JSONObject newJson) {
        return Collections.emptyList();
    }

    /**
     * Получить описание изменения.
     *
     * @param patch    изменения
     * @param oldJson  старый json
     * @param type     тип данных
     * @return описание изменения
     */
    default List<String> processDescription(final JSONObject patch, final JSONObject oldJson, final String type) {
        return Collections.emptyList();
    }

    default Object getOldValue(final JSONObject oldJson, final JSONObject patch, final String type) {
        return null;
    }

    default Object getNewValue(final JSONObject oldJson, final JSONObject patch, final String type) {
        return null;
    }

    default boolean shouldSkipAttribute(final JSONObject oldJson, final JSONObject patch, final String type) {
        return false;
    }

    /**
     * Получает значение по пути из JSONObject.
     *
     * @param object  JSONObject
     * @param path    JSONPath
     * @return значение
     */
    default Object getNestedJsonValue(final JSONObject object, final String path) {
        if (isNull(object) || isNull(path)) {
            return null;
        }
        try {
            return object.query(path);
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Преобразует строку yyyy-MM-dd в dd.MM.yyyy.
     * @param value строка вида yyyy-MM-dd
     * @return строка вида dd.MM.yyyy
     */
    default String getDateValue(final Object value) {
        if (isNull(value)) {
            return null;
        }
        try {
            LocalDate date = LocalDate.parse((String) value, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        } catch (Exception ex) {
            return null;
        }
    }

    default String getValueFromArray(final JSONObject patch, final String arrayWrapperPath) {
        return getValueFromArray(patch, arrayWrapperPath, null);
    }

    default String getValueFromArray(
        final JSONObject patch, final String arrayWrapperPath, final String attributeValuePath
    ) {
        final String operation = patch.getString("op");
        if (!"add".equals(operation)) {
            return null;
        }

        final JSONArray jsonArray = (JSONArray) getNestedJsonValue(patch, arrayWrapperPath);
        if (jsonArray != null && jsonArray.length() > 0) {
            final List<String> values = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                values.add(isNull(attributeValuePath) ? ((String) jsonArray.get(i)) :
                    retrieveAttributeValue(jsonArray.getJSONObject(i), attributeValuePath));
            }
            return String.join(", ", values);
        }

        return isNull(attributeValuePath) ? retrieveAttributeValue(patch, "value") :
            retrieveAttributeValue(patch, "value", attributeValuePath);
    }

    default String retrieveAttributeValue(
        final JSONObject object, final String attributeValuePath
    ) {
        return retrieveAttributeValue(object, null, attributeValuePath);
    }

    default String retrieveAttributeValue(
        final JSONObject jsonObject, final String objectPath, final String attributeValuePath
    ) {
        try {
            if (isNull(objectPath)) {
                return jsonObject.getString(attributeValuePath);
            } else {
                final Object object = jsonObject.get(objectPath);
                if (object instanceof JSONObject) {
                    return ((JSONObject) object).getString(attributeValuePath);
                } else {
                    return retrieveAttributeValue((JSONArray) object, attributeValuePath);
                }
            }
        } catch (Exception e) {
            return null;
        }
    }

    default String retrieveAttributeValue(final JSONArray jsonArray, final String attributeValuePath) {
        if (jsonArray != null && jsonArray.length() > 0) {
            final List<String> values = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                values.add(retrieveAttributeValue(jsonArray.getJSONObject(i), attributeValuePath));
            }
            return String.join(", ", values);
        } else {
            return null;
        }
    }

    default String getDateTimeValue(final Object value) {
        if (isNull(value)) {
            return null;
        }
        try {
            LocalDateTime dateTime = LocalDateTime.parse((String) value);
            return dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
        } catch (Exception ex) {
            return null;
        }
    }

}
