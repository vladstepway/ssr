package ru.croc.ugd.ssr.dto.apartmentdefectconfirmation;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RestPersonDto {
    /**
     * ФИО
     */
    private final String fullName;
    /**
     * Телефон
     */
    private final String phone;
}
