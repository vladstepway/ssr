package ru.croc.ugd.ssr.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PersonValidateDto {

    private final boolean isValid;
    private final String notValidMessage;
}
