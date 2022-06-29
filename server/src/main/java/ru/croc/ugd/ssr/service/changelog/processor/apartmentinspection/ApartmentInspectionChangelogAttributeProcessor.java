package ru.croc.ugd.ssr.service.changelog.processor.apartmentinspection;

import static java.util.Optional.ofNullable;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.service.changelog.processor.ChangelogAttributeDescriptionProcessor;

/**
 * Обработчик изменений актов по дефектам.
 */
@Component("apartmentInspectionChangelogAttributeProcessor")
public class ApartmentInspectionChangelogAttributeProcessor implements ChangelogAttributeDescriptionProcessor {
    private static final String BOOLEAN_TYPE = "boolean";

    @Override
    public Object getOldValue(final JSONObject oldJson, final JSONObject patch, final String type) {
        final String path = ofNullable(patch)
            .map(p -> p.getString("path"))
            .orElse(null);
        final Object value = getNestedJsonValue(oldJson, path);

        return value;
    }

    @Override
    public Object getNewValue(final JSONObject oldJson, final JSONObject patch, final String type) {
        final String operation = patch.getString("op");
        try {
            if (operation.equals("copy")) {
                return getNestedJsonValue(oldJson, patch.getString("from"));
            }
            if (operation.equals("remove")) {
                return null;
            } else {
                return getNewValue(patch, type);
            }
        } catch (JSONException ex) {
            return null;
        }
    }

    private Object getNewValue(final JSONObject patch, final String type) {
        switch (type) {
            case BOOLEAN_TYPE:
                return patch.getBoolean("value");
            default:
                return patch.getString("value");
        }
    }

    public String retrieveAttributeValue(final JSONObject object, final String attributeValuePath) {
        if (object.has("ApartmentDefectData")) {
            return retrieveAttributeValue(object, "ApartmentDefectData", attributeValuePath);
        }
        return retrieveAttributeValue(object, null, attributeValuePath);
    }
}
