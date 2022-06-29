package ru.croc.ugd.ssr.dto.flat;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class RestFlatDto {
    private final String flatId;
    private final String flatNum;
    private final String roomNum;
    private final String affairId;
    private final List<RestFlatLiverDto> livers;
}
