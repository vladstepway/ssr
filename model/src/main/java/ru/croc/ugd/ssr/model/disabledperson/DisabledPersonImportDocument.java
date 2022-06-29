package ru.croc.ugd.ssr.model.disabledperson;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.croc.ugd.ssr.disabledperson.DisabledPersonImport;
import ru.reinform.cdp.document.model.DocumentAbstract;

public class DisabledPersonImportDocument extends DocumentAbstract<DisabledPersonImport> {

    @Getter
    @Setter
    @JsonProperty("disabledPersonImport")
    private DisabledPersonImport document;

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
