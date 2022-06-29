package ru.croc.ugd.ssr.integration.service.wsdl.flat.model.external.xmlparsing;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Groupvalue {
    @JsonProperty("item")
    private List<Item> item;
}
