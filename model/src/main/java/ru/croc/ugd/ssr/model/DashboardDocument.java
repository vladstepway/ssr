package ru.croc.ugd.ssr.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.croc.ugd.ssr.Dashboard;
import ru.reinform.cdp.document.model.DocumentAbstract;

import javax.annotation.Nonnull;

/**
 * Класс-обертка бизнес-документа "Дашборд ССР" с root-элементом.
 */
@Getter
@Setter
public class DashboardDocument extends DocumentAbstract<Dashboard> {

    @JsonProperty("dashboard")
    private Dashboard document;

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
    public Dashboard getDocument() {
        return document;
    }
}
