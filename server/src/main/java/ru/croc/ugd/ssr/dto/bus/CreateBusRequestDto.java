package ru.croc.ugd.ssr.dto.bus;

import lombok.Builder;
import lombok.Value;
import ru.croc.ugd.ssr.service.bus.request.BusRequestType;

import java.util.Map;

@Value
@Builder
public class CreateBusRequestDto {

    private final BusRequestType busRequestType;
    private final String serviceNumber;
    private final String serviceTypeCode;
    private final String ochdFolderGuid;
    private final Map<String, String> customVariables;
}
