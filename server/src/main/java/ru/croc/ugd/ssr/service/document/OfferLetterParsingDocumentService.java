package ru.croc.ugd.ssr.service.document;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.db.dao.OfferLetterParsingDao;
import ru.croc.ugd.ssr.model.offerletterparsing.OfferLetterParsingDocument;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.document.model.DocumentType;

/**
 * OfferLetterParsingDocumentService.
 */
@Service
@AllArgsConstructor
public class OfferLetterParsingDocumentService extends SsrAbstractDocumentService<OfferLetterParsingDocument> {

    private final OfferLetterParsingDao offerLetterParsingDao;

    @NotNull
    @Override
    public DocumentType<OfferLetterParsingDocument> getDocumentType() {
        return SsrDocumentTypes.OFFER_LETTER_PARSING;
    }

    /**
     * Существует разбор письма с предложением.
     *
     * @param letterId ИД письма с предложением
     * @param affairId ИД семьи
     * @return существует ли разбор письма с предложением
     */
    public boolean existsByLetterIdAndAffairId(final String letterId, final String affairId) {
        return offerLetterParsingDao.existsByLetterIdAndAffairId(letterId, affairId);
    }

    /**
     * Получение разбора письма с предложением.
     *
     * @param letterId ИД письма с предложением
     * @param affairId ИД семьи
     * @return разбор письма с предложением
     */
    public OfferLetterParsingDocument fetchByLetterIdAndAffairId(final String letterId, final String affairId) {
        return offerLetterParsingDao.fetchByLetterIdAndAffairId(letterId, affairId)
            .map(this::parseDocumentData)
            .orElse(null);
    }
}
