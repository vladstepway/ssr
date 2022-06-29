package ru.croc.ugd.ssr.integration.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus;
import ru.croc.ugd.ssr.integration.command.CommissionInspectionPublishReasonCommand;

/**
 * MockCommissionInspectionEventPublisher.
 */
@Slf4j
@Component
@ConditionalOnMissingBean(DefaultCommissionInspectionEventPublisher.class)
public class MockCommissionInspectionEventPublisher implements CommissionInspectionEventPublisher {

    @Override
    public void publishStatus(final CommissionInspectionPublishReasonCommand publishReasonCommand) {
        final CommissionInspectionFlowStatus status = publishReasonCommand.getStatus();
        log.info(
            "Mock commission inspection event publisher invoked for status {}, statusCode: {}",
            status.name(),
            status.getId()
        );
    }

    @Override
    public void publishStatusToBk(final String message) {
        log.info("Mock commission inspection event publisher invoked for BK status queue; xml {}", message);
    }

    @Override
    public void publishToBk(final String message) {
        log.info("Mock commission inspection event publisher invoked for BK queue; xml {}", message);
    }
}
