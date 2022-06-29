package ru.croc.ugd.ssr.dto.mdm.response;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Address {
    private final String unom;
    private final String flat;
    private final Estates estates;
}
