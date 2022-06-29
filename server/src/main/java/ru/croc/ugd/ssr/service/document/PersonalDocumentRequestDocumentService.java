package ru.croc.ugd.ssr.service.document;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.db.dao.PersonalDocumentRequestDao;
import ru.croc.ugd.ssr.model.personaldocument.PersonalDocumentRequestDocument;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.db.model.DocumentData;
import ru.reinform.cdp.document.model.DocumentType;
import ru.reinform.cdp.document.service.AbstractDocumentService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

/**
 * PersonalDocumentRequestDocumentService.
 */
@Service
@AllArgsConstructor
public class PersonalDocumentRequestDocumentService extends AbstractDocumentService<PersonalDocumentRequestDocument> {

    private final PersonalDocumentRequestDao personalDocumentRequestDao;

    @NotNull
    @Override
    public DocumentType<PersonalDocumentRequestDocument> getDocumentType() {
        return SsrDocumentTypes.PERSONAL_DOCUMENT_REQUEST;
    }

    /**
     * Получение последнего активного запроса документов по ИД документа жителя.
     *
     * @param personDocumentId ИД документа жителя
     * @return запрос документов
     */
    public Optional<PersonalDocumentRequestDocument> findLastActiveRequestByPersonDocumentId(
        final String personDocumentId
    ) {
        return personalDocumentRequestDao.findLastActiveRequestByPersonDocumentId(personDocumentId)
            .map(this::parseDocumentData);
    }

    /**
     * Получить запрос документов по идентификатору.
     *
     * @param id ИД
     * @return запрос документов
     */
    public Optional<PersonalDocumentRequestDocument> fetchById(final String id) {
        try {
            return Optional.of(this.fetchDocument(id));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Получение запросов документов по ИД документа жителя.
     *
     * @param personDocumentId ИД документа жителя
     * @return запросы документов
     */
    public List<PersonalDocumentRequestDocument> findByPersonDocumentId(final String personDocumentId) {
        return personalDocumentRequestDao.findByPersonDocumentId(personDocumentId)
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    private PersonalDocumentRequestDocument parseDocumentData(@Nonnull DocumentData documentData) {
        return parseDocumentJson(documentData.getJsonData());
    }

    private PersonalDocumentRequestDocument parseDocumentJson(@Nonnull String json) {
        return jsonMapper.readObject(json, PersonalDocumentRequestDocument.class);
    }
}
