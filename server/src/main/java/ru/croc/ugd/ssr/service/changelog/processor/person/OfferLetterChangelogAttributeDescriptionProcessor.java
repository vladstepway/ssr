package ru.croc.ugd.ssr.service.changelog.processor.person;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.service.changelog.processor.ChangelogAttributeDescriptionProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * Обработчик изменения писем с предложениями.
 */
@Component("offerLetterChangelogAttributeProcessor")
public class OfferLetterChangelogAttributeDescriptionProcessor implements ChangelogAttributeDescriptionProcessor {

    /**
     * Возвращает описание изменения по добавлению письма с предложением.
     * Пример:
     *   Добавлено письмо с предложением
     *     ИД: letter_id
     *
     * @param patch   jsonPatch
     * @param oldJson старый json документа
     * @param type    тип атрибута
     * @return описание изменения по добавлению письма с предложением.
     */
    @Override
    public List<String> processDescription(JSONObject patch, JSONObject oldJson, String type) {
        List<String> strings = new ArrayList<>();
        String op = patch.getString("op");
        if (!"add".equals(op)) {
            return strings;
        }

        // обрабатываем добавление массива целиком
        // пример jsonPath: [{"op": "add", "path": "/Person/PersonData/offerLetters/offerLetter",
        //     "value": [{"letterId": "456-963"}]}]
        Object nestedJsonValue = getNestedJsonValue(patch, "/value/0");
        if (nestedJsonValue != null) {
            JSONObject jsonObject = (JSONObject) nestedJsonValue;
            patch = new JSONObject().put("value", jsonObject);
        }

        nestedJsonValue = getNestedJsonValue(patch, "/value/offerLetter");
        if (nestedJsonValue != null) {
            JSONArray jsonArray = (JSONArray) nestedJsonValue;
            if (jsonArray.length() > 0) {
                patch = new JSONObject().put("value", jsonArray.getJSONObject(0));
            }
        }

        final String letterId = getLetterId(patch);
        if (StringUtils.isNotEmpty(letterId)) {
            strings.add("Добавлено письмо с предложением");
            strings.add("ИД: " + letterId);
        }
        return strings;
    }

    private String getLetterId(JSONObject patch) {
        try {
            if (patch.getJSONObject("value").has("letterId")) {
                return patch.getJSONObject("value").getString("letterId");
            } else {
                return "Нет данных";
            }
        } catch (Exception ex) {
            return null;
        }

    }
}
