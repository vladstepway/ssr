package ru.croc.ugd.ssr.integration.service.wsdl.flat.model.external.xmlparsings;

import org.testcontainers.shaded.com.fasterxml.jackson.annotation.JsonProperty;

public class Attribute {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("typeId")
    private Integer typeId;
    @JsonProperty("name")
    private String name;
    @JsonProperty("type")
    private String type;
    @JsonProperty("isPrimaryKey")
    private String isPrimaryKey;
    @JsonProperty("isEdit")
    private String isEdit;
    @JsonProperty("isReq")
    private String isReq;
    @JsonProperty("fieldMask")
    private String fieldMask;
    @JsonProperty("tehName")
    private String tehName;
    @JsonProperty("maxLength")
    private Integer maxLength;
    @JsonProperty("maxLengthDecimal")
    private String maxLengthDecimal;
    @JsonProperty("dictId")
    private String dictId;
    @JsonProperty("refCatalog")
    private String refCatalog;
    @JsonProperty("isDeleted")
    private String isDeleted;
    @JsonProperty("isDeletedTmp")
    private String isDeletedTmp;
    @JsonProperty("isMulti")
    private String isMulti;
    @JsonProperty("table")
    private Table table;
}
