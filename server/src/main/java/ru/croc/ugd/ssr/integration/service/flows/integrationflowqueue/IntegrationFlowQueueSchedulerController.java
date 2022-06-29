package ru.croc.ugd.ssr.integration.service.flows.integrationflowqueue;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/scheduler/integration-flow-queue")
public class IntegrationFlowQueueSchedulerController {

    private final IntegrationFlowQueueScheduler integrationFlowQueueScheduler;

    @PostMapping(value = "/process")
    public void processFlowMessages() {
        integrationFlowQueueScheduler.processFlowMessages();
    }
}
