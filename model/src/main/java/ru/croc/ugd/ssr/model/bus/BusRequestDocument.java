package ru.croc.ugd.ssr.model.bus;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.croc.ugd.ssr.bus.BusRequest;
import ru.reinform.cdp.document.model.DocumentAbstract;

public class BusRequestDocument extends DocumentAbstract<BusRequest> {

    @Getter
    @Setter
    @JsonProperty("busRequest")
    private BusRequest document;

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
