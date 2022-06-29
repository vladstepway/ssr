package ru.croc.ugd.ssr.scheduler.contractappointment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.service.contractappointment.ContractAppointmentService;
import ru.croc.ugd.ssr.service.document.ContractAppointmentDocumentService;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContractAppointmentScheduler {
    @Value("${schedulers.contract-appointment.days-before-auto-cancellation}")
    private int daysBeforeAutoCancellation;

    private final ContractAppointmentService contractAppointmentService;
    private final ContractAppointmentDocumentService contractAppointmentDocumentService;

    @Scheduled(cron = "${schedulers.check-contract-appointment-cancellation.cron:0 0 0 * * ?}")
    public void appointmentAutoCancellation() {
        appointmentAutoCancellation(daysBeforeAutoCancellation);
    }

    public void appointmentAutoCancellation(final int daysBeforeAutoCancellation) {
        final LocalDate appointmentDateBefore = LocalDate.now().minusDays(daysBeforeAutoCancellation);
        contractAppointmentDocumentService
            .findRegisteredAppointmentsByAppointmentDateTimeBefore(appointmentDateBefore)
            .forEach(contractAppointmentService::processAutoCancellation);
    }

    @Scheduled(cron = "${schedulers.cancel-unsigned-appointments.cron:0 0 0 * * ?}")
    public void cancelUnsignedAppointments() {
        log.info("Определение договоров, которые не были подписаны сотрудником и всеми правообладателями");
        contractAppointmentDocumentService
            .findUnsignedAppointmentsForYesterday()
            .forEach(contractAppointmentService::processUnsignedCancellation);
    }

    public void cancelUnsignedAppointmentsByEmployee() {
        log.info("Определение договоров, которые не были подписаны сотрудником");
        contractAppointmentDocumentService
            .findUnsignedAppointmentsForYesterday()
            .forEach(contractAppointmentService::processUnsignedByEmployeeCancellation);
    }

    public void cancelUnsignedAppointmentsByOwners() {
        log.info("Определение договоров, которые не были подписаны всеми правообладателями");
        contractAppointmentDocumentService
            .findUnsignedAppointmentsForYesterday()
            .forEach(contractAppointmentService::processUnsignedByOwnersCancellation);
    }
}
