package ru.croc.ugd.ssr.model.trade;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.croc.ugd.ssr.trade.TradeAddition;
import ru.reinform.cdp.document.model.DocumentAbstract;

public class TradeAdditionDocument extends DocumentAbstract<TradeAddition> {

    @Getter
    @Setter
    @JsonProperty("tradeAddition")
    private TradeAddition document;

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
