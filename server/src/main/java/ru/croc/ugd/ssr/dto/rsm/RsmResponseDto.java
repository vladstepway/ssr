package ru.croc.ugd.ssr.dto.rsm;

import lombok.Builder;
import lombok.Value;
import ru.croc.ugd.ssr.rsm.RsmObjectResponse;

@Value
@Builder
public class RsmResponseDto {

    private final String responseEno;
    private final String etpmvMessageId;
    private final RsmObjectResponse rsmObjectResponse;
}
