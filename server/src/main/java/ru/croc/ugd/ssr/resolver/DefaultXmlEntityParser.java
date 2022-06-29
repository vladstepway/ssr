package ru.croc.ugd.ssr.resolver;

import org.springframework.stereotype.Component;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import java.io.StringReader;

/**
 * DefaultXmlEntityParser.
 */
@Component
public class DefaultXmlEntityParser implements EntityResolver {

    /**
     * resolveEntity.
     *
     * @param publicId publicId
     * @param systemId systemId
     * @return InputSource inputSource
     */
    public InputSource resolveEntity(final String publicId, final String systemId) {
        return new InputSource(new StringReader(""));
    }

}
