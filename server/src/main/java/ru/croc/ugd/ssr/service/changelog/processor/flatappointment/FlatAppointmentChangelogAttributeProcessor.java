package ru.croc.ugd.ssr.service.changelog.processor.flatappointment;

import static java.util.Optional.ofNullable;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.dto.flatappointment.FlatAppointmentFlowStatus;
import ru.croc.ugd.ssr.service.changelog.processor.ChangelogAttributeDescriptionProcessor;

/**
 * Обработчик изменений заявления на осмотр квартиры.
 */
@Component("flatAppointmentChangelogAttributeProcessor")
public class FlatAppointmentChangelogAttributeProcessor implements ChangelogAttributeDescriptionProcessor {

    private static final String STATUS_ATTRIBUTE_TYPE = "status";

    @Override
    public String getOldValue(final JSONObject oldJson, final JSONObject patch, final String type) {
        final String path = ofNullable(patch)
            .map(p -> p.getString("path"))
            .orElse(null);
        final String attributeValue = (String) getNestedJsonValue(oldJson, path);
        return attributeValue != null && STATUS_ATTRIBUTE_TYPE.equals(type)
            ? getFlatAppointmentStatus(attributeValue)
            : attributeValue;
    }

    @Override
    public String getNewValue(final JSONObject oldJson, final JSONObject patch, final String type) {
        final String operation = patch.getString("op");
        try {
            if (operation.equals("remove")) {
                return null;
            } else {
                return getNewValue(patch, type);
            }
        } catch (JSONException ex) {
            return null;
        }
    }

    private String getNewValue(final JSONObject patch, final String type) {
        if (STATUS_ATTRIBUTE_TYPE.equals(type)) {
            return getFlatAppointmentStatus(patch.getString("value"));
        }
        return patch.getString("value");
    }

    private String getFlatAppointmentStatus(final String statusId) {
        return FlatAppointmentFlowStatus.of(statusId).getSsrStatus();
    }
}
