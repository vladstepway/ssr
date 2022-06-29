package ru.croc.ugd.ssr.service.changelog.processor.realestate;

import static java.util.Optional.ofNullable;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.enums.ResettlementStatus;
import ru.croc.ugd.ssr.service.changelog.processor.ChangelogAttributeDescriptionProcessor;

import java.util.Objects;

/**
 * Обработчик изменений изменений расселяемых домов.
 */
@Component("realEstateChangelogAttributeProcessor")
public class RealEstateChangelogAttributeProcessor implements ChangelogAttributeDescriptionProcessor {

    private static final String STATUS_ATTRIBUTE_TYPE = "status";
    private static final String BOOLEAN_TYPE = "boolean";

    @Override
    public Object getOldValue(final JSONObject oldJson, final JSONObject patch, final String type) {
        final String path = ofNullable(patch)
            .map(p -> p.getString("path"))
            .orElse(null);
        final Object attributeValue = getNestedJsonValue(oldJson, path);
        return attributeValue != null && STATUS_ATTRIBUTE_TYPE.equals(type)
            ? getResettlementStatus((String) attributeValue)
            : attributeValue;
    }

    @Override
    public Object getNewValue(final JSONObject oldJson, final JSONObject patch, final String type) {
        final String operation = patch.getString("op");
        try {
            if (operation.equals("copy")) {
                return getNestedJsonValue(oldJson, patch.getString("from"));
            }
            if (operation.equals("remove")) {
                return "Нет данных";
            } else {
                return getNewValue(patch, type);
            }
        } catch (JSONException ex) {
            return null;
        }
    }

    private Object getNewValue(final JSONObject patch, final String type) {
        switch (type) {
            case STATUS_ATTRIBUTE_TYPE:
                return getResettlementStatus(patch.getString("value"));
            case BOOLEAN_TYPE:
                return patch.getBoolean("value");
            default:
                return patch.getString("value");
        }
    }

    private String getResettlementStatus(final String statusCode) {
        return ResettlementStatus.of(statusCode)
            .map(ResettlementStatus::getDescription)
            .orElse("Нет даннных");
    }
}
