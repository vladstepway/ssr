package ru.croc.ugd.ssr.service.bus.response;

import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.bus.BusRequest;
import ru.croc.ugd.ssr.bus.BusRequestData;
import ru.croc.ugd.ssr.model.bus.BusRequestDocument;
import ru.croc.ugd.ssr.model.integration.busresponse.SmevResponse;
import ru.croc.ugd.ssr.service.bus.request.BusRequestType;
import ru.croc.ugd.ssr.service.bus.response.processor.BusResponseProcessor;
import ru.croc.ugd.ssr.service.document.BusRequestDocumentService;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class BusResponseService {

    private final BusRequestDocumentService busRequestDocumentService;
    private final List<BusResponseProcessor> busResponseProcessors;

    public Optional<BusRequestDocument> findRequestByServiceNumber(final String serviceNumber) {
        return busRequestDocumentService.findRequestByServiceNumber(serviceNumber);
    }

    public void handleResponse(final BusRequestDocument busRequestDocument, final SmevResponse response) {
        log.info("Start bus response handling for request {}", busRequestDocument.getId());

        ofNullable(busRequestDocument.getDocument())
            .map(BusRequest::getBusRequestData)
            .map(BusRequestData::getSsrRequestTypeCode)
            .flatMap(BusRequestType::ofCode)
            .flatMap(this::getBusResponseProcessor)
            .ifPresent(busResponseProcessor -> busResponseProcessor.process(busRequestDocument, response));

        log.info("Stop bus response handling for request {}", busRequestDocument.getId());
    }

    private Optional<BusResponseProcessor> getBusResponseProcessor(final BusRequestType busRequestType) {
        return busResponseProcessors.stream()
            .filter(factory -> busRequestType == factory.getBusRequestType())
            .findFirst();
    }
}
