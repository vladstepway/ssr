package ru.croc.ugd.ssr.integration.service.wsdl.flat.model.external.xmlparsing;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Category {
    @JsonProperty("value")
    private short value;
    @JsonProperty("idHier")
    private String idHier;
    @JsonProperty("nameHier")
    private String nameHier;
}
