package ru.croc.ugd.ssr.service;

import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.config.RequestResponseLoggingProperties;

import java.util.List;

@Service
@AllArgsConstructor
@ConditionalOnProperty(name = "ugd.ssr.request-response-logging.uri-filter.enabled")
public class RequestResponseLoggingService {

    private final RequestResponseLoggingProperties requestResponseLoggingProperties;

    public void updateUriFilter(List<String> uriList) {
        requestResponseLoggingProperties.getUriFilter().setValues(uriList);
    }

    public List<String> fetchUriFilter() {
        return requestResponseLoggingProperties.getUriFilter().getValues();
    }
}
