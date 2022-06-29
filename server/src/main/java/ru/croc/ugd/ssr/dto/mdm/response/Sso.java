package ru.croc.ugd.ssr.dto.mdm.response;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Sso {
    private final String value;
}
