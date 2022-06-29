package ru.croc.ugd.ssr.service.trade.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * TradeAdditionDecodedValues.
 */
@Data
@NoArgsConstructor
public class TradeAdditionDecodedValues {
    private final Map<String, TradeAdditionPersonDecodedValue> personIdDataMapping = new HashMap<>();
    private final Map<String, String> oldUnomAddressMapping = new HashMap<>();
    private final Map<String, String> newUnomAddressMapping = new HashMap<>();
    private final Map<String, String> newUnomCadNumberMapping = new HashMap<>();

    public String getDecodedPersonData() {
        return personIdDataMapping
            .entrySet()
            .stream()
            .map(stringStringEntry -> getDecodedPersonData(
                stringStringEntry.getValue(),
                stringStringEntry.getKey()
            ))
            .collect(Collectors.joining(" "));
    }

    private String getDecodedPersonData(
        TradeAdditionPersonDecodedValue personData,
        String personId
    ) {
        return "PersonId " + personId + " - Имя: " + (personData == null
            || StringUtils.isEmpty(personData.getPersonFio())
            ? "Не найдено"
            : personData.getPersonFio());
    }

    public String getOldUnomDecodedData() {
        return getUnomDecodedDataFromMapping(oldUnomAddressMapping);
    }

    public String getNewUnomDecodedData() {
        return getUnomDecodedDataFromMapping(newUnomAddressMapping);
    }

    /**
     * isAnyNotFound.
     *
     * @return if has any empty value in mapping
     */
    public boolean isAnyNotFound() {
        return personIdDataMapping.values().stream()
            .anyMatch(personIdDataMapping -> Objects.isNull(personIdDataMapping)
                || StringUtils.isEmpty(personIdDataMapping.getPersonFio()))
            || oldUnomAddressMapping.values().stream().anyMatch(StringUtils::isEmpty)
            || newUnomAddressMapping.values().stream().anyMatch(StringUtils::isEmpty);
    }

    private String getUnomDecodedDataFromMapping(final Map<String, String> mapping) {
        return mapping
            .entrySet()
            .stream()
            .map(stringStringEntry -> getUnomDecodedData(
                stringStringEntry.getValue(),
                stringStringEntry.getKey()
            ))
            .collect(Collectors.joining());
    }

    private String getUnomDecodedData(final String address, final String unom) {
        return "UNOM " + unom + " - адрес: " + (address == null ? "Не найден" : (address + "\n"));
    }
}
