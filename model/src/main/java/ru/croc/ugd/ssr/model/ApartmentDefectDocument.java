package ru.croc.ugd.ssr.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.Nonnull;
import ru.croc.ugd.ssr.ApartmentDefect;
import ru.reinform.cdp.document.model.DocumentAbstract;

public class ApartmentDefectDocument extends DocumentAbstract<ApartmentDefect> {

  @JsonProperty("ApartmentDefect")
  private ApartmentDefect document;

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
  public ApartmentDefect getDocument() {
    return document;
  }
}
