package ru.croc.ugd.ssr.model.affairCollation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.croc.ugd.ssr.affaircollation.AffairCollation;
import ru.reinform.cdp.document.model.DocumentAbstract;

public class AffairCollationDocument extends DocumentAbstract<AffairCollation> {

    @Getter
    @Setter
    @JsonProperty("affairCollation")
    private AffairCollation document;

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
