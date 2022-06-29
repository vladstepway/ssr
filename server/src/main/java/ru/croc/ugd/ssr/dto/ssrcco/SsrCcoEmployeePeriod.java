package ru.croc.ugd.ssr.dto.ssrcco;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class SsrCcoEmployeePeriod {

    private final LocalDate periodFrom;
    private final LocalDate periodTo;
}
