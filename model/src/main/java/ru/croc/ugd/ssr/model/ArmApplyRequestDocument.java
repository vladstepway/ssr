package ru.croc.ugd.ssr.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.croc.ugd.ssr.ArmApplyRequest;
import ru.reinform.cdp.document.model.DocumentAbstract;

import javax.annotation.Nonnull;

/**
 * Класс-обертка бизнес-документа "Запрос на принятие/отказ" с root-элементом.
 */
@Getter
@Setter
public class ArmApplyRequestDocument extends DocumentAbstract<ArmApplyRequest> {

    @JsonProperty("ArmApplyRequest")
    private ArmApplyRequest document;

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

    @Nonnull
    @Override
    public ArmApplyRequest getDocument() {
        return document;
    }
}
