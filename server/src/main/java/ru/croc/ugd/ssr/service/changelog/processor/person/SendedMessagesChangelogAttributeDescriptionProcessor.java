package ru.croc.ugd.ssr.service.changelog.processor.person;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.service.changelog.processor.ChangelogAttributeDescriptionProcessor;

import java.util.ArrayList;
import java.util.List;


/**
 * Обработчик изменения отправленных сообщений.
 */
@Component("sendedMessagesChangelogAttributeProcessor")
public class SendedMessagesChangelogAttributeDescriptionProcessor implements ChangelogAttributeDescriptionProcessor {

    /**
     * Возвращает описание изменения по добавлению договора.
     * Пример:
     *   Отправлено сообщение в ЛК
     *     ИД: messageID
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

        Object nestedJsonValue = getNestedJsonValue(patch, "/value/Message");
        if (nestedJsonValue != null) {
            JSONArray jsonArray = (JSONArray) nestedJsonValue;
            if (jsonArray.length() > 0) {
                patch = new JSONObject().put("value", jsonArray.getJSONObject(0));
            }
        }

        strings.add(getMessageText(patch));
        strings.add("ИД: " + getMessageId(patch));

        return strings;
    }

    private String getMessageId(JSONObject patch) {
        try {
            return patch.getJSONObject("value").getString("messageID");
        } catch (Exception ex) {
            return null;
        }
    }

    private String getMessageText(JSONObject patch) {
        try {
            return patch.getJSONObject("value").getString("messageText");
        } catch (Exception ex) {
            return null;
        }
    }
}
