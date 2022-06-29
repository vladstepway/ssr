package ru.croc.ugd.ssr.model.contractappointment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.croc.ugd.ssr.contractappointment.ContractAppointment;
import ru.reinform.cdp.document.model.DocumentAbstract;

public class ContractAppointmentDocument extends DocumentAbstract<ContractAppointment> {

    @Getter
    @Setter
    @JsonProperty("contractAppointment")
    private ContractAppointment document;

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
