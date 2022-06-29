package ru.croc.ugd.ssr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Стартовый класс приложения.
 **/
@SpringBootApplication(scanBasePackages = {"ru.croc.ugd.ssr"})
public class ServerApp {
    /**
     * Стартовый метод приложения.
     *
     * @param args аргументы
     */
    public static void main(String[] args) {
        SpringApplication.run(new Class<?>[]{ServerApp.class}, args);
    }

}
