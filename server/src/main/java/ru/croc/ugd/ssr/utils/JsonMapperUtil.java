package ru.croc.ugd.ssr.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ru.reinform.cdp.utils.mapper.JsonMapper;

public final class JsonMapperUtil {
    public static JsonMapper getJsonMapper() {
        return new JsonMapper(getMapper());
    }

    public static ObjectMapper getMapper() {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setDefaultPropertyInclusion(Include.ALWAYS);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return mapper;
    }
}
