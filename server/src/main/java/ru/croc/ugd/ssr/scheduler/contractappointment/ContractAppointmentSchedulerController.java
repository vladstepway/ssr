package ru.croc.ugd.ssr.scheduler.contractappointment;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/scheduler/contract-appointments")
public class ContractAppointmentSchedulerController {

    private final ContractAppointmentScheduler contractAppointmentScheduler;

    @PostMapping(value = "/auto-cancellation")
    public void appointmentAutoCancellation(@RequestParam("days") final int daysBeforeAutoCancellation) {
        contractAppointmentScheduler.appointmentAutoCancellation(daysBeforeAutoCancellation);
    }

    @PostMapping(value = "/cancel-unsigned")
    public void cancelUnsigned() {
        contractAppointmentScheduler.cancelUnsignedAppointments();
    }

    @PostMapping(value = "/cancel-unsigned-by-employee")
    public void cancelUnsignedByEmployee() {
        contractAppointmentScheduler.cancelUnsignedAppointmentsByEmployee();
    }

    @PostMapping(value = "/cancel-unsigned-by-owners")
    public void cancelUnsignedByOwners() {
        contractAppointmentScheduler.cancelUnsignedAppointmentsByOwners();
    }
}
