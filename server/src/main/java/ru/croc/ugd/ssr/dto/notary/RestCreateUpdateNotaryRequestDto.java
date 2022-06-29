package ru.croc.ugd.ssr.dto.notary;

import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * Данные нотариуса.
 */
@Builder
@Value
public class RestCreateUpdateNotaryRequestDto {

    /**
     * Регистрационный номер нотариуса.
     */
    private final String registrationNumber;
    /**
     * Фамилия, имя, отчество.
     */
    private final String fullName;
    /**
     * Телефоны.
     */
    private final List<String> phones;
    /**
     * Адрес.
     */
    private final RestNotaryAddressDto address;
    /**
     * Дополнительная информация.
     */
    private final String additionalInformation;
    /**
     * Логин пользователя.
     */
    private final String employeeLogin;
}
