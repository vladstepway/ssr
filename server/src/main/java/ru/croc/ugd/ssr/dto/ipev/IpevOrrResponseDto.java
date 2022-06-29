package ru.croc.ugd.ssr.dto.ipev;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class IpevOrrResponseDto {

    private final List<String> cadastralNumbers;
    private final String fileStoreId;
}
