package ru.croc.ugd.ssr.service.document;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.db.dao.NotaryDao;
import ru.croc.ugd.ssr.model.notary.NotaryDocument;
import ru.croc.ugd.ssr.service.DocumentConverterService;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.db.model.DocumentData;
import ru.reinform.cdp.document.model.DocumentType;
import ru.reinform.cdp.document.service.AbstractDocumentService;

import java.util.List;
import java.util.Optional;

/**
 * NotaryDocumentService.
 */
@Slf4j
@Service
@AllArgsConstructor
public class NotaryDocumentService extends AbstractDocumentService<NotaryDocument> {

    private final NotaryDao notaryDao;
    private final DocumentConverterService documentConverterService;

    @NotNull
    @Override
    public DocumentType<NotaryDocument> getDocumentType() {
        return SsrDocumentTypes.NOTARY;
    }

    /**
     * Получение всех нотариусов.
     *
     * @param fullName Фильтр по фио нотариуса.
     * @param login Фильтр по логину.
     * @param includeUnassignedEmployee Фильтр по закрепленным за нотариусом сотрудникам.
     * @return Список найденных карточек нотариусов.
     */
    @NotNull
    public List<NotaryDocument> findAll(
        final String fullName,
        final String login,
        final boolean includeUnassignedEmployee
    ) {
        final List<DocumentData> notaries = notaryDao
            .findByFullNameAndLoginAndEmployeeAssigned(fullName, login, includeUnassignedEmployee);
        return documentConverterService.parseDocumentData(notaries, NotaryDocument.class);
    }

    /**
     * Получить логин сотрудника относящегося к нотарису.
     *
     * @param notaryId ИД notaryId
     * @return логин сотрудника.
     */
    public Optional<String> fetchNotaryLoginById(final String notaryId) {
        return notaryDao.fetchNotaryLoginById(notaryId);
    }

    /**
     * Получить NotaryDocument по идентификатору.
     *
     * @param id id
     * @return NotaryDocument
     */
    public Optional<NotaryDocument> fetchById(final String id) {
        try {
            return Optional.of(this.fetchDocument(id));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

}
