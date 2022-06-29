package ru.croc.ugd.ssr.service.changelog.processor.cip;

import static java.util.Optional.ofNullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.service.changelog.processor.ChangelogAttributeDescriptionProcessor;

/**
 * Обработчик изменений центров информирования о переселении.
 */
@Component("cipChangelogAttributeProcessor")
public class CipChangelogAttributeProcessor implements ChangelogAttributeDescriptionProcessor {

    private static final String EMPLOYEE_ATTRIBUTE_TYPE = "employee";
    private static final String HOUSE_ATTRIBUTE_TYPE = "house";

    @Override
    public String getOldValue(final JSONObject oldJson, final JSONObject patch, final String type) {
        final String path = ofNullable(patch)
            .map(p -> p.getString("path"))
            .orElse(null);
        return getOldValue(oldJson, path, type);
    }

    private String getOldValue(final JSONObject oldJson, final String path, final String type) {
        final Object jsonValue = getNestedJsonValue(oldJson, path);
        switch (type) {
            case EMPLOYEE_ATTRIBUTE_TYPE:
                if (jsonValue instanceof JSONObject) {
                    return retrieveAttributeValue((JSONObject) getNestedJsonValue(oldJson, path), "login");
                } else if (jsonValue instanceof JSONArray) {
                    return retrieveAttributeValue((JSONArray) getNestedJsonValue(oldJson, path), "login");
                }
                break;
            case HOUSE_ATTRIBUTE_TYPE:
                if (jsonValue instanceof JSONObject) {
                    return retrieveAttributeValue((JSONObject) getNestedJsonValue(oldJson, path), "id");
                } else if (jsonValue instanceof JSONArray) {
                    return retrieveAttributeValue((JSONArray) getNestedJsonValue(oldJson, path), "id");
                }
                break;
            default:
                break;
        }
        return (String) getNestedJsonValue(oldJson, path);
    }

    @Override
    public String getNewValue(final JSONObject oldJson, final JSONObject patch, final String type) {
        final String operation = patch.getString("op");
        try {
            if (operation.equals("remove")) {
                return null;
            } else {
                if ("replace".equals(operation)) {
                    return patch.getString("value");
                } else {
                    return getNewValue(patch, type);
                }
            }
        } catch (JSONException ex) {
            return null;
        }
    }

    private String getNewValue(final JSONObject patch, final String type) {
        switch (type) {
            case EMPLOYEE_ATTRIBUTE_TYPE:
                return getValueFromArray(patch, "/value/Employee", "login");
            case HOUSE_ATTRIBUTE_TYPE:
                return getValueFromArray(patch, "/value/Link", "id");
            default:
                return patch.getString("value");
        }
    }

}
