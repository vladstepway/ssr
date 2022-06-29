package ru.croc.ugd.ssr.dto.flat;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class AffairDto {
    private final String affairId;
    private final String roomNum;
    private final List<RestFlatLiverDto> livers;
}
