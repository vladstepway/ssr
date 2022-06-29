package ru.croc.ugd.ssr.dto.mdm.response;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class MdmResponse {
    private final int errorCode;
    private final String requestId;
    private final int foundRecords;
    private final List<PersonDetail> data;
}
