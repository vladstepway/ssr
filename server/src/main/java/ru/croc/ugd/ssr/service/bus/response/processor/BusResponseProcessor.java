package ru.croc.ugd.ssr.service.bus.response.processor;

import ru.croc.ugd.ssr.model.bus.BusRequestDocument;
import ru.croc.ugd.ssr.model.integration.busresponse.SmevResponse;
import ru.croc.ugd.ssr.service.bus.request.BusRequestType;

public interface BusResponseProcessor {

    BusRequestType getBusRequestType();

    void process(final BusRequestDocument busRequestDocument, final SmevResponse response);
}
