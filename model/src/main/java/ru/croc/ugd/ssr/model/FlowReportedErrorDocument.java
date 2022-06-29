package ru.croc.ugd.ssr.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.croc.ugd.ssr.flowreportederror.FlowReportedError;
import ru.reinform.cdp.document.model.DocumentAbstract;

import javax.annotation.Nonnull;

@Getter
@Setter
public class FlowReportedErrorDocument extends DocumentAbstract<FlowReportedError> {

    @Nonnull
    @JsonProperty("flowReportedError")
    private FlowReportedError document;

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
