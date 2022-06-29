package ru.croc.ugd.ssr.scheduler.contractdigitalsign;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.service.contractdigitalsign.ContractDigitalSignEmailService;
import ru.croc.ugd.ssr.service.contractdigitalsign.ContractDigitalSignService;

@Component
@AllArgsConstructor
public class ContractDigitalSignScheduler {

    private final ContractDigitalSignService contractDigitalSignService;
    private final ContractDigitalSignEmailService contractDigitalSignEmailService;

    @Scheduled(cron = "${schedulers.send-contract-digital-sign-notification.cron: 0 1 0 * * ?}")
    public void sendContractDigitalSignNotifications() {
        contractDigitalSignService.sendContractDigitalSignNotificationsForToday();
    }

    @Scheduled(cron = "${schedulers.send-email-digital-sign-contracts.cron:0 0 8 * * ?}")
    public void sendEmailDigitalSignContracts() {
        contractDigitalSignEmailService.sendContractDigitalSignRemainderEmail();
    }

}
