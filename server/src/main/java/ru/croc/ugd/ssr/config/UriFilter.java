package ru.croc.ugd.ssr.config;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UriFilter {
    /**
     * Флаг включения фильтрации по URI.
     */
    private boolean enabled;
    /**
     * Список URI, для которых должно осуществляться логирование.
     */
    private List<String> values;
}
