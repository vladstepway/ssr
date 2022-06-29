package ru.croc.ugd.ssr.dto.mdm.request;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Binary {
    private final String field;
    private final String operator;
    private final String value;
}
