package ru.croc.ugd.ssr.service;

import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.config.CacheConfig;

@Slf4j
@Service
@AllArgsConstructor
public class CachingService {
    CacheManager cacheManager;

    public void evictAllCaches() {
        cacheManager.getCacheNames()
            .forEach(this::clearCache);
    }

    public void evictRealEstateCache() {
        clearCache(CacheConfig.REAL_ESTATE);
    }

    public void evictCcoCache() {
        clearCache(CacheConfig.CCO_ADDRESS_BY_UNOM);
    }

    @Scheduled(cron = "${schedulers.cache.cco-address-by-unom.cron:0 0/15 * * * *}")
    public void clearCacheForCcoAddressByUnom() {
        log.info("Clear cache for cco address by unom");
        clearCache(CacheConfig.CCO_ADDRESS_BY_UNOM);
    }

    private void clearCache(final String cacheName) {
        ofNullable(cacheManager.getCache(cacheName))
            .ifPresent(Cache::clear);
    }
}
