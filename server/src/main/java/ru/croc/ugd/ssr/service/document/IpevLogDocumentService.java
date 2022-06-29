package ru.croc.ugd.ssr.service.document;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.model.ipev.IpevLogDocument;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.document.model.DocumentType;

/**
 * IpevLogDocumentService.
 */
@Slf4j
@Service
@AllArgsConstructor
public class IpevLogDocumentService extends SsrAbstractDocumentService<IpevLogDocument> {

    @NotNull
    @Override
    public DocumentType<IpevLogDocument> getDocumentType() {
        return SsrDocumentTypes.IPEV_LOG;
    }

}
