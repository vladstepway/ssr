package ru.croc.ugd.ssr.service.document;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.db.dao.EgrnFlatDao;
import ru.croc.ugd.ssr.exception.SsrException;
import ru.croc.ugd.ssr.model.egrn.EgrnFlatRequestDocument;
import ru.croc.ugd.ssr.service.DocumentWithFolder;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.db.model.DocumentData;
import ru.reinform.cdp.document.model.DocumentType;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * EgrnFlatRequestDocumentService.
 */
@Slf4j
@Service
@AllArgsConstructor
public class EgrnFlatRequestDocumentService extends DocumentWithFolder<EgrnFlatRequestDocument> {

    private final EgrnFlatDao egrnFlatDao;

    @NotNull
    @Override
    public DocumentType<EgrnFlatRequestDocument> getDocumentType() {
        return SsrDocumentTypes.EGRN_FLAT_REQUEST;
    }

    public EgrnFlatRequestDocument fetchLast(
        final String unom, final String flatNumber, final String statusCode
    ) {
        final List<DocumentData> egrnFlatRequestDocuments = egrnFlatDao
            .fetchAllByUnomAndFlatNumberAndStatusCode(unom.trim(), flatNumber.trim(), statusCode);

        return egrnFlatRequestDocuments.stream()
            .map(this::parseDocumentData)
            .max(Comparator.comparing(egrnFlatRequestDocument -> egrnFlatRequestDocument
                .getDocument()
                .getEgrnFlatRequestData()
                .getCreationDateTime()
            ))
            .orElseThrow(() -> new SsrException("В системе отсутствуют данные по выписке для данной квартиры"));
    }

    public List<EgrnFlatRequestDocument> fetchLastNonResidentialByUnom(final String unom) {
        return egrnFlatDao
            .fetchLastNonResidentialByUnom(unom)
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    public Optional<EgrnFlatRequestDocument> fetchRequestByServiceNumber(final String serviceNumber) {
        final List<DocumentData> requestsByServiceNumber = egrnFlatDao.fetchRequestsByServiceNumber(serviceNumber);

        if (requestsByServiceNumber.isEmpty()) {
            log.warn("Unable to find egrn flat request by serviceNumber {}", serviceNumber);
            return Optional.empty();
        }

        if (requestsByServiceNumber.size() > 1) {
            log.warn("More than 1 egrn flat request have been found; serviceNumber {}", serviceNumber);
        }

        return requestsByServiceNumber.stream()
            .findFirst()
            .map(this::parseDocumentData);
    }

    public Optional<EgrnFlatRequestDocument> fetchByCadNumAndStatusAndCcoDocumentNotNull(
        final String cadNum, final String status
    ) {
        return egrnFlatDao.fetchByCadNumAndStatusAndCcoDocumentNotNull(cadNum, status)
            .stream()
            .map(this::parseDocumentData)
            .max(Comparator.comparing(egrnFlatRequestDocument -> egrnFlatRequestDocument
                .getDocument()
                .getEgrnFlatRequestData()
                .getCreationDateTime()
            ));
    }
}
