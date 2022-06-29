package ru.croc.ugd.ssr.service.document;

import static java.util.Objects.isNull;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.db.dao.BusRequestDao;
import ru.croc.ugd.ssr.model.bus.BusRequestDocument;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.db.model.DocumentData;
import ru.reinform.cdp.document.model.DocumentType;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class BusRequestDocumentService extends SsrAbstractDocumentService<BusRequestDocument> {

    private final BusRequestDao busRequestDao;

    @NotNull
    @Override
    public DocumentType<BusRequestDocument> getDocumentType() {
        return SsrDocumentTypes.BUS_REQUEST;
    }

    public Optional<BusRequestDocument> findRequestByServiceNumber(final String serviceNumber) {
        if (isNull(serviceNumber)) {
            return Optional.empty();
        }
        final List<DocumentData> requestsByServiceNumber = busRequestDao.findRequestsByServiceNumber(serviceNumber);

        if (requestsByServiceNumber.size() > 1) {
            log.warn("More than 1 bus request has been sent with service number {}", serviceNumber);
        }

        return requestsByServiceNumber.stream()
            .findFirst()
            .map(this::parseDocumentData);
    }
}
