package ru.croc.ugd.ssr.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.croc.ugd.ssr.DashboardRequest;
import ru.reinform.cdp.document.model.DocumentAbstract;

import javax.annotation.Nonnull;

/**
 * Класс-обертка бизнес-документа "Запрос на создание дашборда ССР" с root-элементом.
 */
@Getter
@Setter
public class DashboardRequestDocument extends DocumentAbstract<DashboardRequest> {

    @JsonProperty("DashboardRequest")
    private DashboardRequest document;

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
    public DashboardRequest getDocument() {
        return document;
    }
}
