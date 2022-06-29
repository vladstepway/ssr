package ru.croc.ugd.ssr.service.offerletterparsing;

import ru.croc.ugd.ssr.model.offerletterparsing.OfferLetterParsingDocument;

/**
 * Сервис для работы с разборами писем с предложением.
 */
public interface OfferLetterParsingService {

    /**
     * Завершает процесс разбора адреса из письма с предложением, если он не был завершен.
     *
     * @param offerLetterParsingDocument разбор письма с предложением
     */
    void finishBpmProcessIfNeeded(final OfferLetterParsingDocument offerLetterParsingDocument);
}
