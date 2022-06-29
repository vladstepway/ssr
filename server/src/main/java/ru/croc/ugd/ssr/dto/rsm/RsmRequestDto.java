package ru.croc.ugd.ssr.dto.rsm;

import lombok.Builder;
import lombok.Value;
import ru.croc.ugd.ssr.rsm.RsmObjectRequest;

@Value
@Builder
public class RsmRequestDto {

    private final String requestEno;
    private final String etpmvMessageId;
    private final RsmObjectRequest rsmObjectRequest;
}
