package ru.croc.ugd.ssr.service.document;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.db.dao.SsrCcoDao;
import ru.croc.ugd.ssr.model.ssrcco.SsrCcoDocument;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.db.model.DocumentData;
import ru.reinform.cdp.document.model.DocumentType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * SsrCcoDocumentService.
 */
@Slf4j
@Service
@AllArgsConstructor
public class SsrCcoDocumentService extends SsrAbstractDocumentService<SsrCcoDocument> {

    private final SsrCcoDao ssrCcoDao;

    @NotNull
    @Override
    public DocumentType<SsrCcoDocument> getDocumentType() {
        return SsrDocumentTypes.SSR_CCO;
    }

    public Optional<SsrCcoDocument> fetchByPsDocumentId(final String psDocumentId) {
        final List<DocumentData> ssrCcos = ssrCcoDao.fetchByPsDocumentId(psDocumentId);

        if (ssrCcos.size() > 1) {
            log.warn("More than one ssr cco have been found; psDocumentId {}", psDocumentId);
        }

        return ssrCcos
            .stream()
            .map(this::parseDocumentData)
            .findFirst();
    }

    public Optional<LocalDateTime> fetchLastUpdateDateTime() {
        return ssrCcoDao.fetchLastUpdateDateTime()
            .map(LocalDateTime::parse);
    }

    public Optional<SsrCcoDocument> fetchByUnom(final String unom) {
        final List<DocumentData> ssrCcos = ssrCcoDao.fetchByUnom(unom);

        if (ssrCcos.size() > 1) {
            log.warn("More than one ssr cco have been found by unom = {}", unom);
        }

        return ssrCcos.stream()
            .map(this::parseDocumentData)
            .findFirst();
    }

    public List<SsrCcoDocument> fetchAll() {
        return ssrCcoDao.fetchAll()
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

}
