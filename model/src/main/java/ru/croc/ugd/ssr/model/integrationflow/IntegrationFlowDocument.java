package ru.croc.ugd.ssr.model.integrationflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.croc.ugd.ssr.integrationflow.IntegrationFlow;
import ru.reinform.cdp.document.model.DocumentAbstract;

public class IntegrationFlowDocument extends DocumentAbstract<IntegrationFlow> {

    @Getter
    @Setter
    @JsonProperty("integrationFlow")
    private IntegrationFlow document;

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
