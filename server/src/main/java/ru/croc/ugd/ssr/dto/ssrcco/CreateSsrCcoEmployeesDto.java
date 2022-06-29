package ru.croc.ugd.ssr.dto.ssrcco;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class CreateSsrCcoEmployeesDto {

    private final List<SsrCcoDto> ccos;
    private final List<SsrCcoEmployeeDto> employees;
}
