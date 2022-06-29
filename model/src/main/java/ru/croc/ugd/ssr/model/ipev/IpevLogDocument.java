package ru.croc.ugd.ssr.model.ipev;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.croc.ugd.ssr.ipev.IpevLog;
import ru.reinform.cdp.document.model.DocumentAbstract;

public class IpevLogDocument extends DocumentAbstract<IpevLog> {

    @Getter
    @Setter
    @JsonProperty("ipevLog")
    private IpevLog document;

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
