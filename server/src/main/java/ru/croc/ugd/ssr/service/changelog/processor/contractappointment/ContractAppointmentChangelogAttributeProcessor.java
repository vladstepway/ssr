package ru.croc.ugd.ssr.service.changelog.processor.contractappointment;

import static java.util.Optional.ofNullable;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.dto.contractappointment.ContractAppointmentFlowStatus;
import ru.croc.ugd.ssr.service.changelog.processor.ChangelogAttributeDescriptionProcessor;

/**
 * Обработчик изменений заявления на подписания договора.
 */
@Component("contractAppointmentChangelogAttributeProcessor")
public class ContractAppointmentChangelogAttributeProcessor implements ChangelogAttributeDescriptionProcessor {

    private static final String STATUS_ATTRIBUTE_TYPE = "status";
    private static final String FILE_NAME_ATTRIBUTE_TYPE = "fileName";

    @Override
    public String getOldValue(final JSONObject oldJson, final JSONObject patch, final String type) {
        final String path = ofNullable(patch)
            .map(p -> p.getString("path"))
            .orElse(null);
        final String attributeValue = (String) getNestedJsonValue(oldJson, path);
        return attributeValue != null && STATUS_ATTRIBUTE_TYPE.equals(type)
            ? getContractAppointmentStatus(attributeValue)
            : attributeValue;
    }

    @Override
    public String getNewValue(final JSONObject oldJson, final JSONObject patch, final String type) {
        final String operation = patch.getString("op");
        try {
            if (operation.equals("remove")) {
                return "Нет данных";
            } else {
                return getNewValue(patch, type);
            }
        } catch (JSONException ex) {
            return null;
        }
    }

    private String getNewValue(final JSONObject patch, final String type) {
        switch (type) {
            case STATUS_ATTRIBUTE_TYPE:
                return getContractAppointmentStatus(patch.getString("value"));
            case FILE_NAME_ATTRIBUTE_TYPE:
                return patch.getJSONObject("value").getString("fileName");
            default:
                return patch.getString("value");

        }
    }

    private String getContractAppointmentStatus(final String statusId) {
        return ContractAppointmentFlowStatus.of(statusId).getSsrStatus();
    }

}
