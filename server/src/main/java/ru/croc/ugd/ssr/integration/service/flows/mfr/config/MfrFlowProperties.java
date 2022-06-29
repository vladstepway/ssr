package ru.croc.ugd.ssr.integration.service.flows.mfr.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = MfrFlowProperties.PREFIX)
public class MfrFlowProperties {

    static final String PREFIX = "ugd.ssr.mfr-flow";

    private boolean enabled;
}
