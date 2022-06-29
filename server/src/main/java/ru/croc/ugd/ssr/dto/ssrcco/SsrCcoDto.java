package ru.croc.ugd.ssr.dto.ssrcco;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class SsrCcoDto {

    private final String id;
    private final String psDocumentId;
    private final String unom;
    private final String address;
    private final String area;
    private final String district;
    private final List<SsrCcoOrganizationDto> organizations;
    private final List<SsrCcoEmployeeDto> employees;
}
