package ru.croc.ugd.ssr.scheduler.realestate;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/scheduler/real-estates")
public class RealEstateSchedulerController {

    private final RealEstateScheduler realEstateScheduler;

    @PostMapping(value = "/calculate-data")
    public void calculateData() {
        realEstateScheduler.calculateRealEstateData();
    }
}
