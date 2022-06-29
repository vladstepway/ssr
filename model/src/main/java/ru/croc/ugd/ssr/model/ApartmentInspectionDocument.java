package ru.croc.ugd.ssr.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.croc.ugd.ssr.ApartmentInspection;
import ru.reinform.cdp.document.model.DocumentAbstract;

public class ApartmentInspectionDocument extends DocumentAbstract<ApartmentInspection> {

    @Getter
    @Setter
    @JsonProperty("ApartmentInspection")
    private ApartmentInspection document;

    @Override
    public String getId() {
        return document.getDocumentID();
    }

    @Override
    public void assignId(String s) {
        document.setDocumentID(s);
    }

    @Override
    public String getFolderId() {
        return document.getFolderGUID();
    }

    @Override
    public void assignFolderId(String s) {
        document.setFolderGUID(s);
    }
}
