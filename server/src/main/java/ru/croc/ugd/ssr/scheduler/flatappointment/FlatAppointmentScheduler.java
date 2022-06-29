package ru.croc.ugd.ssr.scheduler.flatappointment;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.service.document.FlatAppointmentDocumentService;
import ru.croc.ugd.ssr.service.flatappointment.FlatAppointmentService;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class FlatAppointmentScheduler {

    @Value("${schedulers.flat-appointment.days-before-auto-cancellation}")
    private int daysBeforeAutoCancellation;

    private final FlatAppointmentService flatAppointmentService;
    private final FlatAppointmentDocumentService flatAppointmentDocumentService;

    @Scheduled(cron = "${schedulers.check-flat-appointment-cancellation.cron:0 0 0 * * ?}")
    public void appointmentAutoCancellation() {
        appointmentAutoCancellation(daysBeforeAutoCancellation);
    }

    public void appointmentAutoCancellation(final int daysBeforeAutoCancellation) {
        final LocalDate appointmentDateBefore = LocalDate.now().minusDays(daysBeforeAutoCancellation);
        flatAppointmentDocumentService.findRegisteredAppointmentsByAppointmentDateTimeBefore(appointmentDateBefore)
            .forEach(flatAppointmentService::processAutoCancellation);
    }
}
