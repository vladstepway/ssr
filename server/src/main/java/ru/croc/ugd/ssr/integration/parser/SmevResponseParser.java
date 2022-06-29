package ru.croc.ugd.ssr.integration.parser;

import static java.util.Objects.isNull;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.integration.config.IntegrationPropertyConfig;
import ru.croc.ugd.ssr.integration.util.XmlUtils;
import ru.croc.ugd.ssr.mapper.SsrSmevResponseMapper;
import ru.croc.ugd.ssr.model.SsrSmevResponseDocument;
import ru.croc.ugd.ssr.model.integration.busresponse.SmevResponse;
import ru.croc.ugd.ssr.service.document.SsrSmevResponseDocumentService;
import ru.reinform.cdp.utils.core.RIXmlUtils;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

/**
 * Парсер ответов SMEV.
 */
@Slf4j
@Service
@AllArgsConstructor
public class SmevResponseParser {

    private final Environment environment;
    private final XmlUtils xmlUtils;
    private final IntegrationPropertyConfig config;
    private final SsrSmevResponseDocumentService ssrSmevResponseDocumentService;
    private final SsrSmevResponseMapper ssrSmevResponseMapper;

    /**
     * Процессинг входящего сообщения SMEV.
     *
     * @param message message
     * @return ssrSmevResponseDocument id
     */
    public String processMessageSmev(final String message) {
        final SmevResponse response = RIXmlUtils.unmarshal(message, SmevResponse.class);
        if (isNull(response.getStatusCode()) || isNull(response.getServiceNumber())) {
            log.debug("processMessageSmev: statusCode or serviceNumber is missing, message: {}", message);
            return null;
        }
        if (!isSsrRequestInitiator(response)) {
            log.debug("processMessageSmev: received smev response for another system, message: {}", message);
            return null;
        }
        final String smevResponseFileName = storeSmevResponseAsFile(message);

        final SsrSmevResponseDocument ssrSmevResponseDocument = ssrSmevResponseMapper
            .toSsrSmevResponseDocument(response, smevResponseFileName);

        ssrSmevResponseDocumentService.createDocument(ssrSmevResponseDocument, false, null);
        log.debug("processMessageSmev: successfully saved message: {}", message);

        return ssrSmevResponseDocument.getId();
    }

    private boolean isSsrRequestInitiator(final SmevResponse response) {
        final String responseServiceNumber = response.getServiceNumber();
        if (responseServiceNumber == null) {
            return false;
        }

        final String[] allowedRequestInitiators = config.getAllowedRequestInitiators().split(",");

        return Stream.of(allowedRequestInitiators)
            .anyMatch(responseServiceNumber::startsWith);
    }

    private String storeSmevResponseAsFile(final String message) {
        final String smevResponseFileName = "importSmevXml-"
            + ZonedDateTime.now().toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy.MM.dd-HH.mm.ss-n"));

        xmlUtils.writeXmlFile(
            environment.getProperty("integration.saveSmevXmlFolder"),
            smevResponseFileName,
            message
        );

        return smevResponseFileName;
    }
}
