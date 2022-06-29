package ru.croc.ugd.ssr.service.notary;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "tasks.duration.notary")
public class NotaryTaskDurationConfig {

    private String refuseNoBooking;
    private String refuseNoDocuments;
    private String appointmentReminder;
    private String cancellationClosed;
    private String bookingClosed;
}
