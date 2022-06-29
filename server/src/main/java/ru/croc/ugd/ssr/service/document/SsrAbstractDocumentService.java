package ru.croc.ugd.ssr.service.document;

import static java.util.Objects.isNull;
import static java.util.Optional.of;

import ru.reinform.cdp.db.model.DocumentData;
import ru.reinform.cdp.document.model.DocumentAbstract;
import ru.reinform.cdp.document.service.AbstractDocumentService;

import java.util.Optional;
import javax.annotation.Nonnull;

public abstract class SsrAbstractDocumentService<T extends DocumentAbstract> extends AbstractDocumentService<T> {
    protected T parseDocumentData(@Nonnull DocumentData documentData) {
        return parseDocumentJson(documentData.getJsonData());
    }

    protected T parseDocumentJson(@Nonnull String json) {
        return jsonMapper.readObject(json, getDocumentType().getTypeClass());
    }

    public T updateDocument(@Nonnull final T document) {
        return super.updateDocument(document.getId(), document, true, true, null);
    }

    public T updateDocument(@Nonnull final T document, final String notes) {
        return super.updateDocument(document.getId(), document, true, true, notes);
    }

    public T createOrUpdateDocument(final T document, final String notes) {
        if (isNull(document.getId())) {
            return createDocument(document, true, notes);
        }
        return super.updateDocument(document.getId(), document, true, true, notes);
    }

    public Optional<T> fetchById(final String id) {
        try {
            return of(fetchDocument(id));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
