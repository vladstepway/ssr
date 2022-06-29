package ru.croc.ugd.ssr.model.personaldocument;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.croc.ugd.ssr.personaldocument.PersonalDocumentApplication;
import ru.reinform.cdp.document.model.DocumentAbstract;

public class PersonalDocumentApplicationDocument extends DocumentAbstract<PersonalDocumentApplication> {

    @Getter
    @Setter
    @JsonProperty("personalDocumentApplication")
    private PersonalDocumentApplication document;

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
