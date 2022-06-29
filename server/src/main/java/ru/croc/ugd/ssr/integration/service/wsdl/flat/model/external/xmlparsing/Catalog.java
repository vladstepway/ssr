package ru.croc.ugd.ssr.integration.service.wsdl.flat.model.external.xmlparsing;

import com.fasterxml.jackson.annotation.JsonProperty;

@lombok.Data
public class Catalog {
    @JsonProperty("name")
    private String name;
    @JsonProperty("data")
    private Data data;
    @JsonProperty("categories")
    private Categories categories;
}
