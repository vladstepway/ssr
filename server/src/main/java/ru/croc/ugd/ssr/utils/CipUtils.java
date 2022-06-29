package ru.croc.ugd.ssr.utils;

import static java.util.Optional.ofNullable;

import ru.croc.ugd.ssr.CipType;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Класс для обработки данных по ЦИПам.
 */
public class CipUtils {

    /**
     * Получение адреса ЦИПа с районом и округом.
     *
     * @param cip cip
     * @return адрес ЦИПа с районом и округом
     */
    public static String getCipAddress(final CipType cip) {
        final String additionalAddress = Stream.of(
            ofNullable(cip).map(CipType::getArea).map(CipType.Area::getName),
            ofNullable(cip).map(CipType::getDistrict).map(CipType.District::getName))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .filter(address -> !address.isEmpty())
            .collect(Collectors.joining(", "));

        return ofNullable(cip)
            .map(CipType::getAddress)
            .map(address -> concatAdditionalAddress(address, additionalAddress))
            .orElse(null);
    }

    private static String concatAdditionalAddress(final String address, final String additionalAddress) {
        return ofNullable(additionalAddress)
            .map(nonNullAdditionalAddress -> address + " (" + nonNullAdditionalAddress + ")")
            .orElse(address);
    }
}
