package ru.croc.ugd.ssr.service.document;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.db.dao.CourtInfoDao;
import ru.croc.ugd.ssr.model.courtinfo.CourtInfoDocument;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.db.model.DocumentData;
import ru.reinform.cdp.document.model.DocumentType;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * CourtInfoDocumentService.
 */
@Slf4j
@Service
@AllArgsConstructor
public class CourtInfoDocumentService extends SsrAbstractDocumentService<CourtInfoDocument> {

    private final CourtInfoDao courtInfoDao;

    @NotNull
    @Override
    public DocumentType<CourtInfoDocument> getDocumentType() {
        return SsrDocumentTypes.COURT_INFO;
    }

    public List<CourtInfoDocument> fetchAllByAffairId(final String affairId) {
        return courtInfoDao.fetchAllByAffairId(affairId)
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    public Optional<CourtInfoDocument> fetchByCaseIdAndAffairId(final String caseId, final String affairId) {
        final List<DocumentData> courtInfos = courtInfoDao.fetchAllByCaseIdAndAffairId(caseId, affairId);

        if (courtInfos.size() > 1) {
            log.warn("More than one court info document have been found: caseId = {}, affairId = {}", caseId, affairId);
        }

        return courtInfos.stream()
            .findFirst()
            .map(this::parseDocumentData);
    }
}
