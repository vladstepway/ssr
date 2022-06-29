package ru.croc.ugd.ssr.service.changelog.processor.notary;

import static java.util.Optional.ofNullable;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.dto.notary.NotaryApplicationFlowStatus;
import ru.croc.ugd.ssr.service.changelog.processor.ChangelogAttributeDescriptionProcessor;

/**
 * Обработчик изменений заявления на посещение нотариуса.
 */
@Component("notaryApplicationChangelogAttributeProcessor")
public class NotaryApplicationChangelogAttributeProcessor implements ChangelogAttributeDescriptionProcessor {

    private static final String STATUS_ATTRIBUTE_TYPE = "status";
    private static final String FILE_ATTRIBUTE_TYPE = "file";
    private static final String COMMENT_ATTRIBUTE_TYPE = "comment";

    @Override
    public String getOldValue(final JSONObject oldJson, final JSONObject patch, final String type) {
        final String path = ofNullable(patch)
            .map(p -> p.getString("path"))
            .orElse(null);
        final String attributeValue = (String) getNestedJsonValue(oldJson, path);
        return attributeValue != null && STATUS_ATTRIBUTE_TYPE.equals(type)
            ? getNotaryApplicationStatus(attributeValue)
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
                return getNotaryApplicationStatus(patch.getString("value"));
            case FILE_ATTRIBUTE_TYPE:
                return getValueFromArray(patch, "/value/files", "name");
            case COMMENT_ATTRIBUTE_TYPE:
                return getValueFromArray(patch, "/value/processingComment");
            default:
                return patch.getString("value");
        }
    }

    private String getNotaryApplicationStatus(final String statusId) {
        return NotaryApplicationFlowStatus.of(statusId).getSsrStatus();
    }

}
