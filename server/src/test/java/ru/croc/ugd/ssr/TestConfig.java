package ru.croc.ugd.ssr;


import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.croc.ugd.ssr.config.SecurityConfig;
import ru.croc.ugd.ssr.db.JpaConfig;
import ru.croc.ugd.ssr.mq.interop.MqConfig;

/**
 * Тестовая конфигурация.
 */
@Configuration
@EnableAutoConfiguration
@Import({ MqConfig.class, JpaConfig.class, SecurityConfig.class })
@ComponentScan(basePackages = {"ru.croc.ugd.ssr", "ru.reinform.cdp.search.reindex"})
public class TestConfig {
}
