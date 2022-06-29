package ru.croc.ugd.ssr.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.croc.ugd.ssr.shipping.ShippingDaySchedule;
import ru.reinform.cdp.document.model.DocumentAbstract;

public class ShippingDayScheduleDocument extends DocumentAbstract<ShippingDaySchedule> {

    @Getter
    @JsonProperty("shippingDaySchedule")
    private ShippingDaySchedule document;

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
