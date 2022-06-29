package ru.croc.ugd.ssr.service.document;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.db.dao.GuardianshipDao;
import ru.croc.ugd.ssr.model.guardianship.GuardianshipRequestDocument;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.db.model.DocumentData;
import ru.reinform.cdp.document.model.DocumentType;
import ru.reinform.cdp.document.service.AbstractDocumentService;

import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

/**
 * GuardianshipRequestDocumentService.
 */
@Slf4j
@Service
@AllArgsConstructor
public class GuardianshipRequestDocumentService
    extends AbstractDocumentService<GuardianshipRequestDocument> {

    private final GuardianshipDao guardianshipDao;

    @NotNull
    @Override
    public DocumentType<GuardianshipRequestDocument> getDocumentType() {
        return SsrDocumentTypes.GUARDIANSHIP_REQUEST;
    }

    public List<GuardianshipRequestDocument> fetchByAffairIdAndSkipInactive(
        final String affairId, final boolean skipInactive
    ) {
        return guardianshipDao.findRequestByAffairIdAndSkipInactive(affairId, skipInactive)
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    public List<GuardianshipRequestDocument> fetchByAffairIdAndProcessInstanceIdIsNull(final String affairId) {
        return guardianshipDao.findRequestByAffairIdAndProcessInstanceIdIsNull(affairId)
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    public boolean existsNonCompleteByAffairId(final String affairId) {
        return guardianshipDao.existsNonCompleteRequestByAffairId(affairId);
    }

    private GuardianshipRequestDocument parseDocumentData(@Nonnull DocumentData documentData) {
        return parseDocumentJson(documentData.getJsonData());
    }

    private GuardianshipRequestDocument parseDocumentJson(@Nonnull String json) {
        return jsonMapper.readObject(json, GuardianshipRequestDocument.class);
    }
}
