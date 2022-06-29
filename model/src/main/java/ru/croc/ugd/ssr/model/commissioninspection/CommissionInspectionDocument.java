package ru.croc.ugd.ssr.model.commissioninspection;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.croc.ugd.ssr.commission.CommissionInspection;
import ru.reinform.cdp.document.model.DocumentAbstract;

public class CommissionInspectionDocument extends DocumentAbstract<CommissionInspection> {

    @Getter
    @Setter
    @JsonProperty("commissionInspection")
    private CommissionInspection document;

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
