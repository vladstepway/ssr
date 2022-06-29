
package ru.croc.ugd.ssr.service.changelog.processor.apartmentinspection;

import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.service.DictionaryService;
import ru.croc.ugd.ssr.service.changelog.processor.ChangelogAttributeDescriptionProcessor;

import java.util.ArrayList;
import java.util.List;


/**
 * Обработчик причины закрытия акта без согласия.
 */
@AllArgsConstructor
@Component("apartmentActClosureReasonChangelogAttributeProcessor")
public class ApartmentActClosureChangelogAttributeProcessor implements ChangelogAttributeDescriptionProcessor {

    private static final String ACT_CLOSURE_REASON_DICTIONARY = "ugd_ssr_actClosureReason";

    private final DictionaryService dictionaryService;

    @Override
    public List<String> processDescription(JSONObject patch, JSONObject oldJson, String type) {
        final List<String> strings = new ArrayList<>();
        strings.add("Акт закрыт без получения согласия");
        strings.add("Причина закрытия акта: " + getActClosureReason(patch.getString("value")));
        return strings;
    }

    private String getActClosureReason(String value) {
        return dictionaryService.getNameByCode(ACT_CLOSURE_REASON_DICTIONARY, value);
    }

}