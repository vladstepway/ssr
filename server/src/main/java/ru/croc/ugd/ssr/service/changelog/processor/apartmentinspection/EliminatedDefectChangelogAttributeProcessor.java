package ru.croc.ugd.ssr.service.changelog.processor.apartmentinspection;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.service.changelog.processor.ChangelogAttributeDescriptionProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * Обработчик устранения дефектов в актах по дефектам.
 */
@Component("eliminatedDefectChangelogAttributeProcessor")
public class EliminatedDefectChangelogAttributeProcessor implements ChangelogAttributeDescriptionProcessor {

    @Override
    public boolean shouldSkipAttribute(final JSONObject oldJson, final JSONObject patch, final String type) {
        final String op = patch.getString("op");
        return !"replace".equals(op);
    }

    @Override
    public String getNewValue(final JSONObject oldJson, final JSONObject patch, final String type) {
        final String path = patch.getString("path");
        final String op = patch.getString("op");

        if (!"replace".equals(op)) {
            return null;
        }
        final String description = (String) getNestedJsonValue(
            oldJson, path.replaceAll("isEliminated", "description")
        );
        final String flatElement = (String) getNestedJsonValue(
            oldJson, path.replaceAll("isEliminated", "flatElement")
        );

        final List<String> strings = new ArrayList<>();
        strings.add("Элемент квартиры: " + flatElement);
        strings.add("Описание: " + description);

        return String.join("; ", strings);
    }
}
