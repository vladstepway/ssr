package ru.croc.ugd.ssr.service.document;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.db.dao.PfrSnilsRequestDao;
import ru.croc.ugd.ssr.model.PfrSnilsRequestDocument;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.db.model.DocumentData;
import ru.reinform.cdp.document.model.DocumentType;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class PfrSnilsRequestDocumentService extends SsrAbstractDocumentService<PfrSnilsRequestDocument> {

    private final PfrSnilsRequestDao pfrSnilsRequestDao;

    @NotNull
    @Override
    public DocumentType<PfrSnilsRequestDocument> getDocumentType() {
        return SsrDocumentTypes.PFR_SNILS_REQUEST;
    }

    public Optional<PfrSnilsRequestDocument> fetchRequestByServiceNumber(final String serviceNumber) {
        final List<DocumentData> requestsByServiceNumber = pfrSnilsRequestDao
            .fetchRequestsByServiceNumber(serviceNumber);

        if (requestsByServiceNumber.isEmpty()) {
            log.warn("Unable to find pfr snils request by serviceNumber {}", serviceNumber);
            return Optional.empty();
        }

        if (requestsByServiceNumber.size() > 1) {
            log.warn("More than 1 pfr snils request have been found; serviceNumber {}", serviceNumber);
        }

        return requestsByServiceNumber.stream()
            .findFirst()
            .map(this::parseDocumentData);
    }

    public List<PfrSnilsRequestDocument> fetchDocumentsWithLimitedRequests() {
        return pfrSnilsRequestDao.fetchDocumentsWithLimitedRequests()
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

}
