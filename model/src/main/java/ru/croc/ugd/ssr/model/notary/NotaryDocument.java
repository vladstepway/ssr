package ru.croc.ugd.ssr.model.notary;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.croc.ugd.ssr.notary.Notary;
import ru.reinform.cdp.document.model.DocumentAbstract;

public class NotaryDocument extends DocumentAbstract<Notary> {

    @Getter
    @Setter
    @JsonProperty("notary")
    private Notary document;

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
