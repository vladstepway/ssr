package ru.croc.ugd.ssr.integration.service.notification;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.dto.shipping.BookingInformation;
import ru.croc.ugd.ssr.dto.shipping.EdpResponseStatusDto;
import ru.croc.ugd.ssr.dto.shipping.ShippingFlowStatus;
import ru.croc.ugd.ssr.enums.ShippingApplicationSource;
import ru.croc.ugd.ssr.integration.command.PublishReasonCommand;
import ru.croc.ugd.ssr.integration.service.ShippingEventPublisher;
import ru.croc.ugd.ssr.mapper.ShippingMapper;
import ru.croc.ugd.ssr.model.ShippingApplicationDocument;
import ru.croc.ugd.ssr.shipping.ShippingApplication;
import ru.croc.ugd.ssr.shipping.ShippingApplicationType;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * DefaultShippingElkNotificationService.
 */
@Slf4j
@Component
@AllArgsConstructor
public class DefaultShippingElkNotificationService implements ShippingElkNotificationService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final ElkUserNotificationService elkUserNotificationService;
    private final ShippingEventPublisher shippingEventPublisher;
    private final ShippingMapper shippingMapper;

    @Override
    public void sendStatus(final ShippingFlowStatus status) {
        final PublishReasonCommand publishReasonCommand = createPublishReasonCommand(status);

        shippingEventPublisher.publishCurrentShippingStatus(publishReasonCommand);
    }

    @Override
    public void sendStatus(final ShippingFlowStatus status, final ShippingApplicationDocument document) {
        ofNullable(document.getDocument())
            .map(ShippingApplication::getShippingApplicationData)
            .map(ShippingApplicationType::getSource)
            .map(ShippingApplicationSource::fromValue)
            .ifPresent(source -> sendNotification(document, source, status));
    }

    @Override
    public void sendStatusToBk(String message) {
        shippingEventPublisher.publishStatusToBk(message);
    }

    @Override
    public void sendToBk(String message) {
        shippingEventPublisher.publishToBk(message);
    }

    private void sendNotification(
        final ShippingApplicationDocument document,
        final ShippingApplicationSource source,
        final ShippingFlowStatus status
    ) {
        if (source == ShippingApplicationSource.MPGU) {
            final PublishReasonCommand publishReasonCommand = createPublishReasonCommand(status, document);

            shippingEventPublisher.publishCurrentShippingStatus(publishReasonCommand);
        } else {
            if (nonNull(status.getNotificationTitle())
                && nonNull(status.getEmailNotificationTemplatePath())
                && nonNull(status.getElkNotificationTemplatePath())
            ) {
                try {
                    elkUserNotificationService.sendShippingNotification(document, status, null);
                } catch (Exception e) {
                    log.error("Сообщение не было отправлено в ЕЛК: {}", e.getMessage(), e);
                }
            }
        }
    }

    private PublishReasonCommand createPublishReasonCommand(
        final ShippingFlowStatus status, final ShippingApplicationDocument document
    ) {
        final BookingInformation bookingInformation = shippingMapper.toBookingInformation(document);
        final String statusText = createStatusText(status, document.getDocument().getShippingApplicationData());

        return PublishReasonCommand.builder()
            .bookingInformation(bookingInformation)
            .responseReasonDate(ZonedDateTime.now())
            .edpResponseStatusData(EdpResponseStatusDto.builder()
                .shippingFlowStatus(status)
                .edpResponseStatusText(statusText)
                .build())
            .build();
    }

    private PublishReasonCommand createPublishReasonCommand(
        final ShippingFlowStatus status
    ) {
        return PublishReasonCommand.builder()
            .responseReasonDate(ZonedDateTime.now())
            .edpResponseStatusData(EdpResponseStatusDto.builder()
                .shippingFlowStatus(status)
                .edpResponseStatusText(status.getStatusText())
                .build())
            .build();
    }

    private String createStatusText(final ShippingFlowStatus status,
                                    final ShippingApplicationType shippingApplication) {
        final String statusTextTemplate = status.getStatusText();

        final LocalDateTime shippingDateStart = shippingApplication.getShippingDateStart();
        final String moveDate = shippingDateStart.format(DATE_FORMATTER);
        final String moveTime = shippingDateStart.format(TIME_FORMATTER);

        final LocalDateTime cancelLocalDate = shippingDateStart.minusDays(1);
        final String cancelDate = cancelLocalDate.format(DATE_FORMATTER);
        final String cancelTime = cancelLocalDate.format(TIME_FORMATTER);

        switch (status) {
            case SHIPPING_DECLINED:
            case SHIPPING_DECLINED_BY_PREFECTURE:
                return String.format(statusTextTemplate, shippingApplication.getEno());
            case RECORD_RESCHEDULED:
                return String.format(statusTextTemplate, moveDate, moveTime, cancelDate, shippingApplication.getEno());
            case RECORD_ADDED:
                final String addressFrom = shippingApplication.getApartmentFrom().getStringAddress();
                final String addressTo = shippingApplication.getApartmentTo().getStringAddress();
                return String.format(statusTextTemplate, addressFrom, addressTo, moveDate, moveTime, cancelTime,
                    cancelDate);
            case SHIPPING_REMINDING:
                return String.format(statusTextTemplate, moveDate, moveTime, cancelDate);
            default:
                return statusTextTemplate;
        }
    }
}
