package ru.croc.ugd.ssr.service.document;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.model.affairCollation.AffairCollationDocument;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.document.model.DocumentType;

/**
 * AffairCollationDocumentService.
 */
@Slf4j
@Service
@AllArgsConstructor
public class AffairCollationDocumentService extends SsrAbstractDocumentService<AffairCollationDocument> {

    @NotNull
    @Override
    public DocumentType<AffairCollationDocument> getDocumentType() {
        return SsrDocumentTypes.AFFAIR_COLLATION;
    }
}
