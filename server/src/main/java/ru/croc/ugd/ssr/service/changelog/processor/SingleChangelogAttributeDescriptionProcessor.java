package ru.croc.ugd.ssr.service.changelog.processor;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Обработчик изменения единичного поля.
 */
@Component("singleChangelogAttributeProcessor")
public class SingleChangelogAttributeDescriptionProcessor implements ChangelogAttributeDescriptionProcessor {

    /**
     * Возвращает описание изменения единичного поля.
     * Пример:
     *   UNOM дома
     *     Старое значение: 123
     *     Новое значение: 222
     *
     * @param patch   jsonPatch
     * @param oldJson старый json документа
     * @param type    тип атрибута
     * @return описание изменения единичного поля.
     */
    @Override
    public List<String> processDescription(JSONObject patch, JSONObject oldJson, String type) {
        List<String> strings = new ArrayList<>();
        String oldValue = getOldValue(oldJson, patch, type);
        String newValue = getNewValue(oldJson, patch, type);
        strings.add("Старое значение: " + oldValue);
        strings.add("Новое значение: " + newValue);

        return strings;
    }

    @Override
    public String getOldValue(final JSONObject oldJson, final JSONObject patch, final String type) {
        final String path = ofNullable(patch)
            .map(p -> p.getString("path"))
            .orElse(null);
        if (isNull(path) || isNull(oldJson)) {
            return "Нет данных";
        }
        Object nestedValue = getNestedJsonValue(oldJson, path);
        return nestedValue == null ? "Нет данных" : prepareDataByType(nestedValue.toString(), type);
    }

    @Override
    public String getNewValue(final JSONObject oldJson, final JSONObject patch, final String type) {
        final String op = ofNullable(patch)
            .map(p -> p.getString("op"))
            .orElse(null);
        try {
            if (op == null || op.equals("remove")) {
                return "Нет данных";
            } else if (op.equals("copy") || op.equals("move")) {
                return prepareDataByType(getNestedJsonValue(oldJson, patch.getString("from")).toString(), type);
            } else {
                return prepareDataByType(patch.get("value").toString(), type);
            }
        } catch (JSONException ex) {
            return "Нет данных";
        }
    }

    private String prepareDataByType(String data, String type) {
        if ("date".equals(type)) {
            return getDateValue(data);
        } else if ("datetime".equals(type)) {
            return getDateTimeValue(data);
        } else if ("boolean".equals(type)) {
            return data.equals("true") ? "Да" : "Нет";
        } else if ("numBoolean".equals(type)) {
            return data.equals("1") ? "Да" : "Нет";
        } else if ("gender".equals(type)) {
            switch (data) {
                case "1":
                    return "Мужской";
                case "2":
                    return "Женский";
                case "0":
                    return "Не задан";
                default:
                    return data;
            }
        } else if ("relocationStatus".equals(type)) {
            switch (data) {
                case "1":
                    return "Уведомлен о переселении";
                case "2":
                    return "Получено письмо с предложением";
                case "3":
                    return "Проведен показ квартиры";
                case "4":
                    return "Получено согласие";
                case "5":
                    return "Получен отказ";
                case "6":
                    return "Проект договора подготовлен";
                case "7":
                    return "Проект договора аннулирован";
                case "8":
                    return "Проект договора выдан";
                case "9":
                    return "Договор подписан";
                case "10":
                    return "Договор заключен";
                case "11":
                    return "Без договора. Служба судебных приставов";
                case "14":
                    return "Выданы ключи";
                case "15":
                    return "Квартира освобождена";
                default:
                    return data;
            }
        } else if ("statusLiving".equals(type)) {
            switch (data) {
                case "1":
                    return "Собственник (частная собственность)";
                case "2":
                    return "Пользователь (частная собственность)";
                case "3":
                    return "Наниматель (найм/пользование)";
                case "4":
                    return "Проживающий (найм/пользователь)";
                case "5":
                    return "Свободная";
                case "0":
                    return "Отсутствует";
                default:
                    return data;
            }
        } else {
            return data;
        }
    }
}
