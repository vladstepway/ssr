package ru.croc.ugd.ssr.scheduler.realestate;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.service.realestate.RealEstateService;

@Component
@RequiredArgsConstructor
public class RealEstateScheduler {

    private final RealEstateService realEstateService;

    @Scheduled(cron = "${schedulers.real-estate-data-calculation.cron:0 0 0 * * ?}")
    public void calculateRealEstateData() {
        realEstateService.calculateData();
    }
}
