package ru.croc.ugd.ssr.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.croc.ugd.ssr.RealEstate;
import ru.reinform.cdp.document.model.DocumentAbstract;

import javax.annotation.Nonnull;

/**
 * Класс-обертка основного бизнес-документа системы с root-элементом.
 */
@Getter
@Setter
public class RealEstateDocument extends DocumentAbstract<RealEstate> {

    @Nonnull
    @JsonProperty("RealEstate")  //должно соответствовать корневому элементу XSD
    private RealEstate document;

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
