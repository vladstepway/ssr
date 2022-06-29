package ru.croc.ugd.ssr.integration.util;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.integration.config.IntegrationPropertyConfig;

import java.net.URL;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

/**
 * Методы по работе с SOAP.
 */
@Component
@RequiredArgsConstructor
public class SoapUtils {

    private static final Logger LOG = LoggerFactory.getLogger(SoapUtils.class);

    private final IntegrationPropertyConfig config;

    /**
     * Отправка soap-сообщения.
     *
     * @param request запрос
     * @return soap-сообщение
     */
    public SOAPMessage sendSoapMessage(SOAPMessage request) {
        try {
            SOAPConnectionFactory factory = SOAPConnectionFactory.newInstance();
            SOAPConnection connection = factory.createConnection();
            URL url = new URL(config.getUrl());
            SOAPMessage response = connection.call(request, url);
            connection.close();
            return response;
        } catch (Exception e) {
            LOG.error("Невозможно отправить SOAP сообщение: {} ", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Формирование xml-запроса.
     *
     * @param snils СНИСЛ
     * @return soap-запрос
     */
    public SOAPMessage createGetSsoIdRequest(String snils) {
        try {
            MessageFactory factory = MessageFactory.newInstance();
            SOAPMessage message = factory.createMessage();

            SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
            envelope.addNamespaceDeclaration("fin", "http://find2.ws.elk.itb.ru/");

            MimeHeaders hd = message.getMimeHeaders();
            hd.addHeader("X-APPLICATION-TOKEN", config.getToken());

            SOAPElement body = envelope.getBody();
            SOAPElement findObjects = body.addChildElement("findObjects", "fin");

            SOAPElement systemInfo = findObjects.addChildElement("systemInfo");
            SOAPElement from = systemInfo.addChildElement("from");
            from.setTextContent(config.getSystemInfoFrom());
            SOAPElement to = systemInfo.addChildElement("to");
            to.setTextContent(config.getSystemInfoTo());

            SOAPElement select = findObjects.addChildElement("select");
            SOAPElement objectType = select.addChildElement("objectType");
            objectType.setTextContent("USER");
            SOAPElement offset = select.addChildElement("offset");
            offset.setTextContent("0");
            SOAPElement limit = select.addChildElement("limit");
            limit.setTextContent("10");
            SOAPElement attachObjTypes = select.addChildElement("attachObjTypes");
            attachObjTypes.setTextContent("ALL");
            SOAPElement filter = select.addChildElement("filter");
            SOAPElement logicOper = filter.addChildElement("logicOper");
            SOAPElement oper = logicOper.addChildElement("oper");
            oper.setTextContent("AND");
            SOAPElement cond = logicOper.addChildElement("cond");
            SOAPElement binOper = cond.addChildElement("binOper");
            SOAPElement field = binOper.addChildElement("field");
            field.setTextContent("Snils");
            SOAPElement operIn = binOper.addChildElement("oper");
            operIn.setTextContent("=");
            SOAPElement value = binOper.addChildElement("value");
            value.setTextContent(snils);

            return message;
        } catch (SOAPException e) {
            LOG.error(e.getMessage());
            return null;
        }
    }

}
