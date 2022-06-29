package ru.croc.ugd.ssr.service.changelog.processor.person;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.service.changelog.processor.ChangelogAttributeDescriptionProcessor;

import java.util.ArrayList;
import java.util.List;


/**
 * Обработчик изменения двойников СНИЛС.
 */
@Component("snilsDoublesChangelogAttributeProcessor")
public class SnilsDoublesChangelogAttributeDescriptionProcessor implements ChangelogAttributeDescriptionProcessor {

    /**
     * Возвращает описание изменения по добавлению договора.
     * Пример:
     *   Двойники СНИЛС
     *     Добавлена 1 запись (N записей)
     *     Номер документа: docNum, …
     *
     * @param patch   jsonPatch
     * @param oldJson старый json документа
     * @param type    тип атрибута
     * @return описание изменения по добавлению договора.
     */
    @Override
    public List<String> processDescription(JSONObject patch, JSONObject oldJson, String type) {
        List<String> strings = new ArrayList<>();
        String op = patch.getString("op");
        if (!"add".equals(op)) {
            return strings;
        }

        Object nestedJsonValue = getNestedJsonValue(patch, "/value/SNILS_Double");
        if (nestedJsonValue != null) {
            JSONArray jsonArray = (JSONArray) nestedJsonValue;
            if (jsonArray.length() > 0) {
                patch = new JSONObject().put("value", jsonArray);
            }
        }

        strings.add("Добавлено записей: " + getAddedCount(patch));
        for (Object value : patch.getJSONArray("value")) {
            JSONObject jsonObject = (JSONObject) value;
            strings.add("Номер документа: " + getDocNum(jsonObject));
        }

        return strings;
    }

    private String getDocNum(JSONObject patch) {
        try {
            return patch.getString("docNum");
        } catch (Exception ex) {
            return null;
        }
    }

    private Integer getAddedCount(JSONObject patch) {
        try {
            return patch.getJSONArray("value").length();
        } catch (Exception ex) {
            return null;
        }
    }
}
