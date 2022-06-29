package ru.croc.ugd.ssr.service.emailnotificant;

import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.service.DictionaryConverter;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Класс-конвертер для значений из справочника ugd_ssr_notificantEmails.
 */
@Service
public class EmailNotificantConverter implements DictionaryConverter<EmailNotificant> {

    /**
     * Конвертирует значения, полученные из справочника, в EmailNotificant.
     *
     * @param elements элементы справочника
     * @return множество EmailNotificant
     */
    @Override
    public Set<EmailNotificant> convertElements(final List<Map<String, Object>> elements) {
        return elements
            .stream()
            .map(this::convertElement)
            .collect(Collectors.toSet());
    }

    private EmailNotificant convertElement(final Map<String, Object> element) {
        return EmailNotificant.builder()
            .areaCode((String) element.get("areaCode"))
            .area((String) element.get("area"))
            .fullName((String) element.get("fullName"))
            .position((String) element.get("position"))
            .department((String) element.get("department"))
            .email((String) element.get("email"))
            .notificationType(NotificationType.fromValue((String) element.get("notificationType")))
            .cc((Boolean) element.get("cc"))
            .code((Integer) element.get("code"))
            .build();
    }
}
