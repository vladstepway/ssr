package ru.croc.ugd.ssr.integration.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

/**
 * Парсер soap-сообщений.
 */
@Component
public class SoapMessageParser {

    private static final Logger LOG = LoggerFactory.getLogger(SoapMessageParser.class);

    /**
     * Вытаскиваем из soap-сообщения снилс или возвращаем null.
     *
     * @param message soap-сообщение
     * @return списл
     */
    public String getSnilsFromSoapMessage(SOAPMessage message) {
        try {
            String snils = "";

            SOAPBody soapBody = message.getSOAPBody();
            NodeList nodeListBody = soapBody.getChildNodes();
            for (int i = 0; i < nodeListBody.getLength(); i++) {
                Node node = nodeListBody.item(i);
                if (node instanceof Element && "findObjectsResponse".equals(node.getLocalName())) {
                    NodeList nodeListFindObjectsResponse = node.getChildNodes();
                    for (int j = 0; j < nodeListFindObjectsResponse.getLength(); j++) {
                        Node nodeFound = nodeListFindObjectsResponse.item(j);
                        if (nodeFound instanceof Element && "found".equals(nodeFound.getLocalName())) {
                            NodeList nodeListFounds = nodeFound.getChildNodes();
                            for (int k = 0; k < nodeListFounds.getLength(); k++) {
                                Node nodeObjects = nodeListFounds.item(k);
                                if (nodeObjects instanceof Element && "objects".equals(nodeObjects.getLocalName())) {
                                    NodeList nodeListObjects = nodeObjects.getChildNodes();
                                    for (int l = 0; l < nodeListObjects.getLength(); l++) {
                                        Node nodeObject = nodeListObjects.item(l);
                                        if (nodeObject instanceof Element
                                                && "object".equals(nodeObject.getLocalName())) {
                                            NodeList nodeListObject = nodeObject.getChildNodes();
                                            for (int h = 0; h < nodeListObject.getLength(); h++) {
                                                Node nodeAttribute = nodeListObject.item(h);
                                                if (nodeAttribute instanceof Element
                                                        && "attribute".equals(nodeAttribute.getLocalName())) {
                                                    NodeList nodeListAttribute = nodeAttribute.getChildNodes();
                                                    for (int g = 0; g < nodeListAttribute.getLength(); g++) {
                                                        Node nodeValue = nodeListAttribute.item(g);
                                                        if (nodeValue instanceof Element
                                                                && "value".equals(nodeValue.getLocalName())) {
                                                            snils = nodeValue.getTextContent();
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return snils;
        } catch (SOAPException e) {
            LOG.error(e.getMessage());
            return null;
        }
    }

}
