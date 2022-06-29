package ru.croc.ugd.ssr.service.document;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.db.dao.PersonalDocumentDao;
import ru.croc.ugd.ssr.model.personaldocument.PersonalDocumentDocument;
import ru.croc.ugd.ssr.service.DocumentWithFolder;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.document.model.DocumentType;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * PersonalDocumentDocumentService.
 */
@Service
@AllArgsConstructor
public class PersonalDocumentDocumentService extends DocumentWithFolder<PersonalDocumentDocument> {

    private final PersonalDocumentDao personalDocumentDao;

    @NotNull
    @Override
    public DocumentType<PersonalDocumentDocument> getDocumentType() {
        return SsrDocumentTypes.PERSONAL_DOCUMENT;
    }

    /**
     * Получение сведений о документах по ИД заявления.
     *
     * @param applicationDocumentId ИД заявления
     * @return сведения о документах
     */
    public Optional<PersonalDocumentDocument> findByApplicationDocumentId(final String applicationDocumentId) {
        return personalDocumentDao.findByApplicationDocumentId(applicationDocumentId)
            .map(this::parseDocumentData);
    }

    /**
     * Получение сведений о документах по ИД семьи.
     *
     * @param affairId ИД семьи
     * @return сведения о документах
     */
    public List<PersonalDocumentDocument> findAllByAffairId(final String affairId) {
        return personalDocumentDao.findAllByAffairId(affairId)
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    /**
     * Получение сведений о документах по ИД единого файла документов в FileStore.
     *
     * @param unionFileStoreId ИД единого файла документов в FileStore
     * @return сведения о документах
     */
    public Optional<PersonalDocumentDocument> findByUnionFileStoreId(final String unionFileStoreId) {
        return personalDocumentDao.findByUnionFileStoreId(unionFileStoreId)
            .map(this::parseDocumentData);
    }

    /**
     * Получить последние сведения о документах.
     *
     * @param affairId ИД семьи
     * @return сведения о документах
     */
    public Optional<PersonalDocumentDocument> findLastByAffairId(final String affairId) {
        return personalDocumentDao.findLastByAffairId(affairId)
            .map(this::parseDocumentData);
    }
}
