package ru.croc.ugd.ssr.dto.mdm.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DocPassportRf {
    private final String series;
    @JsonProperty("value")
    private final String number;
    @JsonProperty("issue_date")
    private final String issueDate;
    @JsonProperty("issuer_code")
    private final String issuerCode;
}
