package ru.croc.ugd.ssr.service.bus.response.processor;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.model.PfrSnilsRequestDocument;
import ru.croc.ugd.ssr.model.bus.BusRequestDocument;
import ru.croc.ugd.ssr.model.integration.busresponse.SmevResponse;
import ru.croc.ugd.ssr.pfr.PfrSnilsRequestType;
import ru.croc.ugd.ssr.service.bus.request.BusRequestType;
import ru.croc.ugd.ssr.service.document.PfrSnilsRequestDocumentService;
import ru.croc.ugd.ssr.service.pfr.PfrSnilsRequestService;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Component
@AllArgsConstructor
public class PfrSnilsResponseProcessor implements BusResponseProcessor {

    private static final String SUCCESS_STATUS_CODE = "1004";
    private static final String[] FINAL_STATUS_CODES = {"2", "3", "4", "5", "6", "7", "request_expired", "-1",
        "1001", "1002", "1004", "1005", "1006", "1008", "1009"};

    private final PfrSnilsRequestService pfrSnilsRequestService;
    private final PfrSnilsRequestDocumentService pfrSnilsRequestDocumentService;

    @Override
    public BusRequestType getBusRequestType() {
        return BusRequestType.PFR_SNILS_EXTENDED;
    }

    @Override
    public void process(final BusRequestDocument busRequestDocument, final SmevResponse response) {
        final Optional<PfrSnilsRequestDocument> optionalPfrSnilsRequestDocument = pfrSnilsRequestDocumentService
            .fetchRequestByServiceNumber(response.getServiceNumber());

        log.info(
            "Start PfrSnilsRequest response processing: serviceNumber = {}, status = {}",
            response.getServiceNumber(),
            response.getStatusCode()
        );

        if (!optionalPfrSnilsRequestDocument.isPresent()) {
            log.warn(
                "Unable to process PfrSnilsRequest response due to request absence: serviceNumber = {}",
                response.getServiceNumber()
            );

            return;
        }

        final PfrSnilsRequestDocument pfrSnilsRequestDocument = optionalPfrSnilsRequestDocument.get();
        final PfrSnilsRequestType pfrSnilsRequest = pfrSnilsRequestDocument
            .getDocument()
            .getPfrSnilsRequestData();

        if (SUCCESS_STATUS_CODE.equals(pfrSnilsRequest.getStatusCode())) {
            log.info(
                "Skip PfrSnilsRequest response processing for already completed request: requestDocumentId = {}",
                pfrSnilsRequestDocument.getId()
            );
            return;
        }

        if (SUCCESS_STATUS_CODE.equals(response.getStatusCode())) {
            final PfrSnilsRequestDocument updatedPfrSnilsRequestDocument = pfrSnilsRequestService
                .fillResponseData(pfrSnilsRequestDocument, response);

            pfrSnilsRequestDocumentService.updateDocument(updatedPfrSnilsRequestDocument);
        } else {
            pfrSnilsRequest.setStatusCode(response.getStatusCode());
            pfrSnilsRequest.setErrorDescription(response.getNote());

            if (isFinalStatusCode(response.getStatusCode())) {
                pfrSnilsRequestService.updatePersonStatus(pfrSnilsRequest, response.getNote());
            }

            pfrSnilsRequestDocumentService.updateDocument(pfrSnilsRequestDocument);
        }

        log.info(
            "Finish PfrSnilsRequest response processing: serviceNumber = {}, status = {}",
            response.getServiceNumber(),
            response.getStatusCode()
        );
    }

    private boolean isFinalStatusCode(final String statusCode) {
        return Arrays.asList(FINAL_STATUS_CODES).contains(statusCode);
    }
}
