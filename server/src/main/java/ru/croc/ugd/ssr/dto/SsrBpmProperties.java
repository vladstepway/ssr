package ru.croc.ugd.ssr.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "ugd.ssr.bpm")
public class SsrBpmProperties {

    private Map<String, String> documentTypeLdapGroupMap;
}
