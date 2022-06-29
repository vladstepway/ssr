package ru.croc.ugd.ssr.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.config.SystemProperties;
import ru.reinform.cdp.filestore.model.signature.SignatureVerifyInfo;
import ru.reinform.cdp.filestore.service.FilestoreSignRemoteService;
import ru.reinform.cdp.utils.rest.utils.SendRestUtils;

@Service
@RequiredArgsConstructor
public class SsrFilestoreSignService {

    private static final String VERIFY_URL = "/v2/sign/verify/{fileId}/all?systemCode={systemCode}";
    private static final String SIGN_URL = "/v2/sign/{fileId}/{signId}?systemCode={systemCode}";

    @Value("${filestore.url}")
    private String filestoreUrl;

    private final SendRestUtils sendRestUtils;
    private final SystemProperties systemProperties;
    private final FilestoreSignRemoteService filestoreSignRemoteService;

    public SignatureVerifyInfo verifySign(final String fileId) {
        return this.sendRestUtils.sendJsonRequest(
            filestoreUrl + VERIFY_URL,
            HttpMethod.GET,
            null,
            SignatureVerifyInfo.class,
            fileId,
            systemProperties.getSystem()
        );
    }

    public byte[] getSign(final String fileId, final String signId) {
        return this.sendRestUtils.sendJsonRequest(
            filestoreUrl + SIGN_URL,
            HttpMethod.GET,
            null,
            byte[].class,
            fileId,
            signId,
            systemProperties.getSystem()
        );
    }

    public String addSign(final String fileId, final byte[] signContent) {
        return filestoreSignRemoteService.createOrAddSigner(fileId, signContent, systemProperties.getSystem());
    }
}
