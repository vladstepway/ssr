package ru.croc.ugd.ssr.model.feedback;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.croc.ugd.ssr.feedback.Survey;
import ru.reinform.cdp.document.model.DocumentAbstract;

public class SurveyDocument extends DocumentAbstract<Survey> {

    @Getter
    @Setter
    @JsonProperty("survey")
    private Survey document;

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
