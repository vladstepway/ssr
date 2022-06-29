package ru.croc.ugd.ssr.service.utils;

import org.springframework.util.StringUtils;
import ru.croc.ugd.ssr.ApartmentInspectionType;

/**
 * Класс для обработки данных по квартирам.
 */
public class ApartmentUtils {

    private static final String REPLACE_FLAT_NUMBER_PATTERN = ", квартира [\\dA-Za-zА-Яа-я/]+";

    private ApartmentUtils() {
    }

    /**
     * Удаление данных о квартире из акта.
     * @param apartmentInspection акт
     */
    public static void removeRoomNumber(final ApartmentInspectionType apartmentInspection) {
        if (apartmentInspection != null && !StringUtils.isEmpty(apartmentInspection.getAddress())) {
            String cleanFlatAddress = apartmentInspection
                .getAddress()
                .replaceAll(REPLACE_FLAT_NUMBER_PATTERN, "");
            apartmentInspection.setAddress(cleanFlatAddress);
        }
    }

}
