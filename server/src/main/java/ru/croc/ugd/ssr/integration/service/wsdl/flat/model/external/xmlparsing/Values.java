package ru.croc.ugd.ssr.integration.service.wsdl.flat.model.external.xmlparsing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Values {
    @JsonProperty("value")
    private Value value;
    @JsonProperty("groupvalue")
    private Groupvalue groupvalue;
}
