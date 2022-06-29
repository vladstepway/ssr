package ru.croc.ugd.ssr.integration.service;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.integration.parser.SoapMessageParser;
import ru.croc.ugd.ssr.integration.util.SoapUtils;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.service.mdm.MdmExternalPersonInfoService;

import java.util.concurrent.CompletableFuture;

/**
 * Сервис по интеграции с МДМ.
 */
@Slf4j
@Service
public class MdmIntegrationService {

    @Value("${ugd.ssr.new-update-snils.enabled:false}")
    private boolean newUpdateSnilsEnabled;
    @Value("${ugd.ssr.new-update-snils.mdm-request.enabled:false}")
    private boolean newUpdateSnilsMdmRequestEnabled;

    private final SoapUtils soapUtils;
    private final SoapMessageParser parser;
    private final MdmExternalPersonInfoService mdmExternalPersonInfoService;

    public MdmIntegrationService(
        final SoapUtils soapUtils,
        final SoapMessageParser parser,
        @Lazy final MdmExternalPersonInfoService mdmExternalPersonInfoService
    ) {
        this.soapUtils = soapUtils;
        this.parser = parser;
        this.mdmExternalPersonInfoService = mdmExternalPersonInfoService;
    }

    /**
     * Получение ssoId по СНИЛС.
     *
     * @param snils СНИЛС
     * @return ssoId
     */
    public String getSsoId(final String snils) {
        log.debug("Запрос на получение SsoId по СНИЛС: {}", snils);

        final String ssoId = (newUpdateSnilsEnabled && newUpdateSnilsMdmRequestEnabled)
            ? getSsoIdNew(snils)
            : getSsoIdLegacy(snils);

        log.debug("По запросу получен SsoId: {}", ssoId);
        return ssoId;
    }

    private String getSsoIdNew(final String snils) {
        return ofNullable(snils)
            .map(this::cleanSnils)
            .map(mdmExternalPersonInfoService::requestSsoIdBySnils)
            .orElse(null);
    }

    private String getSsoIdLegacy(final String snils) {
        return ofNullable(snils)
            .map(this::cleanSnils)
            .map(soapUtils::createGetSsoIdRequest)
            .map(soapUtils::sendSoapMessage)
            .map(parser::getSnilsFromSoapMessage)
            .orElse(null);
    }

    private String cleanSnils(final String snils) {
        return snils.replaceAll("-", "").replaceAll(" ", "");
    }

    /**
     * Получение ssoId по СНИЛС.
     *
     * @param snils СНИЛС
     * @return ssoId
     */
    @Async
    public CompletableFuture<String> getSsoIdAsync(final String snils) {
        return CompletableFuture.completedFuture(getSsoId(snils));
    }

    /**
     * Обновить ssoId жителю.
     *
     * @param personDocument житель
     * @return житель
     */
    public PersonDocument updatePersonSsoId(final PersonDocument personDocument) {
        PersonType personData = personDocument.getDocument().getPersonData();

        String snils = personData.getSNILS();
        if (isNull(snils)) {
            return personDocument;
        }

        String ssoId = getSsoId(snils);
        if (isNull(ssoId) || ssoId.isEmpty()) {
            return personDocument;
        }

        personData.setSsoID(ssoId);

        return personDocument;
    }

}
