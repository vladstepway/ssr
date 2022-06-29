package ru.croc.ugd.ssr.dto.mdm.request;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MdmRequest {
    private final Query query;
    private final String protocol;
}
