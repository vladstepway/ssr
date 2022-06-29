package ru.croc.ugd.ssr.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * ApartmentInspectionConfig.
 */
@ConfigurationProperties(prefix = "ugd.ssr.opmosru")
@Data
public class SoapMosruProperties {
    private String url;
    private String username;
    private String password;
    private int timeoutms;
}
