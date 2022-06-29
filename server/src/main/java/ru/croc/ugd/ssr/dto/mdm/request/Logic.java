package ru.croc.ugd.ssr.dto.mdm.request;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class Logic {
    private final String operator;
    private final List<Condition> conditions;
}
