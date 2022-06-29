package ru.croc.ugd.ssr.service.document;

import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.db.dao.NotaryApplicationDao;
import ru.croc.ugd.ssr.model.notary.NotaryApplicationDocument;
import ru.croc.ugd.ssr.service.DocumentWithFolder;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.document.model.DocumentType;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * NotaryApplicationDocumentService.
 */
@Service
@AllArgsConstructor
public class NotaryApplicationDocumentService extends DocumentWithFolder<NotaryApplicationDocument> {

    private final NotaryApplicationDao notaryApplicationDao;

    @NotNull
    @Override
    public DocumentType<NotaryApplicationDocument> getDocumentType() {
        return SsrDocumentTypes.NOTARY_APPLICATION;
    }

    public List<NotaryApplicationDocument> findNonCancelledApplicationsByPersonDocumentId(final String personId) {
        return notaryApplicationDao.findNonCancelledApplicationsByPersonDocumentId(personId)
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    /**
     * Получение заявления на посещение нотариуса по ЕНО.
     * @param eno ЕНО.
     * @return заявление на посещение нотариуса
     */
    public Optional<NotaryApplicationDocument> findByEno(final String eno) {
        return notaryApplicationDao.findByEno(eno)
            .map(this::parseDocumentData);
    }

    public boolean existsByEno(final String eno) {
        return notaryApplicationDao.existsByEno(eno);
    }

    /**
     * Получение по ИД семьи заявлений на посещение нотариуса, по которым услуга оказана и договор заключен.
     * @param affairId ИД семьи
     * @return заявления на посещение нотариуса
     */
    public List<NotaryApplicationDocument> findApplicationsWithSignedContractByAffairId(final String affairId) {
        return ofNullable(affairId)
            .map(notaryApplicationDao::findApplicationsWithSignedContractByAffairId)
            .orElse(Collections.emptyList())
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }
}
