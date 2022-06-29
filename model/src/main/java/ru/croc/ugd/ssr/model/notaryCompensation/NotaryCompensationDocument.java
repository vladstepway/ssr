package ru.croc.ugd.ssr.model.notaryCompensation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.croc.ugd.ssr.notarycompensation.NotaryCompensation;
import ru.reinform.cdp.document.model.DocumentAbstract;

public class NotaryCompensationDocument extends DocumentAbstract<NotaryCompensation> {

    @Getter
    @Setter
    @JsonProperty("notaryCompensation")
    private NotaryCompensation document;

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
