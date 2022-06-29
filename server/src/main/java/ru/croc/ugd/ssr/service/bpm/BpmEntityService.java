package ru.croc.ugd.ssr.service.bpm;

import ru.reinform.cdp.document.model.DocumentType;

public interface BpmEntityService {

    DocumentType getDocumentType();

    String getProcessInstanceId(final String documentId);
}
