package ru.croc.ugd.ssr.scheduler.flatappointment;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/scheduler/flat-appointments")
public class FlatAppointmentSchedulerController {

    private final FlatAppointmentScheduler flatAppointmentScheduler;

    @PostMapping(value = "/auto-cancellation")
    public void appointmentAutoCancellation(@RequestParam("days") final int daysBeforeAutoCancellation) {
        flatAppointmentScheduler.appointmentAutoCancellation(daysBeforeAutoCancellation);
    }
}
