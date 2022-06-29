package ru.croc.ugd.ssr.parser;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import ru.croc.ugd.ssr.exception.SsrException;
import ru.croc.ugd.ssr.resolver.DefaultXmlEntityParser;

import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Парсер данных жителя из xml документа.
 */
@Slf4j
@Component
public class PersonXmlParser {
    /**
     * Парсер xml файла.
     *
     * @param is InputStream
     * @return Document Document
     */
    public Document parserPersonXml(final InputStream is) {
        try {
            final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setValidating(false);
            dbf.setIgnoringComments(false);
            dbf.setIgnoringElementContentWhitespace(true);
            dbf.setNamespaceAware(true);
            dbf.setCoalescing(true);

            final DocumentBuilder db = dbf.newDocumentBuilder();
            db.setEntityResolver(new DefaultXmlEntityParser());

            final Document document = db.parse(is);
            document.getDocumentElement().normalize();

            return document;
        } catch (Exception e) {
            throw new SsrException(e.getMessage(), e);
        }
    }
}
