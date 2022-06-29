package ru.croc.ugd.ssr.integration.service.wsdl.flat.model.external.xmlparsing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Value {
    @JsonProperty("value")
    private String value;
    @JsonProperty("id")
    private String id;
}
