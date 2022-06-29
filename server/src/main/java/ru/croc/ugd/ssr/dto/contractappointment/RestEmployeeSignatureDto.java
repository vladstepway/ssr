package ru.croc.ugd.ssr.dto.contractappointment;

import lombok.Builder;
import lombok.Value;

/**
 * Данные об электронных подписях сотрудника ДГИ.
 */
@Value
@Builder
public class RestEmployeeSignatureDto {

    /**
     * ФИО сотрудника.
     */
    private final String employeeFullName;
    /**
     * Данные электронной подписи акта.
     */
    private final RestFileSignatureDto actSignature;
    /**
     * Данные электронной подписи договора.
     */
    private final RestFileSignatureDto contractSignature;
}
