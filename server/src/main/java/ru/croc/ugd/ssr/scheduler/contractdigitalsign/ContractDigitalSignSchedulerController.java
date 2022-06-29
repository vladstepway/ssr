package ru.croc.ugd.ssr.scheduler.contractdigitalsign;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/scheduler/contract-digital-signs")
public class ContractDigitalSignSchedulerController {

    private final ContractDigitalSignScheduler contractDigitalSignScheduler;

    @PostMapping(value = "/send-sign-notifications")
    public void sendContractDigitalSignNotifications() {
        contractDigitalSignScheduler.sendContractDigitalSignNotifications();
    }

    @PostMapping(value = "/send-remainder-email")
    public void sendEmailDigitalSignContracts() {
        contractDigitalSignScheduler.sendEmailDigitalSignContracts();
    }

}
