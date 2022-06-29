package ru.croc.ugd.ssr.service.document;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.model.disabledperson.DisabledPersonImportDocument;
import ru.croc.ugd.ssr.service.DocumentWithFolder;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.document.model.DocumentType;

/**
 * DisabledPersonImportDocumentService.
 */
@Service
@AllArgsConstructor
public class DisabledPersonImportDocumentService extends DocumentWithFolder<DisabledPersonImportDocument> {

    @NotNull
    @Override
    public DocumentType<DisabledPersonImportDocument> getDocumentType() {
        return SsrDocumentTypes.DISABLED_PERSON_IMPORT;
    }
}
