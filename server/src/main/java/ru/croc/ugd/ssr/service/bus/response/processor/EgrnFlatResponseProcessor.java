package ru.croc.ugd.ssr.service.bus.response.processor;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.egrn.EgrnFlatRequestData;
import ru.croc.ugd.ssr.model.bus.BusRequestDocument;
import ru.croc.ugd.ssr.model.egrn.EgrnFlatRequestDocument;
import ru.croc.ugd.ssr.model.integration.busresponse.SmevResponse;
import ru.croc.ugd.ssr.service.RealEstateDocumentService;
import ru.croc.ugd.ssr.service.bus.request.BusRequestType;
import ru.croc.ugd.ssr.service.document.EgrnFlatRequestDocumentService;
import ru.croc.ugd.ssr.service.egrn.EgrnFlatRequestService;

import java.util.Optional;

@Slf4j
@Component
@AllArgsConstructor
public class EgrnFlatResponseProcessor implements BusResponseProcessor {

    private static final String SUCCESS_STATUS_CODE = "1004";

    private final EgrnFlatRequestDocumentService egrnFlatRequestDocumentService;
    private final EgrnFlatRequestService egrnFlatRequestService;
    private final RealEstateDocumentService realEstateDocumentService;

    @Override
    public BusRequestType getBusRequestType() {
        return BusRequestType.EGRN_FLAT;
    }

    @Override
    public void process(final BusRequestDocument busRequestDocument, final SmevResponse response) {
        final Optional<EgrnFlatRequestDocument> optionalEgrnFlatRequestDocument = egrnFlatRequestDocumentService
            .fetchRequestByServiceNumber(response.getServiceNumber());

        log.info(
            "Start EgrnFlatRequest response processing: serviceNumber = {}, status = {}",
            response.getServiceNumber(),
            response.getStatusCode()
        );

        if (!optionalEgrnFlatRequestDocument.isPresent()) {
            log.warn(
                "Unable to process EgrnFlatRequest response due to request absence: serviceNumber = {}",
                response.getServiceNumber()
            );

            return;
        }

        final EgrnFlatRequestDocument egrnFlatRequestDocument = optionalEgrnFlatRequestDocument.get();
        final EgrnFlatRequestData egrnFlatRequestData = egrnFlatRequestDocument
            .getDocument()
            .getEgrnFlatRequestData();

        if (SUCCESS_STATUS_CODE.equals(egrnFlatRequestData.getStatusCode())) {
            log.info(
                "Skip EgrnFlatRequest response processing for already completed request: requestDocumentId = {}",
                egrnFlatRequestDocument.getId()
            );

            return;
        }

        if (SUCCESS_STATUS_CODE.equals(response.getStatusCode())) {
            final EgrnFlatRequestDocument updatedEgrnFlatRequestDocument = egrnFlatRequestService
                .fillResponseData(egrnFlatRequestDocument, response);

            egrnFlatRequestDocumentService.updateDocument(updatedEgrnFlatRequestDocument);

            realEstateDocumentService.updateByEgrnResponse(egrnFlatRequestDocument);

            egrnFlatRequestService.sendResidencePlaceRegistrationNotificationIfNeeded(
                updatedEgrnFlatRequestDocument
            );
        } else {
            egrnFlatRequestData.setStatusCode(response.getStatusCode());
            egrnFlatRequestData.setErrorDescription(response.getNote());

            egrnFlatRequestDocumentService.updateDocument(egrnFlatRequestDocument);
        }

        log.info(
            "Finish EgrnFlatRequest response processing: serviceNumber = {}, status = {}",
            response.getServiceNumber(),
            response.getStatusCode()
        );
    }
}
