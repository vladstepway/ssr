package ru.croc.ugd.ssr.config;

import org.springframework.context.annotation.Configuration;
import ru.reinform.cdp.utils.rest.config.BaseSwaggerConfig;

/**
 * Конфигурация swagger-документации.
 */
@Configuration
public class SwaggerConfig extends BaseSwaggerConfig {

    @Override
    protected String getBaseControllersPackage() {
        return "ru";
    }

    @Override
    protected String getAppName() {
        return "SSR REST API";
    }

}
