package ru.croc.ugd.ssr.service.document;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.db.dao.CompensationDao;
import ru.croc.ugd.ssr.model.compensation.CompensationDocument;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.document.model.DocumentType;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * CompensationDocumentService.
 */
@Service
@AllArgsConstructor
public class CompensationDocumentService extends SsrAbstractDocumentService<CompensationDocument> {

    private final CompensationDao compensationDao;

    @NotNull
    @Override
    public DocumentType<CompensationDocument> getDocumentType() {
        return SsrDocumentTypes.COMPENSATION;
    }

    public List<CompensationDocument> findAllByUnom(final String unom) {
        return compensationDao.findAllByUnom(unom)
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    public Optional<CompensationDocument> findByResettlementRequestIdAndRealEstateId(
        final String resettlementRequestId,
        final String realEstateId
    ) {
        return compensationDao.findByResettlementRequestIdAndRealEstateId(resettlementRequestId, realEstateId)
            .map(this::parseDocumentData);
    }
}
