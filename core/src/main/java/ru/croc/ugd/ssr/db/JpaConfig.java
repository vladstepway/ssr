package ru.croc.ugd.ssr.db;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Добавления JPA-конвертеров из библиотеки в автоскан Hibernate.
 */
@Configuration
@EntityScan(basePackages = { "ru.reinform.cdp.db","ru.croc.ugd.ssr.model" })
@EnableJpaRepositories(basePackages = {"ru.reinform.cdp.db","ru.croc.ugd.ssr.db"})
public class JpaConfig {
}
