package ru.croc.ugd.ssr.service.changelog.processor.person;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.croc.ugd.ssr.service.changelog.processor.ChangelogAttributeDescriptionProcessor;

import java.util.ArrayList;
import java.util.List;


/**
 * Обработчик изменения согласий/отказов/выдачи недостающих документов.
 */
@Component("agreementChangelogAttributeProcessor")
public class AgreementChangelogAttributeDescriptionProcessor implements ChangelogAttributeDescriptionProcessor {

    /**
     * Возвращает описание изменения по добавлению договора.
     * Пример:
     *     Добавлено решение жителя event от date
     *     Значение event - 1-Согласие, 2-Отказ, 3-Недостающие документы, 4-Закрепление площади,
     *                      5-Обращение в Фонд реновации, 6-Принят полный комплект документов от жителя
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

        Object nestedJsonValue = getNestedJsonValue(patch, "/value/agreement");
        if (nestedJsonValue != null) {
            JSONArray jsonArray = (JSONArray) nestedJsonValue;
            if (jsonArray.length() > 0) {
                patch = new JSONObject().put("value", jsonArray.getJSONObject(0));
            }
        }

        String date = getDate(patch);
        strings.add(
            "Добавлено решение жителя "
                + getEvent(patch)
                + (StringUtils.hasText(date) ? " от " + date : "")
        );

        return strings;
    }

    private String getEvent(JSONObject patch) {
        try {
            switch (patch.getJSONObject("value").getString("event")) {
                case "1": return "Согласие";
                case "2": return "Отказ";
                case "3": return "Недостающие документы";
                case "4": return "Закрепление площади";
                case "5": return "Обращение в Фонд реновации";
                case "6": return "Принят полный комплект документов от жителя";
                default: return "Нет данных";
            }
        } catch (Exception ex) {
            return null;
        }
    }

    private String getDate(JSONObject patch) {
        try {
            return getDateValue(patch.getJSONObject("value").getString("date"));
        } catch (Exception ex) {
            return null;
        }
    }
}
