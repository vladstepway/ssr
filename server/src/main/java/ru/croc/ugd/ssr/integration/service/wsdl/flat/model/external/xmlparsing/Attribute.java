package ru.croc.ugd.ssr.integration.service.wsdl.flat.model.external.xmlparsing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Attribute {
    @JsonProperty("values")
    private Values values;
    @JsonProperty("name")
    private String name;
    @JsonProperty("fieldId")
    private Integer fieldId;
    @JsonProperty("type")
    private String type;
}
