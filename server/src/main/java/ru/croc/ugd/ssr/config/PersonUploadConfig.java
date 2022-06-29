package ru.croc.ugd.ssr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class PersonUploadConfig {

    @Bean
    public List<String> snilsToProcessList() {
        return new ArrayList<>();
    }
}
