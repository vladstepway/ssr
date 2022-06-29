package ru.croc.ugd.ssr.dto.notary;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;
import ru.croc.ugd.ssr.dto.RestEmployeeResponseDto;

import java.util.List;

/**
 * Данные нотариуса.
 */
@Builder
@Value
@JsonInclude(JsonInclude.Include.ALWAYS)
public class RestNotaryResponseDto {

    /**
     * ИД карточки.
     */
    private final String id;
    /**
     * Статус карточки.
     */
    private final String status;
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
     * Связанные сотрудники.
     */
    private final RestEmployeeResponseDto employee;
    /**
     * Адрес.
     */
    private final RestNotaryAddressDto address;
    /**
     * Дополнительная информация.
     */
    private final String additionalInformation;
}
