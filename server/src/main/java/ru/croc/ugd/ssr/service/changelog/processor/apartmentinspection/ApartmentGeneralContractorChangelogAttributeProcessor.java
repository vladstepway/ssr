package ru.croc.ugd.ssr.service.changelog.processor.apartmentinspection;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.service.changelog.processor.ChangelogAttributeDescriptionProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * Обработчик изменений по генподрядчикам в актах по дефектам.
 */
@Component("apartmentGeneralContractorChangelogAttributeProcessor")
public class ApartmentGeneralContractorChangelogAttributeProcessor implements ChangelogAttributeDescriptionProcessor {

    @Override
    public List<String> processDescription(final JSONObject patch, final JSONObject oldJson, final String type) {
        final List<String> descriptionMessages = new ArrayList<>();
        final String op = patch.getString("op");
        if (!"replace".equals(op)) {
            return descriptionMessages;
        }

        final String path = patch.getString("path").replace("/isAssigned", "");
        final Object value = getNestedJsonValue(oldJson, path);
        if (value instanceof JSONObject) {
            descriptionMessages.add(
                retrieveAttributeValue((JSONObject) getNestedJsonValue(oldJson, path), "orgFullName")
            );
        } else if (value instanceof JSONArray) {
            descriptionMessages.add(
                retrieveAttributeValue((JSONArray) getNestedJsonValue(oldJson, path), "orgFullName")
            );
        }

        return descriptionMessages;
    }
}
