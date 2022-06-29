package ru.croc.ugd.ssr.integration.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SmevResponseScheduler {

    private final SmevResponseProcessor smevResponseProcessor;

    @Value("${schedulers.smev-response-processor.enabled:true}")
    private boolean isEnabledSmevResponseScheduler;

    @Scheduled(fixedDelayString = "${schedulers.smev-response-processor.fixed-delay:900000}")
    public void process() {
        if (isEnabledSmevResponseScheduler) {
            log.info("Scheduled SsrSmevResponseDocument processor started");

            smevResponseProcessor.process();

            log.info("Scheduled SsrSmevResponseDocument processor finished");
        }
    }
}
