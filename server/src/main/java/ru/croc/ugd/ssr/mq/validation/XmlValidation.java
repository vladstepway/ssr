package ru.croc.ugd.ssr.mq.validation;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateMessage;

import java.io.InputStream;
import java.io.StringReader;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

/**
 * Класс для валидации xml.
 */
@Component
@AllArgsConstructor
public class XmlValidation {

    private static final Logger LOG = LoggerFactory.getLogger(XmlValidation.class);

    private Schema schema;

    /**
     * Конструктор.
     */
    public XmlValidation() {
        this.schema = readSchemaFor617();
    }

    /**
     * Метод для валдиации файла перед отправкой его в очередь.
     *
     * @param xml - Валидируемая xml
     * @return true - файл прошел валидацию (готов к отправке)
     */
    public boolean validateXml(String xml) {
        try {
            if (this.schema == null) {
                this.schema = readSchemaFor617();
            }
            JAXBContext jc = JAXBContext.newInstance(CoordinateMessage.class);

            Unmarshaller unmarshaller = jc.createUnmarshaller();
            unmarshaller.setSchema(schema);
            StringReader xmlReader = new StringReader(xml);
            // Валидируем
            unmarshaller.unmarshal(xmlReader);
            LOG.info("Xml прошла валидацию перед отправкой");
            return true;
        } catch (JAXBException e) {
            LOG.error("Не удалось провалидировать xml перед отправкой в очередь: {0}", e);
            return false;
        }
    }

    private Schema readSchemaFor617() {
        try {
            final InputStream classPathResource = XmlValidation.class
                    .getResourceAsStream("/schema/serviceV6_1_7.xsd");
            final Source source = new StreamSource(classPathResource);
            final SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            return sf.newSchema(source);
        } catch (SAXException e) {
            LOG.error("Не удалось прочитать схему для валидации: {0}", e);
        }
        return null;
    }
}
