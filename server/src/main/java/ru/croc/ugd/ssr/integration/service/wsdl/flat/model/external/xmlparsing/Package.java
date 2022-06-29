package ru.croc.ugd.ssr.integration.service.wsdl.flat.model.external.xmlparsing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Package {
    @JsonProperty("catalog")
    private Catalog catalog;
    @JsonProperty("count")
    private Byte count;
    @JsonProperty("id")
    private String id;
}
