package ru.croc.ugd.ssr.model.flatAppointment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.croc.ugd.ssr.flatappointment.FlatAppointment;
import ru.reinform.cdp.document.model.DocumentAbstract;

public class FlatAppointmentDocument extends DocumentAbstract<FlatAppointment> {

    @Getter
    @Setter
    @JsonProperty("flatAppointment")
    private FlatAppointment document;

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

}
