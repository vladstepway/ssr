package ru.croc.ugd.ssr.integration.service.wsdl;

import lombok.AllArgsConstructor;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.soap.saaj.SaajSoapMessage;

import javax.xml.soap.MimeHeaders;

@AllArgsConstructor
public class CredentialsCallback implements WebServiceMessageCallback {
    private String userName;
    private String passWd;

    @Override
    public void doWithMessage(WebServiceMessage message) {
        if (message instanceof SaajSoapMessage) {
            SaajSoapMessage soapMessage = (SaajSoapMessage) message;
            MimeHeaders mimeHeader = soapMessage.getSaajMessage().getMimeHeaders();
            mimeHeader.setHeader("username", userName);
            mimeHeader.setHeader("password", passWd);
        }
    }
}
