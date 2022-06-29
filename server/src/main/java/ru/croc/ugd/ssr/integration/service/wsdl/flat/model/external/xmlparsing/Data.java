package ru.croc.ugd.ssr.integration.service.wsdl.flat.model.external.xmlparsing;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@lombok.Data
public class Data {
    @JsonProperty("attribute")
    private List<Attribute> attribute;
    @JsonProperty("action")
    private String action;
}
