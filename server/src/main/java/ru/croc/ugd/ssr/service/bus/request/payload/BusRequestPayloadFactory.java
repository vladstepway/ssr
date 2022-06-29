package ru.croc.ugd.ssr.service.bus.request.payload;

import ru.croc.ugd.ssr.dto.bus.CreateBusRequestDto;
import ru.croc.ugd.ssr.service.bus.request.BusRequestType;

public interface BusRequestPayloadFactory {

    BusRequestType getBusRequestType();

    String getRequestPayload(final CreateBusRequestDto createBusRequestDto);
}
