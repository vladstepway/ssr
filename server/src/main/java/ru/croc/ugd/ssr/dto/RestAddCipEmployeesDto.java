package ru.croc.ugd.ssr.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class RestAddCipEmployeesDto {

    private final List<String> cipIds;
    private final List<String> employeeLogins;
}
