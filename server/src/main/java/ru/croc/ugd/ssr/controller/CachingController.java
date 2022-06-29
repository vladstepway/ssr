package ru.croc.ugd.ssr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.service.CachingService;

@RestController
@RequestMapping("/cache")
public class CachingController {

    @Autowired
    CachingService cachingService;

    @PostMapping(value = "/clear/all")
    public void clearAllCaches() {
        cachingService.evictAllCaches();
    }

    @PostMapping(value = "/clear/realestate")
    public void clearRealEstateCache() {
        cachingService.evictRealEstateCache();
    }

    @PostMapping(value = "/clear/cco")
    public void clearCcoCache() {
        cachingService.evictCcoCache();
    }
}
