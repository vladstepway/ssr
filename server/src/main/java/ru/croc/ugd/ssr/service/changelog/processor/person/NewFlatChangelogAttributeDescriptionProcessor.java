package ru.croc.ugd.ssr.service.changelog.processor.person;

import static java.util.Optional.ofNullable;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.service.changelog.processor.ChangelogAttributeDescriptionProcessor;

import java.util.ArrayList;
import java.util.List;


/**
 * Обработчик изменения квартир для заселения.
 */
@Component("newFlatChangelogAttributeProcessor")
public class NewFlatChangelogAttributeDescriptionProcessor implements ChangelogAttributeDescriptionProcessor {

    /**
     * Возвращает описание изменения по добавлению заселяемой квартиры.
     * Пример:
     *   Добавлены данные заселяемой квартиры
     *     УНОМ дома: OKSUNOM
     *     Номер квартиры: OKSFlatNum
     *
     * @param patch   jsonPatch
     * @param oldJson старый json документа
     * @param type    тип атрибута
     * @return описание изменения по добавлению заселяемой квартиры.
     */
    @Override
    public List<String> processDescription(JSONObject patch, JSONObject oldJson, String type) {
        List<String> strings = new ArrayList<>();
        String op = patch.getString("op");
        if (!"add".equals(op)) {
            return strings;
        }

        // обрабатываем добавление массива целиком
        Object nestedJsonValue = getNestedJsonValue(patch, "/value/0");
        if (nestedJsonValue != null) {
            JSONObject jsonObject = (JSONObject) nestedJsonValue;
            patch = new JSONObject().put("value", jsonObject);
        }

        nestedJsonValue = getNestedJsonValue(patch, "/value/newFlat");
        if (nestedJsonValue != null) {
            JSONArray jsonArray = (JSONArray) nestedJsonValue;
            if (jsonArray.length() > 0) {
                patch = new JSONObject().put("value", jsonArray.getJSONObject(0));
            }
        }

        ofNullable(getUnom(patch))
            .ifPresent(unom -> strings.add("УНОМ дома: " + unom));
        ofNullable(getFlatNum(patch))
            .ifPresent(flatNum -> strings.add("Номер квартиры: " + flatNum));
        ofNullable(getAddress(patch))
            .ifPresent(address -> strings.add("Адрес: " + address));

        return strings;
    }

    private Integer getUnom(JSONObject patch) {
        try {
            return patch.getJSONObject("value").getInt("ccoUnom");
        } catch (Exception ex) {
            return null;
        }
    }

    private String getFlatNum(JSONObject patch) {
        try {
            return patch.getJSONObject("value").getString("ccoFlatNum");
        } catch (Exception ex) {
            return null;
        }
    }

    private String getAddress(JSONObject patch) {
        try {
            return patch.getJSONObject("value").getString("ccoAddress");
        } catch (Exception ex) {
            return null;
        }
    }
}
