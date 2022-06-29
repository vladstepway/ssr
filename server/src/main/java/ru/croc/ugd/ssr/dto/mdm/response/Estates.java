package ru.croc.ugd.ssr.dto.mdm.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Estates {
    private final boolean validation;
    @JsonProperty("estate_type")
    private final String estateType;
    @JsonProperty("ownership_right")
    private final String ownershipRight;
}
