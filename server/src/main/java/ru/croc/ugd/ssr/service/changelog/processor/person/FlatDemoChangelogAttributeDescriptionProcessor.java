package ru.croc.ugd.ssr.service.changelog.processor.person;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.service.changelog.processor.ChangelogAttributeDescriptionProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * Обработчик изменения просмотров квартир.
 */
@Component("flatDemoChangelogAttributeProcessor")
public class FlatDemoChangelogAttributeDescriptionProcessor implements ChangelogAttributeDescriptionProcessor {

    /**
     * Возвращает описание изменения по проведению просмотра.
     * Пример:
     *   Внесены данные по просмотру квартиры
     *     Дата просмотра: date
     *
     * @param patch   jsonPatch
     * @param oldJson старый json документа
     * @param type    тип атрибута
     * @return описание изменения по проведению просмотра.
     */
    @Override
    public List<String> processDescription(JSONObject patch, JSONObject oldJson, String type) {
        List<String> strings = new ArrayList<>();
        String op = patch.getString("op");
        if (!"add".equals(op)) {
            return strings;
        }

        Object nestedJsonValue = getNestedJsonValue(patch, "/value/flatDemoItem");
        if (nestedJsonValue != null) {
            JSONArray jsonArray = (JSONArray) nestedJsonValue;
            if (jsonArray.length() > 0) {
                patch = new JSONObject().put("value", jsonArray.getJSONObject(0));
            }
        }

        strings.add("Дата просмотра: " + getDemoDate(patch));
        return strings;
    }

    private String getDemoDate(JSONObject patch) {
        try {
            return getDateValue(patch.getJSONObject("value").getString("date"));
        } catch (Exception ex) {
            return null;
        }
    }
}
