package ru.croc.ugd.ssr.dto.mdm.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class Contacts {
    @JsonProperty("contact_mobile_registration")
    private final List<ContactMobileRegistration> contactMobileRegistrations;
}
