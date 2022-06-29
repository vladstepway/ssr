package ru.croc.ugd.ssr.model.ssrcco;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.croc.ugd.ssr.ssrcco.SsrCco;
import ru.reinform.cdp.document.model.DocumentAbstract;

@Getter
@Setter
public class SsrCcoDocument extends DocumentAbstract<SsrCco> {

    @JsonProperty("ssrCco")
    private SsrCco document;

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
