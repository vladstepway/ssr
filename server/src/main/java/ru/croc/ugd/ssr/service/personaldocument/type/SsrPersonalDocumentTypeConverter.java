package ru.croc.ugd.ssr.service.personaldocument.type;

import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.service.DictionaryConverter;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Класс-конвертер для значений из справочника ugd_ssr_personalDocumentType.
 */
@Service
public class SsrPersonalDocumentTypeConverter implements DictionaryConverter<SsrPersonalDocumentType> {

    /**
     * Конвертирует значения, полученные из справочника, в PersonalDocumentType.
     *
     * @param elements элементы справочника
     * @return множество PersonalDocumentType
     */
    @Override
    public Set<SsrPersonalDocumentType> convertElements(final List<Map<String, Object>> elements) {
        return elements
            .stream()
            .map(this::convertElement)
            .collect(Collectors.toSet());
    }

    private SsrPersonalDocumentType convertElement(final Map<String, Object> element) {
        return SsrPersonalDocumentType.builder()
            .code((String) element.get("code"))
            .name((String) element.get("name"))
            .kindCode((String) element.get("kindCode"))
            .sortNumber((int) element.get("sortNumber"))
            .build();
    }
}
