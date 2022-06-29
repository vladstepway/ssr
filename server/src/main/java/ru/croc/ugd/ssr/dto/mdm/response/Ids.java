package ru.croc.ugd.ssr.dto.mdm.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class Ids {
    @JsonProperty("SSO")
    private final List<Sso> ssoList;
}
