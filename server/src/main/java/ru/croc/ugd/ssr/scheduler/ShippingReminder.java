package ru.croc.ugd.ssr.scheduler;

import static ru.croc.ugd.ssr.dto.shipping.ShippingFlowStatus.RECALL_NOT_POSSIBLE;
import static ru.croc.ugd.ssr.dto.shipping.ShippingFlowStatus.SHIPPING_REMINDING;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.db.dao.ShippingApplicationDao;
import ru.croc.ugd.ssr.integration.service.notification.ShippingElkNotificationService;
import ru.croc.ugd.ssr.model.ShippingApplicationDocument;
import ru.croc.ugd.ssr.service.DocumentConverterService;

import java.time.LocalDateTime;

/**
 * ShippingReminder.
 */
@Component
@AllArgsConstructor
public class ShippingReminder {

    private final ShippingApplicationDao shippingApplicationDao;
    private final DocumentConverterService documentConverterService;
    private final ShippingElkNotificationService shippingElkNotificationService;

    /**
     * Sends shipping reminders.
     */
    @Scheduled(cron = "${schedulers.shipping-reminder.cron:50 * * * * ?}")
    public void sendShippingReminders() {
        final LocalDateTime startsAt = LocalDateTime.now().plusDays(2).withSecond(0).withNano(0);

        shippingApplicationDao.findActualApplicationsByStartDate(startsAt)
            .stream()
            .map(documentData -> documentConverterService
                .parseDocumentData(documentData, ShippingApplicationDocument.class)
            )
            .forEach(shippingApplication -> {
                shippingElkNotificationService.sendStatus(SHIPPING_REMINDING, shippingApplication);
            });
    }

    /**
     * Sends shipping recall not possible.
     */
    @Scheduled(cron = "${schedulers.shipping-recall-not-possible.cron:50 * * * * ?}")
    public void sendShippingRecallNotPossible() {
        final LocalDateTime startsAt = LocalDateTime.now().plusDays(1).withSecond(0).withNano(0);

        shippingApplicationDao.findActualApplicationsByStartDate(startsAt)
            .stream()
            .map(documentData -> documentConverterService
                .parseDocumentData(documentData, ShippingApplicationDocument.class)
            )
            .forEach(shippingApplication -> {
                shippingElkNotificationService.sendStatus(RECALL_NOT_POSSIBLE, shippingApplication);
            });
    }
}
