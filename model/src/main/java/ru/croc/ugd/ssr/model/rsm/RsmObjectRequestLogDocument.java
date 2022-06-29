package ru.croc.ugd.ssr.model.rsm;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.croc.ugd.ssr.rsm.RsmObjectRequestLog;
import ru.reinform.cdp.document.model.DocumentAbstract;

public class RsmObjectRequestLogDocument extends DocumentAbstract<RsmObjectRequestLog> {

    @Getter
    @Setter
    @JsonProperty("rsmObjectRequestLog")
    private RsmObjectRequestLog document;

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
