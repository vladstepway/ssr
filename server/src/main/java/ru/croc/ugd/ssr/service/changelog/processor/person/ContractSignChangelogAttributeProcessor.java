package ru.croc.ugd.ssr.service.changelog.processor.person;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.service.changelog.processor.ChangelogAttributeDescriptionProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * Обработчик подписания договоров.
 */
@Component("contractSignChangelogAttributeProcessor")
public class ContractSignChangelogAttributeProcessor implements ChangelogAttributeDescriptionProcessor {

    /**
     * Возвращает описание изменения по подписанию договора.
     * Пример:
     *   Подписан договор
     *     ИД: orderId
     *     Дата подписания: contractSignDate
     *
     * @param patch   jsonPatch
     * @param oldJson старый json документа
     * @param type    тип атрибута
     * @return описание изменения по добавлению договора.
     */
    @Override
    public List<String> processDescription(final JSONObject patch, final JSONObject oldJson, final String type) {
        final List<String> strings = new ArrayList<>();
        final String op = patch.getString("op");
        if (!"add".equals(op)) {
            return strings;
        }

        final String contractPath = patch.getString("path")
            .substring(0, patch.getString("path").lastIndexOf("/contractSignDate"));
        final JSONObject nestedJsonValue = (JSONObject) getNestedJsonValue(oldJson, contractPath);

        strings.add("ИД: " + getOrderId(nestedJsonValue));
        strings.add("Дата подписания: " + getContractSignDate(patch));
        return strings;
    }

    private String getOrderId(final JSONObject patch) {
        try {
            return patch.getString("orderId");
        } catch (Exception ex) {
            return null;
        }
    }

    private String getContractSignDate(final JSONObject patch) {
        try {
            return getDateValue(patch.getString("value"));
        } catch (Exception ex) {
            return null;
        }
    }
}
