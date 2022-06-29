package ru.croc.ugd.ssr.model.commissionorganisation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.croc.ugd.ssr.commissionorganisation.CommissionInspectionOrganisation;
import ru.reinform.cdp.document.model.DocumentAbstract;

public class CommissionInspectionOrganisationDocument extends DocumentAbstract<CommissionInspectionOrganisation> {

    @Getter
    @Setter
    @JsonProperty("commissionInspectionOrganisation")
    private CommissionInspectionOrganisation document;

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
