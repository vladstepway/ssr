package ru.croc.ugd.ssr.service.document;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.model.feedback.SurveyDocument;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.document.model.DocumentType;

@Service
@AllArgsConstructor
public class SurveyDocumentService extends SsrAbstractDocumentService<SurveyDocument> {

    @NotNull
    @Override
    public DocumentType<SurveyDocument> getDocumentType() {
        return SsrDocumentTypes.SURVEY;
    }
}
