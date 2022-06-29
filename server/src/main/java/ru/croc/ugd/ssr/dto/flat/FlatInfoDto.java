package ru.croc.ugd.ssr.dto.flat;

import lombok.Builder;
import lombok.Value;
import ru.croc.ugd.ssr.FlatType;

import java.util.List;

@Value
@Builder
public class FlatInfoDto {
    private final FlatType flat;
    private final List<AffairDto> affairs;
}
