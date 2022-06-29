package ru.croc.ugd.ssr.model.personuploadlog;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.croc.ugd.ssr.personuploadlog.PersonUploadLog;
import ru.reinform.cdp.document.model.DocumentAbstract;

public class PersonUploadLogDocument extends DocumentAbstract<PersonUploadLog> {

    @Getter
    @Setter
    @JsonProperty("personUploadLog")
    private PersonUploadLog document;

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
