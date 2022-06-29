package ru.croc.ugd.ssr.service.changelog.processor.person;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.service.changelog.processor.ChangelogAttributeDescriptionProcessor;

import java.util.ArrayList;
import java.util.List;


/**
 * Обработчик изменения выдачи ключей.
 */
@Component("keysIssueChangelogAttributeProcessor")
public class KeysIssueChangelogAttributeDescriptionProcessor implements ChangelogAttributeDescriptionProcessor {

    /**
     * Возвращает описание изменения по добавлению договора.
     * Пример:
     *   Квартира освобождена
     *     Дата освобождения: actDate
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

        Object nestedJsonValue = getNestedJsonValue(patch, "/value/keysIssue");
        if (nestedJsonValue != null) {
            JSONObject jsonObject = (JSONObject) nestedJsonValue;
            if (jsonObject.length() > 0) {
                patch = new JSONObject().put("value", jsonObject);
            }
        }

        strings.add("Дата выдачи: " + getActDate(patch));
        return strings;
    }

    private String getActDate(JSONObject patch) {
        try {
            return getDateValue(patch.getJSONObject("value").getString("actDate"));
        } catch (Exception ex) {
            return null;
        }
    }
}
