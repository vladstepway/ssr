package ru.croc.ugd.ssr.service.mdm;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.croc.ugd.ssr.dto.mdm.request.MdmRequest;
import ru.croc.ugd.ssr.integration.config.IntegrationPropertyConfig;
import ru.reinform.cdp.utils.rest.utils.AbstractSendRestUtils;

@Component
@RequiredArgsConstructor
public class MdmSendRestUtils extends AbstractSendRestUtils {

    private final IntegrationPropertyConfig config;

    public String sendMdmRequest(final MdmRequest request) {
        final RestTemplate restTemplate = this.sendRestUtilsFactory.getRestTemplateInstance();
        final HttpHeaders headers = new HttpHeaders();
        headers.add("X-APPLICATION-TOKEN", config.getRestToken());
        return super.sendRequest(
            config.getRestUrl(), request, headers, HttpMethod.POST, restTemplate, String.class, null, (Object[]) null
        );
    }
}
