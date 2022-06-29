package ru.croc.ugd.ssr.model.egrn;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.croc.ugd.ssr.egrn.EgrnFlatRequest;
import ru.reinform.cdp.document.model.DocumentAbstract;

public class EgrnFlatRequestDocument extends DocumentAbstract<EgrnFlatRequest> {

    @Getter
    @Setter
    @JsonProperty("egrnFlatRequest")
    private EgrnFlatRequest document;

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
