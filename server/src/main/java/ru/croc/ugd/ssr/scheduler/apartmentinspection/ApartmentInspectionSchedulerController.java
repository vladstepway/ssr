package ru.croc.ugd.ssr.scheduler.apartmentinspection;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/scheduler/apartment-inspections")
public class ApartmentInspectionSchedulerController {

    private final ApartmentInspectionScheduler apartmentInspectionScheduler;

    @PostMapping(value = "/actualize-task-candidates")
    public void actualizeTaskCandidates() {
        apartmentInspectionScheduler.actualizeTaskCandidates();
    }
}
