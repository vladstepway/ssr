package ru.croc.ugd.ssr.model.guardianship;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.croc.ugd.ssr.guardianship.GuardianshipRequest;
import ru.reinform.cdp.document.model.DocumentAbstract;

public class GuardianshipRequestDocument extends DocumentAbstract<GuardianshipRequest> {

    @Getter
    @Setter
    @JsonProperty("guardianshipRequest")
    private GuardianshipRequest document;

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
