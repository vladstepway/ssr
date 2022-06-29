package ru.croc.ugd.ssr.service.document;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.db.dao.PersonalDocumentApplicationDao;
import ru.croc.ugd.ssr.dto.personaldocument.RestPersonalDocumentApplicationDto;
import ru.croc.ugd.ssr.model.personaldocument.PersonalDocumentApplicationDocument;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.db.model.DocumentData;
import ru.reinform.cdp.document.model.DocumentType;
import ru.reinform.cdp.document.service.AbstractDocumentService;

import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

/**
 * PersonalDocumentApplicationDocumentService.
 */
@Service
@AllArgsConstructor
public class PersonalDocumentApplicationDocumentService
    extends AbstractDocumentService<PersonalDocumentApplicationDocument> {

    private final PersonalDocumentApplicationDao personalDocumentApplicationDao;

    @NotNull
    @Override
    public DocumentType<PersonalDocumentApplicationDocument> getDocumentType() {
        return SsrDocumentTypes.PERSONAL_DOCUMENT_APPLICATION;
    }

    /**
     * Существуют заявления на предоставление документов.
     *
     * @param personDocumentId ИД документа жителя
     * @return существуют ли заявления
     */
    public boolean existsByPersonDocumentId(final String personDocumentId) {
        return personalDocumentApplicationDao.existsByPersonDocumentId(personDocumentId);
    }

    public boolean existsByEno(final String eno) {
        return personalDocumentApplicationDao.existsByEno(eno);
    }

    /**
     * Получить заявления на предоставление документов по жителю.
     *
     * @param personDocumentId ИД документа жителя
     * @param includeRequestedApplications фильтр наличия запроса по заявлению
     * @return заявления на предоставление документов
     */
    public List<PersonalDocumentApplicationDocument> findAll(
        final String personDocumentId, final boolean includeRequestedApplications
    ) {
        return personalDocumentApplicationDao.findAll(personDocumentId, includeRequestedApplications)
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    private PersonalDocumentApplicationDocument parseDocumentData(@Nonnull DocumentData documentData) {
        return parseDocumentJson(documentData.getJsonData());
    }

    private PersonalDocumentApplicationDocument parseDocumentJson(@Nonnull String json) {
        return jsonMapper.readObject(json, PersonalDocumentApplicationDocument.class);
    }

}
