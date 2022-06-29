package ru.croc.ugd.ssr.service.ics;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.service.ChedFileService;
import ru.reinform.cdp.filestore.service.FilestoreRemoteService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class IcsFileService {

    private static final String CHED_ICS_DOCUMENT_CODE = "13050";
    private static final String CHED_ICS_DOCUMENT_TYPE = "CalendarFileInvitation";

    private final FilestoreRemoteService filestoreRemoteService;
    private final ChedFileService chedFileService;

    /**
     * Создать и залить в FileStore файл ics.
     * @param summary summary
     * @param description description
     * @param location location
     * @param appointmentDateTime appointmentDateTime
     * @param appointmentDuration appointmentDuration
     * @param icsFileName icsFileName
     * @param folderId folderId
     * @return ИД файла в FileStore
     */
    public Optional<String> createIcsFileInFileStore(
        final String summary,
        final String description,
        final String location,
        final LocalDateTime appointmentDateTime,
        final Duration appointmentDuration,
        final String icsFileName,
        final String folderId
    ) {
        return generateIcsFile(summary, description, location, appointmentDateTime, appointmentDuration)
            .map(icsFileBytes -> filestoreRemoteService.createFile(
                icsFileName,
                "text/calendar",
                icsFileBytes,
                folderId,
                "ics",
                null,
                null,
                "UGD"
            ));

    }

    private static Optional<byte[]> generateIcsFile(
        final String summary,
        final String description,
        final String location,
        final LocalDateTime appointmentDateTime,
        final Duration appointmentDuration
    ) {
        final Calendar calendar = new Calendar();
        calendar.getProperties().add(new ProdId("-//SSR//EN"));
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(CalScale.GREGORIAN);

        final DateTime startDateTime = new DateTime(
            Date.from(toUtcInstant(appointmentDateTime))
        );
        startDateTime.setUtc(true);

        final DateTime endDateTime = new DateTime(
            Date.from(toUtcInstant(appointmentDateTime.plus(appointmentDuration)))
        );
        endDateTime.setUtc(true);

        final VEvent event = createVEvent(startDateTime, endDateTime, summary, description, location);
        calendar.getComponents().add(event);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            final CalendarOutputter outputter = new CalendarOutputter();
            outputter.output(calendar, out);
            return Optional.of(out.toByteArray());
        } catch (IOException e) {
            log.warn("Unable to create ics file due to {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    public Optional<String> uploadIcsFileInChed(final String icsFileStoreId) {
        try {
            final String icsChedFileId = chedFileService.uploadFileToChed(
                icsFileStoreId,
                CHED_ICS_DOCUMENT_CODE,
                CHED_ICS_DOCUMENT_TYPE
            );
            return Optional.ofNullable(icsChedFileId);
        } catch (Exception e) {
            log.warn("Unable to upload ics file {} from fileStore to ched", icsFileStoreId, e);
            return Optional.empty();
        }
    }

    private static Instant toUtcInstant(final LocalDateTime localDateTime) {
        final ZonedDateTime ldtZoned = localDateTime.atZone(ZoneId.systemDefault());
        final ZonedDateTime ldtUtcZoned = ldtZoned.withZoneSameInstant(ZoneOffset.UTC);

        return ldtUtcZoned.toInstant();
    }

    private static VEvent createVEvent(
        final DateTime startDateTime,
        final DateTime endDateTime,
        final String summary,
        final String description,
        final String location
    ) {
        final VEvent event = new VEvent(startDateTime, endDateTime, summary);
        event.getProperties().add(new Uid(UUID.randomUUID().toString()));
        event.getProperties().add(new Description(description));
        event.getProperties().add(new Location(location));

        return event;
    }
}
