package ru.croc.ugd.ssr.model.contractDigitalSign;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.croc.ugd.ssr.contractdigitalsign.ContractDigitalSign;
import ru.reinform.cdp.document.model.DocumentAbstract;

public class ContractDigitalSignDocument extends DocumentAbstract<ContractDigitalSign> {

    @Getter
    @Setter
    @JsonProperty("contractDigitalSign")
    private ContractDigitalSign document;

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
