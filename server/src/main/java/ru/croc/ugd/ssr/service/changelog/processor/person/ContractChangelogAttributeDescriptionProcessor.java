package ru.croc.ugd.ssr.service.changelog.processor.person;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.service.changelog.processor.ChangelogAttributeDescriptionProcessor;

import java.util.ArrayList;
import java.util.List;


/**
 * Обработчик изменения договоров.
 */
@Component("contractChangelogAttributeProcessor")
public class ContractChangelogAttributeDescriptionProcessor implements ChangelogAttributeDescriptionProcessor {

    /**
     * Возвращает описание изменения по добавлению договора.
     * Пример:
     *   Добавлен договор
     *     ИД: order_id
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

        Object nestedJsonValue = getNestedJsonValue(patch, "/value/contract");
        if (nestedJsonValue != null) {
            JSONArray jsonArray = (JSONArray) nestedJsonValue;
            if (jsonArray.length() > 0) {
                patch = new JSONObject().put("value", jsonArray.getJSONObject(0));
            }
        }

        final String orderId = getOrderId(patch);
        if (StringUtils.isNotEmpty(orderId)) {
            strings.add("Добавлен договор");
            strings.add("ИД: " + getOrderId(patch));
        }
        return strings;
    }

    private String getOrderId(JSONObject patch) {
        try {
            return patch.getJSONObject("value").getString("orderId");
        } catch (Exception ex) {
            return null;
        }
    }
}
