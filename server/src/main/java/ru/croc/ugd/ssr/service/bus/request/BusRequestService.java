package ru.croc.ugd.ssr.service.bus.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.dto.bus.CreateBusRequestDto;
import ru.croc.ugd.ssr.mapper.BusRequestDocumentMapper;
import ru.croc.ugd.ssr.model.bus.BusRequestDocument;
import ru.croc.ugd.ssr.service.bus.request.payload.BusRequestPayloadFactory;
import ru.croc.ugd.ssr.service.document.BusRequestDocumentService;
import ru.reinform.cdp.bus.rest.api.BusRestApi;
import ru.reinform.cdp.bus.rest.model.BusRequest;
import ru.reinform.cdp.bus.rest.model.SyncResponse;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class BusRequestService {

    private static final String BUS_REQUEST_SYSTEM_ID = "ugd_ssr";

    private final BusRestApi busRestApi;
    private final BusRequestDocumentService busRequestDocumentService;
    private final List<BusRequestPayloadFactory> busRequestPayloadFactories;
    private final BusRequestDocumentMapper busRequestDocumentMapper;

    public BusRequestDocument sendBusRequest(final CreateBusRequestDto createBusRequestDto) {
        log.info("Send bus request {}", createBusRequestDto);
        final BusRequestType busRequestType = createBusRequestDto.getBusRequestType();
        final String busRequestPayload = getBusRequestPayloadFactory(busRequestType)
            .map(busRequestPayloadFactory -> busRequestPayloadFactory.getRequestPayload(createBusRequestDto))
            .orElse(null);

        final SyncResponse response = sendBusRequestSync(busRequestType.getDocumentType(), busRequestPayload);

        final BusRequestDocument busRequestDocument = busRequestDocumentMapper
            .toBusRequestDocument(createBusRequestDto, busRequestPayload, response);

        return busRequestDocumentService.createDocument(busRequestDocument, false, null);
    }

    private SyncResponse sendBusRequestSync(final String documentType, final String busRequestPayload) {
        final BusRequest busRequest = new BusRequest();

        busRequest.setDocument(busRequestPayload);
        busRequest.setDocumentType(documentType);
        busRequest.setSystemID(BUS_REQUEST_SYSTEM_ID);

        return busRestApi.sync(busRequest);
    }

    private Optional<BusRequestPayloadFactory> getBusRequestPayloadFactory(final BusRequestType busRequestType) {
        return busRequestPayloadFactories.stream()
            .filter(factory -> busRequestType == factory.getBusRequestType())
            .findFirst();
    }
}
