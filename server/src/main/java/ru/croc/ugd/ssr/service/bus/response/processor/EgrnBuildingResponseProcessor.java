package ru.croc.ugd.ssr.service.bus.response.processor;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.egrn.EgrnBuildingRequestData;
import ru.croc.ugd.ssr.model.bus.BusRequestDocument;
import ru.croc.ugd.ssr.model.egrn.EgrnBuildingRequestDocument;
import ru.croc.ugd.ssr.model.integration.busresponse.SmevResponse;
import ru.croc.ugd.ssr.service.bus.request.BusRequestType;
import ru.croc.ugd.ssr.service.document.EgrnBuildingRequestDocumentService;
import ru.croc.ugd.ssr.service.egrn.EgrnBuildingRequestService;

import java.util.Optional;

@Slf4j
@Component
@AllArgsConstructor
public class EgrnBuildingResponseProcessor implements BusResponseProcessor {

    private static final String SUCCESS_STATUS_CODE = "1004";

    private final EgrnBuildingRequestDocumentService egrnBuildingRequestDocumentService;
    private final EgrnBuildingRequestService egrnBuildingRequestService;

    @Override
    public BusRequestType getBusRequestType() {
        return BusRequestType.EGRN_BUILDING;
    }

    @Override
    public void process(final BusRequestDocument busRequestDocument, final SmevResponse response) {
        final Optional<EgrnBuildingRequestDocument> optionalEgrnBuildingRequestDocument =
            egrnBuildingRequestDocumentService.fetchRequestByServiceNumber(response.getServiceNumber());

        log.info(
            "Start EgrnBuildingRequest response processing: serviceNumber = {}, status = {}",
            response.getServiceNumber(),
            response.getStatusCode()
        );

        if (!optionalEgrnBuildingRequestDocument.isPresent()) {
            log.warn(
                "Unable to process EgrnBuildingRequest response due to request absence: serviceNumber = {}",
                response.getServiceNumber()
            );

            return;
        }

        final EgrnBuildingRequestDocument egrnBuildingRequestDocument = optionalEgrnBuildingRequestDocument.get();
        final EgrnBuildingRequestData egrnBuildingRequestData = egrnBuildingRequestDocument
            .getDocument()
            .getEgrnBuildingRequestData();

        if (SUCCESS_STATUS_CODE.equals(egrnBuildingRequestData.getStatusCode())) {
            log.info(
                "Skip EgrnBuildingRequest response processing for already completed request: requestDocumentId = {}",
                egrnBuildingRequestDocument.getId()
            );

            return;
        }

        if (SUCCESS_STATUS_CODE.equals(response.getStatusCode())) {
            final EgrnBuildingRequestDocument updatedEgrnBuildingRequestDocument = egrnBuildingRequestService
                .fillResponseData(egrnBuildingRequestDocument, response);

            egrnBuildingRequestDocumentService.updateDocument(updatedEgrnBuildingRequestDocument);

            egrnBuildingRequestService.sendFlatRequests(updatedEgrnBuildingRequestDocument);
        } else {
            egrnBuildingRequestData.setStatusCode(response.getStatusCode());
            egrnBuildingRequestData.setErrorDescription(response.getNote());

            egrnBuildingRequestDocumentService.updateDocument(egrnBuildingRequestDocument);
        }

        log.info(
            "Finish EgrnBuildingRequest response processing: serviceNumber = {}, status = {}",
            response.getServiceNumber(),
            response.getStatusCode()
        );
    }
}
