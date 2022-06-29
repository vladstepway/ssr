package ru.croc.ugd.ssr.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@Conditional({NotHazelcastClientCondition.class})
@EnableCaching
public class CacheConfig {
    public static final String REAL_ESTATE = "ugd_ssr_realEstateDocumentService";
    public static final String DICTIONARY_UPLOAD_DATA = "dictionaryUploadData";
    public static final String DICTIONARY_GET_DATA = "dictionaryGetData";
    public static final String ASSIGNEES = "ugd_ssr_assignees";
    public static final String BUSINESS_CALENDAR_GET_DATE = "businessCalendarGetDate";
    public static final String CCO_ADDRESS_BY_UNOM = "ccoAddressByUnom";

    @Primary
    @Bean()
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
            REAL_ESTATE,
            DICTIONARY_UPLOAD_DATA,
            DICTIONARY_GET_DATA,
            ASSIGNEES,
            BUSINESS_CALENDAR_GET_DATE,
            CCO_ADDRESS_BY_UNOM
        );
    }
}
