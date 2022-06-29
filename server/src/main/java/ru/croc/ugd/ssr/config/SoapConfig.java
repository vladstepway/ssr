package ru.croc.ugd.ssr.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.transport.http.ClientHttpRequestMessageSender;
import ru.croc.ugd.ssr.integration.service.wsdl.flat.FetchFlatDetailsSoapService;

@Configuration
public class SoapConfig {
    @Autowired
    private SoapMosruProperties soapMosruProperties;

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        // this package must match the package in the <generatePackage> specified in
        // pom.xml
        marshaller.setContextPath("ru.croc.ugd.ssr.wsdl");
        return marshaller;
    }

    @Bean
    public ClientHttpRequestMessageSender clientHttpRequestMessageSender() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(soapMosruProperties.getTimeoutms());
        requestFactory.setReadTimeout(soapMosruProperties.getTimeoutms());
        return new ClientHttpRequestMessageSender(requestFactory);
    }

    @Bean
    public FetchFlatDetailsSoapService fetchFlatDetailsSoapService(
            Jaxb2Marshaller marshaller,
            ClientHttpRequestMessageSender clientHttpRequestMessageSender) {
        FetchFlatDetailsSoapService fetchFlatDetailsSoapService =
                new FetchFlatDetailsSoapService(soapMosruProperties);
        fetchFlatDetailsSoapService.setMessageSender(clientHttpRequestMessageSender);
        fetchFlatDetailsSoapService.setDefaultUri(soapMosruProperties.getUrl());
        fetchFlatDetailsSoapService.setMarshaller(marshaller);
        fetchFlatDetailsSoapService.setUnmarshaller(marshaller);
        return fetchFlatDetailsSoapService;
    }
}
