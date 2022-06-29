package ru.croc.ugd.ssr.model.disabledperson;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.croc.ugd.ssr.disabledperson.DisabledPerson;
import ru.reinform.cdp.document.model.DocumentAbstract;

public class DisabledPersonDocument extends DocumentAbstract<DisabledPerson> {

    @Getter
    @Setter
    @JsonProperty("disabledPerson")
    private DisabledPerson document;

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
