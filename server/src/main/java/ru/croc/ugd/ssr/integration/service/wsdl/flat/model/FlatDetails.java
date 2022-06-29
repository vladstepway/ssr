package ru.croc.ugd.ssr.integration.service.wsdl.flat.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FlatDetails {
    @JsonProperty("addressIdentification")
    private String addressIdentification;
    @JsonProperty("amountOfLivingRooms")
    private String amountOfLivingRooms;
    @JsonProperty("uniqueFlatNumber")
    private String uniqueFlatNumber;
    @JsonProperty("floor")
    private String floor;
    @JsonProperty("fullSquare")
    private String fullSquare;
    @JsonProperty("calculatedSquare")
    private String calculatedSquare;
    @JsonProperty("flatTypeExternalRefId")
    private String flatTypeExternalRefId;
    @JsonProperty("flatNumber")
    private String flatNumber;
    @JsonProperty("livingSquare")
    private String livingSquare;
    @JsonProperty("sectionNumber")
    private String sectionNumber;
    private String unom;
}
