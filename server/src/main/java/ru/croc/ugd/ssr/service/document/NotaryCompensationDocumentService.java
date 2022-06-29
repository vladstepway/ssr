package ru.croc.ugd.ssr.service.document;

import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.db.dao.NotaryCompensationDao;
import ru.croc.ugd.ssr.model.notaryCompensation.NotaryCompensationDocument;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.document.model.DocumentType;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * NotaryCompensationDocumentService.
 */
@Service
@AllArgsConstructor
public class NotaryCompensationDocumentService extends SsrAbstractDocumentService<NotaryCompensationDocument> {

    private final NotaryCompensationDao notaryCompensationDao;

    @NotNull
    @Override
    public DocumentType<NotaryCompensationDocument> getDocumentType() {
        return SsrDocumentTypes.NOTARY_COMPENSATION;
    }

    public boolean existsByEno(final String eno) {
        return notaryCompensationDao.existsByEno(eno);
    }

    /**
     * Получение открытых заявлений на возмещение оплаты услуг нотариуса по ИД семьи.
     *
     * @param affairId ИД семьи
     * @return заявления на возмещение оплаты услуг нотариуса
     */
    public List<NotaryCompensationDocument> findOpenApplicationsByAffairId(final String affairId) {
        return ofNullable(affairId)
            .map(notaryCompensationDao::findOpenApplicationsByAffairId)
            .orElse(Collections.emptyList())
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    /**
     * Получение заявлений на возмещение оплаты услуг нотариуса с положительным решением по ИД семьи.
     *
     * @param affairId ИД семьи
     * @return заявления на возмещение оплаты услуг нотариуса
     */
    public List<NotaryCompensationDocument> findPerformedApplicationsByAffairId(final String affairId) {
        return ofNullable(affairId)
            .map(notaryCompensationDao::findPerformedApplicationsByAffairId)
            .orElse(Collections.emptyList())
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }
}
