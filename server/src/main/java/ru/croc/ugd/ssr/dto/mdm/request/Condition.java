package ru.croc.ugd.ssr.dto.mdm.request;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Condition {
    private final Binary binary;
}
