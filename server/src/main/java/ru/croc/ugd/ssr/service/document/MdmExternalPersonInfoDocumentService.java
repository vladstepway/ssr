package ru.croc.ugd.ssr.service.document;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.model.MdmExternalPersonInfoDocument;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.document.model.DocumentType;

@Slf4j
@Service
@AllArgsConstructor
public class MdmExternalPersonInfoDocumentService extends SsrAbstractDocumentService<MdmExternalPersonInfoDocument> {

    @NotNull
    @Override
    public DocumentType<MdmExternalPersonInfoDocument> getDocumentType() {
        return SsrDocumentTypes.MDM_EXTERNAL_PERSON_INFO;
    }

}
