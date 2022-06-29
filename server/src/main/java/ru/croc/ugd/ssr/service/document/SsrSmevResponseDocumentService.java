package ru.croc.ugd.ssr.service.document;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.croc.ugd.ssr.db.dao.SsrSmevResponseDao;
import ru.croc.ugd.ssr.model.SsrSmevResponseDocument;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.db.model.DocumentData;
import ru.reinform.cdp.document.model.DocumentType;
import ru.reinform.cdp.document.service.AbstractDocumentService;

import java.util.stream.Stream;
import javax.annotation.Nonnull;

/**
 * SsrSmevResponseDocumentService.
 */
@Slf4j
@Service
@AllArgsConstructor
public class SsrSmevResponseDocumentService extends AbstractDocumentService<SsrSmevResponseDocument> {

    private final SsrSmevResponseDao ssrSmevResponseDao;

    @NotNull
    @Override
    public DocumentType<SsrSmevResponseDocument> getDocumentType() {
        return SsrDocumentTypes.SSR_SMEV_RESPONSE;
    }

    @Transactional
    public Stream<SsrSmevResponseDocument> findByProcessEndDateTimeIsNullAndStream() {
        return ssrSmevResponseDao.findByProcessEndDateTimeIsNullAndStream()
            .map(this::parseDocumentData);
    }

    private SsrSmevResponseDocument parseDocumentData(@Nonnull DocumentData documentData) {
        return parseDocumentJson(documentData.getJsonData());
    }

    private SsrSmevResponseDocument parseDocumentJson(@Nonnull String json) {
        return jsonMapper.readObject(json, SsrSmevResponseDocument.class);
    }
}
