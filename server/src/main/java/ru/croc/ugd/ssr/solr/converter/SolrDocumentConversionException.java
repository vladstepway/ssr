package ru.croc.ugd.ssr.solr.converter;

import static java.lang.String.format;

import ru.croc.ugd.ssr.exception.SsrException;

/**
 * SolrDocumentConversionException.
 */
public class SolrDocumentConversionException extends SsrException {

    /**
     * Creates SolrDocumentConversionException.
     * @param documentId documentId
     */
    public SolrDocumentConversionException(final String documentId) {
        super(format("Ошибка конвертации документа [%s] в формат SOLR", documentId));
    }
}
