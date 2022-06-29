package ru.croc.ugd.ssr.integration.service.wsdl.flat.model.external.xmlparsings;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Attributes {
    @JsonProperty("attribute")
    private List<Attribute> attribute;
}
