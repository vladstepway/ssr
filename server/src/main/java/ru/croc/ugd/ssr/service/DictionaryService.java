package ru.croc.ugd.ssr.service;

import static java.text.MessageFormat.format;
import static java.util.Objects.isNull;

import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.config.CacheConfig;
import ru.reinform.cdp.security.utils.RiAuthenticationUtils;
import ru.reinform.cdp.utils.rest.utils.SendRestUtils;

import java.util.List;
import java.util.Map;

/**
 * Сервис по работе со справочниками.
 */
@Service
@RequiredArgsConstructor
public class DictionaryService {

    private final SendRestUtils sendRestUtils;
    private final RiAuthenticationUtils riAuthenticationUtils;
    @Value("${mdm.url}")
    private String mdmUrl;

    private final String dictUrl = "/api/v2/dictionary/dto/{0}";

    /**
     * Получить элемент справочника по коду.
     *
     * @param dictionaryCode код справочника
     * @param elementCode код элемента
     * @return объект справочника
     */
    public JSONObject getElementByCode(String dictionaryCode, String elementCode) {
        String json = "{"
            + "  \"nickAttr\": \"code\","
            + "  \"values\": ["
            + "    \"" + elementCode + "\""
            + "  ]"
            + "}";
        try {
            String result = sendRestUtils.sendJsonRequest(
                mdmUrl + "/api/v2/dictionary/dto/" + dictionaryCode, HttpMethod.POST, json, String.class
            );
            JSONArray jsonArray = new JSONArray(result);
            if (jsonArray.length() > 0) {
                return jsonArray.getJSONObject(0);
            }
        } catch (Exception ex) {
            return null;
        }
        return null;
    }

    /**
     * Получить наименование элемента справочника по коду.
     *
     * @param dictionaryCode код справочника
     * @param elementCode код элемента
     * @return наименование
     */
    @Cacheable(value = CacheConfig.DICTIONARY_GET_DATA)
    public String getNameByCode(String dictionaryCode, String elementCode) {
        JSONObject element = getElementByCode(dictionaryCode, elementCode);
        if (!isNull(element)) {
            return element.optString("name");
        }
        return null;
    }

    /**
     * Получить элементы справочника по коду от имени сервисного пользователя.
     *
     * @param dictionaryCode код справочника
     * @return список элементов справочника
     */
    public List<Map<String, Object>> getDictionaryElementsAsServiceUser(final String dictionaryCode) {
        return riAuthenticationUtils.executeAsServiceuser(() -> getDictionaryElements(dictionaryCode));
    }

    /**
     * Получить элементы справочника по коду.
     *
     * @param dictionaryCode код справочника
     * @return список элементов справочника
     */
    public List<Map<String, Object>> getDictionaryElements(final String dictionaryCode) {
        return sendRestUtils.sendJsonRequest(
            mdmUrl + format(dictUrl, dictionaryCode), HttpMethod.GET, null, List.class
        );
    }

}
