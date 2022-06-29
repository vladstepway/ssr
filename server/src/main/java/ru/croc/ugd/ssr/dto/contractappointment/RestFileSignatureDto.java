package ru.croc.ugd.ssr.dto.contractappointment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

/**
 * Данные об электронной подписи.
 */
@Value
@Builder
public class RestFileSignatureDto {

    /**
     * Номер ЭП.
     */
    private final String number;
    /**
     * ФИО владельца.
     */
    private final String fullName;
    /**
     * Дата и время выдачи ЭП.
     */
    private final LocalDateTime dateTimeFrom;
    /**
     * Дата и время окончания действия ЭП.
     */
    private final LocalDateTime dateTimeTo;
    /**
     * Дата и время подписания.
     */
    private final LocalDateTime signDateTime;
    /**
     * Подпись подтверждена.
     */
    @JsonProperty("isVerified")
    private final boolean verified;
}
