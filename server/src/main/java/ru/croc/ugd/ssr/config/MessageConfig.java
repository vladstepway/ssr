package ru.croc.ugd.ssr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * Конфиг сообщений.
 */
@Configuration
public class MessageConfig {

    /**
     * Создание бина сообщений.
     *
     * @return бин
     */
    @Bean
    public ResourceBundleMessageSource messageSource() {

        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasenames("messages/messages");
        source.setDefaultEncoding("UTF-8");
        source.setUseCodeAsDefaultMessage(true);

        return source;
    }
}
