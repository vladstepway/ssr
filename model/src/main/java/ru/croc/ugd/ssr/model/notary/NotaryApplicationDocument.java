package ru.croc.ugd.ssr.model.notary;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.croc.ugd.ssr.notary.NotaryApplicantFile;
import ru.croc.ugd.ssr.notary.NotaryApplicantFiles;
import ru.croc.ugd.ssr.notary.NotaryApplication;
import ru.croc.ugd.ssr.notary.NotaryApplicationHistoryEvent;
import ru.croc.ugd.ssr.notary.NotaryApplicationHistoryEvents;
import ru.croc.ugd.ssr.notary.NotaryApplicationType;
import ru.croc.ugd.ssr.notary.ProcessingComments;
import ru.reinform.cdp.document.model.DocumentAbstract;

import java.time.LocalDateTime;
import java.util.List;

public class NotaryApplicationDocument extends DocumentAbstract<NotaryApplication> {

    @Getter
    @Setter
    @JsonProperty("notaryApplication")
    private NotaryApplication document;

    @Override
    public String getId() {
        return document.getDocumentID();
    }

    @Override
    public void assignId(String id) {
        document.setDocumentID(id);
    }

    @Override
    public String getFolderId() {
        return document.getFolderGUID();
    }

    @Override
    public void assignFolderId(String id) {
        document.setFolderGUID(id);
    }

    public void addFiles(final List<NotaryApplicantFile> files) {
        final NotaryApplicationType notaryApplication = document.getNotaryApplicationData();
        final NotaryApplicantFiles notaryApplicantFiles = ofNullable(notaryApplication.getApplicantFiles())
            .orElseGet(NotaryApplicantFiles::new);

        notaryApplicantFiles.getFiles().addAll(files);
        notaryApplication.setApplicantFiles(notaryApplicantFiles);
    }

    public void addHistoryEvent(final String statusId) {
        addHistoryEvent(statusId, null, null);
    }

    public void addHistoryEvent(final String statusId, final String comment) {
        addHistoryEvent(statusId, comment, null);
    }

    public void addHistoryEvent(final String statusId, final LocalDateTime appointmentDateTime) {
        addHistoryEvent(statusId, null, appointmentDateTime);
    }

    public void addHistoryEvent(final String statusId, final String comment, final LocalDateTime appointmentDateTime) {
        final NotaryApplicationType notaryApplication = document.getNotaryApplicationData();
        final NotaryApplicationHistoryEvents historyEvents = ofNullable(notaryApplication.getHistory())
            .orElseGet(NotaryApplicationHistoryEvents::new);

        if (nonNull(comment)) {
            final ProcessingComments processingComments = ofNullable(notaryApplication.getProcessingComments())
                .orElseGet(ProcessingComments::new);
            processingComments.getProcessingComment().add(comment);
            notaryApplication.setProcessingComments(processingComments);
        }

        historyEvents.getHistory().add(createEvent(statusId, comment, appointmentDateTime));
        notaryApplication.setHistory(historyEvents);
    }

    private static NotaryApplicationHistoryEvent createEvent(
        final String statusId, final String comment, final LocalDateTime appointmentDateTime
    ) {
        final NotaryApplicationHistoryEvent event = new NotaryApplicationHistoryEvent();
        event.setEventDate(LocalDateTime.now());
        event.setEventId(statusId);
        event.setComment(comment);
        event.setAppointmentDateTime(appointmentDateTime);

        return event;
    }
}
