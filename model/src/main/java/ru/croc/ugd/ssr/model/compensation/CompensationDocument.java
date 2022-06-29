package ru.croc.ugd.ssr.model.compensation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.croc.ugd.ssr.Compensation;
import ru.reinform.cdp.document.model.DocumentAbstract;

public class CompensationDocument extends DocumentAbstract<Compensation> {

    @Getter
    @Setter
    @JsonProperty("compensation")
    private Compensation document;

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
