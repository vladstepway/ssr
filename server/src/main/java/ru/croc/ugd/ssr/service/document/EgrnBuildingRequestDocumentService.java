package ru.croc.ugd.ssr.service.document;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.db.dao.EgrnBuildingDao;
import ru.croc.ugd.ssr.model.egrn.EgrnBuildingRequestDocument;
import ru.croc.ugd.ssr.service.DocumentWithFolder;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.db.model.DocumentData;
import ru.reinform.cdp.document.model.DocumentType;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * EgrnBuildingRequestDocumentService.
 */
@Slf4j
@Service
@AllArgsConstructor
public class EgrnBuildingRequestDocumentService extends DocumentWithFolder<EgrnBuildingRequestDocument> {

    private final EgrnBuildingDao egrnBuildingDao;

    @NotNull
    @Override
    public DocumentType<EgrnBuildingRequestDocument> getDocumentType() {
        return SsrDocumentTypes.EGRN_BUILDING_REQUEST;
    }

    public Optional<EgrnBuildingRequestDocument> fetchRequestByServiceNumber(final String serviceNumber) {
        final List<DocumentData> requestsByServiceNumber = egrnBuildingDao.fetchRequestsByServiceNumber(serviceNumber);

        if (requestsByServiceNumber.isEmpty()) {
            log.warn("Unable to find egrn building request by serviceNumber {}", serviceNumber);
            return Optional.empty();
        }

        if (requestsByServiceNumber.size() > 1) {
            log.warn("More than 1 egrn building request have been found; serviceNumber {}", serviceNumber);
        }

        return requestsByServiceNumber.stream()
            .findFirst()
            .map(this::parseDocumentData);
    }

    public Optional<EgrnBuildingRequestDocument> fetchLastRequestByCadastralNumberAndStatusCode(
        final String cadastralNumber, final String statusCode
    ) {
        return egrnBuildingDao.fetchRequestsByCadastralNumberAndStatusCode(cadastralNumber, statusCode)
            .stream()
            .map(this::parseDocumentData)
            .max(Comparator.comparing(egrnBuildingRequestDocument -> egrnBuildingRequestDocument
                .getDocument()
                .getEgrnBuildingRequestData()
                .getCreationDateTime()
            ));
    }

    public Optional<EgrnBuildingRequestDocument> fetchLastRequestByCadastralNumber(final String cadastralNumber) {
        return egrnBuildingDao.fetchRequestsByCadastralNumber(cadastralNumber)
            .stream()
            .map(this::parseDocumentData)
            .max(Comparator.comparing(egrnBuildingRequestDocument -> egrnBuildingRequestDocument
                .getDocument()
                .getEgrnBuildingRequestData()
                .getCreationDateTime()
            ));
    }
}
