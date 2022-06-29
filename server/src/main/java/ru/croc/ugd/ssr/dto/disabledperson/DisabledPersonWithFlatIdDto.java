package ru.croc.ugd.ssr.dto.disabledperson;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DisabledPersonWithFlatIdDto {
    private final RestDisabledPersonDto disabledPerson;
    private final String flatId;
}
