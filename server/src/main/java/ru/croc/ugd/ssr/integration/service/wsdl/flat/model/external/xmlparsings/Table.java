package ru.croc.ugd.ssr.integration.service.wsdl.flat.model.external.xmlparsings;

import org.testcontainers.shaded.com.fasterxml.jackson.annotation.JsonProperty;

public class Table {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("fullName")
    private String fullName;
    @JsonProperty("technicalName")
    private String technicalName;
    @JsonProperty("shortName")
    private String shortName;
    @JsonProperty("accountingObject")
    private String accountingObject;
    @JsonProperty("keywords")
    private String keywords;
    @JsonProperty("vid")
    private String vid;
    @JsonProperty("type")
    private String type;
    @JsonProperty("period")
    private String period;
    @JsonProperty("hasGeo")
    private String hasGeo;
    @JsonProperty("categories")
    private String categories;
    @JsonProperty("oiv")
    private String oiv;
    @JsonProperty("packageId")
    private Integer packageId;
    @JsonProperty("attributes")
    private Attributes attributes;
}
