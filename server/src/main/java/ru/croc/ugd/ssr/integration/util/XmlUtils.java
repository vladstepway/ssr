package ru.croc.ugd.ssr.integration.util;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import ru.reinform.cdp.filestore.service.FilestoreRemoteService;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.GregorianCalendar;
import java.util.Optional;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Методы по работе с XML.
 */
@Component
@AllArgsConstructor
public class XmlUtils {

    private static final Logger LOG = LoggerFactory.getLogger(XmlUtils.class);

    private final FilestoreRemoteService filestoreRemoteService;

    /**
     * Конвертируем XML Document в строку.
     *
     * @param document xml
     * @return строка
     */
    public String xmlDocumentToString(Document document) {
        DOMSource domSource = new DOMSource(document);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = tf.newTransformer();
            transformer.transform(domSource, result);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return writer.toString();
    }

    /**
     * Получить текущую дату в формате для XML.
     *
     * @return дата.
     */
    public XMLGregorianCalendar getXmlDateNow() {
        try {
            ZonedDateTime now = ZonedDateTime.now();
            GregorianCalendar gregorianCalendar = GregorianCalendar.from(now);
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
        } catch (DatatypeConfigurationException e) {
            LOG.error(e.getMessage());
            return null;
        }
    }

    /**
     * Получить текущую дату в формате для XML.
     *
     * @param zonedDateTime zonedDateTime
     * @return дата.
     */
    public XMLGregorianCalendar getXmlDate(ZonedDateTime zonedDateTime) {
        try {
            GregorianCalendar gregorianCalendar = GregorianCalendar.from(zonedDateTime);
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
        } catch (DatatypeConfigurationException e) {
            LOG.error(e.getMessage());
            return null;
        }
    }

    /**
     * Запись xml на диск.
     *
     * @param absolutePath абсолютный путь до папки
     * @param fileName     наименование файла
     * @param xmlString    xml
     */
    public void writeXmlFile(String absolutePath, String fileName, String xmlString) {
        FileUtils.writeStringFile(absolutePath, fileName + ".xml", xmlString);
    }

    /**
     * Записывает файл в необходимую дирректорию.
     *
     * @param xmlString текст.
     * @param dir       дирректория
     * @return директория + имя файла
     */
    public String writeXmlFile(String xmlString, String dir) {
        String fileName = "xml-" + LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyy.MM.dd-HH.mm.ss-n"));
        this.writeXmlFile(dir, fileName, xmlString);
        return dir + fileName;
    }

    /**
     * Получить xml из soap-сообщения.
     *
     * @param message soap-сообщение
     * @return xml
     */
    public String getXmlFromMessage(SOAPMessage message) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            message.writeTo(stream);
            return new String(stream.toByteArray(), StandardCharsets.UTF_8);
        } catch (Exception er) {
            return null;
        }
    }

    /**
     * Из объекта в xml.
     *
     * @param object  объект.
     * @param classes classes.
     * @return строку-xml
     */
    public String transformObjectToXmlString(Object object, Class<?>... classes) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(classes);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);

            StringWriter sw = new StringWriter();
            sw.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            jaxbMarshaller.marshal(object, sw);
            return sw.toString();
        } catch (JAXBException e) {
            LOG.error("Ошибка в процессе создания xml из объекта сообщения");
            LOG.error("Exception: ", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Из xml в объект.
     *
     * @param <R>     объект.
     * @param xml     объект.
     * @param classes classes.
     * @return строку-xml
     */
    public <R> R transformXmlToObject(Node xml, Class<?>... classes) {
        try {

            JAXBContext jaxbContext = JAXBContext.newInstance(classes);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            return (R) jaxbUnmarshaller.unmarshal(xml);
        } catch (JAXBException e) {
            LOG.error("Ошибка в процессе преобразования объекта из xml");
            LOG.error("Exception: ", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Из xml в объект.
     *
     * @param xml     объект.
     * @param <R>     объект.
     * @param classes объект.
     * @return строку-xml
     */
    public <R> R transformXmlToObject(String xml, Class<?>... classes) {
        try {

            JAXBContext jaxbContext = JAXBContext.newInstance(classes);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            return (R) jaxbUnmarshaller.unmarshal(new StringReader(xml));
        } catch (JAXBException e) {
            LOG.error("Ошибка в процессе преобразования объекта из xml");
            LOG.error("Exception: ", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Сохранить xml в Альфреско.
     *
     * @param xml      xml
     * @param folderId ид папки в альфреско
     * @return ид файла в альфреско
     */
    public String saveXmlToAlfresco(String xml, String folderId) {
        String fileName = "xml-"
            + ZonedDateTime.now().toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy.MM.dd-HH.mm.ss-n"))
            + ".xml";
        return filestoreRemoteService.createFile(
            fileName, "text/xml", xml.getBytes(), folderId, "xml", "SSR", "SSR", "UGD"
        );
    }

    /**
     * Parses xml to object.
     *
     * @param xml xml
     * @param classes classes
     * @param <T> T
     * @return parsed xml
     */
    public <T> Optional<T> parseXml(
        final String xml, final Class<?>[] classes
    ) {
        try {
            JAXBContext context = JAXBContext.newInstance(classes);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return Optional.of(
                (T) unmarshaller.unmarshal(new StringReader(xml))
            );
        } catch (Exception e) {
            LOG.error("Something went wrong during xml unmarshalling; xml: {}", xml, e);
            return Optional.empty();
        }
    }
}
